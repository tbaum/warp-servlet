package com.wideplay.example.servlets;

import net.jcip.annotations.NotThreadSafe;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 21, 2007
 * Time: 9:33:19 AM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@NotThreadSafe
public class Counter {
    private int count;

    public int getCount() {
        return count;
    }

    public void increment() { count++; }

}
