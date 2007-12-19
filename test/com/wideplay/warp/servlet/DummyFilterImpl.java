package com.wideplay.warp.servlet;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 1:41:11 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class DummyFilterImpl implements Filter {
    int num;
    public DummyFilterImpl() {
    }

    public DummyFilterImpl(int num) {
        this.num = num;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
