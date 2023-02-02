package poc.restToSoap.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

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

    @Bean
    public MessageChannel gatewayRequest() {
        return MessageChannels.direct("gateway-request").get();
    }

    @Bean
    public MessageChannel gatewayReply() {
        return MessageChannels.direct("gateway-reply").get();
    }

    @Bean
    public IntegrationFlow toUppercaseFlow(@Qualifier("gatewayRequest") MessageChannel inMsgChannel) {
        return IntegrationFlows.from(inMsgChannel)
                .log()
                .transform(Message.class,(msg) -> {
                    log.info("{}",msg);
                    String payload = (String)msg.getPayload();
                    return "~["+payload.toUpperCase()+"]~";
                })
                .channel(gatewayReply())
                .get();
    }
}
