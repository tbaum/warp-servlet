package com.wideplay.warp.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 *
 * A general module where some of the other web artifacts are bound. Mostly stuff that's
 * not specific to filters or servlets, but that warp-servlet needs to provide.
 *
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class WebModule extends AbstractModule {

    protected void configure() {

        //bind request and response providers
        bind(HttpServletRequest.class).toProvider(RequestProvider.class);
        bind(HttpServletResponse.class).toProvider(ResponseProvider.class);
        bind(ServletContext.class).toProvider(ServletContextProvider.class);

        bind(new TypeLiteral<Map<String, String[]>>(){ }).annotatedWith(RequestParameters.class)
                .toProvider(RequestParametersProvider.class);
    }
}
