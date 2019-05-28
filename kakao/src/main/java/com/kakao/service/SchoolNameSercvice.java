package com.kakao.service;

import java.util.List;

public interface SchoolNameSercvice {

    List<String> getSchoolCount(List<String> schoolNameList, String text);

    List<String> searchSchool();

    List<String> getSchoolNameSet(String pSchoolName);

    String getInput(String path);
}
