package com.fsg.fsgdata.eiprestlet;

import com.ctrip.framework.apollo.mockserver.EmbeddedApollo;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
public class EipRestletApplicationTests {
	private static Logger log = LoggerFactory.getLogger(EipRestletApplicationTests.class);
	@ClassRule
	public static EmbeddedApollo embeddedApollo = new EmbeddedApollo();

	@Produce("direct:dataEndpoint")
	protected ProducerTemplate dataEndpoint;

	@EndpointInject(value = "mock:data")
	protected MockEndpoint mockData;

	@Test
	@DirtiesContext
	void dataEndpoint() throws InterruptedException {
		List<Map<String, Object>> expectBody = new ArrayList<Map<String, Object>>() {{
			add(new HashMap<String, Object>() {{
				put("ID", 3);
				put("NAME", "Freud3");
				put("AGE", 29);
			}});
		}};

		mockData.expectedBodiesReceived(expectBody.toString());

		Map<String, Object> headers = new HashMap<>();
		headers.put("resource", "user");
		headers.put(Exchange.HTTP_QUERY, "filterCondition=id%3D3");
		dataEndpoint.sendBodyAndHeaders("", headers);

		mockData.assertIsSatisfied();
	}
}
