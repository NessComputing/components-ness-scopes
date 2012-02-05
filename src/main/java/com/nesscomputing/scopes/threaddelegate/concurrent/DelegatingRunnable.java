package com.nesscomputing.scopes.threaddelegate.concurrent;

import static com.nesscomputing.scopes.threaddelegate.ThreadDelegatedScope.SCOPE;

import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext;


class DelegatingRunnable implements Runnable
{
    private final Runnable wrappedRunnable;
    private final ThreadDelegatedContext callerContext;

    DelegatingRunnable(final Runnable wrappedRunnable)
    {
        this.wrappedRunnable = wrappedRunnable;
        this.callerContext = SCOPE.getContext();
    }

    @Override
    public void run()
    {
        final ThreadDelegatedContext originalContext = SCOPE.getContext();
        try {
            // Assign the caller context.
            SCOPE.changeScope(callerContext);

            wrappedRunnable.run();
        }
        finally {
            // Reassign the original context.
            SCOPE.changeScope(originalContext);
        }
    }
}
