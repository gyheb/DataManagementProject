import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.Jedis;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RedisStore implements Runnable {


    Jedis jedis = new Jedis("127.0.0.1", 6379);
    private boolean quit;


        @Override
        public void run () {
            while (!quit) {

                // printing + checking ping to redis server
                System.out.println("Connected to Redis server, " +
                        "Server ping: " + jedis.ping());

                // connecting to a specific server
                MongoClient mongoClient = new MongoClient("mongodb://localhost:27017");

                // connecting a specific database and a specific collection
                MongoDatabase db = mongoClient.getDatabase("EventDB");
                MongoCollection<Document> events = db.getCollection("events");
                MongoCollection<Document> time = db.getCollection("time");
                System.out.println("Connected to mongoDB server, "
                        + "Transfering data to Redis . . .");


                //  Event variable
                Event event = new Event();

                // checking if time collection has an object
                if (!(time.countDocuments() == 0)) {
                    Document found = time.find().first();

                    if (found != null) {
                        Date lastDateStored = (Date) found.get("Time");

                        /*
                         * using cursor to run on every object in "Events" collection
                         * that's greater than the last date stored
                         * */
                        MongoCursor<Document> cursor = events.find(new Document()
                                .append("timestamp", new Document()
                                        .append("$gt", lastDateStored)))
                                .projection(Projections.excludeId()).iterator();

                        // initiating object mapper to turn string into a Json Object
                        ObjectMapper objectMapper = new ObjectMapper();

                        // using a String format to represent the time and date more effectively
                        SimpleDateFormat date = new SimpleDateFormat("MM/dd/Y:hh:mm:ss");


                        // running on each object in the collection
                        while (cursor.hasNext()) {

                            try {
                                // inserting the current object string into eventJson
                                Document eventJs = cursor.next();

                                // converting the document to a readable Json
                                String eventJson = objectMapper.writeValueAsString(eventJs);

                                // using the read value to read the string as Json-Object
                                event = objectMapper.readValue(eventJson, Event.class);

                                // adding the key and value to redis
                                jedis.set(event.getReporterId() + ":"
                                        + date.format(event.getTimestamp()), eventJson);

                                // print the doc that's added to redis
                                System.out.println("Copied: " + event.getReporterId() + ":"
                                        + date.format(event.getTimestamp()));


                            } catch (JsonProcessingException e) {
                                System.out.println("while loop error: " + e.getMessage());
                                ;
                            }
                        }

                        // storing the current last date in mongoDB
                        if(!(event.getTimestamp() == null)) {
                            Bson updatedTime = new Document("Time", event.getTimestamp());
                            Bson updateOperation = new Document("$set", updatedTime);
                            time.updateOne(found, updateOperation);
                        }


                        // allow us to control the sleep time via ymal file
                        try {
                            Yaml yaml = new Yaml();
                            String path = "src/main/resources/keys.yaml";
                            Map<String, Integer> map = yaml.load(new FileInputStream(new File(path)));
                            Integer sleepTime = Integer.parseInt(String.valueOf(map.get("redisSleepTime")));
                            Thread.sleep(sleepTime); //yaml
                        } catch (InterruptedException | FileNotFoundException e) {
                            System.out.println("sleep time error: " + e.getMessage());
                        }
                    }
                }
            }
        }

        // stop method --not in use--
        public void stop(){
            quit = true;
        }

}
