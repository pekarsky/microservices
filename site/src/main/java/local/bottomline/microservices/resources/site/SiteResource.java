package local.bottomline.microservices.resources.site;

import local.bottomline.microservices.entity.Site;
import local.bottomline.microservices.dao.microcervicesdao.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SiteResource {
    private SiteService siteService;

    @Autowired
    public SiteResource(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/healthcheck")
    public String healthCheck(){
        return "Ok";
    }

    @GetMapping("/site/{id}")
    public Site getById(@PathVariable Long id){
        return siteService.findById(id);
    }

}
