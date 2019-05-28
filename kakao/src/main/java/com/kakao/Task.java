/*********************************************************************************************
 * PROJECT NAME   : 학교 이름과 개수 찾기
 * FILE NAME      : Task.java
 * DESCRIPTION    : ~/resources/comments 에 있는 글 중에 학교이름을 찾아서 result.txt에 저장하기
 *********************************************************************************************/

package com.kakao;

import com.kakao.service.SchoolNameSercvice;
import com.kakao.service.SchoolNameServiceImpl;
import com.kakao.util.FileUtil;
import java.util.*;


public class Task {

    public static void main(String[] args) {
        Task task = new Task();
        task.proc();
    }

    private void proc() {

        SchoolNameSercvice schoolNameSercvice = new SchoolNameServiceImpl();


        // 대한민국 학교 이름 정보 조회
        List<String> schoolNameList = schoolNameSercvice.searchSchool();



        // 학교이름으로 개수확인
        String comments = schoolNameSercvice.getInput("/comments");
        List<String> resultList = schoolNameSercvice.getSchoolCount(schoolNameList, comments);



        // 결과 .txt로 저장
        new FileUtil().putOutputString("result.txt", resultList);




        // [디버깅용] 학교이름들 다지우고 나머지 commments 만들기
        for(String schoolName : schoolNameList) {
            comments = comments.replaceAll(schoolName, "");
            List<String> nameSet = schoolNameSercvice.getSchoolNameSet(schoolName);


            for(String s: nameSet) {
                comments = comments.replaceAll(s, "");
            }
        }

        new FileUtil().putString("comments_after.txt", comments);
    }

}
