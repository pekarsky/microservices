package local.bottomline.microservices.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PAYMENT", schema = "CLAR")
@Getter
@Setter
public class Payment extends PaymodeEntity{
    @Id
    @Column(name = "PMT_ID", unique = true, nullable = false, precision = 20)
    private Long id;

    @Column(name="DISBUR_SITE_ID")
    private Long disburserSiteId;

    @Column(name="COLLECT_SITE_ID")
    private Long collectorSiteId;

}
