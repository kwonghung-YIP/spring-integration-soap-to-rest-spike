package poc.restToSoap.svc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class DelegatedService {

    final private MessageChannel msgChannel;
    
    public DelegatedService(@Qualifier("myChannel") MessageChannel msgChannel) {
        this.msgChannel = msgChannel;
    }

    public void apply(String payload) {
        Message msg = MessageBuilder.withPayload(payload).build();
        msgChannel.send(msg);
    }

}
