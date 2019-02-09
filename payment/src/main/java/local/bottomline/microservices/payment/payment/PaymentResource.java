package local.bottomline.microservices.payment.payment;

import local.bottomline.microservices.entity.Payment;
import local.bottomline.microservices.dao.microcervicesdao.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentResource {
    private PaymentService paymentService;

    @Autowired
    public PaymentResource(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/healthcheck")
    public String healthCheck(){
        return "Ok";
    }

    @GetMapping("/payment/{id}")
    public Payment getById(@PathVariable Long id){
        return paymentService.findById(id);
    }


}
