package com.lichuan.test01.utility;

import java.util.Date;
import java.util.Random;

/**
 * Created by guoym on 15-6-25.
 */
public class RandomUtil {
    private final static char AllChar[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'X', 'Y', 'Z'};

    public static String RandomStr(int len) {
        Random r = new Random(new Date().getTime());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int ret = r.nextInt(AllChar.length);
            if (ret < 0) {
                ret = 0;
            }

            if (ret >= AllChar.length) {
                ret = AllChar.length - 1;
            }
            sb.append(AllChar[ret]);
        }
        return sb.toString();
    }
}
