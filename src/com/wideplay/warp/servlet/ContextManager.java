/**
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Repackaged by Wideplay Interactive Group under the Warp-servlet module.
 */

package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 2:35:22 PM
 *
 * Manages the current "context" for warp-servlet internally as regards sessions,
 * requests, injectors and conversations. Everything is thread local except the
 * injector which is a static singleton =(
 *
 *
 * @author crazybob@google.com (Bob Lee), Dhanji R. Prasanna (dhanji gmail com)
 */
@ThreadSafe
class ContextManager {

    //after publication by calling setInjector(), is effectively immutable
    private static volatile Injector globalInjector = null;

    private static final ThreadLocal<Context> localContext = new ThreadLocal<Context>();
    private static final AtomicReference<ServletContext> servletContext =
            new AtomicReference<ServletContext>();


    static void setInjector(Injector injector) {
        if (null != injector && null != globalInjector)
            throw new IllegalStateException("Tried to set an Injector when one has already been set. " +
                    "Did you accidentally register two WebFilters?");

        globalInjector = injector;
    }

    static Injector getInjector() {
        return globalInjector;
    }

    static void set(HttpServletRequest request, HttpServletResponse response) {
        localContext.set(new Context(request, response));
    }

    //absolutely must be called at the end of a request
    static void unset() {

        localContext.remove();
    }

    static HttpServletRequest getRequest() {
        return getContext().getRequest();
    }

    static HttpServletResponse getResponse() {
        return getContext().getResponse();
    }

    private static Context getContext() {
        Context context = localContext.get();
        if (context == null) {
            throw new OutOfScopeException("Cannot access scoped object. Either we"
                    + " are not currently inside an HTTP Servlet request, or you may"
                    + " have forgotten to apply " + WebFilter.class.getName()
                    + " as a servlet filter for this request.");
        }
        return context;
    }

    static ServletContext getServletContext() {
        return servletContext.get();
    }

    public static void setServletContext(ServletContext servletContext) {
        ContextManager.servletContext.set(servletContext);
    }

    static class Context {

        private final HttpServletRequest request;
        private final HttpServletResponse response;

        Context(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;


        }

        HttpServletRequest getRequest() {
            return request;
        }

        HttpServletResponse getResponse() {
            return response;
        }
    }
}
