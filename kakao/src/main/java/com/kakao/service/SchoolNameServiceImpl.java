package com.kakao.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.model.CareerNetData;
import com.kakao.util.FileUtil;
import com.kakao.util.KMPAlgorithm;
import com.kakao.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;

public class SchoolNameServiceImpl implements SchoolNameSercvice{


    private static final Logger log = LoggerFactory.getLogger(SchoolNameServiceImpl.class);

    private String[][] schoolType;

    private Map<String, String> replaceNameSet;
    private Map<String, String> replaceTypeSet;
    private Map<String, String> replaceSpecialSet;

    // 커리어넷 URL
    private static String CAREERNET_BASE_URL = "http://www.career.go.kr/cnet/openapi/getOpenApi";

    // 커리어넷 오픈 API 인증키
    private static String API_KEY = "46d7168b141f2c862659a2cbbaff8764";


    public SchoolNameServiceImpl() {

        // 학교타입 줄임말 초기화
        replaceTypeSet = new HashMap<>();
        replaceTypeSet.put("대학교", "대");
        replaceTypeSet.put("초등학교", "초");
        replaceTypeSet.put("중학교", "중");
        replaceTypeSet.put("고등학교", "고");


        // 특정 학교 이름 줄임말 리스트 초기화
        replaceNameSet = new HashMap<>();
        replaceNameSet.put("여자", "여");
        replaceNameSet.put("외국어", "외");
        replaceNameSet.put("예술", "예");
        replaceNameSet.put("과학", "과");
        replaceNameSet.put("공업", "공");
        replaceNameSet.put("상업", "상");
        replaceNameSet.put("체육", "체");
        replaceNameSet.put("경영", "경");
        replaceNameSet.put("사범대학부속고", "사대부고");

        // 특이 케이스 줄임말 리스트
        replaceSpecialSet = new HashMap<>();
        replaceSpecialSet.put("여상고", "여상"); // 전남여자상업고 -> 광주여상
        replaceSpecialSet.put("여전대", "여전"); // 숭의여자전문대 -> 숭의여전
        replaceSpecialSet.put("(마이스터고)", "");


        // 학교 타입 초기화
        schoolType = new String[][]{{"고등학교","high_list"},{"중학교", "midd_list"},{"초등학교", "elem_list"},{"대학교", "univ_list"},{"특수학교", "seet_list"},{"대안학교", "alte_list"}};
    }

    /**
     * 학교 개수 구하기
     * <p>
     * 문자열 탐색 알고리즘 KMP, 보이어무어 중 KMP 알고리즘을 사용
     */
    public List<String> getSchoolCount(List<String> schoolNameList, String text) {

        log.info("********************************************************");
        log.info(" 학교이름 개수 찾기");
        log.info("");

        // 결과 저장 리스트
        List<String> resultList = new ArrayList<>();


        for (String schoolName : schoolNameList) {

            // 학교이름처럼 불릴만한 set 가져오기
            List<String> schoolNameSet = getSchoolNameSet(schoolName);

            int cnt = 0;
            for (String schoolSetName : schoolNameSet) {
                cnt += KMPAlgorithm.patternCount(text, schoolSetName);
            }

            if (cnt != 0) {
                resultList.add(schoolName + " " + cnt);
            }

        }


        log.info("중복없이 총 "+resultList.size()+"개 학교의 이름 검색");

        return resultList;
    }


    /**
     * 커리어넷 오픈 API 를 통해 학교정보 조회
     * <p>
     * 고등, 중등, 초등, 대학, 특수학교, 대안학교 정보 순서대로 검색
     */
    public List<String> searchSchool() {
        log.info("********************************************************");
        log.info(" 커리어넷 학교정보 호출");
        log.info("");
        List<String> schoolNameList = new ArrayList<>();

        for (String[] type : schoolType) {

            String CareerNetDataJson = new RestUtil().get(CAREERNET_BASE_URL + "?apiKey=" + API_KEY + "&svcType=api&svcCode=SCHOOL&contentType=json&gubun=" + type[1] + "&thisPage=1&perPage=10000");
            try {
                CareerNetData careernetData = new ObjectMapper().readValue(CareerNetDataJson, CareerNetData.class);

                // 학교이름만 중복제거하여 리스트에 추가
                schoolNameList.addAll(careernetData.getDataSearch().getSchoolNameList());
                log.info(type[0]+" 완료...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return schoolNameList;
    }


    /**
     * 학교이름 set 찾기
     *
     * 줄여부르는 학교이름들을 찾아서 List로 리턴
     *
     * ex > 광주숭일고등학교 -> 광주숭일고
     * 전남대학교사범대학부속고등학교 -> 전대사대부고, 전남대학교사범대학부속고
     * 전님여자상업고등학교 -> 전남여자상업고, 전남여상업고, 전남여자상고, 전남여상
     */
    public List<String> getSchoolNameSet(String schoolName) {

        // 학교 타입별 단어 필터링, 대구여자상업고등학교 -> 대구여자상업고 꼴로 바뀌어 1개만 추가
        List<String> schoolNameList = schoolTypeFilter(schoolName);

        // 학교 이름별 줄임말 추가, 대구여자상업고 -> { 대구여상업고, 대구여상고, 대구여자상고, 대구여자상업고 } 꼴로 가능할만한 이름이 추가
        addSchoolName(schoolNameList);

        // 특이하게 줄여지는 학교이름 변경, { 대구여상업고, 대구여상고, 대구여자상고, 대구여자상업고 } 중, 대구여상고 -> 대구여상 으로 변경
        schoolSpecialNameFilter(schoolNameList);

        // 중복제거하고 리턴
        return new ArrayList<>(new HashSet<>(schoolNameList));
    }




    private void schoolSpecialNameFilter (List<String> schoolNameList) {
        Iterator replaceTypeKeySet = replaceSpecialSet.keySet().iterator();

        while (replaceTypeKeySet.hasNext()) {

            String key = (String) replaceTypeKeySet.next();

            for(int i = 0 ; i < schoolNameList.size();i++) {

                String schoolName = schoolNameList.get(i);
                if(!schoolName.contains(key)) continue;

                schoolNameList.set(i, schoolName.replace(key, replaceSpecialSet.get(key)));
            }
        }
    }



    private void addSchoolName (List<String> schoolNameList) {

        // 학교이름에서 파생 가능한 약어들을 분류
        Iterator replaceNameKeySet = replaceNameSet.keySet().iterator();
        String schoolName = schoolNameList.get(0);


        while (replaceNameKeySet.hasNext()) {

            String key = (String) replaceNameKeySet.next();
            if (!schoolName.contains(key)) continue;

            schoolNameList.add(schoolName.replace(key, replaceNameSet.get(key)));



            int schoolNameListSize = schoolNameList.size();

            for (int i = 0; i < schoolNameListSize; i++) {

                String schoolSetName = schoolNameList.get(i);
                if (!schoolSetName.contains(key)) continue;

                schoolSetName = schoolSetName.replace(key, replaceNameSet.get(key));
                schoolNameList.add(schoolSetName);
            }
        }
    }



    private List<String> schoolTypeFilter (String schoolName) {
        List<String> schoolNameList = new ArrayList<>();

        Iterator replaceTypeKeySet = replaceTypeSet.keySet().iterator();

        while (replaceTypeKeySet.hasNext()) {

            String key = (String) replaceTypeKeySet.next();
            if (!schoolName.contains(key)) continue;

            // ex) 숭일고등학교 -> 숭일고 꼴로 저장
            schoolNameList.add(schoolName.replace(key, replaceTypeSet.get(key)));
        }


        // 학교타입에 존재하지 않는 학교는 대안학교 or 특수학교이므로 변환되지않은 처음 학교이름을 추가
        // 특수학교나, 대안학교는 이름에 대해 규칙이 모호해서 예외로 처리
        if (schoolNameList.size() == 0) schoolNameList.add(schoolName);

        return schoolNameList;
    }


    /**
     * /resources/comments 텍스트 변형
     * <p>
     * comments 문자열 특수문자 및 개행, 공백 제거
     * 광주 숭일 고등학교 -> 광주숭일고등학교 로 줄여주어 '숭일고' 를 검색하는데 공백이 영향주지 않도록함
     */
    public String getInput(String path) {
        log.info("********************************************************");
        log.info(" 리소스/comments 비교대상 글 가져오기");
        log.info("");
        String input = new FileUtil().getInputString(path);
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        String res = input.replaceAll(match, " ")
                .replaceAll("\n", " ")
                .replaceAll("\\p{Z}", "");

        log.info(res.substring(0, 20)+"...( "+res.getBytes().length+" byte )");
        System.out.println(res);
        return res;
    }
}
