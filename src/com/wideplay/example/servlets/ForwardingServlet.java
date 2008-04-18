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
 * This servlet simply forwards its request to a jsp page: sampleInclude.jsp
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Singleton
public class ForwardingServlet extends HttpServlet {



    protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //lets say hi!
        final PrintWriter out = httpServletResponse.getWriter();

        //this content will NEVER be seen
        out.println("<html><head><title>Warp::Servlet powered servlet</title></head>");
        out.println("<body>");
        out.println("This content should never be seen");
        out.println("</body></html>");

        request .getRequestDispatcher("/sampleInclude.jsp")
                .forward(request, httpServletResponse);
    }
}