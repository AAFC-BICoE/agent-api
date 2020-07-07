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

/**
 * Filter to intercept HTTP requests and validate the id is a valid UUID.
 */
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

  /**
   * Returns true if the given method is supported by this class. (given method is
   * present in {@link RequestUuidValidationFilter#METHODS} )
   * 
   * @param method
   *                 - HTTP method to validate
   * @return
   */
  private static boolean isValidMethod(String method) {
    return Arrays.asList(METHODS).stream()
        .anyMatch(elemment -> StringUtils.equalsIgnoreCase(method, elemment));
  }

  /**
   * Removes the pre-set web path prefix from the given uri.
   * 
   * @param uri
   *              - uri to strip
   * @throws -
   *             {@link IllegalStateException} if the pre-set web path prefix does
   *             not match the start of the uri.
   * @return uri with web path prefix stripped
   */
  private String stripPrefix(String uri) {
    if (!uri.startsWith(webPathPrefix)) {
      throw new IllegalStateException(
          "Incorrect web path prefix set expected: " + webPathPrefix + " but URI was: " + uri);
    }
    return uri.replaceFirst(webPathPrefix, "");
  }

  /**
   * Splits a given uri into an array of seperate parts.
   *
   * @param uri
   *              - uri to split
   * @return array of seperate URI parts
   */
  private static String[] splitPath(String uri) {
    return Arrays.asList(uri.split(SEPARATOR)).stream()
        .filter(ele -> StringUtils.isNotBlank(ele))
        .toArray(String[]::new);
  }

  /**
   * Returns the endpoint of the given path if it is present in the pre-set list
   * of endpoints. Endpoint should be the first element of the given path. (ex.
   * /{endpoint}/{id}/...)
   * 
   * @param path
   * @return
   */
  private Optional<String> parseEndpoint(String[] path) {
    if (path.length == 0) {
      return Optional.empty();
    }
    return endpoints.stream().filter(end -> StringUtils.equalsIgnoreCase(end, path[0])).findFirst();
  }

  /**
   * Returns the id part of the given path or Optional empty if one is not
   * present. ID should be the second element of the path. (ex.
   * /{endpoint}/{id}/...)
   *
   * @param path
   *               - path to parse
   * @return
   */
  private Optional<String> parseUUID(String[] path) {
    if (path.length < 2) {
      return Optional.empty();
    }
    return Optional.of(path[1]);
  }

  /**
   * Sets the given response to an error response for a given uuid and uri.
   *
   * @param resp
   *               - response to set
   * @param uuid
   *               - uuid of the request
   * @param uri
   *               - uri of the requeest
   */
  @SneakyThrows
  private static void setErrorResponse(HttpServletResponse resp, String uuid, String uri) {
    resp.setStatus(HttpStatus.BAD_REQUEST_400);
    resp.setContentType(ContentType.APPLICATION_JSON.toString());
    resp.getWriter().write(getErrorBody(uuid, uri));
    resp.flushBuffer();
  }

  /**
   * Returns a JSON string representing the body of the error message for a given
   * uuid and uri.
   *
   * @param uuid
   *               - uuid of the request
   * @param uri
   *               - uri of the request
   * @return JSON string representing the body of the error message
   */
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

  /**
   * Returns true if the given string can be parsed as a UUID.
   *
   * @param uuid
   *               - string to validate
   * @return true if the given string can be parsed as a UUID
   */
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
