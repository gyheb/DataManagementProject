import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class Producer {

    public Producer() throws FileNotFoundException {

        // location for the kafka server
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.6.128:9092");

        /*
         * since we cant send object directly on the network so we need to serialize to send it to the broker
         * and deserialize when the consumer ask for data
         * */
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        /*
         * now we're making the actual kafka producer and we giving all the
         * details such as properties and type of object
         * */
        KafkaProducer<String, Event> producer = new KafkaProducer<String, Event>(properties);

        // using a yaml var
        Yaml yaml = new Yaml();
        // path to the keys.yaml file
        String path = "src/main/resources/keys.yaml";
        // since its a key, value we need a Map. then we insert the file into map
        Map<String, Integer> map = yaml.load(new FileInputStream(new File(path)));

        // setting a variables with the values needed
        int producerEndLoopNumber = Integer.parseInt(String.valueOf(map.get("proEndLoopNum")));
        int producerStartLoopNumber = Integer.parseInt(String.valueOf(map.get("proStartLoopNum")));
        int metricValueRanNum = Integer.parseInt(String.valueOf(map.get("metricValueRanNum")));
        int proSleepTime = Integer.parseInt(String.valueOf(map.get("proSleepTime")));


        for (int i = producerStartLoopNumber; i < producerEndLoopNumber; i++) {

            Event event = new Event(
                i,
                new Date(),
                i,
                (int) (Math.random() * metricValueRanNum),
                "Random event"
            );

            // we have to make a producer record to send data ( topic , key , value)
            ProducerRecord<String, Event> producerRecord =
                    new ProducerRecord<String, Event>("Event-info", event.getMessage(), event);

            // sending data
            producer.send(producerRecord);

            try {
                Thread.sleep(proSleepTime);
            } catch (InterruptedException e) {
                System.out.println("sleep time error: " + e.getMessage());;
            }
        }
        // for when u finish
        producer.close();
    }

}
