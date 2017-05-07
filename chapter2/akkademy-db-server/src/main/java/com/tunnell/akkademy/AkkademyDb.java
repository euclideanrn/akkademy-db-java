package com.tunnell.akkademy;

import akka.actor.AbstractActor;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.tunnell.akkademy.messages.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by TunnellZhao on 2017/4/28.
 * <br/><br/><p>
 * AkkademyDb, first akka actor, simply store the message in memory, and send response to the caller.
 * <br/><br/>
 * Supported commands:
 * <li/>{@link SetRequest}
 * <li/>{@link GetRequest}
 * <br/>support key query, or regex range query<br/>
 * <li/>{@link DeleteRequest}
 */
class AkkademyDb extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final Map<String, Object> map = new HashMap<>();

    private AkkademyDb() {
        receive(ReceiveBuilder

                /**
                 * If command {@link SetRequest} matches, then put the message in memory
                 *
                 * Return {@link SingleResponse}
                 */
                .match(SetRequest.class, message -> {
                    log.debug("Received [Set] request: {}", message);

                    String key = message.getKey();
                    if (message.setIfNotExists()) {
                        map.putIfAbsent(key, message.getValue());
                    } else {
                        map.put(message.getKey(), message.getValue());
                    }

                    SingleResponse response;
                    if (message.setAndGet()) {
                        response = new SingleResponse(message.getKey(), map.get(key));
                    } else {
                        response = new SingleResponse();
                    }

                    tellSuccess(response);
                })

                /**
                 * If command {@link GetRequest} matches, then get value by key, and do response to client
                 *
                 * Return {@link SingleResponse} if just get value by key
                 * Return {@link BatchResponse} if get value by regex key
                 */
                .match(GetRequest.class, message -> {
                    log.debug("Received [Get] request: {}", message);

                    Map<String, Object> results;
                    if (message.isRegex()) {
                        results = map.keySet().stream()
                                .filter(key -> message.getPattern().matcher(key).matches())
                                .collect(Collectors.toMap(Function.identity(), map::get));

                        tellSuccess(new BatchResponse(results));
                    } else {
                        tellSuccess(new SingleResponse(message.getKey(), map.get(message.getKey())));
                    }
                })

                /**
                 * If command {@link DeleteRequest} matches, then delete value by the given key
                 *
                 * Return {@link SingleResponse} if just get value by key
                 */
                .match(DeleteRequest.class, message -> {
                    log.debug("Received [Delete] request: {}", message);

                    String key = message.getKey();
                    Object val = message.deleteAndGet()
                            ? map.get(key)
                            : null;

                    map.remove(key);
                    tellSuccess(new SingleResponse(
                            message.getKey(), val
                    ));
                })

                /**
                 * If command does not match, then return {@link UnsupportedCommandException} to client
                 */
                .matchAny(o -> {
                    log.warning("Received unknown type message: {}", o);
                    tellFailure(new UnsupportedCommandException());
                })

                .build()
        );
    }

    private void tellSuccess(Object message) {
        sender().tell(new Status.Success(message), self());
    }

    private void tellFailure(Throwable throwable) {
        sender().tell(new Status.Failure(throwable), self());
    }
}
