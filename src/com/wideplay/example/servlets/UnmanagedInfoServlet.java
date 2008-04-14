package com.wideplay.example.servlets;

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
 * This servlet shows some simple info about the request.
 *
 * This servlet is ***********NOT********** managed by warp-servlet and only exists for functional testing purposes.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class UnmanagedInfoServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //lets say hi!
        final PrintWriter out = httpServletResponse.getWriter();

        out.println("<html><head><title>Warp::Servlet powered servlet</title></head>");
        out.println("<body>");
        out.println(String.format(""
                + "Server Name: %s <br/>"
                + "Servlet Name: %s <br/>"
                + "Servlet Info: %s <br/>"
                + "Request Path Info: %s <br/>"
                + "Request Path (translated): %s <br/>"
                + "Servlet Path: %s <br/>"
                + "Request URI: %s <br/>"
                + "Request URL: %s <br/>",

                request.getServerName(),
                getServletName(),
                getServletInfo(),
                request.getPathInfo(),
                request.getPathTranslated(),
                request.getServletPath(),
                request.getRequestURI(),
                request.getRequestURL()
        ));
        out.println("</body></html>");

        //write!
        out.flush();
    }
}