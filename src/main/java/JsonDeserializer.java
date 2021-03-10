import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import java.io.IOException;
import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<Event> {
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public Event deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        Event event= null;
        try {
            event =  objectMapper.readValue(bytes, Event.class);
        } catch (IOException e) {
            System.out.println("Deserializing error: " + e.getMessage());
        }

        return event;

    }


    @Override
    public void close() {

    }
}
