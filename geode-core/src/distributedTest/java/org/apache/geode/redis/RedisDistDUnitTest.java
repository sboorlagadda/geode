/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.redis;

import static org.apache.geode.distributed.ConfigurationProperties.LOG_LEVEL;
import static org.apache.geode.distributed.ConfigurationProperties.REDIS_BIND_ADDRESS;
import static org.apache.geode.distributed.ConfigurationProperties.REDIS_PORT;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import redis.clients.jedis.Jedis;

import org.apache.geode.internal.AvailablePortHelper;
import org.apache.geode.internal.net.SocketCreator;
import org.apache.geode.test.dunit.AsyncInvocation;
import org.apache.geode.test.dunit.IgnoredException;
import org.apache.geode.test.dunit.LogWriterUtils;
import org.apache.geode.test.dunit.rules.ClientVM;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.junit.categories.RedisTest;

@Category({RedisTest.class})
public class RedisDistDUnitTest implements Serializable {

  public static final String TEST_KEY = "key";

  private MemberVM locator;
  private MemberVM server1;
  private MemberVM server2;
  private ClientVM client1;
  private ClientVM client2;

  private int server1Port;
  private int server2Port;
  private String localHost;

  private static final int JEDIS_TIMEOUT = 20 * 1000;

  @Rule
  public ClusterStartupRule clusterStartupRule = new ClusterStartupRule();

  private static final Random r = new Random();

  @Before
  public void setup() throws Exception {
    localHost = SocketCreator.getLocalHost().getHostName();
    final int[] ports = AvailablePortHelper.getRandomAvailableTCPPorts(2);

    locator = clusterStartupRule.startLocatorVM(0);

    server1Port = ports[0];
    server2Port = ports[1];

    Properties serverProps = new Properties();
    serverProps.setProperty(LOG_LEVEL, LogWriterUtils.getDUnitLogLevel());
    serverProps.setProperty(REDIS_BIND_ADDRESS, localHost);

    serverProps.setProperty(REDIS_PORT, "" + server1Port);
    server1 = clusterStartupRule.startServerVM(1, serverProps, locator.getPort());
    serverProps.setProperty(REDIS_PORT, "" + server2Port);
    server2 = clusterStartupRule.startServerVM(2, serverProps, locator.getPort());

    client1 = clusterStartupRule.startClientVM(3, false, server1.getPort(), server2.getPort());
    client2 = clusterStartupRule.startClientVM(3, false, server1.getPort(), server2.getPort());
  }

  @Test
  public void testConcurrentCreatesSucceeds()
      throws InterruptedException, ExecutionException, TimeoutException {
    final Jedis jedis1 = new Jedis(localHost, server1Port, JEDIS_TIMEOUT);
    final Jedis jedis2 = new Jedis(localHost, server2Port, JEDIS_TIMEOUT);
    final long pushes = 20;

    AsyncInvocation asyncInvocation =
        client1.invokeAsync(() -> concurrentCreates(pushes, server2Port));
    client2.invoke(() -> concurrentCreates(pushes, server1Port));
    asyncInvocation.await(5, TimeUnit.SECONDS);

    long result1 = jedis1.llen(TEST_KEY);
    long result2 = jedis2.llen(TEST_KEY);
    assertThat(2 * pushes).isEqualTo(result1);
    assertThat(result1).isEqualTo(result2);
  }

  private void concurrentCreates(long pushes, int port) {
    Jedis jedis = new Jedis(localHost, port, JEDIS_TIMEOUT);
    for (int i = 0; i < pushes; i++) {
      if (r.nextBoolean()) {
        jedis.lpush(TEST_KEY, randString());
      } else {
        jedis.rpush(TEST_KEY, randString());
      }
    }
  }

  @Test
  public void testConcurrentCreatesAndDestroysSucceed()
      throws InterruptedException, ExecutionException, TimeoutException {
    IgnoredException.addIgnoredException("RegionDestroyedException");
    IgnoredException.addIgnoredException("IndexInvalidException");
    // Expect to run with no exception
    AsyncInvocation asyncInvocation =
        client1.invokeAsync(() -> concurrentCreatesAndDestroys(40, server1Port));
    client2.invoke(() -> concurrentCreatesAndDestroys(40, server2Port));
    asyncInvocation.await(10, TimeUnit.SECONDS);
  }

  private void concurrentCreatesAndDestroys(int ops, int port) {
    final String hKey = TEST_KEY + "hash";
    final String lKey = TEST_KEY + "list";
    final String zKey = TEST_KEY + "zset";
    final String sKey = TEST_KEY + "set";
    Jedis jedis = new Jedis(localHost, port, JEDIS_TIMEOUT);
    for (int i = 0; i < ops; i++) {
      int n = r.nextInt(4);
      if (n == 0) {
        if (r.nextBoolean()) {
          jedis.hset(hKey, randString(), randString());
        } else {
          jedis.del(hKey);
        }
      } else if (n == 1) {
        if (r.nextBoolean()) {
          jedis.lpush(lKey, randString());
        } else {
          jedis.del(lKey);
        }
      } else if (n == 2) {
        if (r.nextBoolean()) {
          jedis.zadd(zKey, r.nextDouble(), randString());
        } else {
          jedis.del(zKey);
        }
      } else {
        if (r.nextBoolean()) {
          jedis.sadd(sKey, randString());
        } else {
          jedis.del(sKey);
        }
      }
    }
  }

  /**
   * Just make sure there are no unexpected server crashes
   */
  @Test
  public void testConcurrentOperationsSucceed()
      throws InterruptedException, ExecutionException, TimeoutException {
    // Expect to run with no exception
    AsyncInvocation asyncInvocation =
        client1.invokeAsync(() -> concurrentOperations(100, server1Port));
    client2.invoke(() -> concurrentOperations(100, server2Port));
    asyncInvocation.await(5, TimeUnit.SECONDS);
  }

  private void concurrentOperations(int ops, int port) {
    final String hKey = TEST_KEY + "hash";
    final String lKey = TEST_KEY + "list";
    final String zKey = TEST_KEY + "zset";
    final String sKey = TEST_KEY + "set";
    Jedis jedis = new Jedis(localHost, port, JEDIS_TIMEOUT);
    Random r = new Random();
    for (int i = 0; i < ops; i++) {
      int n = r.nextInt(4);
      if (n == 0) {
        jedis.hset(hKey, randString(), randString());
        jedis.hgetAll(hKey);
        jedis.hvals(hKey);
      } else if (n == 1) {
        jedis.lpush(lKey, randString());
        jedis.rpush(lKey, randString());
        jedis.ltrim(lKey, 0, 100);
        jedis.lrange(lKey, 0, -1);
      } else if (n == 2) {
        jedis.zadd(zKey, r.nextDouble(), randString());
        jedis.zrangeByLex(zKey, "(a", "[z");
        jedis.zrangeByScoreWithScores(zKey, 0, 1, 0, 100);
        jedis.zremrangeByScore(zKey, r.nextDouble(), r.nextDouble());
      } else {
        jedis.sadd(sKey, randString());
        jedis.smembers(sKey);
        jedis.sdiff(sKey, "afd");
        jedis.sunionstore("dst", sKey, "afds");
      }
    }
  }

  private String randString() {
    return Long.toHexString(Double.doubleToLongBits(Math.random()));
  }

}
