package com.wideplay.warp.servlet;

import com.google.inject.Provider;

import javax.servlet.ServletContext;

/**
 * @author Dhanji R. Prasanna (dhanji@gmail com)
 */
class ServletContextProvider implements Provider<ServletContext> {
    public ServletContext get() {
        return ContextManager.getServletContext();
    }
}
