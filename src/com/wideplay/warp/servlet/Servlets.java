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
 * Subsequently repackaged by Wideplay Interactive Group under the Warp-servlet
 * module.
 */

package com.wideplay.warp.servlet;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.wideplay.warp.servlet.conversation.ConversationScoped;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * <p>
 *
 * Use this uninstantiable class to start configuring your warp-servlet Guice module. You may
 * also directly use the scopes provided here or bind them to scope annotations (they are not
 * bound by default).
 *
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.WebFilter Configuring the WebFilter
 */
public final class Servlets {
    private Servlets() {
    }

    /**
     *
     * <h3>Mapping EDSL</h3>
     * <p>
     * Part of the EDSL builder language for configuring servlets and filters with warp-servlet.
     * Think of this as an in-code replacement for web.xml tags. Filters and servlets are configured
     * here using simple java method calls. Here is a typical example of registering a filter when
     * creating/configuring your Guice injector:
     *
     * <pre>
     *   Guice.createInjector(..., Servlets.configure()
     *      .filters()
     *      .servlets()
     *          <b>.serve("*.html").with(MyServlet.class)</b>
     *
     *      .buildModule();
     * </pre>
     *
     * This registers a servlet (subclass of {@code HttpServlet}) named {@code MyServlet} to service
     * any web pages ending in .html. You can also use the path-style syntax to register servlets:
     *
     * <pre>
     *          <b>.serve("/my/*").with(MyServlet.class)</b>
     * </pre>
     *
     * You are free to register as many servlets and filters as you like this way:
     *
     * <pre>
     *   Guice.createInjector(..., Servlets.configure()
     *      .filters()
     *          .filter("/*").through(MyFilter.class)
     *          .filter("*.css").through(MyCssFilter.class)
     *          //etc..
     *
     *      .servlets()
     *          .serve("*.html").with(MyServlet.class)
     *          .serve("/my/*").with(MyServlet.class)
     *          //etc..
     *
     *      .buildModule();
     * </pre>
     *
     * You can also map servlets (or filters) to URIs using regular expressions:
     * <pre>
     *      .servlets()
     *          <b>.serveRegex("(.)*ajax(.)*").with(MyAjaxServlet.class)</b>
     * </pre>
     *
     * This will map any URI containing the text "ajax" in it to {@code MyAjaxServlet}. Such as:
     * <ul>
     *   <li>http://www.wideplay.com/ajax.html</li>
     *   <li>http://www.wideplay.com/content/ajax/index</li>
     *   <li>http://www.wideplay.com/it/is_totally_ajaxian</li>
     * </ul>
     *
     * </p>
     *
     * <h3>Initialization Parameters</h3>
     * Servlets (and filters) allow you to pass in init params using the {@code <init-param>}
     * tag in web.xml. You can similarly pass in parameters to Servlets and filters registered
     * in warp-servlet using a {@link java.util.Map} of parameter name/value pairs. For example,
     * to initialize {@code MyServlet} with two parameters (name="Dhanji", site="wideplay.com") you
     * could write:
     *
     * <pre>
     *  Map<String, String> params = new HashMap<String, String>();
     *  params.put("name", "Dhanji");
     *  params.put("site", "wideplay.com");
     *
     *  ...
     *      .servlets()
     *          .serve("/*").with(MyServlet.class, <b>params</b>)
     * </pre>
     *
     * </p>
     * <h3>Binding Guice Keys</h3>
     *
     * <p>
     * Warp-servlet lets you bind Guice-keys rather than classes directly. This lets you hide
     * implementation classes (servlets, filters) with package-local visbility and expose them using only
     * a Guice module and an annotation. Here is how this might work:
     *
     * <pre>
     *
     *      ...
     *      .filters()
     *          .filter("/*").through(<b>Key.get(Filter.class, Fave.class)</b>);
     * </pre>
     *
     * Where {@code Filter.class} refers to the Servlet API interface and {@code Fave.class} is a custom
     * binding annotation that refers to your implementation. Elsewhere (in one of your own modules) you
     * can bind this filter's implementation:
     *
     * <pre>
     * 
     *   bind(Filter.class)<b>.annotatedWith(Fave.class)</b>.to(MyHiddenFilterImpl.class);
     * </pre>
     *
     * See Guice documentation for more information on binding annotations and best practices.
     *
     * Also see <a href="http://www.wideplay.com">http://www.wideplay.com</a> for more details and examples.
     * </p>
     *
     * @return Returns the next step in the EDSL chain.
     * @see com.wideplay.warp.servlet.WebFilter Configuring the WebFilter
     */
    public static WebComponentBindingBuilder configure() {
        return new WebComponentBindingBuilder() {
            public FilterBindingBuilder filters() {
                return new FiltersModuleBuilder();
            }
        };
    }

    /**
     * HTTP servlet request scope.
     */
    public static final Scope REQUEST_SCOPE = new Scope() {
        public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
            final String name = key.toString();
            //noinspection InnerClassTooDeeplyNested
            return new Provider<T>() {
                public T get() {
                    HttpServletRequest request = ContextManager.getRequest();
                    synchronized (request) {
                        @SuppressWarnings("unchecked")
                        T t = (T) request.getAttribute(name);
                        if (t == null) {
                            t = creator.get();
                            request.setAttribute(name, t);
                        }
                        return t;
                    }
                }
            };
        }

        @Override
        public String toString() {
            return "Servlets.REQUEST_SCOPE";
        }
    };

    /**
     * HTTP session scope.
     */
    public static final Scope SESSION_SCOPE = new Scope() {
        public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
            final String name = key.toString();
            //noinspection InnerClassTooDeeplyNested
            return new Provider<T>() {
                public T get() {
                    HttpSession session = ContextManager.getRequest().getSession();
                    synchronized (session) {
                        @SuppressWarnings("unchecked")
                        T t = (T) session.getAttribute(name);
                        if (t == null) {
                            t = creator.get();
                            session.setAttribute(name, t);
                        }
                        return t;
                    }
                }
            };
        }

        @Override
        public String toString() {
            return "Servlets.SESSION_SCOPE";
        }
    };

    /**
     * "Flash" scope. A scope popular among flow-like frameworks for scoping objects <em>between</em> successive requests.
     */
    public static final Scope FLASH_SCOPE = new Scope() {
        public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
            final String flashMapKey = this.toString();

            //noinspection OverlyComplexAnonymousInnerClass,InnerClassTooDeeplyNested
            return new Provider<T>() {

                @SuppressWarnings("unchecked")
                public T get() {
                    final HttpServletRequest request = ContextManager.getRequest();
                    final HttpSession session = request.getSession();

                    //attempt to locate the flashMap from the session (or create one if necessary)
                    Map<Key<T>, Object> requestFlashCache;

                    //create a request-flashmap too
                    synchronized (request) {
                        requestFlashCache = (Map<Key<T>, Object>) request.getAttribute(flashMapKey); //ok to use the same key
                        if (null == requestFlashCache) {
                            requestFlashCache = new HashMap<Key<T>, Object>();
                            request.setAttribute(flashMapKey, requestFlashCache);
                        }
                    }


                    //now attempt to fetch the scoped object from request first, then session, caching in either place
                    synchronized (requestFlashCache) {
                        T t = (T) requestFlashCache.get(key);

                        //look in session cache
                        if (null == t) {
                            //we must cache flash-scoped objects twice, once in session and again in request
                            Map<Key<T>, Object> sessionFlashCache;
                            synchronized (session) {
                                sessionFlashCache = (Map<Key<T>, Object>) session.getAttribute(flashMapKey);
                                if (null == sessionFlashCache) {
                                    sessionFlashCache = new HashMap<Key<T>, Object>();
                                    session.setAttribute(flashMapKey, sessionFlashCache);
                                }
                            }

                            synchronized (sessionFlashCache) {
                                t = (T) sessionFlashCache.get(key);

                                //if it's missing even from the session cache, then create one
                                if (null == t) {
                                    t = creator.get();
                                    sessionFlashCache.put(key, t);
                                }
                                else
                                    sessionFlashCache.remove(key);  //destroy on every second fetch from session
                            }

                            requestFlashCache.put(key, t);
                        }

                        return t;
                    }
                }
            };
        }

        @Override
        public String toString() {
            return "Servlets.FLASH_SCOPE";
        }
    };


    /**
     * "Conversation" scope. Like Seam or Spring Webflow's. Better even =) See www.wideplay.com for details.
     */
    public static final Scope CONVERSATION_SCOPE = new Scope() {
        @SuppressWarnings({"InnerClassTooDeeplyNested"})
        public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
            
            //conversation scoping provider...
            return new Provider<T>() {
                public T get() {

                    //locate the conv manager, and obtain a contextual instance, creating one if absent
                    return ContextManager.getInjector()
                            .getInstance(UrlRewrittenConversation.class)
                            .provide(key, creator, ContextManager.getRequest());
                }
            };
        }

        @Override
        public String toString() {
            return "Servlets.CONVERSATION_SCOPE";
        }
    };

    public static void bindScopes(Binder binder) {
        binder.bindScope(RequestScoped.class, REQUEST_SCOPE);
        binder.bindScope(SessionScoped.class, SESSION_SCOPE);
        binder.bindScope(FlashScoped.class, FLASH_SCOPE);
        binder.bindScope(ConversationScoped.class, CONVERSATION_SCOPE);
    }
}
