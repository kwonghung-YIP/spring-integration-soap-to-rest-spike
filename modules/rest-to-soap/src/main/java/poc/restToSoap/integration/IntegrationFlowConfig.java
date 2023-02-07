package poc.restToSoap.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.interceptor.MessageSelectingInterceptor;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.ws.dsl.Ws;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import lombok.extern.slf4j.Slf4j;
import poc.restToSoap.wsdl.CurrencyName;
import poc.restToSoap.wsdl.CurrencyNameResponse;

@Slf4j
@Configuration
@EnableIntegration
public class IntegrationFlowConfig {

    @Bean
    public MessageChannel myChannel() {
        return MessageChannels.direct("my-channel").get();
    }

    @Bean
    public IntegrationFlow outboundSoapCall(@Qualifier("myChannel") MessageChannel inMsgChannel) {
        return IntegrationFlows.from(inMsgChannel)
                .transform((msg) -> {
                    log.info("here");
                    return msg;
                })
                .log()
                .get();
    }

    @MessagingGateway(name="myGateway")
    static public interface GatewayAsFunction {

        @Gateway(requestChannel = "toUpperCaseRequest", replyTimeout = 2000, replyChannel = "toUpperCaseReply")
        public String toUppercase(@Payload String payload);

        @Gateway(requestChannel = "currencyNameRequest", replyTimeout = 2000, replyChannel = "currencyNameReply")
        public String getCurrencyName(@Payload String currencyCode);
    }
    
    @Bean
    public MessageChannel toUpperCaseRequest() {
        return MessageChannels.direct("touppercase-request").get();
    }

    @Bean
    public MessageChannel toUpperCaseReply() {
        return MessageChannels.direct("touppercase-reply").get();
    }

    @Bean
    public IntegrationFlow toUppercaseFlow(@Qualifier("toUpperCaseRequest") MessageChannel inMsgChannel) {
        return IntegrationFlows.from(inMsgChannel)
                .log()
                .transform(Message.class,(msg) -> {
                    log.info("{}",msg);
                    String payload = (String)msg.getPayload();
                    return "~["+payload.toUpperCase()+"]~";
                })
                .channel(toUpperCaseReply())
                .get();
    }

    @Bean
    public MessageChannel currencyNameRequest() {
        return MessageChannels.direct("currency-name-request").get();
    }

    @Bean
    public MessageChannel currencyNameReply() {
        return MessageChannels.direct("currency-name-reply").get();
    }
    
    @Bean
    public IntegrationFlow callSoap() {
    	Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
    	jaxb2Marshaller.setPackagesToScan("poc.restToSoap.wsdl");
    	
        return IntegrationFlows
                .from(currencyNameRequest())
                .<String,CurrencyName>transform((param) -> {
                	CurrencyName request = new CurrencyName();
                	request.setSCurrencyISOCode(param.toUpperCase());
                	return request;
                })
                //.handle(Ws.simpleOutboundGateway().uri("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso"))
                .log()
                .handle(
            		Ws.marshallingOutboundGateway()
                	  .uri("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso")
                	  .marshaller(jaxb2Marshaller)
                	  .unmarshaller(jaxb2Marshaller)
                )
                .log()
                .<CurrencyNameResponse,String>transform((response) -> response.getCurrencyNameResult())
                .channel(currencyNameReply())
                .intercept(new ChannelInterceptor() {

					@Override
					public Message<?> preSend(Message<?> message, MessageChannel channel) {
						log.info("preSend message in channel {}",channel);
						if (Math.random() > 0.7) {
							throw new MyException();
						} else {
							return message;
						}
					}
                	
                })
                .get();
    }
    
    static public class MyException extends RuntimeException {
    	
    }

}
