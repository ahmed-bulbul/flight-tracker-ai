package ca.bazlur.flighttracker.service;

import ca.bazlur.flighttracker.model.Aircraft;
import ca.bazlur.flighttracker.model.FlightDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
public class FlightDataService {

  private static final Logger logger = LoggerFactory.getLogger(FlightDataService.class);

  private final HttpClient webClient;

  private final ObjectMapper objectMapper;

  @Value("${flight-tracker.adsb-api.base-url}")
  private String baseUrl;

  public FlightDataService(@Value("${flight-tracker.adsb-api.timeout}") Duration timeout, ObjectMapper objectMapper) {
    this.webClient = HttpClient.newBuilder()
        .connectTimeout(timeout)
        .build();
    this.objectMapper = objectMapper;
  }

  public List<Aircraft> findMilitaryAircraft() {
    logger.info("Fetching military aircraft data");
    return makeRequest("/mil");
  }

  public List<Aircraft> findAircraftNear(double latitude, double longitude, int radiusNm) {
    logger.info("Fetching aircraft near {}, {} within {}nm", latitude, longitude, radiusNm);
    String path = String.format("/lat/%.4f/lon/%.4f/dist/%d", latitude, longitude, radiusNm);
    return makeRequest(path);
  }

  public List<Aircraft> findByCallsign(String callsign) {
    logger.info("Fetching aircraft with callsign: {}", callsign);
    return makeRequest("/callsign/" + callsign.toUpperCase());
  }

  public List<Aircraft> findEmergencyAircraft() {
    logger.info("Fetching emergency aircraft (squawk 7700)");
    return makeRequest("/sqk/7700");
  }

  private List<Aircraft> makeRequest(String path) {
    try {

      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + path))
          .GET()
          .build();

      HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
      String responseBody = response.body();
      logger.info("Response: {}", responseBody);

      if (responseBody == null || responseBody.isBlank()) {
        logger.warn("Received empty response from path: {}", path);
        return List.of();
      }

      FlightDataResponse flightDataResponse = objectMapper.readValue(responseBody, FlightDataResponse.class);

      return flightDataResponse != null ? flightDataResponse.getAircraft() : List.of();
    } catch (Exception e) {
      logger.error("Failed to fetch flight data from path: {}", path, e);
      return List.of();
    }
  }
}