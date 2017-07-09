package com.stream.jerye.queue;

import java.util.Iterator;
import java.util.List;

/**
 * Created by jerye on 7/8/2017.
 */

public class Utility {


    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }
}
