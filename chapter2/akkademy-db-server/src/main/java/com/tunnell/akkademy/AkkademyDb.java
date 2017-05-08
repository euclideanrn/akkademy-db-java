package com.tunnell.akkademy;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

/**
 * Created by TunnellZhao on 2017/5/5.
 * <br/><br/>
 * Main access, startup an akkademy-db-server on akka.tcp://akkademy@[host]:[port]
 * <br/><br/>
 * Host and port here is defined by configuration file "akkademy-db-server.conf" in classpath
 */
public class AkkademyDb {

    private static final String CONF_RESOURCE_BASE_NAME = "akkademy-db-server";

    private final ActorSystem system;

    public AkkademyDb() {
        system = ActorSystem.create("akkademy",
                ConfigFactory.load(CONF_RESOURCE_BASE_NAME));

        system.actorOf(Props.create(AkkademyDbActor.class), "akkademy-db");
    }

    public void shutdown() {
        if (system != null) {
            system.shutdown();
        }
    }
}
