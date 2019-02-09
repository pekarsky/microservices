package local.bottomline.microservices.dao.microcervicesdao.service;

import local.bottomline.microservices.dao.microcervicesdao.dao.SiteRepository;
import local.bottomline.microservices.entity.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class SiteService {
    private SiteRepository siteRepository;

    @Autowired
    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public Site findById(Long id){
        return siteRepository.findById(id).orElse(null);
    }
}
