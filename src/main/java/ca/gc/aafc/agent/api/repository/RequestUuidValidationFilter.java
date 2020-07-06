package ca.gc.aafc.agent.api.repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import io.crnk.servlet.CrnkFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class RequestUuidValidationFilter extends CrnkFilter {

  private static final String METHOD = "GET";
  private static final String SEPARATOR = "/";

  @NonNull
  private final List<String> endpoints;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      if (StringUtils.equalsIgnoreCase(httpRequest.getMethod(), METHOD)) {
        log.info("Validating ID if present for URI: " + httpRequest.getRequestURI());

        String[] path = splitPath(httpRequest.getRequestURI());

        String endpoint = parseEndpoint(path);

        int index = indexOfIgnoreCase(path, endpoint);

        if (index < path.length && !isUuidValid(path[index + 1])) {
          httpResponse.sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            path[index + 1] + " is not a valid uuid");
          return;
        }
      }
    }
    super.doFilter(request, response, chain);
  }

  private static String[] splitPath(String uri) {
    String[] path = uri.split(SEPARATOR);
    return Arrays.asList(path).stream().filter(ele -> StringUtils.isNotBlank(ele))
        .toArray(String[]::new);
  }

  private String parseEndpoint(String[] path) {
    return Arrays.asList(path).stream()
        .filter(ele -> endpoints.stream().anyMatch(end -> StringUtils.equalsIgnoreCase(ele, end)))
        .findFirst()
        .orElse(null);
  }

  private static int indexOfIgnoreCase(String[] array, String string) {
    for (int i = 0; i < array.length; i++) {
      String ele = array[i];
      if (StringUtils.equalsIgnoreCase(ele, string)) {
        return i;
      }
    }
    return -1;
  }

  private static boolean isUuidValid(String uuid) {
    try {
      UUID.fromString(uuid);
      return true;
    } catch (IllegalArgumentException e) {
      log.warn("{} is not a valid uuid", () -> uuid);
      return false;
    }
  }
}
