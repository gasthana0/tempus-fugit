/*
 * Copyright (c) 2009, tempus-fugit committers
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

import static com.google.code.tempusfugit.concurrency.IntermittentRule.Repeat.repeat;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;

public class IntermittentTest {

    @Rule public IntermittentRule rule = new IntermittentRule(repeat(100));


    private static int counter = 0;

    @Test
    @Intermittent
    public void annotatedTest() {
        counter++;
    }

    @After
    public void annotatedTestRunsMultipleTimes() {
        assertThat(counter, is(100));
    }

}
