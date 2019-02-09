package local.bottomline.microservices.dao.microcervicesdao.dao;

import local.bottomline.microservices.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
