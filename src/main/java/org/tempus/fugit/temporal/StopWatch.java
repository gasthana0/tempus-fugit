/*
 * Copyright (c) 2009, Toby Weston
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

package org.tempus.fugit.temporal;

import java.util.Date;

public final class StopWatch {

    private DateFactory dateFactory;

    private Date startDate;
    private Date lastMarkDate;

    public static StopWatch start(DateFactory dateFactory) {
        return new StopWatch(dateFactory);
    }

    private StopWatch(DateFactory dateFactory) {
        this.dateFactory = dateFactory;
        this.startDate = dateFactory.create();
    }

    public Date getStartDate() {
        return startDate;
    }

    public long markAndGetTotalElapsedTime() {
        lastMarkDate = dateFactory.create();
        return getTotalElapsedTime();
    }

    private long getTotalElapsedTime() {
        final long startTime = startDate.getTime();
        final long lastMarkTime = lastMarkDate.getTime();
        assert(lastMarkTime >= startTime);
        return lastMarkTime - startTime;
    }

}