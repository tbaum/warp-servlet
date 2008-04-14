package com.wideplay.warp.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        ContextManager.setInjector(null);
    }

    @Test
    public final void setAndGetInjector() {
        //create mocks
        final Injector injector = Guice.createInjector();
        ContextManager.setInjector(injector);

        assert injector == ContextManager.getInjector() : "incorrect injector returned";

        //TODO test in multi-threaded environment
        ContextManager.setInjector(null);

        assert null == ContextManager.getInjector() : "injector not cleared";
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public final void duplicateSetInjector() {
        ContextManager.setInjector(Guice.createInjector());
        ContextManager.setInjector(Guice.createInjector());
    }
}
