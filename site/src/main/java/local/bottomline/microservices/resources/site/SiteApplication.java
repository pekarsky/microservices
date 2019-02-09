package local.bottomline.microservices.resources.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "local.bottomline.microservices")
@EntityScan("local.bottomline.microservices.entity")
@EnableJpaRepositories("local.bottomline.microservices.dao.microcervicesdao.dao")
@EnableEurekaClient
public class SiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiteApplication.class, args);
    }
}
