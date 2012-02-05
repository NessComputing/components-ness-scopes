package com.nesscomputing.scopes.threaddelegate;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext;
import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedScope;
import com.nesscomputing.scopes.threaddelegate.ThreadDelegatedContext.ScopeEvent;

public class TestThreadDelegatedScope
{
    private ThreadDelegatedScope scope = null;

    private final Key<String> fooStringKey = Key.get(String.class, Names.named("foo"));

    @Before
    public void setUp()
    {
        Assert.assertNull(scope);
        this.scope = new ThreadDelegatedScope();
    }

    @After
    public void tearDown()
    {
        Assert.assertNotNull(scope);
        this.scope = null;
    }

    @Test
    public void testNewPlate()
    {
        final ThreadDelegatedContext plate = scope.getContext();
        Assert.assertNotNull(plate);
        Assert.assertEquals(0, plate.size());

        final ThreadDelegatedContext plate2 = scope.getContext();
        Assert.assertNotNull(plate2);
        Assert.assertEquals(0, plate2.size());

        Assert.assertSame(plate, plate2);
    }

    @Test
    public void testScopeLeave()
    {
        final ThreadDelegatedContext plate = scope.getContext();
        Assert.assertNotNull(plate);
        Assert.assertEquals(0, plate.size());

        scope.changeScope(null);

        final ThreadDelegatedContext plate2 = scope.getContext();
        Assert.assertNotNull(plate2);
        Assert.assertEquals(0, plate2.size());

        Assert.assertNotSame(plate, plate2);
    }

    @Test
    public void testScopePromote()
    {
        final ThreadDelegatedContext newPlate = new ThreadDelegatedContext();

        final ThreadDelegatedContext plate = scope.getContext();
        Assert.assertNotNull(plate);
        Assert.assertEquals(0, plate.size());

        scope.changeScope(newPlate);

        final ThreadDelegatedContext plate2 = scope.getContext();
        Assert.assertNotNull(plate2);
        Assert.assertEquals(0, plate2.size());

        // Old plate has disappeared.
        Assert.assertNotSame(plate, plate2);

        // But the plate returned is the new plate.
        Assert.assertSame(newPlate, plate2);
    }

    @Test
    public void testChangeScopeEvents()
    {
        final ThreadDelegatedContext plate = scope.getContext();
        Assert.assertNotNull(plate);
        Assert.assertEquals(0, plate.size());

        final EventRecordingObject fooEventTest = new EventRecordingObject();

        plate.put(fooStringKey, fooEventTest);
        Assert.assertEquals(1, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        scope.changeScope(null);

        Assert.assertEquals(2, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.LEAVE, fooEventTest.getLastEvent());
    }

    @Test
    public void testScopeEnterLeaveEvents()
    {
        final ThreadDelegatedContext plate = new ThreadDelegatedContext();
        final EventRecordingObject fooEventTest = new EventRecordingObject();

        plate.put(fooStringKey, fooEventTest);
        Assert.assertEquals(1, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        scope.changeScope(plate);

        Assert.assertSame(plate, scope.getContext());

        Assert.assertEquals(2, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        scope.changeScope(null);

        Assert.assertNotSame(plate, scope.getContext());
        Assert.assertEquals(3, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.LEAVE, fooEventTest.getLastEvent());

        // Put the plate back in.
        scope.changeScope(plate);

        Assert.assertSame(plate, scope.getContext());

        Assert.assertEquals(4, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        // Replace with a new plate
        scope.changeScope(new ThreadDelegatedContext());

        Assert.assertNotSame(plate, scope.getContext());

        Assert.assertEquals(5, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.LEAVE, fooEventTest.getLastEvent());

        // Put it back in one more time...
        scope.changeScope(plate);

        Assert.assertSame(plate, scope.getContext());

        Assert.assertEquals(6, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());

        // Pathologic case: Replace with itself.
        scope.changeScope(plate);

        Assert.assertSame(plate, scope.getContext());

        Assert.assertEquals(6, fooEventTest.getEventCount());
        Assert.assertEquals(ScopeEvent.ENTER, fooEventTest.getLastEvent());
    }

}
