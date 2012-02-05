package com.nesscomputing.scopes.threaddelegate;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Key;

/**
 * This is the context object for the scope. All members of the context object can potentially
 * be shared between objects, so they should be thread safe.
 */
public class ThreadDelegatedContext
{
    private final Map<Key<?>, Object> contents = Maps.newHashMap();
    private final Set<ScopeListener> listeners = Sets.newHashSet();

    ThreadDelegatedContext()
    {
    }

    synchronized boolean containsKey(@Nonnull final Key<?> key)
    {
        Preconditions.checkArgument(key != null, "Key must not be null!");
        return contents.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    synchronized <T> T get(final Key<T> key)
    {
        Preconditions.checkArgument(key != null, "Key must not be null!");
        final Object result = contents.get(key);
        return (T) result;
    }

    synchronized void put(@Nonnull final Key<?> key, @Nullable final Object value)
    {
        Preconditions.checkArgument(key != null, "Key must not be null!");
        contents.put(key, value);

        if (value instanceof ScopeListener) {
            final ScopeListener listener = ScopeListener.class.cast(value);
            listeners.add(listener);
            // Send an "enter" event to notify the listener that it was put in scope.
            listener.event(ScopeEvent.ENTER);
        }
    }

    @VisibleForTesting
    synchronized void clear()
    {
        event(ScopeEvent.LEAVE);
        listeners.clear();
        contents.clear();
    }

    @VisibleForTesting
    synchronized int size()
    {
        return contents.size();
    }

    synchronized void event(final ScopeEvent event)
    {
        for (ScopeListener listener: listeners) {
            listener.event(event);
        }
    }

    /**
     * Objects put in the ThreadDelegated scope can implement this interface to be notified when
     * they are moved from one thread to another.
     */
    public static interface ScopeListener
    {
        void event(ScopeEvent event);
    }

    public static enum ScopeEvent
    {
        ENTER,
        LEAVE;
    }
}
