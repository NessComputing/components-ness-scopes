package com.nesscomputing.scopes.threaddelegate;


import javax.annotation.Nullable;


import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext.ScopeEvent;

/**
 * Maintains the global state for the ThreadDelegatedScope.
 */
@Singleton
public class ThreadDelegatedScope implements Scope
{
    private final ThreadLocal<ThreadDelegatedContext> threadLocal;

    /** The global scope object to bind. This is created at load time of this class. */
    public static final ThreadDelegatedScope SCOPE = new ThreadDelegatedScope();

    ThreadDelegatedScope()
    {
        this.threadLocal = new ThreadLocal<ThreadDelegatedContext>();
    }

    /**
     * Returns the context (the set of objects bound to the scope) for the current thread.
     * A context may be shared by multiple threads.
     */
    public ThreadDelegatedContext getContext()
    {
        ThreadDelegatedContext context = threadLocal.get();
        if (context == null) {
            context = new ThreadDelegatedContext();
            threadLocal.set(context);
        }
        return context;
    }

    /**
     * A thread enters the scope. Clear the current context. If a new context
     * was given, assign it to the scope, otherwise leave it empty.
     */
    public void changeScope(@Nullable final ThreadDelegatedContext context)
    {
        final ThreadDelegatedContext oldContext = threadLocal.get();
        if (oldContext != null) {
            if (oldContext == context) {
                // If the context gets exchanged with itself, do nothing.
                return;
            }
            else {
                // This must not clear the context. It might still be
                // referenced by another thread.
                oldContext.event(ScopeEvent.LEAVE);
            }
        }

        if (context != null) {
            threadLocal.set(context);
            context.event(ScopeEvent.ENTER);
        }
        else {
            threadLocal.remove();
        }
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped)
    {
        return new ThreadDelegatedScopeProvider<T>(key, unscoped);
    }

    public class ThreadDelegatedScopeProvider<T> implements Provider<T>
    {
        private final Key<T> key;
        private final Provider<T> unscoped;

        public ThreadDelegatedScopeProvider(final Key<T> key, final Provider<T> unscoped)
        {
            Preconditions.checkArgument(key != null, "key must not be null!");
            Preconditions.checkArgument(unscoped != null, "unscoped provider must not be null!");

            this.key = key;
            this.unscoped = unscoped;
        }

        @Override
        public T get()
        {
            final ThreadDelegatedContext context = getContext();
            // This must be synchronized around the context, because otherwise
            // multiple threads will try to set the same value at the same time.
            synchronized(context) {
                if (context.containsKey(key)) {
                    return context.get(key);
                }
                else {
                    final T value = unscoped.get();
                    context.put(key, value);
                    return value;
                }
            }
        }

        private volatile String toString = null;

        @Override
        public synchronized String toString()
        {
            if (toString == null) {
                toString = String.format("ThreadDelegatedScoped provider (Key: %s) of %s", key.toString(), unscoped.toString());
            }
            return toString;
        }
    }
}
