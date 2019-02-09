package local.bottomline.microservices.assembler;

import local.bottomline.microservices.dto.PaymentDto;
import local.bottomline.microservices.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentAssembler extends PaymodeAssembler<Payment, PaymentDto>{
}
