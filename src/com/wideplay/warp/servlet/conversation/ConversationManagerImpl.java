package com.wideplay.warp.servlet.conversation;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.wideplay.warp.servlet.WebFilter;
import net.jcip.annotations.Immutable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 23, 2007
 * Time: 4:54:21 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable @Singleton
class ConversationManagerImpl implements ConversationManager {
    private final Continuation continuation;
    private final ConversationStore store;
    private final Provider<HttpServletRequest> requestProvider;
    private final Provider<HttpServletResponse> responseProvider;

    @Inject
    public ConversationManagerImpl(Continuation continuation, ConversationStore store,
                               Provider<HttpServletRequest> requestProvider,
                               Provider<HttpServletResponse> responseProvider) {

        this.continuation = continuation;
        this.store = store;
        this.requestProvider = requestProvider;
        this.responseProvider = responseProvider;
    }

    public <T> T getAndPutIfAbsent(Key<T> objectKey, Provider<T> creator, HttpServletRequest request) {
        //uses the defined continuation strategy to locate a conversation for the given request
        final String conversationKey = continuation.findKey(request);

        //exception if none are active or it's a stateless request
        if (null == conversationKey)
            throw new NoActiveConversationException("There was no active conversation for the current request. Either you are: " +
                    "\n\n1) Operating outside a request," +
                    "\n2) Have forgotten to apply " + WebFilter.class.getName() + ", " +
                    "\n3) Have not called Conversation.begin(), or called it in another request, OR " +
                    "\n4) Are using a continuation strategy that is not working (cookies disabled? rolled your own?)" +
                    "\n"
            );


        //find the conversation-stored object!
        final ConversationContext conversationContext = store.get(conversationKey);

        //make sure the read is atomic with respect to writes or other reads on the same conv context(!)
        synchronized (conversationContext) {
            T t = conversationContext.get(objectKey);

            //create & store if absent
            if (null == t) {
                t = creator.get();
                conversationContext.put(objectKey, t);
            }

            //return found or created instance
            return t;
        }
    }

    @Deprecated
    public ConversationContext get(HttpServletRequest request) {
        //uses the defined continuation strategy to locate a conversation for the given request
        String key =  continuation.findKey(request);

        //return null if no conversation could be located (i.e. none are active or it's a stateless request)
        return (null == key) ? null : store.get(key);
    }

    //creates a new conversation with the given key
    public void set(String key) {
        continuation.writeKey(requestProvider.get(), responseProvider.get(), key);

        //start a new conversation context against the given key
        store.newConversation(key);
    }
}
