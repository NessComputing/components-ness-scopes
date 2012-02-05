package com.nesscomputing.scopes.threaddelegate;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

/**
 * This is an extension of the "request" scope and somewhat close to the "thread" scope. It allows binding object to
 * an incoming request (controlled by the ThreadDelegatingScopeFilter), but objects in this scope can be "forwarded" to other threads
 * through an Executor or ExecutorService to allow execution "on behalf of the original request". 
 *
 * Objects forwarded can e.g. be tracking information or authentication credentials.
 *
 * This scope was called "Spaghetti" at some point because it is somewhat convoluted and related to threads, which look a lot like Spaghetti.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@ScopeAnnotation
public @interface ThreadDelegated
{
}
