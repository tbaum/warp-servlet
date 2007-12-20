package com.wideplay.example.servlets;

import com.wideplay.warp.servlet.WarpServletContextListener;
import com.wideplay.warp.servlet.Servlets;
import com.google.inject.Injector;
import com.google.inject.Guice;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 1:38:13 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class MyGuiceCreator extends WarpServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(Servlets.configure()
                .filters()
                    .filter("/*").through(RequestPrintingFilter.class)

                .servlets()
                    .serve("/index.html").with(MyIndexServlet.class)

                .buildModule()
        );
    }
}
