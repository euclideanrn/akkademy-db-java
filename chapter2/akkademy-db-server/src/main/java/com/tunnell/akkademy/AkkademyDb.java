package com.tunnell.akkademy;

import akka.actor.AbstractActor;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.tunnell.akkademy.messages.GetRequest;
import com.tunnell.akkademy.messages.KeyNotFoundException;
import com.tunnell.akkademy.messages.SetRequest;
import com.tunnell.akkademy.messages.UnsupportedCommandException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TunnellZhao on 2017/4/28.
 * <br/><br/><p>
 * AkkademyDb, first akka actor, simply store the message in memory, and send response to the caller.
 * <br/><br/>
 * Supported commands:
 * <li/>{@link SetRequest}
 * <li/>{@link GetRequest}
 */
class AkkademyDb extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final Map<String, Object> map = new HashMap<>();

    private AkkademyDb() {
        receive(ReceiveBuilder

                /**
                 * If command {@link SetRequest} matches, then put the message in memory
                 */
                .match(SetRequest.class, message -> {
                    log.info("Received Set request: {}", message);
                    map.put(message.getKey(), message.getValue());
                    sender().tell(new Status.Success(message.getKey()), self());
                })

                /**
                 * If command {@link GetRequest} matches, then get value by key, and do response to client.
                 * <br/>
                 * Return {@link KeyNotFoundException} if the return value for key is null.
                 */
                .match(GetRequest.class, message -> {
                    log.info("Received Get request: {}", message);
                    Object value = map.get(message.getKey());
                    Object response = (value != null)
                            ? value
                            : new Status.Failure(new KeyNotFoundException(message.getKey()));
                    sender().tell(response, self());
                })

                /**
                 * If command does not match, then return {@link UnsupportedCommandException} to client.
                 */
                .matchAny(o -> sender()
                        .tell(new Status.Failure(new UnsupportedCommandException()), self()))

                .build()
        );
    }
}
