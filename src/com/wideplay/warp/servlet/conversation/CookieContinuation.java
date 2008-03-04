package com.wideplay.warp.servlet.conversation;

import com.google.inject.Singleton;
import net.jcip.annotations.Immutable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 23, 2007
 * Time: 5:01:57 PM
 *
 * A really idiot-simple cookie-based continuation strategy. Akin to HTTP sessions'
 * cookie-based continuations.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable @Singleton
class CookieContinuation implements Continuation {
    static final String WARPCONVID = "WARPCONVID";


    public String findKey(HttpServletRequest request) {
        //check if this is the first request in the conversation (then there won't be any cookie set)
        String key = (String) request.getAttribute(WARPCONVID);
        if (null != key)
            return key;

        //simple cookie-based continuation strategy
        for (Cookie cookie : request.getCookies()) {
            if (WARPCONVID.equals(cookie.getName())) {
                final String conversationKey = cookie.getValue();

                //cache this in the request so we don't keep scanning the cookies
                request.setAttribute(WARPCONVID, conversationKey);

                return conversationKey;
            }
        }


        //this request was not part of *any* conversation (i.e. it was stateless)
        return null;
    }

    public void writeKey(HttpServletRequest request,  HttpServletResponse response, String key) {
        //add a cookie to the response
        response.addCookie(new Cookie(WARPCONVID, key));

        //include the current request in this conversation
        request.setAttribute(WARPCONVID, key);
    }
}
