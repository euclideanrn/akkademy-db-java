package com.tunnell.akkademy;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.tunnell.akkademy.messages.SetRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TunnellZhao on 2017/4/28.
 *
 * AkkademyDb, first akka actor, simply store the message in memory, and send response to the caller
 */
class AkkademyDb extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    final Map<String, Object> map = new HashMap<>();

    private AkkademyDb() {
        receive(ReceiveBuilder

                /**
                 * If matches {@link SetRequest}, then store the value by key, and send response to client
                 */
                .match(SetRequest.class, message -> {
                    log.info("Received set request – key: {} value:{} ", message.getKey(), message.getValue());
//                    System.out.printf("Received set request – key: %s value:%s ", message.getKey(), message.getValue());
                    map.put(message.getKey(), message.getValue());
                })

                /**
                 * If no matches, then send a information to client.
                 */
                .matchAny(o -> log.info("Received unknown message {} ", o)).build()
        );
    }
}
