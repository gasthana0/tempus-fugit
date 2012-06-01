/*
 * Copyright (c) 2009-2012, toby weston & tempus-fugit committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.tempusfugit.concurrency;

import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.api.ThreadingPolicy;
import org.jmock.internal.StatePredicate;
import org.jmock.lib.concurrent.internal.FixedTimeout;
import org.jmock.lib.concurrent.internal.InfiniteTimeout;
import org.jmock.lib.concurrent.internal.Timeout;
import org.junit.Assert;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.StringDescription.asString;

public class TimingOutSynchroniser implements ThreadingPolicy {

    private final Lock lock = new ReentrantLock();
    private final Condition awaitingStatePredicate = lock.newCondition();

    private Error firstError = null;

    public void waitUntil(StatePredicate predicate) throws InterruptedException {
        waitUntil(predicate, new InfiniteTimeout());
    }

    /**
     * Waits up to a timeout for a StatePredicate to become active.  Fails the
     * test if the timeout expires.
     */
    public void waitUntil(StatePredicate p, long timeoutMs) throws InterruptedException {
        waitUntil(p, new FixedTimeout(timeoutMs));
    }

    private void waitUntil(StatePredicate predicate, Timeout timeout) throws InterruptedException {
        try {
            lock.lock();
            while (!predicate.isActive()) {
                try {
                    awaitingStatePredicate.await(timeout.timeRemaining(), MILLISECONDS);
                } catch (TimeoutException e) {
                    if (firstError != null)
                        throw firstError;
                    Assert.fail("timed out waiting for " + asString(predicate));
                }
            }
        } finally {
            lock.unlock();
        }

    }

    public Invokable synchroniseAccessTo(final Invokable mockObject) {
        return new Invokable() {
            public Object invoke(Invocation invocation) throws Throwable {
                return synchroniseInvocation(mockObject, invocation);
            }
        };
    }

    private Object synchroniseInvocation(Invokable mockObject, Invocation invocation) throws Throwable {
        try {
            lock.lock();
            try {
                return mockObject.invoke(invocation);
            } catch (Error e) {
                if (firstError == null)
                    firstError = e;
                throw e;
            } finally {
                awaitingStatePredicate.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
