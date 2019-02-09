package local.bottomline.microservices.gateway.entityproxy;

import local.bottomline.microservices.entity.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentProxy extends AbstractEntityProxy {

    public Payment getPayment(@PathVariable Long paymentId) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("payment", paymentId);

        String paymentEndpointUrl = serviceUrlByApplicationName("payment-endpoint");
        ResponseEntity<Payment> responseEntity = new RestTemplate().getForEntity(paymentEndpointUrl + "payment/" + paymentId, Payment.class);
        Payment payment = responseEntity.getBody();
        return payment;
    }
}
