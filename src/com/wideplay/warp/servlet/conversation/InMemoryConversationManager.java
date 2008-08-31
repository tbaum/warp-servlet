package com.wideplay.warp.servlet.conversation;

import com.google.inject.Singleton;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * A very simple impl of the conversation manager that stores the context of conversations
 * inside a concurrent hash map in memory.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ThreadSafe @Singleton
class InMemoryConversationManager implements ConversationManager {
    private final ConcurrentMap<String, InMemoryConversationContext> store =
            new ConcurrentHashMap<String, InMemoryConversationContext>();

    @NotNull
    public ConversationContext getConversation(String key) {
        InMemoryConversationContext context = store.get(key);

        //create a new one -- given that keys are unique we do not need to worry about data races for put()
        if (null == context) {
            context = new InMemoryConversationContext();
            store.put(key, context);

        }

        return context;
    }

    public void endConversation(String key) {
        //create a new one -- given that keys are unique we do not need to worry about data races
        store.remove(key);
    }
}
