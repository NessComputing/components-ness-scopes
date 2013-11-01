/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.scopes.threaddelegate;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext;
import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext.ScopeEvent;

public class TestThreadDelegatedContextEvents
{
    private ThreadDelegatedContext plate = null;

    private final Key<EventRecordingObject> fooStringKey = Key.get(EventRecordingObject.class, Names.named("foo"));
    private final Key<EventRecordingObject> barStringKey = Key.get(EventRecordingObject.class, Names.named("bar"));

    @Before
    public void setUp() throws Exception
    {
        Assert.assertNull(plate);
        this.plate = new ThreadDelegatedContext();
    }

    @After
    public void tearDown() throws Exception
    {
        Assert.assertNotNull(plate);
        this.plate = null;
    }

    @Test
    public void testPutEvent() throws Exception
    {
        Assert.assertFalse(plate.containsKey(fooStringKey));
        final EventRecordingObject eventTest = new EventRecordingObject();

        plate.putIfAbsent(fooStringKey, Providers.of(eventTest));
        Assert.assertEquals(1, eventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, eventTest.getLastEvent());
    }

    @Test
    public void testDoublePutEvent() throws Exception
    {
        Assert.assertFalse(plate.containsKey(fooStringKey));
        final EventRecordingObject fooEventTest = new EventRecordingObject();

        plate.putIfAbsent(fooStringKey, Providers.of(fooEventTest));
        Assert.assertEquals(1, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        Assert.assertFalse(plate.containsKey(barStringKey));
        final EventRecordingObject barEventTest = new EventRecordingObject();

        plate.putIfAbsent(barStringKey, Providers.of(barEventTest));
        Assert.assertEquals(1, barEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, barEventTest.getLastEvent());

        // Make sure that the event count for foo is still 1
        Assert.assertEquals(1, fooEventTest.getEventCount());
    }

    @Test
    public void testEventNotify() throws Exception
    {
        Assert.assertFalse(plate.containsKey(fooStringKey));
        final EventRecordingObject fooEventTest = new EventRecordingObject();

        plate.putIfAbsent(fooStringKey, Providers.of(fooEventTest));
        Assert.assertEquals(1, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        plate.event(ScopeEvent.ENTER);
        Assert.assertEquals(2, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        plate.event(ScopeEvent.LEAVE);
        Assert.assertEquals(3, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.LEAVE, fooEventTest.getLastEvent());
    }


    @Test
    public void testEventClear() throws Exception
    {
        Assert.assertFalse(plate.containsKey(fooStringKey));
        final EventRecordingObject fooEventTest = new EventRecordingObject();

        plate.putIfAbsent(fooStringKey, Providers.of(fooEventTest));
        Assert.assertEquals(1, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        plate.clear();

        Assert.assertEquals(0, plate.size());
        Assert.assertEquals(2, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.LEAVE, fooEventTest.getLastEvent());
    }
}
