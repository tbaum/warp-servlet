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

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 1:42:25 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public final class Servlets {
    private Servlets() {
    }

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

}
