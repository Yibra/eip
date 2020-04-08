package com.fsg.fsgdata.eiprestlet.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsg.fsgdata.eiprestlet.entities.DataResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("databaseProcessor")
public class DatabaseProcessor implements Processor {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void process(Exchange exchange) {
        final String resource = exchange.getIn().getHeader("resource", String.class).toLowerCase();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> data = (List<Map<String, Object>>) exchange.getIn().getBody();
        List<JsonNode> contents = new ArrayList<>();
        data.stream().forEach(row -> contents.add(mapper.valueToTree(row)));

        DataResponse result = new DataResponse();
        result.put(resource, contents);

        // for trace
        exchange.getIn().setBody(result);
        exchange.getIn().setHeader("service", "data/" + resource);
        exchange.getIn().setHeader(Exchange.HTTP_QUERY, exchange.getIn().getHeader(Exchange.HTTP_QUERY));
    }
}
