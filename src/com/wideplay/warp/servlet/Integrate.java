package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.util.Objects;
import com.opensymphony.xwork2.ObjectFactory;
import org.apache.wicket.Application;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.WicketFilter;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 23, 2007
 * Time: 1:46:56 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public final class Integrate {
    private Integrate() {
    }

    public static GuiceComponentInjector with(Application wicketApplication) {
        Objects.nonNull(wicketApplication, "Must provide a valid " + Application.class.getName()
                + " to integrate with warp-servlet (was null)");
        final Injector injector = ContextManager.getInjector();

        if (null == injector)
            throw new IllegalStateException(
                    "Warp-servlet was not active, no Guice Injector context could be found. " +
                    "Did you forget to register " + WebFilter.class.getName() + " in web.xml? Or did you register " +
                    WicketFilter.class.getName() + " *above* it in web.xml, instead of inside Warp-servlet? Ideally, " +
                    "your filters and servlets should should be in Warp-servlet and not appear web.xml at all.");

        return new GuiceComponentInjector(wicketApplication, injector);
    }

    public static class GuiceObjectFactory extends ObjectFactory {

        @Override
        public boolean isNoArgConstructorRequired() {
            return false;
        }

        @Override @SuppressWarnings("unchecked")
        public Object buildBean(Class aClass, Map map) throws Exception {
            Injector injector = ContextManager.getInjector();

            if (null == injector) {
                throw new IllegalStateException("Warp-servlet was not active, no Guice Injector context could be found. " +
                    "Did you forget to register " + WebFilter.class.getName() + " in web.xml? Or did you register " +
                    "StrutsFilter *above* it in web.xml by mistake? Remember " + WebFilter.class.getSimpleName() +
                    " must appear *before* any Filters that use Guice. ");
            }

            //noinspection unchecked
            return injector.getInstance(aClass);
        }
    }
}
