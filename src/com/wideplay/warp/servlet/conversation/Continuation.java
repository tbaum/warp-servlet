package com.wideplay.warp.servlet.conversation;

import com.google.inject.ImplementedBy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 7:24:42 PM
 *
 * <p>
 * A service that is used to identify requests belonging to an existing conversation.
 * Implementations may choose to write conv-keys to the response & request or may use
 * some other means of continuing a conversation by chaining requests together (for example,
 * by piggy-backing the HTTP session).
 * </p>
 *
 * <p>
 * Continuations are typically triggered the first time a conversation-scoped object is
 * asked for by the application, but this is not a rule (request queueing may force earlier
 * calls to this service, i.e. as soon as it arrives). This is done with a call to {@code findKey()}.
 * </p>
 *
 * <p>
 * Write-key is called only once at the beginning of the conversation (i.e. when
 * {@code Conversation.begin()} is invoked. Writing continuation keys (such as via URL-rewriting)
 * must be done manually by the user. For example, via a Filter or by manually encoding each
 * emitted URL. Implementations of Continuation ought to provide some accompanying means
 * to make this easy. The default impl shipped uses simple, Cookie-based continuations.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ImplementedBy(CookieContinuation.class)
public interface Continuation {
    String findKey(HttpServletRequest request);

    void writeKey(HttpServletRequest request,  HttpServletResponse response, String key);
}
