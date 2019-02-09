package local.bottomline.microservices.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDto extends PaymodeDto {
    private Long id;
    private SiteDto disburserSite;
    private SiteDto collectorSite;
}
