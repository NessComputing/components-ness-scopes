package com.nesscomputing.scopes.threaddelegate.concurrent;

import static com.nesscomputing.scopes.threaddelegate.ThreadDelegatedScope.SCOPE;

import java.util.concurrent.Callable;

import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext;


class DelegatingCallable<C> implements Callable<C>
{
    private final Callable<C> wrappedCallable;
    private final ThreadDelegatedContext callerContext;

    DelegatingCallable(final Callable<C> wrappedCallable)
    {
        this.wrappedCallable = wrappedCallable;
        this.callerContext = SCOPE.getContext();
    }

    @Override
    public C call() throws Exception
    {
        final ThreadDelegatedContext originalContext = SCOPE.getContext();
        try {
            // Assign the caller context.
            SCOPE.changeScope(callerContext);

            return wrappedCallable.call();
        }
        finally {
            // Tell the current context, that we are leaving.
            SCOPE.changeScope(originalContext);
        }
    }
}
