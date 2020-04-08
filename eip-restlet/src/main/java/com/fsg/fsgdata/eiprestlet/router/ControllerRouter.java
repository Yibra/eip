package com.fsg.fsgdata.eiprestlet.router;

import com.fsg.fsgdata.eiprestlet.entities.DataResponse;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.management.DefaultManagementAgent;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.ManagementNameStrategy;
import org.springframework.stereotype.Component;

import static org.apache.camel.model.rest.RestParamType.path;

@Component
public class ControllerRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "DataIntegration API").apiProperty("api.version", "1.0.0")
                .apiProperty("cors", "true");

        rest("/hello").id("hello").description("Hello world.")
                .consumes("application/json").produces("application/json")
                .get("/{me}").description("greeting").outType(String.class)
                .param().name("me").type(path).description("name").dataType("string").endParam()
                .responseMessage().code(200).message("say hello.").endResponseMessage()
                .route().transform().simple("Hi ${header.me}.");

        rest("/data/{resource}").id("DataAccess")
                .get().description("Fetch {resource}").outType(DataResponse.class)
                .param().name("resource").type(path).description("data resource").dataType("string").endParam()
                .responseMessage().code(200).message("data content").endResponseMessage()
                .to("direct:dataEndpoint");

        from("direct:dataEndpoint")
                .id("DataBackend")
                .setHeader("endpoint", constant("data"))
                .process("requestProcessor")
                .to("bean:sqlParseProcessor?method=makeSQL(${header.resource}, ${body})")
                .toD("jdbc:${header.datasource}")
                .to("mock:data")
                .process("databaseProcessor");
    }
}
