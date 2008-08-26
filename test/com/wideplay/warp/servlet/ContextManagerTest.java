package com.wideplay.warp.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import static org.easymock.EasyMock.createMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 10:42:46 AM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ContextManagerTest {
    @BeforeMethod
    public final void reset() {
        ContextManager.cleanup();
    }

    @Test
    public final void setAndGetInjector() {
        //create mocks
        final Injector injector = Guice.createInjector();
        ContextManager.setInjector(injector);

        assert injector == ContextManager.getInjector() : "incorrect injector returned";

        //TODO test in multi-threaded environment
        ContextManager.cleanup();

        assert null == ContextManager.getInjector() : "injector not cleared";
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public final void duplicateSetInjector() {
        final WarpServletContextListener listener = new WarpServletContextListener() {

            @Override
            protected Injector getInjector() {
                return createMock(Injector.class);
            }
        };


        listener.contextInitialized(new ServletContextEvent(createMock(ServletContext.class)) { });
        listener.contextInitialized(new ServletContextEvent(createMock(ServletContext.class)) { });
    }
}
