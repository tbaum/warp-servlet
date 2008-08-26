package com.wideplay.warp.servlet;

import com.google.inject.Provider;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides the current HTTP request (threadlocal).
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class RequestProvider implements Provider<HttpServletRequest> {

    public HttpServletRequest get() {
        return ContextManager.getRequest();
    }
}
