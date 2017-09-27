package edu.uga.mahpic.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Created by mnural on 8/5/15.
 */

@Configuration
@PropertySource("classpath:config/config.properties")
public class Config {

    @Autowired
    Environment env;

    public Environment getEnv() {
        return env;
    }

    public String getProperty(String key){
        return env.getProperty(key);
    }

    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(env.getProperty(key));
    }
}