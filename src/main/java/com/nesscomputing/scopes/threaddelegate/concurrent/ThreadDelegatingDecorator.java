package com.nesscomputing.scopes.threaddelegate.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public final class ThreadDelegatingDecorator
{
    private ThreadDelegatingDecorator()
    {
    }

    public static Executor wrapExecutor(final Executor wrappedExecutor)
    {
        return new ThreadDelegatingExecutor(wrappedExecutor);
    }

    public static ExecutorService wrapExecutorService(final ExecutorService wrappedExecutorService)
    {
        return new ThreadDelegatingExecutorService(wrappedExecutorService);
    }
}
