package com.wideplay.warp.servlet;

import com.google.inject.Provider;

import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 12:03:54 AM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class ResponseProvider implements Provider<HttpServletResponse> {
    public HttpServletResponse get() {
        return ContextManager.getResponse();
    }
}
