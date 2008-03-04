package com.wideplay.warp.servlet.conversation;

import com.google.inject.ImplementedBy;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 7:09:18 PM
 *
 * <p>
 * This service is used by warp-servlet to persist conversations between requests (or
 * possibly even longer). The store only ever deals with instances of ConversationContext
 * which represent a single conversation instance. A naive implementation may store
 * ConversationContexts inside a singleton HashMap, thus making conversations available across
 * requests and sessions but not beyond.
 *
 * Clever impls may take objects stored in conversations and save them in a persistent
 * store or serialize them across the wire. This way a custom impl can easily provide transparent
 * conversation replication across a cluster (by storing ConversationContext instances
 * in a data grid).
 *
 *
 * </p>
 *
 * <p>
 * The default impl provides in-memory persistence of conversations in a concurrent hashmap. It is
 * performant but *not* useful if you want to persist conversations across JVMs, in a database or cluster. 
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ImplementedBy(SimpleSingletonConversationStore.class)
public interface ConversationStore {

    /**
     *
     * @param key A unique key identifying a conversation.
     * @return Returns the ConversationContext instance associated with the given key
     *  or null if there is no such instance.
     */
    ConversationContext get(String key);

    /**
     * Creates a new conversation bound to the given key. Effectively starts a new span
     * of the scope. Called from {@code Conversation.begin()}
     *
     * @param key A unique unused key (which may be recycled from previously ended
     *  conversations) to associate with the new ConversationContext.
     * @return Returns a newly created ConversationContext instance bound to the
     *  given key.
     */
    ConversationContext newConversation(String key);

    /**
     * Ends the conversation associated with the given key, disposing of all the
     * scoped objects.
     *
     * @param key A unique key identifying a conversation in progress.
     * @throws IllegalArgumentException Thrown if there was no ConversationContext
     *  associated witht the given key.
     */
    void end(String key);
}
