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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import io.gearpump.cluster.ClusterConfig;
import io.gearpump.cluster.UserConfig;
import io.gearpump.cluster.client.ClientContext;
import io.gearpump.partitioner.HashPartitioner;
import io.gearpump.partitioner.Partitioner;
import io.gearpump.streaming.javaapi.Graph;
import io.gearpump.streaming.javaapi.Processor;
import io.gearpump.streaming.javaapi.StreamApplication;

public class App {
    public static void main(String[] args) {
        main(ClusterConfig.defaultConfig(), args);
    }

    public static void main(Config akkaConf, String[] args) {
        System.out.println("grep:::::Command line parameters");
        if (args != null) {
            for (int i=0; i < args.length; i++) {
                System.out.println("grep:::::arg: " + args[i]);
            }
        }

        System.out.println("Render config:");
        System.out.println(akkaConf.root().render(ConfigRenderOptions.concise()));
        System.out.println("=======================");
        System.out.println("grep:::::akkaConf.getValue(\"tap\") = " + akkaConf.getValue("tap"));
        System.out.println("grep:::::akkaConf.getAnyRef(\"tap\").getClass().getName()) = " + akkaConf.getAnyRef("tap").getClass().getName());

        int taskNumber = 1;
        Processor split = new Processor(SplitTask.class).withParallelism(taskNumber);
        Processor sum = new Processor(SumTask.class).withParallelism(taskNumber);

        Graph graph = new Graph();
        graph.addVertex(split);
        graph.addVertex(sum);

        Partitioner partitioner = new HashPartitioner();
        graph.addEdge(split, partitioner, sum);

        UserConfig appConfig = UserConfig.empty().withString("firstKey", akkaConf.getString("tap.key1")).withString("secondKey", akkaConf.getString("tap.key2"));
        System.out.println("grep:::::appConfig.tap.key1" + akkaConf.getString("tap.key1"));
        System.out.println("grep:::::appConfig" + appConfig.getString("firstKey").get());

        StreamApplication app = new StreamApplication("readConfig", appConfig, graph);
        System.out.println("app : " + app);

        ClientContext masterClient = new ClientContext(akkaConf);
        System.out.println("masterClient: " + masterClient);
        int appId = masterClient.submit(app);
        System.out.println("Application Id is " + Integer.toString(appId));

        masterClient.close();
    }
}
