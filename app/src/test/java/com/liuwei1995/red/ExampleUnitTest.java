package com.liuwei1995.red;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
        String s = "No.5236148 解锁码";
        String account = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            try {
                int parseInt = Integer.parseInt("" + c);
                account += parseInt;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        System.out.println(account);

    }
}