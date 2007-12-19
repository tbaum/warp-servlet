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

        public String toString() {
            return "Servlets.SESSION_SCOPE";
        }
    };
}
