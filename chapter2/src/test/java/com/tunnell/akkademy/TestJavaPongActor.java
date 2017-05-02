package com.tunnell.akkademy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

import java.util.concurrent.*;

/**
 * Actor request/response testing
 * Created by TunnellZhao on 2017/4/30.
 */
public class TestJavaPongActor {

    private ActorRef actorRef;
    private ActorSystem actorSystem;

    @Before
    public void prepare() {
        actorSystem = ActorSystem.create("actor-sys-01");

        actorRef = actorSystem.actorOf(
                JavaPongActor.props("Instantiation test."),
                JavaPongActor.class.getSimpleName());
    }

    @Test
    public void testJavaPongActor() throws InterruptedException {

        actorRef.tell("Ping", ActorRef.noSender());

        actorRef.tell("Err msg request.", ActorRef.noSender());

        Thread.sleep(100);
    }

    @Test
    public void shouldReplyToPingWithPong() throws Exception {

        final CompletionStage<Object> cs = askPong("Ping");
        final CompletableFuture<Object> jFuture =
                (CompletableFuture<Object>) cs;

        final Object result = jFuture.get(1000, TimeUnit.MILLISECONDS);

        actorSystem.log().info("Get response [{}].", result);

        Assert.assertEquals(result, "Pong");
    }

    @Test(expected = ExecutionException.class)
    public void shouldReplyToUnknownMessageWithFailure() throws Exception {

        final CompletionStage<Object> cs = askPong("unknown");
        final CompletableFuture<Object> jFuture =
                (CompletableFuture<Object>) cs;

        try {
            jFuture.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            actorSystem.log().info("Get stack trace: \n{}\n.", stackTrace);
            throw e;
        }
    }

    @Test
    public void testFuture() throws InterruptedException {
        final CompletionStage<Object> cs = askPong("unknown");

        cs.whenComplete((ret, ex) -> {
            actorSystem.log().info("stage-1");
            if (ret != null)
                actorSystem.log().info("Get result ==> {}.", ret);
            if (ex != null)
                actorSystem.log().error(ex, "Failed to communicate with actor. Due to: ", ex);
        });

//        cs.whenCompleteAsync((ret, t) -> {
//            actorSystem.log().info("stage-1");
//            if (ret != null)
//                actorSystem.log().info("Get result ==> {}.", ret);
//
//            if (t != null)
//                actorSystem.log().error(t, "Failed to communicate with actor. Due to: ", t);
//        });

        Thread.sleep(100);
    }

    @Test
    public void printToConsole() throws Exception {
        askPong("Ping").
                thenAccept(x -> actorSystem.log().info("Replied with: {}", x));
        Thread.sleep(100);
    }

    @Test
    public void testComposeFuture() throws InterruptedException {
        askPong("unknown msg")
                .handle((ret, ex) -> ex == null ?
                        CompletableFuture.completedFuture(ret) :
                        askPong("Ping"))
                .thenCompose(this::toCompletableFuture)
                .handle((ret, ex) -> {
                    if (ret != null)
                        actorSystem.log().info("Get result ==> {}.", ret);
                    if (ex != null)
                        actorSystem.log().error(ex, "Failed to communicate with actor. Due to: ", ex);
                    return null;
                });

        Thread.sleep(100);
    }

    private <T> CompletableFuture<T> toCompletableFuture(CompletionStage<T> stage) {
//        CompletableFuture<T> f = new CompletableFuture<>();
//        stage.handle((T t, Throwable ex) -> {
//            if (ex != null) f.completeExceptionally(ex);
//            else f.complete(t);
//            return null;
//        });
        return stage.toCompletableFuture();
    }

    private CompletionStage<Object> askPong(String message) {
        final Future<Object> sFuture = Patterns.ask(actorRef, message, 1000);

        return FutureConverters.toJava(sFuture);
    }
}
