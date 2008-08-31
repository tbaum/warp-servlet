package com.wideplay.warp.servlet.conversation;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>
 *  Scope for Conversations (long-running contextual requests typically from the same user, but not necessarily).
 * Not bound by default, use if you wish by adding this line to your Guice module:
 * </p>
 *
 * <pre>
 *  bindScope(ConversationScoped.class, Servlets.CONVERSATION_SCOPE);
 * </pre>
 *
 * <p>
 * Conversation support in Warp-servlet is highly configurable. Unlike servlet HTTP sessions, conversations do not
 * have to live temporally in the JVM. A conversation store is easily configured (example, a disk or cluster cache).
 *
 * </p>
 *
 * <p>
 * Conversations may be short-lived, i.e. less than the life of a HTTP session or they be very long-lived, even
 * between JVM shutdowns and startups. This makes the scope flexible if you need to keep around some dealings with
 * a user over a long period of time, uncommitted. Conversations are started and ended by invoking into the
 * {@code Conversation} artifact. 
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.Conversation
 */
@ScopeAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface ConversationScoped {
}