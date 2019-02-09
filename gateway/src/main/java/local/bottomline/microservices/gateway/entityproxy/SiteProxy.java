package local.bottomline.microservices.gateway.entityproxy;

import local.bottomline.microservices.entity.Site;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class SiteProxy extends AbstractEntityProxy{

    public Site getSite(@PathVariable Long siteId) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("site", siteId);

        String siteEndpointUrl = serviceUrlByApplicationName("site-endpoint");
        ResponseEntity<Site> responseEntity = new RestTemplate().getForEntity(siteEndpointUrl + "site/" + siteId, Site.class);
        return responseEntity.getBody();
    }
}
