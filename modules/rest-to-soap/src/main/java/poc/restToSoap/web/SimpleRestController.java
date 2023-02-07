package poc.restToSoap.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import poc.restToSoap.integration.IntegrationFlowConfig.GatewayAsFunction;
import poc.restToSoap.integration.IntegrationFlowConfig.MyException;
import poc.restToSoap.svc.DelegatedService;

@Slf4j
@RestController
@RequestMapping("/api")
public class SimpleRestController {

    @Autowired
    private DelegatedService service;
    
    @Autowired
    private GatewayAsFunction gateway;

    @GetMapping("/echo")
    public String echo(@RequestParam String ping) {
        log.info("incoming request {}",ping);
        service.apply(ping);
        return "pong";
    }

    @GetMapping("/upper")
    public String toUppercase(@RequestParam String value) {
        log.info("incoming request {}",value);
        return gateway.toUppercase(value);
    }

    @GetMapping("/currencyname")
    public String getCurrencyName(@RequestParam(name="code") String currencyCode,Principal principal) {
        log.info("incoming request {}",currencyCode);
        return gateway.getCurrencyName(currencyCode);
    }
    
    @ExceptionHandler
    public ResponseEntity<String> handleException(MyException e) {
    	return ResponseEntity.badRequest().body("catch MyException");
    }
    

}
