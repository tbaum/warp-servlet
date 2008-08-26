package com.wideplay.warp.servlet;

/**
 * Thrown when a request-, session-, flash-, or conversation- scoped object is injected and there
 *  is no HTTP request currently active. For example, in the init() or destroy() lifecycle of a servlet.
 * 
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
class OutOfScopeException extends RuntimeException {
    public OutOfScopeException(String msg) {
        super(msg);
    }
}
