package com.wideplay.warp.servlet.conversation;

import com.google.inject.ImplementedBy;

/**
 *
 * <p>
 * This service is used by warp-servlet to persist conversations between requests (or
 * possibly even longer). The store only ever deals with instances of ConversationContext
 * which represent a single conversation instance. A simple implementation may store
 * ConversationContexts inside a singleton HashMap.
 *
 * Clever impls may take objects stored in conversations and save them in a persistent
 * store or serialize them across the wire. This way a custom impl can easily provide transparent
 * conversation replication across a cluster, for example.
 *
 * </p>
 *
 * <p>
 * The default impl provides in-memory persistence of conversations in a concurrent hashmap. It is
 * performant but *not* useful if you want to persist conversations across JVMs, in a database or
 * cluster them. 
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ImplementedBy(InMemoryConversationManager.class)
public interface ConversationManager {

    /**
     *
     * @param key A unique key identifying a conversation.
     * @return Returns the ConversationContext instance associated with the given key
     *  or creates a new one if there is no such instance.
     */
    ConversationContext getConversation(String key);

    /**
     * Ends the conversation associated with the given key, disposing of all the
     * scoped objects.
     *
     * @param key A unique key identifying a conversation in progress.
     * @throws IllegalArgumentException Thrown if there was no ConversationContext
     *  associated witht the given key.
     */
    void endConversation(String key);
}
