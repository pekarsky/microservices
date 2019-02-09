package local.bottomline.microservices.assembler;

import local.bottomline.microservices.dto.SiteDto;
import local.bottomline.microservices.entity.Site;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SiteAssembler extends PaymodeAssembler<Site, SiteDto> {
}
