package ca.gc.aafc.agent.api.openapi;

import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public final class OpenAPIConstants {

  private OpenAPIConstants(){}

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "luusteve/agent-specs/22830_update_api_specs-Person/schema/agent.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  public static URL getOpenAPISpecsURL () throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

}
