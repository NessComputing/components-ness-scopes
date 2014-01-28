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

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestThreadDelegatedContext
{
    private ThreadDelegatedContext plate = null;

    private final Key<String> fooStringKey = Key.get(String.class, Names.named("foo"));
    private final Key<String> barStringKey = Key.get(String.class, Names.named("bar"));

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
    public void testEmptyPlate() throws Exception
    {
        Assert.assertEquals(0, plate.size());
        Assert.assertFalse(plate.containsKey(Key.get(String.class)));
    }

    @Test
    public void testSimplePut() throws Exception
    {
        Assert.assertFalse(plate.containsKey(fooStringKey));

        plate.putIfAbsent(fooStringKey, Providers.of("hallo"));
        Assert.assertEquals(1, plate.size());
        Assert.assertTrue(plate.containsKey(fooStringKey));
        Assert.assertEquals("hallo", plate.get(fooStringKey));
    }

    @Test
    public void testDoubleSamePut() throws Exception
    {
        Assert.assertFalse(plate.containsKey(fooStringKey));

        plate.putIfAbsent(fooStringKey, Providers.of("hallo"));
        Assert.assertEquals(1, plate.size());
        Assert.assertTrue(plate.containsKey(fooStringKey));
        Assert.assertEquals("hallo", plate.get(fooStringKey));

        plate.putIfAbsent(fooStringKey, Providers.of("hallo"));
        Assert.assertEquals(1, plate.size());
        Assert.assertTrue(plate.containsKey(fooStringKey));
        Assert.assertEquals("hallo", plate.get(fooStringKey));
    }

    @Test
    public void testDoublePut() throws Exception
    {
        Assert.assertFalse(plate.containsKey(fooStringKey));
        Assert.assertFalse(plate.containsKey(barStringKey));

        plate.putIfAbsent(fooStringKey, Providers.of("hallo"));
        Assert.assertEquals(1, plate.size());
        Assert.assertTrue(plate.containsKey(fooStringKey));
        Assert.assertEquals("hallo", plate.get(fooStringKey));

        plate.putIfAbsent(barStringKey, Providers.of("world"));
        Assert.assertEquals(2, plate.size());
        Assert.assertTrue(plate.containsKey(barStringKey));
        Assert.assertEquals("world", plate.get(barStringKey));
    }

    @Test
    public void testNullValueOverwrite() throws Exception
    {
        plate.putIfAbsent(fooStringKey, Providers.<String>of(null));
        Assert.assertEquals(1, plate.size());
        Assert.assertTrue(plate.containsKey(fooStringKey));
        Assert.assertNull(plate.get(fooStringKey));

        // Should still return null, not overwriting it.
        plate.putIfAbsent(fooStringKey, Providers.of("world"));
        Assert.assertEquals(1, plate.size());
        Assert.assertTrue(plate.containsKey(fooStringKey));
        Assert.assertEquals(null, plate.get(fooStringKey));
    }

    @Test
    public void testClear() throws Exception
    {
        Assert.assertFalse(plate.containsKey(fooStringKey));
        Assert.assertFalse(plate.containsKey(barStringKey));

        plate.putIfAbsent(fooStringKey, Providers.of("hallo"));
        Assert.assertEquals(1, plate.size());
        Assert.assertTrue(plate.containsKey(fooStringKey));
        Assert.assertEquals("hallo", plate.get(fooStringKey));

        plate.putIfAbsent(barStringKey, Providers.of("world"));
        Assert.assertEquals(2, plate.size());
        Assert.assertTrue(plate.containsKey(barStringKey));
        Assert.assertEquals("world", plate.get(barStringKey));

        plate.clear();

        Assert.assertFalse(plate.containsKey(fooStringKey));
        Assert.assertFalse(plate.containsKey(barStringKey));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullKeyGet() throws Exception
    {
        plate.get(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullKeyPut() throws Exception
    {
        plate.putIfAbsent(null, Providers.of("Hello"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullKeyContains() throws Exception
    {
        plate.containsKey(null);
    }
}
