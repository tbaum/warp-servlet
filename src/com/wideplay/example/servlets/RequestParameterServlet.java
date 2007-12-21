package com.wideplay.example.servlets;

import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.servlet.RequestParameters;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 1:44:20 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Singleton
public class RequestParameterServlet extends HttpServlet {
    //this servlet is also managed by guice, so you can inject, scope or intercept it as you please.
    @Inject
    @RequestParameters
    private Provider<Map<String, String[]>> params;

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //lets say hi!
        final PrintWriter out = httpServletResponse.getWriter();

        out.println("<html><head><title>Warp::Servlet powered servlet</title></head>");
        out.println("<body>");
        out.println("Here are the request parameters (add ?name=value to url to see more appear): " + print(params.get()));
        out.println("</body></html>");

        //write!
        out.flush();
    }

    private static String print(Map<String, String[]> stringMap) {
        StringBuilder builder = new StringBuilder();

        builder.append("{");
        for (String key : stringMap.keySet()) {
            builder.append(key);
            builder.append("=");
            builder.append(Arrays.toString(stringMap.get(key)));
            builder.append(", ");
        }

        builder.append("}");

        return builder.toString();
    }
}