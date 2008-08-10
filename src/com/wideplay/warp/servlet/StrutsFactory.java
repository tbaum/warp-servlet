package com.wideplay.warp.servlet;

import com.opensymphony.xwork2.ObjectFactory;
import com.google.inject.Injector;

import java.util.Map;

/**
 * <p>
 *
 *  Register this class in your struts.xml configuration file instead of using the GuiceObjectFactory
 * plugin. This provides cleaner integration with Guice via the warp-servlet bridge. It also allows you
 * to start up your injector in a listener and configure it as needed rather than in StrutsFilter.
 * Example usage (in struts.xml):
 *
 * </p>
 * <br/>
 * <code>
 *    &lt;constant name="struts.objectFactory" value="<b>com.wideplay.warp.servlet.StrutsFactory</b>" /&gt;
 * </code>
 *
 * <p>
 *  Now, all objects created by struts2 are managed via Guice (the injector from your WarpServletContextListener).
 *  This includes actions, xwork interceptors and generally any "user" object required by Struts2.
 * </p>
 *
 * <p>
 * Another advantage of this integration plugin is that you can use warp-persist's SPR filters via
 * warp-servlet and not bother with Struts interceptors. This is particularly useful if you have other
 * servlets/filters that require persistence support (such as a SOAP service).
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji@gmail.com)
*/
public final class StrutsFactory extends ObjectFactory {

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
