package com.wideplay.warp.servlet.conversation;

import com.google.inject.ImplementedBy;
import com.google.inject.Key;

import java.io.Serializable;

/**
 * 
 * <p>
 * An artifact that represents a single conversation instance. Objects managed
 * by the guice injector are placed inside a ConversationContext and associated
 * with a Key from which they may later be retrieved.
 *
 * ConversationContexts are created, managed and destroyed entirely by a ConversationManager
 * and as such is a part of the implementation of ConversationManager. For example, if a custom
 * ConversationManager persists conversations inside a disk cache, then it may provide an
 * implementation of ConversationContext that is serializable to the disk cache.
 *
 * ConversationContexts are lost (i.e. destroyed) when their associated conversation ends.
 * This interface is typically never exposed to client code and is intended to be used internally.
 *
 * </p>
 *
 * <p>
 * Default impl stores instances in a thread UNSAFE hashmap. Certainly, do not add synchronization behavior
 *  unless you have a very specific reason.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see ConversationManager
 */
@ImplementedBy(InMemoryConversationContext.class)
public interface ConversationContext extends Serializable {
    /**
     *
     * The default impl simply obtains the associated instance from memory.
     *
     * Custom impls of ConversationManager will override this method and use it
     *  to obtain instances that have been serialized to disk, cluster cache or similar
     *  persistent store.
     *
     * @param key The Guice key to the conv-scoped instance being asked for.
     * @return Returns the associated instance if one is present, or null.
     */
    <T> T get(Key<T> key);

    /**
     *
     * The default impl simply obtains the associated instance from memory.
     *
     * Custom impls of ConversationManager will override this method and use it
     *  to store instances by serializing them to disk, cluster cache or similar
     *  persistent store, for example. This is called everytime a new instance is created
     *  for a conversation and is thus the ideal hook to trigger the storage. 
     *
     * @param key The Guice key to the conv-scoped instance being stashed into this conv.
     * @param t The associated instance for *this* conversation .
     */
    <T> void put(Key<T> key, T t);
}
