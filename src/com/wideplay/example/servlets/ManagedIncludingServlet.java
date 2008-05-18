package com.wideplay.example.servlets;

import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 1:44:20 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Singleton
public class ManagedIncludingServlet extends HttpServlet {
    //this servlet is also managed by guice, so you can inject, scope or intercept it as you please.



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //lets say hi!
        final PrintWriter out = response.getWriter();

        out.println("<html><head><title>Warp::Servlet powered servlet</title></head>");
        out.println("<body>");
        out.println("Hello from a servlet that includes another managed servlet (HelloWorldServlet):<p><b>");
        request.getRequestDispatcher("/hello/hi")
                .include(request, response);
        out.println("</b></p></body></html>");

        //write!
        out.flush();
    }
}