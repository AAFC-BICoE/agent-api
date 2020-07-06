package ca.gc.aafc.agent.api.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import io.crnk.servlet.CrnkFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class RequestUuidValidationFilter extends CrnkFilter {

  private static final String METHOD = "GET";
  private static final String SEPARATOR = "/";

  @NonNull
  private final List<String> endpoints;

  @Override
  @SneakyThrows
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      if (StringUtils.equalsIgnoreCase(httpRequest.getMethod(), METHOD)) {
        String uri = httpRequest.getRequestURI();
        log.info("Validating ID if present for URI: " + uri);

        String[] path = splitPath(uri);
        Optional<String> endpoint = parseEndpoint(path);

        String endOfPath = path[path.length - 1];
        if (endpoint.isPresent() && hasId(path, endpoint.get()) && !isUuidValid(endOfPath)) {
          setErrorResponse(httpResponse, endOfPath);
          return;
        } else {
          log.warn("UUID validation not set for resouce with URI: " + uri);
        }
      }
    }
    super.doFilter(request, response, chain);
  }

  private static String[] splitPath(String uri) {
    return Arrays.asList(uri.split(SEPARATOR)).stream()
        .filter(ele -> StringUtils.isNotBlank(ele))
        .toArray(String[]::new);
  }

  private Optional<String> parseEndpoint(String[] path) {
    return Arrays.asList(path).stream()
        .filter(ele -> endpoints.stream().anyMatch(end -> StringUtils.equalsIgnoreCase(ele, end)))
        .findFirst();
  }

  private static boolean hasId(String[] path, String endpoint) {
    return !StringUtils.equalsIgnoreCase(path[path.length - 1], endpoint);
  }

  @SneakyThrows
  private static void setErrorResponse(HttpServletResponse resp, String uuid) {
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, uuid + " is not a valid uuid");
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
