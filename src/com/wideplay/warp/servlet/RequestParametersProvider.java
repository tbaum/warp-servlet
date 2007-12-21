package com.wideplay.warp.servlet;

import com.google.inject.Provider;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 21, 2007
 * Time: 2:12:13 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class RequestParametersProvider implements Provider<Map<String, String[]>> {

    @SuppressWarnings("unchecked")
    public Map<String, String[]> get() {
        return ContextManager.getRequest().getParameterMap();
    }
}
