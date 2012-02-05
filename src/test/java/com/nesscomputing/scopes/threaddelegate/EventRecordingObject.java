package com.nesscomputing.scopes.threaddelegate;

import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext.ScopeEvent;
import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext.ScopeListener;

class EventRecordingObject implements ScopeListener
{
    private int eventCount = 0;
    private ScopeEvent lastEvent = null;

    @Override
    public void event(final ScopeEvent event)
    {
        eventCount++;
        this.lastEvent = event;
    }

    public int getEventCount()
    {
        return eventCount;
    }

    public ScopeEvent getLastEvent()
    {
        return lastEvent;
    }
}
