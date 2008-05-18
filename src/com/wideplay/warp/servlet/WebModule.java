package com.wideplay.warp.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 12:06:43 AM
 *
 * <p>
 *
 * A general module where web artifacts are bound. Mostly stuff that's
 * not specific to filters or servlets.
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
