package org.apache.geode.management.internal.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.connectors.jdbc.internal.configuration.ConnectorService;
import org.apache.geode.test.junit.categories.IntegrationTest;
import org.apache.geode.test.junit.rules.HttpClientRule;
import org.apache.geode.test.junit.rules.LocatorStarterRule;

@Category({IntegrationTest.class})
public class ClusterConfigurationControllerTest {
  @Rule
  public LocatorStarterRule locator = new LocatorStarterRule().withJMXManager().withAutoStart();

  @Rule
  public HttpClientRule client = new HttpClientRule(locator::getHttpPort);

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testCreateMapping() throws Exception {
    ConnectorService.RegionMapping mapping = new ConnectorService.RegionMapping("region", "com.abc.PdxClass", "table", "connection", false);
    String requestBody = objectMapper.writeValueAsString(mapping);
    HttpResponse response = client.postWithBody("/cache/configuration", requestBody);
    assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
  }

  @Test
  public void testControllerUp() throws Exception {
    HttpResponse response = client.get("/version/release");
    assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
  }
}
