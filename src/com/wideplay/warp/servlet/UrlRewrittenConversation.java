package com.wideplay.warp.servlet;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.wideplay.warp.servlet.conversation.ConversationContext;
import com.wideplay.warp.servlet.conversation.ConversationManager;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * <p>
 *
 * An interface to the *current* conversation. Inject this into your objects, pages
 * or services to start or stop the current conversation. And manipulate URLs for continuation.
 *
 * This is *not* the scope. If the target object (receiving this object via injection
 * has *wider* than request scope, injecting a Provider&lt;Conversation&gt; is recommended
 * instead)
 *
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable @Singleton //well-we hope it's immutable--if users plug in their on ConvManager...
class UrlRewrittenConversation implements Conversation {
    static final String CONVERSATION_ID = "wconvid";
    private static final String CONVERSATION_KEY_STRING = String.format(";%s=", CONVERSATION_ID);
    private static final String CONVERSATION_ID_TEMPLATE = CONVERSATION_KEY_STRING + "%s";

    private final ConversationManager conversationManager;

    @Inject
    UrlRewrittenConversation(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

    public void begin() {
        //sanity check
        if (null == ContextManager.getRequest())
            throw new OutOfScopeException("Was not inside a HTTP request. Did you forget to apply "
                + WebFilter.class.getName() + " before calling Conversation.begin()?");

        conversationManager.getConversation(UUID.randomUUID().toString());
    }

    public void end() {
        final String conversationKey = currentConversationKey();

        //why did you call end? =)
        if (null == conversationKey)
            throw new NoActiveConversationException("There was no active conversation to end for the current request. Either you: " +
                    "\n1) Have forgotten to apply " + WebFilter.class.getName() + ", " +
                    "\n2) Have not called Conversation.begin() yet, OR " +
                    "\n3) Have not correctly rewritten the URL for conversation continuation" +
                    "\n"
            );


        conversationManager.endConversation(conversationKey);
    }

    private static String currentConversationKey() {
        return (String) ContextManager.getRequest().getAttribute(CONVERSATION_ID);
    }

    public String rewrite(String url) {
        //TODO Should we fix this so that it is idempotent...

        //append conversation id to the URL
        return url + String.format(CONVERSATION_ID_TEMPLATE, currentConversationKey());
    }

    @NotNull
    public <T> T provide(Key<T> key, Provider<T> creator, HttpServletRequest request) {
        final String conversationKey = (String) request.getAttribute(CONVERSATION_ID);

        //out of scope, but still inside request so throw a different exception
        if (null == conversationKey)
            throw new NoActiveConversationException("There was no active conversation for the current request. Either you: " +
                    "\n1) Have forgotten to apply " + WebFilter.class.getName() + ", " +
                    "\n2) Have not called Conversation.begin() yet, OR " +
                    "\n3) Have not correctly rewritten the URL for conversation continuation" +
                    "\n"
            );


        //retrieve current conversation (or create a new one)
        final ConversationContext conversation = conversationManager.getConversation(conversationKey);

        //try to reactivate the instance if there is one
        T t = conversation.get(key);

        //otherwise create and store a new one
        if (null == t) {
            t = creator.get();
            conversation.put(key, t);
        }

        return t;
    }

    static int conversationKeyOffset(HttpServletRequest request) {
        final String queryString = request.getQueryString();

        //short circuit if there was no querystring
        if (null == queryString)
            return -1;

        final int index = queryString.indexOf(CONVERSATION_KEY_STRING);

        //extract id and store into request as attribute
        if (index > -1) {
            String key = queryString.substring(index + CONVERSATION_KEY_STRING.length());

            request.setAttribute(CONVERSATION_ID, key);
        }

        return index;
    }
}
