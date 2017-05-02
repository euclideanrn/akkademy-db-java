package com.tunnell.akkademy;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

/**
 * Created by TunnellZhao on 2017/4/30.
 */
public class JavaPongActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private JavaPongActor(String message) {
        log.info("Create an instance of JavaPongActor. With message: {}.", message);
    }

    public static Props props(String message) {
        return Props.create(JavaPongActor.class, message);
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
//                .match(String.class,
//                        msg -> java.util.Objects.equals(msg, "Ping"),
//                        msg -> {
//                            logMessage(msg);
//                            sender().tell("Pong", ActorRef.noSender());
//                        })

                .matchEquals("Ping", msg -> {
                    logMessage(msg);
                    sender().tell("Pong", ActorRef.noSender());
                })

                .matchAny(msg -> {
                    logMessage(msg);
                    sender().tell(new Status.Failure(getFailureMessage(msg)), self());
                })

                .build();
    }

    private void logMessage(Object x) {
        log.info("Get a message [{}].", x);
    }

    private Exception getFailureMessage(Object x) {
        return new Exception(String.format("Expected \"Ping\", received unknown message \"%s\"", x));
    }
}
