package com.nesscomputing.scopes.threaddelegate;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Provider;

public class ScopedObject
{
    private final AtomicInteger performances = new AtomicInteger();

    public ScopedObject()
    {
    }

    public void perform()
    {
        performances.incrementAndGet();
    }

    public int getPerformances()
    {
        return performances.intValue();
    }

    public static class TestObjectProvider implements Provider<ScopedObject>
    {
        private static final AtomicInteger HANDOUTS = new AtomicInteger();

        @Override
        public ScopedObject get()
        {
            HANDOUTS.incrementAndGet();
            return new ScopedObject();
        }

        public static int getHandouts()
        {
            return HANDOUTS.intValue();
        }

        public static void reset()
        {
            HANDOUTS.set(0);
        }
    }
}
