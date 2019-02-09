package local.bottomline.microservices.gateway;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import local.bottomline.microservices.assembler.PaymentAssembler;
import local.bottomline.microservices.assembler.SiteAssembler;
import local.bottomline.microservices.dto.PaymentDto;
import local.bottomline.microservices.dto.SiteDto;
import local.bottomline.microservices.entity.Payment;
import local.bottomline.microservices.entity.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import local.bottomline.microservices.web.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class GatewayResource {

    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private PaymentAssembler paymentAssembler;

    @Autowired
    private SiteAssembler siteAssembler;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${example.jms.site.listener}")
    private String siteJmsEndpoint;

    @GetMapping("/healthcheck")
    public String healthCheck(){
        return "Gateway OK";
    }


    @GetMapping("/orchestration/payment/{paymentId}")
    public PaymentDto getPaymentOrchestrated(@PathVariable Long paymentId) {
        Payment payment = getPayment(paymentId);
        PaymentDto result = paymentAssembler.toDto(payment);
        if(result == null){
            throw new EntityNotFoundException("Payment Not Found....");
        }

        Future<SiteDto> disburserSiteFuture = getSiteAsync(payment.getDisburserSiteId());
        Future<SiteDto> collectorSiteFuture = getSiteAsync(payment.getCollectorSiteId());

        try {
            SiteDto disburserSite = disburserSiteFuture.get();
            SiteDto collectorSite = collectorSiteFuture.get();
            result.setDisburserSite(disburserSite);
            result.setCollectorSite(collectorSite);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/orchestration-jms/payment/{paymentId}")
    public PaymentDto getPaymentOrchestratedJms(@PathVariable Long paymentId) {
        Payment payment = getPayment(paymentId);
        PaymentDto result = paymentAssembler.toDto(payment);
        if(result == null){
            throw new EntityNotFoundException("Payment Not Found....");
        }

        SiteDto requestCollectorSiteDto = new SiteDto();
        requestCollectorSiteDto.setId(payment.getCollectorSiteId());
        jmsTemplate.convertAndSend("siteJmsListener", requestCollectorSiteDto);

        SiteDto requestDisburserSiteDto = new SiteDto();
        requestDisburserSiteDto.setId(payment.getDisburserSiteId());
        jmsTemplate.convertAndSend("siteJmsListener", requestDisburserSiteDto);

        return result;
    }

    @GetMapping("/payment/{paymentId}")
    public Payment getPayment(@PathVariable Long paymentId) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("payment", paymentId);

        String paymentEndpointUrl = serviceUrlByApplicationName("payment-endpoint");
        ResponseEntity<Payment> responseEntity = new RestTemplate().getForEntity(paymentEndpointUrl + "payment/" + paymentId, Payment.class);
        Payment payment = responseEntity.getBody();
        return payment;
    }

    @GetMapping("/site/{siteId}")
    public SiteDto getSite(@PathVariable Long siteId) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("site", siteId);

        String siteEndpointUrl = serviceUrlByApplicationName("site-endpoint");
        ResponseEntity<Site> responseEntity = new RestTemplate().getForEntity(siteEndpointUrl + "site/" + siteId, Site.class);
        Site site = responseEntity.getBody();
        return siteAssembler.toDto(site);
    }

    public Future<SiteDto> getSiteAsync(Long siteId){

        String siteEndpointUrl = serviceUrlByApplicationName("site-endpoint");
        Client client = ClientBuilder.newBuilder().build();
        WebTarget webTarget = client.target(siteEndpointUrl + "/site/" + siteId);
        Invocation.Builder request = webTarget.request();
        AsyncInvoker asyncInvoker = request.async();
        Future<SiteDto> futureResp = asyncInvoker.get(SiteDto.class);
        return futureResp;
    }

    @ExceptionHandler(Exception.class)
    public void handleExceptions(Exception exception, HttpServletResponse response){
        response.setStatus(HttpStatus.NOT_FOUND.value());
        try {
            response.getWriter().append(exception.getMessage());
        } catch (IOException e) {
            //ignore (log?)
        }
    }


    private String serviceUrlByApplicationName(String applicationName) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(applicationName, false);
        return instance.getHomePageUrl();
    }
}
