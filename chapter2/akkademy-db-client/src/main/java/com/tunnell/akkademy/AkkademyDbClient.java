package com.tunnell.akkademy;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.event.LoggingAdapter;
import com.tunnell.akkademy.messages.*;
import com.typesafe.config.ConfigFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static scala.compat.java8.FutureConverters.toJava;
import static akka.pattern.Patterns.ask;

/**
 * Created by TunnellZhao on 2017/5/5.
 * <p>
 * Akkademy-db client
 */
public class AkkademyDbClient implements Closeable {

    private final ActorSelection remoteDb;

    private final LoggingAdapter log;

    private final ActorSystem system;

    private static final String ADDRESS_FORMATTER = "akka.tcp://akkademy@%s:%s/user/akkademy-db";

    private static final long TIMEOUT_MILLS = 2000L;

    public AkkademyDbClient(String host, int port) {
        system = ActorSystem.create("LocalSystem", ConfigFactory.load("akkademy-db-client"));
        remoteDb = system.actorSelection(buildRemoteActorAddress(host, port));
        log = system.log();
    }

    public CompletionStage<SingleResponse> set(String key, Object value, boolean setIfNotExists, boolean setAndGet) {
        return toJava(ask(remoteDb, new SetRequest(key, value, setAndGet, setIfNotExists), TIMEOUT_MILLS))
                .thenApply(SingleResponse.class::cast);
    }

    public CompletionStage<SingleResponse> setAndGet(String key, Object value) {
        return set(key, value, false, true);
    }

    public CompletionStage<SingleResponse> get(String key) {
        return toJava(ask(remoteDb, new GetRequest(key), TIMEOUT_MILLS))
                .thenApply(SingleResponse.class::cast);
    }

    public CompletionStage<BatchResponse> getByRegex(String regex) {
        return toJava(ask(remoteDb, new GetRequest(regex, true), TIMEOUT_MILLS))
                .thenApply(BatchResponse.class::cast);
    }

    public CompletionStage<SingleResponse> delete(String key) {
        return toJava(ask(remoteDb, new DeleteRequest(key, false), TIMEOUT_MILLS))
                .thenApply(SingleResponse.class::cast);
    }

    public CompletionStage<SingleResponse> delete(String key, boolean deleteAndGet) {
        return toJava(ask(remoteDb, new DeleteRequest(key, deleteAndGet), TIMEOUT_MILLS))
                .thenApply(SingleResponse.class::cast);
    }

    public CompletionStage<SingleResponse> deleteAndGet(String key) {
        return toJava(ask(remoteDb, new DeleteRequest(key, true), TIMEOUT_MILLS))
                .thenApply(SingleResponse.class::cast);
    }

    public CompletionStage<Object> execute(Object command) {
        return toJava(ask(remoteDb, command, TIMEOUT_MILLS));
    }

    private String buildRemoteActorAddress(String host, int port) {
        return String.format(ADDRESS_FORMATTER, host, port);
    }

    public LoggingAdapter log() {
        return log;
    }


    @Override
    public void close() throws IOException {
        system.shutdown();
    }
}
