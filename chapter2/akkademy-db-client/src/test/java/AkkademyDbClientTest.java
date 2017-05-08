import com.tunnell.akkademy.AkkademyDb;
import com.tunnell.akkademy.AkkademyDbClient;
import com.tunnell.akkademy.messages.BatchResponse;
import com.tunnell.akkademy.messages.SingleResponse;
import com.tunnell.akkademy.messages.UnsupportedCommandException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by TunnellZhao on 2017/5/5.
 * <p>
 * Unit tests for {@link AkkademyDbClient}
 */
public class AkkademyDbClientTest {

    private AkkademyDbClient client;

    private static final String key1 = "apple";
    private static final String val1 = "pie";
    private static final String newVal = "pie-apple";

    private static final String key2 = "pie-apple-pie";
    private static final String val2 = "pie-apple-pie-apple";

    private AkkademyDb akkademyDb;

    @Before
    public void prepare() {
        akkademyDb = new AkkademyDb();

        client = new AkkademyDbClient("127.0.0.1", 2552);
    }

    @Test
    public void itShouldSetRecordAndGet() throws Exception {
        deleteAndCheck(key1);

        SingleResponse setResponse = client.setAndGet(key1, val1).toCompletableFuture().get();
        client.log().info("Receive set response: {}.", setResponse);
        Assert.assertEquals(setResponse.getKey(), key1);
        Assert.assertEquals(setResponse.getValue(), val1);

        getAndCheck(key1, val1);

        deleteAndCheck(key1);
    }

    @Test
    public void itShouldSetRecordOnly() throws Exception {
        deleteAndCheck(key1);

        SingleResponse setResponse = client.set(key1, val1, false, false).toCompletableFuture().get();
        client.log().info("Receive set response: {}.", setResponse);
        Assert.assertNull(setResponse.getKey());
        Assert.assertNull(setResponse.getValue());

        getAndCheck(key1, val1);

        deleteAndCheck(key1);
    }

    @Test
    public void itShouldSetIfNotExistsAndGet() throws Exception {
        deleteAndCheck(key1);

        setAndCheck(key1, val1);

        SingleResponse setResponse = client.set(key1, newVal, true, true).toCompletableFuture().get();
        client.log().info("Receive set response: {}.", setResponse);
        Assert.assertEquals(setResponse.getKey(), key1);
        Assert.assertEquals(setResponse.getValue(), val1);

        getAndCheck(key1, val1);

        deleteAndCheck(key1);
    }

    @Test
    public void itShouldSetIfNotExistsOnly() throws Exception {
        deleteAndCheck(key1);

        setAndCheck(key1, val1);

        SingleResponse setResponse = client.set(key1, newVal, true, false).toCompletableFuture().get();
        client.log().info("Receive set response: {}.", setResponse);
        Assert.assertNull(setResponse.getKey());
        Assert.assertNull(setResponse.getValue());

        getAndCheck(key1, val1);

        deleteAndCheck(key1);
    }

    @Test
    public void itShouldDeleteAndGetWithNullValue() throws Throwable {
        deleteAndCheck(key1);

        SingleResponse deleteResponse = client.delete(key1, true).toCompletableFuture().get();
        client.log().info("Receive set response: {}.", deleteResponse);
        Assert.assertEquals(deleteResponse.getKey(), key1);
        Assert.assertEquals(deleteResponse.getValue(), null);
    }

    @Test
    public void itShouldDeleteAndGetWithNotNullValue() throws Throwable {
        deleteAndCheck(key1);

        setAndCheck(key1, val1);

        SingleResponse deleteResponse = client.delete(key1, true).toCompletableFuture().get();
        client.log().info("Receive set response: {}.", deleteResponse);
        Assert.assertEquals(deleteResponse.getKey(), key1);
        Assert.assertEquals(deleteResponse.getValue(), val1);
    }

    @Test
    public void itShouldGetByRegex() throws ExecutionException, InterruptedException {
        deleteAndCheck(key1);
        deleteAndCheck(key2);

        setAndCheck(key1, val1);
        setAndCheck(key2, val2);

        BatchResponse regexGetResponse = client.getByRegex(".*apple.*").toCompletableFuture().get();
        client.log().info("Receive set response : {}.", regexGetResponse.getResults());
        Assert.assertEquals(regexGetResponse.get(key1), val1);
        Assert.assertEquals(regexGetResponse.get(key2), val2);
        Assert.assertArrayEquals(regexGetResponse.getKeys().toArray(), new Object[] {key1, key2});
        Assert.assertArrayEquals(regexGetResponse.getValues().toArray(), new Object[] {val1, val2});
    }

    @Test
    public void itShouldGetKeys() throws ExecutionException, InterruptedException {
        deleteAndCheck(key1);
        deleteAndCheck(key2);

        setAndCheck(key1, val1);
        setAndCheck(key2, val2);

        BatchResponse regexGetResponse = client.getKeys().toCompletableFuture().get();
        client.log().info("Receive set response: {}.", regexGetResponse);
        Assert.assertArrayEquals(regexGetResponse.getKeys().toArray(), new Object[] {key1, key2});
    }

    @Test(expected = UnsupportedCommandException.class)
    public void itShouldRaiseUnsupportedCommandException() throws Throwable {
        try {
            client.execute("").toCompletableFuture().get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    private void deleteAndCheck(String key) throws ExecutionException, InterruptedException {
        SingleResponse deleteResponse = client.delete(key).toCompletableFuture().get();
        Assert.assertEquals(deleteResponse.getKey(), key);
        Assert.assertNull(deleteResponse.getValue());

        SingleResponse getResponse = client.get(key).toCompletableFuture().get();
        Assert.assertEquals(getResponse.getKey(), key);
        Assert.assertNull(getResponse.getValue());
    }

    private void getAndCheck(String key, Object expected) throws ExecutionException, InterruptedException {
        SingleResponse getResponse = client.get(key).toCompletableFuture().get();
        Assert.assertEquals(getResponse.getKey(), key);
        Assert.assertEquals(getResponse.getValue(), expected);
    }

    private void setAndCheck(String key, Object val) throws ExecutionException, InterruptedException {
        SingleResponse setResponse = client.setAndGet(key, val).toCompletableFuture().get();
        Assert.assertEquals(setResponse.getKey(), key);
        Assert.assertEquals(setResponse.getValue(), val);

        getAndCheck(key, val);
    }

    @After
    public void cleanup() throws IOException {
        client.close();
        akkademyDb.shutdown();
    }
}
