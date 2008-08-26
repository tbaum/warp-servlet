package com.wideplay.warp.servlet;

import com.google.inject.Provider;

import javax.servlet.http.HttpServletResponse;

/**
 * Provides the current HTTP response (threadlocal).
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class ResponseProvider implements Provider<HttpServletResponse> {
    public HttpServletResponse get() {
        return ContextManager.getResponse();
    }
}
