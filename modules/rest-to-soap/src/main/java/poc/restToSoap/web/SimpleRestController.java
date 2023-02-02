package poc.restToSoap.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class SimpleRestController {

    @Autowired
    private DelegatedService service;

    @GetMapping("/echo")
    public String echo(@RequestParam String ping) {
        log.info("incoming request {}",ping);
        service.apply(ping);
        return "pong";
    }

    @GetMapping("/upper")
    public String toUppercase(@RequestParam String value) {
        log.info("incoming request {}",value);
        return service.toUppercase(value);
    }

    @Component
    static public class DelegatedService {

        final private MessageChannel msgChannel;
        final private FuncAsGateway gateway;

        public DelegatedService(@Qualifier("myChannel") MessageChannel msgChannel,FuncAsGateway gateway) {
            this.msgChannel = msgChannel;
            this.gateway = gateway;
        }

        public void apply(String payload) {
            Message msg = MessageBuilder.withPayload(payload).build();
            msgChannel.send(msg);
        }

        public String toUppercase(String payload) {
            return this.gateway.toUppercase(payload);
        }

    }

    @MessagingGateway(name="myGateway")
    static public interface FuncAsGateway {

        @Gateway(requestChannel = "gatewayRequest", replyTimeout = 2000, replyChannel = "gatewayReply")
        public String toUppercase(@Payload String payload);
    }
}
