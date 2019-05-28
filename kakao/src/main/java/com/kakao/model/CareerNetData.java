package com.kakao.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CareerNetData {


    private Content dataSearch;


    public static class Content {
        private List<School> content;

        public List<School> getContent() {
            return content;
        }


        // 중복 제거된 학교 이름만 추리기
        public List<String> getSchoolNameList () {

            HashSet<String> schoolNameSet = new HashSet<>();

            for(School info : content) {
                schoolNameSet.add(info.getSchoolName());
            }

            return new ArrayList<>(schoolNameSet);
        }

        public void setContent(List<School> content) {
            this.content = content;
        }
    }




    public Content getDataSearch() {
        return dataSearch;
    }

    public void setDataSearch(Content dataSearch) {
        this.dataSearch = dataSearch;
    }

}
