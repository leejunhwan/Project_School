/*****************************************************************************
 * PROJECT NAME   : 생태 관광 서비스 ( Echo Tourism Program Service)
 * SUBSYSTEM NAME : ETP-Service
 * FILE NAME      : KMPAlgorithm.java
 * DESCRIPTION    : 장문의 글에서 특정 키워드를 효율적으로 찾기 위한 알고리즘
 *****************************************************************************/

package com.kakao.util;

import java.util.ArrayList;
import java.util.List;

public class KMPAlgorithm {

    public static int patternCount(String string, String pattern) {
        List<Integer> searchIndex = new ArrayList<>();

        char[] s = string.toCharArray();
        char[] p = pattern.toCharArray();
        int[] pi = funcPi(pattern);
        int n = string.length();
        int m = pattern.length();
        int j = 0;

        for (int i = 0; i < n; i++) {
            while (j > 0 && s[i] != p[j]) {
                j = pi[j - 1];
            }
            if (s[i] == p[j]) {
                if (j == m - 1) {
                    searchIndex.add(i - m + 1);

                    // 혹시나 겹친 문자가 있으면 no count를 위해 1개만 처리하도록 j = 0 으로 초기화
                    j = 0;
                } else {
                    j++;
                }
            }
        }
        return searchIndex.size();
    }


    // 패턴 PI 함수 만들기
    private static int[] funcPi(String pattern) {
        int m = pattern.length();
        int j = 0;
        char[] p = pattern.toCharArray();
        int[] pi = new int[m];

        for (int i = 1; i < m; i++) {
            while (j > 0 && p[i] != p[j]) {
                j = pi[j - 1];
            }
            if (p[i] == p[j]) {
                pi[i] = ++j;
            }
        }
        return pi;
    }
}
