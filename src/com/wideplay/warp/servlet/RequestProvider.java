package com.wideplay.warp.servlet;

import com.google.inject.Provider;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 12:03:00 AM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class RequestProvider implements Provider<HttpServletRequest> {

    public HttpServletRequest get() {
        return ContextManager.getRequest();
    }
}
