package com.wideplay.warp.servlet;

import com.google.inject.Injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 7:54:36 PM
 *
 * A hack to expose a package-local class (for testing purposes ONLY) to tests
 * in other packages.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ContextManagerTestDelegator {
    public static Injector getInjector() {
        return ContextManager.getInjector();
    }

    public static void setInjector(Injector injector) {
        ContextManager.setInjector(injector);
    }

    public static void set(HttpServletRequest request, HttpServletResponse response) {
        ContextManager.set(request, response);
    }

    public static void unset() {
        ContextManager.unset();
    }
}
