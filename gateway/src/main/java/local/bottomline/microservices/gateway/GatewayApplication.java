package local.bottomline.microservices.gateway;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import java.util.Arrays;

@SpringBootApplication(scanBasePackages = "local.bottomline.microservices")
@EnableEurekaClient
@EnableJms
public class GatewayApplication {


    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter(){
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

//    @Bean
//    public ActiveMQConnectionFactory activeMQConnectionFactory() {
//        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("your broker URL");
//        factory.setTrustedPackages(Arrays.asList("local.bottomline.microservices.dto"));
//        return factory;
//    }


    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
