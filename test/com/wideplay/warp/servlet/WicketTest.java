package com.wideplay.warp.servlet;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.AfterClass;
import org.apache.wicket.protocol.http.WebApplication;
import com.google.inject.Guice;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 23, 2007
 * Time: 1:58:45 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class WicketTest {

    @BeforeMethod
    void reset() {
        ContextManager.setInjector(null);
    }

    @AfterClass
    void clean() {
        ContextManager.unset();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public final void integrateNullApp() {
        Integrate.with(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public final void integrateAppNoContext() {
        Integrate.with(new WebApplication() {
            public Class getHomePage() {
                return Void.class;
            }
        });
    }


    @Test
    public final void integrateApp() {
        ContextManager.setInjector(Guice.createInjector());

        Integrate.with(new WebApplication() {
            public Class getHomePage() {
                return Void.class;
            }
        });
    }
}
