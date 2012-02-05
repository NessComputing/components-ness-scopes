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
