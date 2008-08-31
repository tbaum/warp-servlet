package com.wideplay.warp.servlet;

/**
 * Indicates URLs were not rewritten properly or begin() was not called.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class NoActiveConversationException extends RuntimeException {
    public NoActiveConversationException(String msg) {
        super(msg);
    }
}