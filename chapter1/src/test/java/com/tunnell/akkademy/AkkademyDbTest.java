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
 *
 * AkkademyDbTest
 */
public class AkkademyDbTest {


    @Test
    public void itShouldPlaceKeyValueFromSetMessageIntoMap() {
        ActorSystem system = ActorSystem.create();

        TestActorRef<AkkademyDb> actorRef =
                TestActorRef.create(system, Props.create(AkkademyDb.class));

        actorRef.tell(new SetRequest("key1", "value1"), ActorRef.noSender());
        actorRef.tell(new SetRequest("key2", "value2"), ActorRef.noSender());

        AkkademyDb akkademyDb = actorRef.underlyingActor();
        assertEquals(akkademyDb.map.get("key1"), "value1");
        assertEquals(akkademyDb.map.get("key2"), "value2");
    }
}