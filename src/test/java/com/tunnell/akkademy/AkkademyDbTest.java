package com.tunnell.akkademy;

import static org.junit.Assert.assertEquals;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.tunnell.akkademy.messages.SetRequest;
import org.junit.Test;

/**
 * Created by TunnellZhao on 2017/4/29.
 */
public class AkkademyDbTest {


    @Test
    public void itShouldPlaceKeyValueFromSetMessageIntoMap() {
        ActorSystem system = ActorSystem.create();

        TestActorRef<AkkademyDb> actorRef =
                TestActorRef.create(system, Props.create(AkkademyDb.class));

        actorRef.tell(new SetRequest("key", "value"), ActorRef.noSender());

        AkkademyDb akkademyDb = actorRef.underlyingActor();
        assertEquals(akkademyDb.map.get("key"), "value");
    }
}