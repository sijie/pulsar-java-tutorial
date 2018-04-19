package tutorial.kafka;

class Utils {
    static String getTopicName(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("You must specify a Pulsar topic as the first argument");
        }

        return args[0];
    }
}
