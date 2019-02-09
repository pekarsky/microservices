package local.bottomline.microservices.assembler;

import local.bottomline.microservices.dto.PaymodeDto;
import local.bottomline.microservices.entity.PaymodeEntity;

public interface PaymodeAssembler<E extends PaymodeEntity, D extends PaymodeDto> {
    E toEntity(D dto);
    D toDto(E entity);
}
