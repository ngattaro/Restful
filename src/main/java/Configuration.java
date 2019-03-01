public class Configuration extends io.dropwizard.Configuration
{

    private String myConfig;

    public String getMyConfig()
    {
            return myConfig;
    }

    public void setMyConfig(String myConfig)
    {
        this.myConfig = myConfig;
    }
}
