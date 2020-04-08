package com.fsg.fsgdata.eiprestlet.processors;

import com.fsg.fsgdata.eiprestlet.ApplicationConfiguration;
import com.fsg.fsgdata.eiprestlet.properties.ResourcesProperties;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("requestProcessor")
public class RequestProcessor implements Processor {
    private static Logger log = LoggerFactory.getLogger(RequestProcessor.class);

    @Autowired
    private ResourcesProperties resources;

    @Override
    public void process(final Exchange exchange) throws Exception {
        final String endpoint = exchange.getIn().getHeader("endpoint", String.class).toUpperCase();
        final String resource = exchange.getIn().getHeader("resource", String.class).toLowerCase();

        log.info(String.format("resource= %s.%s", endpoint, resource));
        exchange.getIn().setHeader("resource", resource);

        Map<String, Object> query =
                URISupport.parseQuery(exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class));
        String filterConditionKey = "filterCondition";
        if (!query.containsKey(filterConditionKey)) {
            filterConditionKey = filterConditionKey.toLowerCase();
        }

        String normalUrl = String.format("%s/%s", endpoint, resource);
        exchange.getIn().setHeader("normalURL", normalUrl);

        String finalCondition = "filterCondition=";
        if (query.containsKey(filterConditionKey)) {
            finalCondition = "filterCondition=" + query.get(filterConditionKey);
        }
        String ds = "meta";
        if (resources.getMapping().containsKey(resource)) {
            ds = resources.getMapping().get(resource);
        }
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, HttpMethods.GET);
        exchange.getIn().setHeader(Exchange.HTTP_QUERY, finalCondition);
        exchange.getIn().setHeader(Exchange.HTTP_PATH, String.format("data/%s", resource));
        exchange.getIn().setHeader("datasource", ds);

        exchange.getIn().setBody(finalCondition);
    }

}