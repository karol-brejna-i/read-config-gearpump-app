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

public class SplitTask extends Task {

    public static String TEXT = "This is a good start for java! bingo! bingo! ";

    public SplitTask(TaskContext taskContext, UserConfig userConf) {
        super(taskContext, userConf);
        System.out.println("grep:::::taskContext.system().settings().config(): " + taskContext.system().settings().config());
        System.out.println("grep:::::userConf: " + userConf);
        System.out.println("grep:::::userConf.getValue(root): " + userConf.getString("fistKey"));

    }

    private Long now() {
        return System.currentTimeMillis();
    }

    @Override
    public void onStart(StartTime startTime) {
        System.out.println("grep:::::userConf: " + userConf);
        System.out.println("grep:::::userConf.getString(fistKey): " + userConf.getString("fistKey"));
        self().tell(new Message("start", now()), self());
    }

    @Override
    public void onNext(Message msg) {
        String[] words = TEXT.split(" ");
        for (int i = 0; i < words.length; i++) {
            context.output(new Message(words[i], now()));
        }
        self().tell(new Message("next", now()), self());
    }
}