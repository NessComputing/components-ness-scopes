package com.nesscomputing.scopes.threaddelegate.concurrent;

import java.util.concurrent.Executor;

class ThreadDelegatingExecutor implements Executor
{
    private final Executor wrappedExecutor;

    ThreadDelegatingExecutor(final Executor wrappedExecutor)
    {
        this.wrappedExecutor = wrappedExecutor;
    }

    @Override
    public void execute(final Runnable wrappedRunnable)
    {
        wrappedExecutor.execute(new DelegatingRunnable(wrappedRunnable));
    }
}
