import io.dropwizard.setup.Environment;
public class Application extends io.dropwizard.Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    public void run(Configuration configuration, Environment environment) throws Exception {
        // register providers , resources, etc ...
        environment.jersey().register(ApiResource.class);
    }
}
