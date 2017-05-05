package com.tunnell.akkademy;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.event.LoggingAdapter;
import com.tunnell.akkademy.messages.GetRequest;
import com.tunnell.akkademy.messages.SetRequest;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.CompletionStage;

import static scala.compat.java8.FutureConverters.toJava;
import static akka.pattern.Patterns.ask;

/**
 * Created by TunnellZhao on 2017/5/5.
 *
 * Akkademy-db client
 */
public class AkkademyDbClient {

    private final ActorSelection remoteDb;

    private final LoggingAdapter log;

    private static final String ADDRESS_FORMATTER = "akka.tcp://akkademy@%s:%s/user/akkademy-db";

    private static final long TIMEOUT_MILLS = 2000L;

    public AkkademyDbClient(String host, int port) {
        ActorSystem system = ActorSystem.create("LocalSystem", ConfigFactory.load("akkademy-db-client"));

        log = system.log();

        remoteDb = system.actorSelection(
                buildRemoteActorAddress(host, port));
    }

    public CompletionStage<Object> set(String key, Object value) {
        return toJava(ask(
                remoteDb,
                new SetRequest(key, value),
                TIMEOUT_MILLS));
    }

    public CompletionStage<Object> get(String key) {
        return toJava(ask(
                remoteDb,
                new GetRequest(key),
                TIMEOUT_MILLS));
    }

    public CompletionStage<Object> execute(Object command) {
        return toJava(ask(
                remoteDb,
                command,
                TIMEOUT_MILLS));
    }

    private String buildRemoteActorAddress(String host, int port) {
        return String.format(ADDRESS_FORMATTER, host, port);
    }

    public LoggingAdapter log() {
        return log;
    }
}
