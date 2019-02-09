package local.bottomline.microservices.gateway;

import local.bottomline.microservices.assembler.SiteAssembler;
import local.bottomline.microservices.dto.SiteDto;
import local.bottomline.microservices.entity.Site;
import local.bottomline.microservices.gateway.entityproxy.SiteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class GatewayJmsListener {

    @Autowired
    private SiteProxy siteProxy;

    @Autowired
    private SiteAssembler siteAssembler;

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination="siteJmsListener", containerFactory = "myFactory")
    public void receiveMessage(Message message, SiteDto dto) throws Exception{
        System.out.println(message);

        jmsTemplate.send(message.getJMSReplyTo(), session -> {
            Site site = siteProxy.getSite(dto.getId());
            Message responseMessage = session.createObjectMessage(siteAssembler.toDto(site));
            responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
            return responseMessage;
        });

    }
}
