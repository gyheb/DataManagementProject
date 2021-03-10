import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;
import java.util.*;

public class Consumer {

    public Consumer() throws JsonProcessingException {
        try {
//            String url = "mongodb+srv://user123:Aa741852963%21@cluster0.on6v6.mongodb.net/" +
//                    "test?authSource=admin&replicaSet=atlas-wwxg6m-shard-0&readPreference=primary&appname=" +
//                    "MongoDB%20Compass&ssl=true";

            MongoClient mongoClient = new MongoClient("mongodb://localhost:27017");

            // connecting a specific database and a specific collection
            MongoDatabase db = mongoClient.getDatabase("EventDB");
            System.out.println("Connected to Database");
            MongoCollection events = db.getCollection("events");
            System.out.println("Server is ready");

            // location for the kafka server
            Properties properties = new Properties();
            properties.put("bootstrap.servers", "192.168.6.128:9092");
            properties.put("group.id", "Event-info");

            // using auto commit to auto commit offsets ( even though its default)
            properties.put("enable.auto.commit", "true");
            // will auto commit the offsets according to the time
            properties.put("auto.commit.interval.ms", "1000");
            // if the offsets config resets or empty, it will start from the earliest offset
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            // inputs and outputs
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

            // consumer object
            KafkaConsumer<String, Event> consumer = new KafkaConsumer<String, Event>(properties);

            // subscribe to a topic
            consumer.subscribe(Arrays.asList("Event-info"));

            // ObjectMapper init
            ObjectMapper objectMapper = new ObjectMapper();

            // running on topic array
            while (true) {
                ConsumerRecords<String, Event> records = consumer.poll(100);
                for (ConsumerRecord<String, Event> record : records) {


                      /*
                      * another way to deserialize an object (StringDeserializer.class)
                      * 1. getting the object string
                      * 2. reading the string with objectMapper into an object
                      * 3. adding all object values to a doc file
                      * */
//                    String jsonString = String.valueOf(record.value());
//                    Event event = objectMapper.readValue(jsonString, Event.class);
//                    Document doc = new Document();
//                    doc.put("reporterId", event.getReporterId());
//                    doc.put("timestamp", event.getTimestamp());
//                    doc.put("metricId", event.getMetricId());
//                    doc.put("metricValue", event.getMetricValue());
//                    doc.put("message", event.getMessage());

                    Document doc = new Document();
                    doc.put("reporterId", record.value().getReporterId());
                    doc.put("timestamp", record.value().getTimestamp());
                    doc.put("metricId", record.value().getMetricId());
                    doc.put("metricValue", record.value().getMetricValue());
                    doc.put("message", record.value().getMessage());

                    // inserting the doc to mongoDB
                    events.insertOne(doc);
                    // print the record to terminal
                    System.out.println("new record added to mongo: " + doc);

                }
            }

        } catch (Exception e) {
            System.out.println("An error has occurred: " + e.getMessage());

        }

    }

}
