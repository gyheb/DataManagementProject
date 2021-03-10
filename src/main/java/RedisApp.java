import redis.clients.jedis.Jedis;

public class RedisApp {

    public static void main(String[] args) {

        RedisStore rs = new RedisStore();
        new Thread(rs).start();
    }
}
