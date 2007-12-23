/**
 * Copyright (C) 2007 Google Inc.
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
 */

package com.wideplay.warp.servlet;


import com.google.inject.Injector;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Register your own subclass of this as a servlet context listener.
 *
 * @author Kevin Bourrillion (kevinb@google.com), Dhanji R. Prasanna (dhanji@gmail.com)
 */
public abstract class WarpServletContextListener
        implements ServletContextListener {

    static final String INJECTOR_NAME = Injector.class.getName();

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        final Injector injector = getInjector();
        servletContext.setAttribute(INJECTOR_NAME, injector);
        ContextManager.setInjector(injector);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.removeAttribute(INJECTOR_NAME);
        
        //clear internal context
        ContextManager.setInjector(null);
    }

    /**
     * Override this method to create (or otherwise obtain a reference to) your
     * injector.
     *
     * @return Returns a new or located injector to plug into your warp-servlet application.
     */
    protected abstract Injector getInjector();
}
