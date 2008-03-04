package com.wideplay.example.servlets;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 1:44:20 PM
 *
 *
 * This servlet is a thin wrapper that wraps and delegates to a scoped servlet (see docs on wideplay.com for info).
 * So that the scoped servlet can take on request or session scope..
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 *
 */
@Singleton    //MUST be a scope other than request, session or flash!
public class ScopedServletWrappingServlet extends HttpServlet {
    @Inject
    Provider<ScopedServlet> scopedServletProvider;

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //simply delegate to our scoped servlet!
        scopedServletProvider.get().doGet(httpServletRequest, httpServletResponse);
    }
}