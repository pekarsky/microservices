package local.bottomline.microservices.gateway;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.converters.Auto;
import local.bottomline.microservices.assembler.PaymentAssembler;
import local.bottomline.microservices.assembler.SiteAssembler;
import local.bottomline.microservices.dto.PaymentDto;
import local.bottomline.microservices.dto.SiteDto;
import local.bottomline.microservices.entity.Payment;
import local.bottomline.microservices.entity.Site;
import local.bottomline.microservices.gateway.entityproxy.PaymentProxy;
import local.bottomline.microservices.gateway.entityproxy.SiteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessagingMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import local.bottomline.microservices.web.exception.EntityNotFoundException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
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

    @Autowired
    private SiteProxy siteProxy;

    @Autowired
    private PaymentProxy paymentProxy;

    @Autowired
    MessageConverter jacksonJmsMessageConverter;


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
    public PaymentDto getPaymentOrchestratedJms(@PathVariable Long paymentId) throws JMSException {
        Payment payment = getPayment(paymentId);
        PaymentDto result = paymentAssembler.toDto(payment);
        if(result == null){
            throw new EntityNotFoundException("Payment Not Found....");
        }

        SiteDto disburserSiteDto = getSiteWithJms(payment.getDisburserSiteId());
        SiteDto collectorSiteDto = getSiteWithJms(payment.getCollectorSiteId());
        result.setDisburserSite(disburserSiteDto);
        result.setCollectorSite(collectorSiteDto);
        return result;
    }

    private SiteDto getSiteWithJms(Long siteId) throws JMSException {
        SiteDto requestSite = new SiteDto();
        requestSite.setId(siteId);
        Message jmsResponse = jmsTemplate.sendAndReceive("siteJmsListener", session -> jacksonJmsMessageConverter.toMessage(requestSite, session));
        SiteDto responseSiteDto = (SiteDto) ((ObjectMessage)jmsResponse).getObject();
        return responseSiteDto;
    }


    @GetMapping("/payment/{paymentId}")
    public Payment getPayment(@PathVariable Long paymentId) {
        return paymentProxy.getPayment(paymentId);
    }

    @GetMapping("/site/{siteId}")
    public SiteDto getSite(@PathVariable Long siteId) {
        Site site = siteProxy.getSite(siteId);
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
