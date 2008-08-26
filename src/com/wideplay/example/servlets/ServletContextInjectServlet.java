package com.wideplay.example.servlets;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Example of injecting the ServletContext with a Provider.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Singleton
public class ServletContextInjectServlet extends HttpServlet {
    //this servlet is also managed by guice, so you can inject, scope or intercept it as you please.


    @Inject
    Provider<ServletContext> context;

    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("ServletContext: " + context.get());
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //lets say hi!
        final PrintWriter out = httpServletResponse.getWriter();

        out.println("<html><head><title>Warp::Servlet powered servlet</title></head>");
        out.println("<body>");

        out.print("ServletContext: " + context.get());
        out.println("</body></html>");

        //write!
        out.flush();
    }
}