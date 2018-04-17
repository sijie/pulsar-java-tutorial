package tutorial.schema;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.pulsar.client.api.Schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TweetSchema implements Schema<Tweet> {
    private final Kryo kryo;

    public TweetSchema() {
        this.kryo = new Kryo();
        kryo.register(Tweet.class);
    }

    public Tweet decode(byte[] bytes) {
        Tweet tweet;

        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            Input input = new Input(in);
            tweet = kryo.readObject(input, Tweet.class);
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tweet;
    }

    public byte[] encode(Tweet tweet) {
        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Output output = new Output(out);
            kryo.writeObject(output, tweet);
            bytes = output.toBytes();
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
