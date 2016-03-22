/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.trustedanalytics.examples.gearpump;


import io.gearpump.Message;
import io.gearpump.cluster.UserConfig;
import io.gearpump.streaming.javaapi.Task;
import io.gearpump.streaming.task.StartTime;
import io.gearpump.streaming.task.TaskContext;
import org.slf4j.Logger;

import java.util.HashMap;

public class SumTask extends Task {

    private Logger LOG = super.LOG();
    private HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

    public SumTask(TaskContext taskContext, UserConfig userConf) {
        super(taskContext, userConf);
    }

    @Override
    public void onStart(StartTime startTime) {
    }

    @Override
    public void onNext(Message messagePayLoad) {
        String word = (String) (messagePayLoad.msg());
        Integer current = wordCount.get(word);
        if (current == null) {
            current = 0;
        }
        Integer newCount = current + 1;
        wordCount.put(word, newCount);
    }
}