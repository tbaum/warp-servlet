package com.wideplay.warp.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provider;
import static com.wideplay.warp.servlet.UrlRewrittenConversation.CONVERSATION_ID;
import com.wideplay.warp.servlet.conversation.ConversationScoped;
import static org.easymock.EasyMock.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * TODO: this test is pretty fragile because it relies on statics and is not concurrent
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ConversationTest {
    private static final String CONV_KEY = "asdkasodk";
    private static final String REWRITTEN_URLS = "rewrittenURLs";
    private static final String CONV_KEY2 = "!aosdk35533";

    @DataProvider(name = REWRITTEN_URLS)
    public Object[][] get() {
        return new Object[][] {
            { String.format("?askaoksdoaksd=;%s=", CONVERSATION_ID),  "pdasdasd" },
            { String.format("?ask=asdasd&asd=asdaoksdo&aksd;%s=", CONVERSATION_ID),  "pdasdasd" },
            { String.format("?ask=asdasd&asd=a;/s/daoksdo&aksd;%s=", CONVERSATION_ID),  "pdasdasd" },
            { String.format("?ask=asdasd&asd=asapdlasdla[psldap[lsdpa[lsda[psdlaksd;%s=", CONVERSATION_ID),  "pdasdasd" },
            { String.format(";asodkaoksd;%s=", CONVERSATION_ID),  "seacreature" },
            { String.format("?ask=asdasd&asd=asapdlasdla[psldap[lsdpa[lsda[psdlaksd;%s=", CONVERSATION_ID),
                    "pdasdasd-pdasdasd-pdasdasd-pdasdasd" },
        };
    }


    @Test(dataProvider = REWRITTEN_URLS)
    public final void stripConversationKeyFromUrl(String prefix, final String key) {
        final HttpServletRequest request = createMock(HttpServletRequest.class);
        final String queryString = prefix + key;

        expect(request.getQueryString()).andReturn(queryString);
        request.setAttribute(CONVERSATION_ID, key);

        replay(request);


        final int offset = UrlRewrittenConversation.conversationKeyOffset(request);

        //make sure offset points to the right spot
        final String extract = queryString.substring(offset);
        assert offset == prefix.length() - (CONVERSATION_ID.length() + 2)
                : "offset into query string was wrong: " + extract;


        assert extract.startsWith(";" + CONVERSATION_ID + "=");
        assert extract.endsWith(key);

        verify(request);
    }


    private static List<TestConvObject> references;

    @Test
    public final void conversationScopingOfInstancesInOneRequest() throws IOException, ServletException {
        references = new LinkedList<TestConvObject>();

        //create mocks
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);


        //begin mock script ***
        expect(request.getQueryString())
                .andReturn("?asd=asd;" + CONVERSATION_ID + "=" + CONV_KEY)
                .times(1);

        expect(request.getServletPath())
                .andReturn("/");
        expect(request.getMethod())
                .andReturn("GET");

        request.setAttribute(CONVERSATION_ID, CONV_KEY);
        expectLastCall().once();

        expect(request.getAttribute(CONVERSATION_ID))
                .andReturn(CONV_KEY)
                .anyTimes();


        //run mock script ***
        replay(request, response);
        ContextManager.setInjector(Guice.createInjector(new AbstractModule() {
            protected void configure() {
                install(Servlets.configure().filters().servlets()

                            .serve("/*").with(ConvTestServlet.class)

                        .buildModule());
                bind(TestConvObject.class).in(Servlets.CONVERSATION_SCOPE);
            }
        }));


        //initiate request via filter
        new WebFilter().doFilter(request, response, createMock(FilterChain.class));


     
        //ensure cleared static state =(
        Exception expected = null;
        try {
            ContextManager.getRequest();
            ContextManager.getResponse();
            ContextManager.getInjector();
        } catch (OutOfScopeException e) {
            expected = e;
        }

        assert expected instanceof OutOfScopeException;


        //lets make sure the conversation scoped instances are all the same
        assert references.size() == 5;

        TestConvObject ref = references.get(0);
        for (TestConvObject reference : references)
            assert ref.equals(reference);       //we dont do sameness tests in case you wana try custom stores later



        //assert expectations
        verify(request, response);
    }


    //Warning: this test is a big fragile, as it asserts the number of times a request is accessed
    @Test
    public final void conversationScopingOfInstancesAcrossRequests() throws IOException, ServletException {
        references = new LinkedList<TestConvObject>();

        final int numberOfRequests = 8;

        //create mocks
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);


        //begin mock script ***
        expect(request.getQueryString())
                .andReturn("?asd=asd;" + CONVERSATION_ID + "=" + CONV_KEY)
                .times(numberOfRequests);

        expect(request.getServletPath())
                .andReturn("/")
                .times(numberOfRequests);
        expect(request.getMethod())
                .andReturn("GET")
                .times(numberOfRequests);

        request.setAttribute(CONVERSATION_ID, CONV_KEY);
        expectLastCall().times(numberOfRequests);

        final int numberOfInjections = 5;
        expect(request.getAttribute(CONVERSATION_ID))
                .andReturn(CONV_KEY)
                .times(numberOfRequests * numberOfInjections);       //number of requests  * once per conv inject


        //run mock script ***
        replay(request, response);
        ContextManager.setInjector(Guice.createInjector(new AbstractModule() {
            protected void configure() {
                install(Servlets.configure().filters().servlets()

                            .serve("/*").with(ConvTestServlet.class)

                        .buildModule());
                bind(TestConvObject.class).in(Servlets.CONVERSATION_SCOPE);
            }
        }));


        //initiate request via filter
        final FilterChain chain = createNiceMock(FilterChain.class);
        for (int i = 0; i < numberOfRequests; i++) {
            new WebFilter().doFilter(request, response, chain);

            //lets make sure the conversation scoped instances are all the same
            assert references.size() == (i + 1) * numberOfInjections;

            TestConvObject ref = references.get(0);
            for (TestConvObject reference : references)
                assert ref.equals(reference);       //we dont do sameness tests in case you wana try custom stores later

        }

        //assert expectations
        verify(request, response);
    }


    @Test
    public final void multipleConversationScopingAcrossRequests() throws IOException, ServletException {
        references = new LinkedList<TestConvObject>();

        final int numberOfRequestsInConv1 = 4;
        final int numberOfRequestsInConv2 = 5;

        //create mocks
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);


        //begin mock script ***
        expect(request.getQueryString())
                .andReturn("?asd=asd;" + CONVERSATION_ID + "=" + CONV_KEY)
                .times(numberOfRequestsInConv1);

        expect(request.getServletPath())
                .andReturn("/")
                .times(numberOfRequestsInConv1);
        expect(request.getMethod())
                .andReturn("GET")
                .times(numberOfRequestsInConv1);

        request.setAttribute(CONVERSATION_ID, CONV_KEY);
        expectLastCall().times(numberOfRequestsInConv1);

        final int numberOfInjections = 5;
        expect(request.getAttribute(CONVERSATION_ID))
                .andReturn(CONV_KEY)
                .times(numberOfRequestsInConv1 * numberOfInjections);       //number of requests  * once per conv inject


        //run mock script ***
        replay(request, response);
        ContextManager.setInjector(Guice.createInjector(new AbstractModule() {
            protected void configure() {
                install(Servlets.configure().filters().servlets()

                            .serve("/*").with(ConvTestServlet.class)

                        .buildModule());
                bind(TestConvObject.class).in(Servlets.CONVERSATION_SCOPE);
            }
        }));


        //initiate first conversation (4 requests)
        final FilterChain chain = createNiceMock(FilterChain.class);
        for (int i = 0; i < numberOfRequestsInConv1; i++) {
            new WebFilter().doFilter(request, response, chain);

            //lets make sure the conversation scoped instances are all the same
            assert references.size() == (i + 1) * numberOfInjections;

            TestConvObject ref = references.get(0);
            for (TestConvObject reference : references)
                assert ref.equals(reference);       //we dont do sameness tests in case you wana try custom stores later

        }

        verify(request);

        //copy references out & clear.
        List<TestConvObject> firstConversation = new ArrayList<TestConvObject>(references);
        references.clear();


        request = createMock(HttpServletRequest.class);
        expect(request.getQueryString())
                .andReturn("?asasdadasd&&%%$$++asdd=asd;" + CONVERSATION_ID + "=" + CONV_KEY2)
                .times(numberOfRequestsInConv2);

        expect(request.getServletPath())
                .andReturn("/")
                .times(numberOfRequestsInConv2);
        expect(request.getMethod())
                .andReturn("GET")
                .times(numberOfRequestsInConv2);

        request.setAttribute(CONVERSATION_ID, CONV_KEY2);
        expectLastCall().times(numberOfRequestsInConv2);

        expect(request.getAttribute(CONVERSATION_ID))
                .andReturn(CONV_KEY2)
                .times(numberOfRequestsInConv2 * numberOfInjections);       //number of requests  * once per conv inject

        replay(request);

        //initiate second conversation by using a different request
        for (int i = 0; i < numberOfRequestsInConv2; i++) {
            new WebFilter().doFilter(request, response, chain);

            //lets make sure the conversation scoped instances are all the same
            assert references.size() == (i + 1) * numberOfInjections;

            TestConvObject ref = references.get(0);
            for (TestConvObject reference : references)
                assert ref.equals(reference);       //we dont do sameness tests in case you wana try custom stores later

        }


        //verify that nothing from the first conversation matched the second (i.e. all new instances)
        for (TestConvObject testConvObject : firstConversation) {
            for (TestConvObject reference : references) {
                assert !testConvObject.equals(reference);
            }
        }


        //assert expectations
        verify(request, response);
    }

    @AfterMethod
    public final void clean() {
        references = null;
    }

    public static class ConvTestServlet extends HttpServlet {
        private final Provider<TestConvObject> convObject;
        private final Conversation conversation;

        @Inject
        public ConvTestServlet(Provider<TestConvObject> convObject, Conversation conversation) {
            this.convObject = convObject;
            this.conversation = conversation;
        }

        protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse)
                throws ServletException, IOException {

            //start conversation if this is the first request
            if (references.isEmpty())
                conversation.begin();

            references.add(convObject.get());
            references.add(convObject.get());
            references.add(convObject.get());
            references.add(convObject.get());
            references.add(convObject.get());
        }
    }

    @ConversationScoped
    public static class TestConvObject {
        final String value = UUID.randomUUID().toString();

        @Override
        public boolean equals(Object o) {
            if (! (o instanceof TestConvObject))
                return false;

            TestConvObject that = (TestConvObject)o;

            return that.value.equals(this.value);
        }
    }


}


