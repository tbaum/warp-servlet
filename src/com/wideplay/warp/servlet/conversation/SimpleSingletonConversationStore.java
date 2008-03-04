package com.wideplay.warp.servlet.conversation;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 23, 2007
 * Time: 5:08:54 PM
 *
 * A very simple impl of the conversation store that stores the context of conversations
 * inside a singleton concurrent hash map.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ThreadSafe
class SimpleSingletonConversationStore implements ConversationStore {
    private final ConcurrentMap<String, HashMapConversationContext> store =
            new ConcurrentHashMap<String, HashMapConversationContext>();

    public ConversationContext get(String key) {
        return store.get(key);
    }

    public ConversationContext newConversation(String key) {
        final HashMapConversationContext context = new HashMapConversationContext();
        store.put(key, context);

        return context;
    }

    public void end(String key) {
        store.remove(key);
    }
}
