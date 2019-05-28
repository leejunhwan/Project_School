package com.kakao;

import com.kakao.service.SchoolNameSercvice;
import com.kakao.service.SchoolNameServiceImpl;
import org.junit.Test;

import java.util.List;

public class MethodTest {


    @Test
    public void test () {
        SchoolNameSercvice schoolNameSercvice = new SchoolNameServiceImpl();
        List<String> schoolNameList = schoolNameSercvice.getSchoolNameSet("서울연희미용고등학교");

        for(String s : schoolNameList) System.out.println(s);
    }
}
