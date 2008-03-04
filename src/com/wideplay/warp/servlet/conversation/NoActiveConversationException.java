package com.wideplay.warp.servlet.conversation;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 2:37:33 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class NoActiveConversationException extends RuntimeException {
    public NoActiveConversationException(String msg) {
        super(msg);
    }
}