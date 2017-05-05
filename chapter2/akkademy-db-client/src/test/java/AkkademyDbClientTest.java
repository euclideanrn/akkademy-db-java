import com.tunnell.akkademy.AkkademyDbClient;
import com.tunnell.akkademy.messages.KeyNotFoundException;
import com.tunnell.akkademy.messages.UnsupportedCommandException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * Created by TunnellZhao on 2017/5/5.
 *
 * Unit tests for {@link AkkademyDbClient}
 */
public class AkkademyDbClientTest {

    private AkkademyDbClient client;

    @Before
    public void prepare() {
        client = new AkkademyDbClient("127.0.0.1", 2552);
    }

    @Test
    public void itShouldSetRecord() throws Exception {
        String key = "apple";
        String val = "pie";

        CompletionStage<Object> setFuture = client.set(key, val);
        client.log().info("Receive set result: {}.", setFuture.toCompletableFuture().get());

        String result = String.valueOf(
                client.get(key).toCompletableFuture().get());

        Assert.assertEquals(result, val);
    }

    @Test(expected = KeyNotFoundException.class)
    public void itShouldRaiseKeyNotFoundException() throws Throwable {
        String key = "pie-apple";

        try {
            client.get(key).toCompletableFuture().get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = UnsupportedCommandException.class)
    public void itShouldRaiseUnsupportedCommandException() throws Throwable {
        String key = "pie-apple-pie";

        try {
            client.execute(key).toCompletableFuture().get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
}
