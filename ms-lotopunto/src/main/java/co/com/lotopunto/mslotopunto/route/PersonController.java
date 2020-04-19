package co.com.lotopunto.mslotopunto.route;

import co.com.lotopunto.mslotopunto.dto.PersonResponse;
import co.com.lotopunto.mslotopunto.entities.Person;
import co.com.lotopunto.mslotopunto.processors.SpreadsheetProcessor;
import co.com.lotopunto.mslotopunto.processors.PersonDatabaseProcessor;
import co.com.lotopunto.mslotopunto.processors.PersonSpreadSheetProcessor;
import co.com.lotopunto.mslotopunto.services.PersonService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersonController extends RouteBuilder {

    private final SpreadsheetProcessor spreadsheetProcessor;

    private final PersonDatabaseProcessor personDatabaseProcessor;

    private final PersonSpreadSheetProcessor personSpreadSheetProcessor;

    private final PersonService personService;

    public PersonController(SpreadsheetProcessor spreadsheetProcessor, PersonDatabaseProcessor personDatabaseProcessor,
                            PersonSpreadSheetProcessor personSpreadSheetProcessor, PersonService personService) {
        this.spreadsheetProcessor = spreadsheetProcessor;
        this.personDatabaseProcessor = personDatabaseProcessor;
        this.personSpreadSheetProcessor = personSpreadSheetProcessor;
        this.personService = personService;
    }

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .bindingMode(RestBindingMode.json)
                .apiContextPath("/api")
                .contextPath("/loto/v1")
                .apiProperty("host", "")
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)

                .corsHeaderProperty("Access-Control-Allow-Origin", "*")
                .corsHeaderProperty("Access-Control-Allow-Methods",
                        "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH")
                .corsHeaderProperty("Access-Control-Allow-Headers",
                        "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization")
                .corsHeaderProperty("Access-Control-Max-Age", "3600")

                .apiProperty("api.title", "API Loto punto")
                .apiProperty("api.contact.name", "Jhovanny Canas").apiProperty("api.version", "0.0.1");

        rest().description("Service loto punto")
                .consumes("application/json")
                .produces("application/json")

        .get("/persons")
                .description("consult all persons the lotus point")
                .outType(PersonResponse[].class)
                .responseMessage().code(200).message("OK").endResponseMessage()
                .responseMessage().code(404).message("The resource was not found").endResponseMessage()
                .responseMessage().code(500).message("Error generating the query").endResponseMessage()
                .route().streamCaching()
                .bean(personService, "getAllLoto")
                .endRest();

        from("google-sheets-stream://spreadsheets?accessToken={{google-sheets.token}}" +
                "&spreadsheetId={{google-sheets.spreadsheet-id}}&delay=60000&clientSecret={{google-sheets.client-secret}}" +
                "&range=Sheet1!A2:C&refreshToken={{google-sheets.refresh-token}}")
                .convertBodyTo(String.class)
                .log("${body}")
                .bean(spreadsheetProcessor)
                .to("direct:multicasting");

                from("direct:multicasting").multicast(new AggregationStrategy() {
                    @Override
                    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                        if (oldExchange == null) {
                            return newExchange;
                        }

                        List<Person> listPersonAll = new ArrayList<>();
                        List<Person> listPersonOld = (List<Person>) oldExchange.getIn().getBody();
                        List<Person> listPersonNew = (List<Person>) newExchange.getIn().getBody();
                        listPersonAll.addAll(listPersonOld);
                        listPersonAll.addAll(listPersonNew);

                        oldExchange.getIn().setBody(listPersonAll);
                        return oldExchange;
                    }
                }).parallelProcessing().to("direct:personlotodatabase", "direct:personlotospreadsheet")
                .end().process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                List<Person> people = (List<Person>) exchange.getIn().getBody();
                personService.saveLotoPersona(people);
            }
        }).log("Save loto punto person").end();

        from("direct:personlotodatabase").streamCaching().bean(personDatabaseProcessor).end();
        from("direct:personlotospreadsheet").streamCaching().bean(personSpreadSheetProcessor).end();
    }
}
