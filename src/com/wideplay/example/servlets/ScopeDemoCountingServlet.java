package com.wideplay.example.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import net.jcip.annotations.NotThreadSafe;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 21, 2007
 * Time: 9:32:27 AM
 *
 * Simple example of a managed servlet that can benefit
 * from scope, uses injected object Counter to show different scopes.
 *
 * Keep refreshing this page to see how the various counts increase (or dont).
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Singleton
@NotThreadSafe  //just a demo!!
public class ScopeDemoCountingServlet extends HttpServlet {
    @Inject
    Counter badCounter;    //scope-widening injection of a no-scope into "this" (singleton) *BAD* practice

    @Inject
    Provider<Counter> goodCounter;    //proper injection of a no-scope into "this" (singleton) *GOOD* practice

    @Inject
    @Named(REQUEST)
    Provider<Counter> requestCounter;  //a request scoped counter, using a provider (proper way to avoid scope-widening)

    @Inject
    @Named(FLASH)
    Provider<Counter> flashCounter;  //a flash-scoped counter, using a provider (proper way to avoid scope-widening)

    @Inject
    @Named(SESSION)
    Provider<Counter> sessionCounter;  //a session scoped counter, using a provider (proper way to avoid scope-widening)


    static final String REQUEST = "request";
    static final String FLASH = "flash";
    static final String SESSION = "session";


    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //first increment all our counters
        badCounter.increment();
        goodCounter.get().increment();
        requestCounter.get().increment();
        flashCounter.get().increment();
        sessionCounter.get().increment();


        //lets say hi!
        final PrintWriter out = httpServletResponse.getWriter();

        out.println("<html><head><title>Warp::Servlet powered servlet</title></head>");
        out.println("<body>");
        out.println(String.format("ScopeDemoCountingServlet no-scope (scope-widened) counter: %d (should never increment, but does!)<br/>", badCounter.getCount()));
        out.println(String.format("ScopeDemoCountingServlet no-scope (properly) counter: %d (should never increment)<br/>", goodCounter.get().getCount()));
        out.println(String.format("ScopeDemoCountingServlet request scope counter: %d (should reset and increment to 1 each time)<br/>", requestCounter.get().getCount()));
        out.println(String.format("ScopeDemoCountingServlet flash-scope counter: %d (should increment to 2 and reset)<br/>", flashCounter.get().getCount()));
        out.println(String.format("ScopeDemoCountingServlet session-scope (properly) counter: %d " +
                "(should keep incrementing, and reset when you flush cookies in browser)<br/>", sessionCounter.get().getCount()));
        out.println("</body></html>");

        //write!
        out.flush();
    }


}
