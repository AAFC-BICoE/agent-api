package ca.gc.aafc.agent.api.repository;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

import io.crnk.core.engine.http.HttpStatus;
import io.crnk.servlet.CrnkFilter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RequestUuidValidationFilter extends CrnkFilter {

  private static final String[] METHODS = {"GET", "PATCH", "DELETE"};
  private static final String SEPARATOR = "/";
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  private final List<String> endpoints;
  private final String webPathPrefix;

  public RequestUuidValidationFilter(
    @NonNull String webPathPrefix,
    @NonNull List<String> endpoints
  ) {
    this.endpoints = endpoints;
    this.webPathPrefix = webPathPrefix;
  }

  @Override
  @SneakyThrows
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      if (isValidMethod(httpRequest.getMethod())) {
        String uri = stripPrefix(httpRequest.getRequestURI());
        log.info("Validating ID if present for URI: " + uri);

        String[] path = splitPath(uri);
        Optional<String> endpoint = parseEndpoint(path);
        Optional<String> uuid = parseUUID(path);

        if (endpoint.isPresent()) {
          if (uuid.isPresent() && !isUuidValid(uuid.get())) {
            setErrorResponse(httpResponse, uuid.get(), uri);
            return;
          }
        } else {
          log.warn("UUID validation not set for resouce with URI: " + uri);
        }
      }
    }
    super.doFilter(request, response, chain);
  }

  private static boolean isValidMethod(String method) {
    return Arrays.asList(METHODS).stream()
        .anyMatch(elemment -> StringUtils.equalsIgnoreCase(method, elemment));
  }

  private String stripPrefix(String uri) {
    if (!uri.startsWith(webPathPrefix)) {
      throw new IllegalStateException(
          "Incorrect web path prefix set expected: " + webPathPrefix + " but URI was: " + uri);
    }
    return uri.replaceFirst(webPathPrefix, "");
  }

  private static String[] splitPath(String uri) {
    return Arrays.asList(uri.split(SEPARATOR)).stream()
        .filter(ele -> StringUtils.isNotBlank(ele))
        .toArray(String[]::new);
  }

  private Optional<String> parseEndpoint(String[] path) {
    if (path.length == 0) {
      return Optional.empty();
    }
    return endpoints.stream().filter(end -> StringUtils.equalsIgnoreCase(end, path[0])).findFirst();
  }

  private Optional<String> parseUUID(String[] path) {
    if (path.length < 2) {
      return Optional.empty();
    }
    return Optional.of(path[1]);
  }

  @SneakyThrows
  private static void setErrorResponse(HttpServletResponse resp, String uuid, String uri) {
    resp.setStatus(HttpStatus.BAD_REQUEST_400);
    resp.setContentType(ContentType.APPLICATION_JSON.toString());
    resp.getWriter().write(getErrorBody(uuid, uri));
    resp.flushBuffer();
  }

  @SneakyThrows
  private static String getErrorBody(String uuid, String uri) {
    Map<String, String> metaData = ImmutableMap.of(
      "path", uri,
      "timestamp", OffsetDateTime.now().toString());

    Map<String, Object> errorData = ImmutableMap.of(
      "status", Integer.toString(HttpStatus.BAD_REQUEST_400),
      "title", HttpStatus.toMessage(HttpStatus.BAD_REQUEST_400),
      "detail", uuid + " is not a valid uuid",
      "meta", metaData);
    Map<String, Object[]> returnBody = ImmutableMap.of("errors", new Object[] { errorData });
    return JSON_MAPPER.writeValueAsString(returnBody);
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
