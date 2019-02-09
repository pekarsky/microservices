package local.bottomline.microservices.dao.microcervicesdao.service;

import local.bottomline.microservices.dao.microcervicesdao.dao.PaymentRepository;
import local.bottomline.microservices.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment findById(Long id){
        return paymentRepository.findById(id).orElse(null);
    }
}
