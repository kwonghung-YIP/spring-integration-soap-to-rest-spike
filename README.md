## REST to SOAP wrapper with Spring Integration

This spike illustrates how to use [Spring Integration library](https://docs.spring.io/spring-integration/reference/html/index.html) to implement a REST wrapper for the [CurrencyName SOAP endpoint](http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?op=CurrencyName), where the endpoint is provided by [DataFlex Web Service](http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso) which is available to the public. The CurrencyName endpoint converts a currency code (e.g. JPY) into a currency name (e.g. Yen).

## How to run this spike

* Run the rest-to-soap spring boot application

```bash
cd modules/rest-to-soap
./mvnw clean spring-boot:run
```

* Call the REST API with curl and expected "Yen" to be returned

```bash
curl -u hung:pass1234 -v -s "http://localhost:8080/api/currencyname?code=jpy"
```

## Highlight features

* The REST controller handles many incoming requests simultaneously, all these requests are sent to a single request message channel, and all the SOAP responses are returned to the REST controller via a single reply message channel. To map the message received from the reply message channel to the corresponding REST request, the message gateway plays an important role here. To test this scope mapping, a Jmeter test plan has been included in this spike which performs the load test to the REST controller.

## Setup Jmeter for testing

* Download and extract [Jmeter](https://jmeter.apache.org/download_jmeter.cgi)
* Install the [Jmeter Plugin Manager](https://jmeter-plugins.org/install/Install/)
* Launch the Plugin Manager in Jmeter, and install the [Random CSV Data Set](https://github.com/Blazemeter/jmeter-bzm-plugins/blob/master/random-csv-data-set/RandomCSVDataSetConfig.md)

## Run the Jmeter test plan

* load the test plan in "/jmeter/load-test.jmx"
* Expand the test plan tree, adjust the **Filename** parameter under the **bzm - Random CSV Data Set Config** step
* Check the test result in **Summary Report** and **View Result Tree**

## Reference

* [Consuming a SOAP web service](https://spring.io/guides/gs/consuming-web-service/)
* [Producing a SOAP web service](https://spring.io/guides/gs/producing-web-service/)
* [Messaging Gateway](https://docs.spring.io/spring-integration/reference/html/messaging-endpoints.html#gateway)

## For calling other API

```bash
curl -u hung:pass1234 -v -s http://localhost:8080/api/echo\?ping\=hung
```

```bash
curl -u hung:pass1234 -v -s http://localhost:8080/api/upper\?value\=hung
```
