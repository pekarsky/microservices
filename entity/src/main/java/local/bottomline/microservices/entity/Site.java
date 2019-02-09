package local.bottomline.microservices.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SITE", schema = "CLAR")
@Getter
@Setter
public class Site extends PaymodeEntity{

    @Id
    @Column(name = "SITE_ID", unique = true, nullable = false, precision = 20)
    private Long id;

    @Column(name = "TYPE_CODE", nullable = false, length = 1)
    public String typeCode;

    @Column(name = "DESCRIPTOR")
    public String descriptor;
}
