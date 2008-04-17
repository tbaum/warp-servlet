package com.wideplay.warp.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Module;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 1:03:53 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class EdslTest {
    @Test
    public final void configureServlets() {

        //the various possible config calls--

        Module webModule = Servlets.configure()
                .filters()
                    .filter("/*").through(DummyFilterImpl.class)

                    .filter("*.html").through(DummyFilterImpl.class)

                    .filter("*.html").through(DummyFilterImpl.class, new HashMap<String, String>())

                    .filterRegex("/person/[0-9]*").through(DummyFilterImpl.class)

                    .filterRegex("/person/[0-9]*").through(Key.get(DummyFilterImpl.class))

                    .filterRegex("/person/[0-9]*").through(Key.get(DummyFilterImpl.class), new HashMap<String, String>())

                    .filter("/*").through(Key.get(DummyFilterImpl.class))

                .servlets()

                    .serve("/*").with(DummyServlet.class)
                    .serve("/*").with(DummyServlet.class, new HashMap<String, String>())

                    .serve("*.html").with(Key.get(DummyServlet.class))
                    .serve("*.html").with(Key.get(DummyServlet.class), new HashMap<String, String>())

                    .serveRegex("/person/[0-9]*").with(DummyServlet.class)
                    .serveRegex("/person/[0-9]*").with(DummyServlet.class, new HashMap<String, String>())

                    .serveRegex("/person/[0-9]*").with(Key.get(DummyServlet.class))
                    .serveRegex("/person/[0-9]*").with(Key.get(DummyServlet.class), new HashMap<String, String>())

                .buildModule();

        Guice.createInjector(webModule, new AbstractModule() {
            protected void configure() {
                Servlets.bindScopes(binder());
            }
        });
    }
}
