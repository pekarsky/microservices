package local.bottomline.microservices.gateway;

import local.bottomline.microservices.assembler.PaymentAssembler;
import local.bottomline.microservices.dto.SiteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class GatewayJmsListener {

    @Autowired
    private PaymentAssembler paymentAssembler;

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination="siteJmsListener", containerFactory = "myFactory")
    public void receiveMessage(Message message) throws Exception{
//    public void receiveMessage(SiteDto siteDto){
        System.out.println(message);
        SiteDto dto = message.getBody(SiteDto.class);
       // System.out.println("site received: " + siteDto.getId());

        jmsTemplate.send(message.getJMSReplyTo(), session -> {
            dto.setDescriptor("response");
            Message responseMessage = session.createObjectMessage(dto);
            responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
            return responseMessage;
        });

    }
}
