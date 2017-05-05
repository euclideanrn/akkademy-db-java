package com.tunnell.akkademy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.junit.Test;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertEquals;
import static scala.compat.java8.FutureConverters.toJava;

public class TestJavaPongActor2 {
    private ActorSystem system = ActorSystem.create();
    private ActorRef actorRef =
            system.actorOf(JavaPongActor.props("Instantiation test."), "BruceWillis");

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReplyToPingWithPong() throws Exception {
        Future sFuture = ask(actorRef, "Ping", 1000);
        final CompletionStage<String> cs = toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
        assertEquals("Pong", jFuture.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test(expected = ExecutionException.class)
    @SuppressWarnings("unchecked")
    public void shouldReplyToUnknownMessageWithFailure() throws Exception {
        Future sFuture = ask(actorRef, "unknown", 1000);
        final CompletionStage<String> cs = toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
        jFuture.get(1000, TimeUnit.MILLISECONDS);
    }

    //Future Examples
    @Test
    public void shouldPrintToConsole() throws Exception {
        askPong("Ping").thenAccept(x -> System.out.println("replied with: " + x));
        Thread.sleep(100);
        //no assertion - just prints to console. Try to complete a CompletableFuture instead.
    }

    @Test
    public void shouldTransform() throws Exception {
        char result = (char) get(askPong("Ping").thenApply(x -> x.charAt(0)));
        assertEquals('P', result);
    }

    /**
     * There is was a bug with the scala-java8-compat library 0.3.0 - thenCompose throws exception
     * https://github.com/scala/scala-java8-compat/issues/26
     * <p>
     * I confirmed fixed in 0.6.0-SNAPSHOT (10 months later). Just in time for publishing!
     */
    @Test
    public void shouldTransformAsync() throws Exception {
        CompletionStage cs = askPong("Ping").
                thenCompose(x -> askPong("Ping"));
        assertEquals(get(cs), "Pong");
    }

    @Test
    public void shouldEffectOnError() throws Exception {
        askPong("cause error").handle((x, t) -> {
            if (t != null) {
                System.out.println("Error: " + t);
            }
            return null;
        });
    }

    @Test
    public void shouldRecoverOnError() throws Exception {
        CompletionStage<String> cs = askPong("cause error").exceptionally(t -> "default");

        assertEquals("default", get(cs));
    }

    @Test
    public void shouldRecoverOnErrorAsync() throws Exception {
        CompletionStage<String> cf = askPong("cause error")
                .handle((pong, ex) -> ex == null
                        ? CompletableFuture.completedFuture(pong)
                        : askPong("Ping")
                )
//                .thenCompose(this::toCompletableFuture);
                .thenCompose(fn -> fn);
        assertEquals("Pong", get(cf));
    }

    @Test
    public void shouldChainTogetherMultipleOperations() throws Exception {
        CompletionStage<String> cs = askPong("Ping")
//                .thenCompose(x -> toCompletableFuture(askPong("Ping" + x)))
                .thenCompose(x -> askPong("Ping" + x))
                .handle((x, t) -> t != null
                        ? "default"
                        : x);

        assertEquals("default", get(cs));
    }

    @Test
    public void shouldPrintErrorToConsole() throws Exception {
        askPong("cause error").handle((x, t) -> {
            if (t != null) {
                System.out.println("Error: " + t);
            }
            return null;
        });
        Thread.sleep(100);
    }

    //Helpers
    private Object get(CompletionStage cs) throws Exception {
        return cs.toCompletableFuture().get(1000, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    private CompletionStage<String> askPong(String message) {
        Future sFuture = ask(actorRef, message, 1000);
        return toJava(sFuture);
    }

    @SuppressWarnings("unused")
    private <T> CompletableFuture<T> toCompletableFuture(CompletionStage<T> stage) {

        /**
         * BUG fixed since scala-java8-compat version 0.6.0
         * but before this version, we need to convert CS to CF manually by creating a new CF instance.
         * */
//        CompletableFuture<T> f = new CompletableFuture<>();
//        stage.handle((T t, Throwable ex) -> {
//            if (ex != null) f.completeExceptionally(ex);
//            else f.complete(t);
//            return null;
//        });
//        return f;

        /** so now we just call toCF from CS */
        return stage.toCompletableFuture();
    }
}
