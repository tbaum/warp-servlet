package com.wideplay.warp.servlet.conversation;

import com.google.inject.ImplementedBy;
import com.google.inject.Key;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 7:03:53 PM
 *
 * <p>
 * An artifact that represents a single conversation instance. Objects managed
 * by the guice injector are placed inside a ConversationContext and associated
 * with a Key from which they may later be retrieved.
 *
 * ConversationContexts are created, managed and destroyed entirely by a ConversationStore
 * and as such is a part of the implementation of ConversationStore. For example, if a custom
 * ConversationStore persists conversations inside a disk cache, then it may provide an
 * implementation of ConversationContext that is serializable.
 *
 * ConversationContexts are lost (i.e. destroyed) when their associated conversation ends.
 * This interface is typically never exposed to client code and is intended to be used internally.
 *
 * </p>
 *
 * <p>
 * Default impl stores instances in a thread UNSAFE hashmap. You should not need to change the
 * default impl too much even with customized stores. Certainly, do not add synchronization behavior
 * unless you have a very specific reason.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.conversation.ConversationStore
 */
@ImplementedBy(HashMapConversationContext.class)
public interface ConversationContext extends Serializable {
    <T> T get(Key<T> key);

    <T> void put(Key<T> key, T t);
}
