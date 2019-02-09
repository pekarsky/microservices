package local.bottomline.microservices.gateway.entityproxy;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEntityProxy{

    @Autowired
    private EurekaClient discoveryClient;

    protected String serviceUrlByApplicationName(String applicationName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(applicationName, false);
        return instance.getHomePageUrl();
    }
}
