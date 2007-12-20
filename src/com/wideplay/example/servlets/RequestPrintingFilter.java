package com.wideplay.example.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 1:39:23 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Singleton //bind this filter in singleton scope!
public class RequestPrintingFilter implements Filter {
    //this filter is managed and benefits from ctor-injection
    private final Logger logger;

    @Inject
    public RequestPrintingFilter(Logger logger) {
        this.logger = logger; //guice automagically binds java.util.Loggers for all classes
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Filter initialized!!!");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //lets print the request out
        System.out.println(servletRequest);
        logger.info(servletRequest.toString());

        //continue down filter chain (all filters typically obey this rule, unless they want to suppress the output)
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
        System.out.println("Filter destroyed!!!");
    }
}
