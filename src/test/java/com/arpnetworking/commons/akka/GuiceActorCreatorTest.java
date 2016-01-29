/**
 * Copyright 2016 Groupon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arpnetworking.commons.akka;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.TestActorRef;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import scala.collection.JavaConversions;

import java.util.Collection;
import java.util.Iterator;

/**
 * Tests for the GuiceActorCreator class.
 *
 * @author Ville Koskela (vkoskela at groupon dot com)
 */
public class GuiceActorCreatorTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatorProps() {
        final Props props = GuiceActorCreator.props(_injector, TestActor.class);
        final Collection<Object> args = JavaConversions.asJavaCollection(props.args());
        Assert.assertEquals(2, args.size());
        final Iterator<Object> argIterator = args.iterator();
        Assert.assertSame(_injector, argIterator.next());
        Assert.assertSame(TestActor.class, argIterator.next());
    }

    @Test
    public void testProduce() {
        final Props props = Props.create(TestActor.class);
        final TestActorRef<TestActor> actorRef = TestActorRef.create(ActorSystem.create(), props);
        final TestActor expectedActor = actorRef.underlyingActor();
        Mockito.doReturn(expectedActor).when(_injector).getInstance(TestActor.class);
        final GuiceActorCreator creator = new GuiceActorCreator(_injector, TestActor.class);
        final Actor actualActor = creator.produce();
        Assert.assertSame(expectedActor, actualActor);
    }

    @Test
    public void testActorClassAccessor() {
        final GuiceActorCreator creator = new GuiceActorCreator(_injector, TestActor.class);
        Assert.assertEquals(TestActor.class, creator.actorClass());
    }

    @Test
    public void testToString() {
        final String creatorAsString = new GuiceActorCreator(_injector, TestActor.class).toString();
        Assert.assertNotNull(creatorAsString);
        Assert.assertFalse(creatorAsString.isEmpty());
    }

    @Mock
    private Injector _injector;

    private static class TestActor extends UntypedActor {

        @Override
        public void onReceive(final Object message) throws Exception {
            // Do nothing.
        }
    }
}
