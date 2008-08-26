package com.wideplay.warp.servlet;

import com.google.inject.Provider;

import java.util.Map;

/**
 * Provides the current HTTP request parameters in a map (threadlocal).
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class RequestParametersProvider implements Provider<Map<String, String[]>> {

    @SuppressWarnings("unchecked")
    public Map<String, String[]> get() {
        return ContextManager.getRequest().getParameterMap();
    }
}
