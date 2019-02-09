package local.bottomline.microservices.dao.microcervicesdao.dao;

import local.bottomline.microservices.entity.Site;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends CrudRepository<Site, Long> {
}
