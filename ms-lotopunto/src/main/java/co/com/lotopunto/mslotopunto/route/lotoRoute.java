package co.com.lotopunto.mslotopunto.route;

import co.com.lotopunto.mslotopunto.dto.LotoResponse;
import co.com.lotopunto.mslotopunto.entities.PersonLoto;
import co.com.lotopunto.mslotopunto.processors.ApiSpreadsheetProcessor;
import co.com.lotopunto.mslotopunto.processors.PersonLotoDatabaseProcessor;
import co.com.lotopunto.mslotopunto.processors.PersonLotoSpreadSheetProcessor;
import co.com.lotopunto.mslotopunto.services.LotoPuntoService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class lotoRoute extends RouteBuilder {

    @Autowired
    private ApiSpreadsheetProcessor apiSpreadsheetProcessor;

    @Autowired
    private PersonLotoDatabaseProcessor personLotoDatabaseProcessor;

    @Autowired
    private PersonLotoSpreadSheetProcessor personLotoSpreadSheetProcessor;

    @Autowired
    private LotoPuntoService lotoPuntoService;

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

        .get("/lotopuntos")
                .description("consult all the lotus point")
                .outType(LotoResponse[].class)
                .responseMessage().code(200).message("OK").endResponseMessage()
                .responseMessage().code(404).message("The resource was not found").endResponseMessage()
                .responseMessage().code(500).message("Error generating the query").endResponseMessage()
                .route().streamCaching()
                .bean(lotoPuntoService, "getAllLoto")
                .endRest();

        from("google-sheets-stream://spreadsheets?accessToken={{google-sheets.token}}" +
                "&spreadsheetId={{google-sheets.spreadsheet-id}}&delay=60000&clientSecret={{google-sheets.client-secret}}" +
                "&range=Sheet1!A2:C&refreshToken={{google-sheets.refresh-token}}")
                .convertBodyTo(String.class)
                .log("${body}")
                .bean(apiSpreadsheetProcessor)
                .to("direct:multicasting");

                from("direct:multicasting").multicast(new AggregationStrategy() {
                    @Override
                    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                        if (oldExchange == null) {
                            return newExchange;
                        }

                        List<PersonLoto> listPersonAll = new ArrayList<>();
                        List<PersonLoto> listPersonOld = (List<PersonLoto>) oldExchange.getIn().getBody();
                        List<PersonLoto> listPersonNew = (List<PersonLoto>) newExchange.getIn().getBody();
                        listPersonAll.addAll(listPersonOld);
                        listPersonAll.addAll(listPersonNew);

                        oldExchange.getIn().setBody(listPersonAll);
                        return oldExchange;
                    }
                }).parallelProcessing().to("direct:personlotodatabase", "direct:personlotospreadsheet")
                .end().process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                List<PersonLoto> personLotos = (List<PersonLoto>) exchange.getIn().getBody();
                lotoPuntoService.saveLotoPersona(personLotos);
            }
        }).log("Save loto punto person").end();

        from("direct:personlotodatabase").streamCaching().bean(personLotoDatabaseProcessor).end();
        from("direct:personlotospreadsheet").streamCaching().bean(personLotoSpreadSheetProcessor).end();
    }
}
