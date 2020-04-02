package ca.gc.aafc.agent.api.utils;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class TestUtils {

  private TestUtils() {
  }

  /**
   * Creates a JSON API Map from the provided type name, attributes and id.
   * 
   * @param typeName     "type" in JSON API
   * @param attributeMap key/value representing the "attributes" in JSON API
   * @param id           id of the resource or null if there is none
   * @return
   */
  public static Map<String, Object> toJsonAPIMap(
    String typeName,
    Map<String, Object> attributeMap,
    String id
  ) {
    ImmutableMap.Builder<String, Object> bldr = new ImmutableMap.Builder<>();
    bldr.put("type", typeName);
    if (id != null) {
      bldr.put("id", id);
    }
    bldr.put("attributes", attributeMap);

    return ImmutableMap.of("data", bldr.build());
  }

}