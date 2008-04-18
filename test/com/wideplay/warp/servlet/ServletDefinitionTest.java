package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Key;
import static com.wideplay.warp.servlet.uri.UriPatternType.SERVLET;
import static com.wideplay.warp.servlet.uri.UriPatternType.get;
import static org.easymock.EasyMock.*;
import org.testng.annotations.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dhanji R. Prasanna (dhanji@gmail com)
 */
public class ServletDefinitionTest {
    @Test
    public final void filterInitAndConfig() throws ServletException {

        Injector injector = createMock(Injector.class);

        final HttpServlet mockServlet = new HttpServlet() {

        };
        expect(injector.getInstance(Key.get(HttpServlet.class)))
                .andReturn(mockServlet)
                .anyTimes();


        replay(injector);

        //some init params
        final Map<String, String> initParams = new HashMap<String, String>() {{
            put("ahsd", "asdas24dok");
            put("ahssd", "asdasd124ok");
            put("ahfsasd", "asda124sdok");
            put("ahsasgd", "a124sdasdok");
            put("ahsd124124", "as124124124dasdok");
        }};

        final ServletDefinition servletDefinition = new ServletDefinition("/*", Key.get(HttpServlet.class), get(SERVLET), initParams);

        ServletContext servletContext = createMock(ServletContext.class);
        final String contextName = "thing__!@@44__SRV" + getClass();
        expect(servletContext.getServletContextName())
                .andReturn(contextName);

        replay(servletContext);

        servletDefinition.init(servletContext, injector);

        assert null != mockServlet.getServletContext();
        assert contextName.equals(mockServlet.getServletContext().getServletContextName());
        assert Key.get(HttpServlet.class).toString().equals(mockServlet.getServletName());

        final ServletConfig servletConfig = mockServlet.getServletConfig();
        final Enumeration names = servletConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();

            assert initParams.containsKey(name);
            assert initParams.get(name).equals(servletConfig.getInitParameter(name));
        }
    }
}
