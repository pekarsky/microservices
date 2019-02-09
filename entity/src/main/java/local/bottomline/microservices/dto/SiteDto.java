package local.bottomline.microservices.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteDto extends PaymodeDto{
    private Long id;
    private String typeCode;
    private String descriptor;
}
