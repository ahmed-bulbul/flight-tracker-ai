package ca.bazlur.flighttracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record Aircraft(
    @JsonProperty("hex") String hexCode,
    @JsonProperty("flight") String callsign,
    @JsonProperty("r") String registration,
    @JsonProperty("t") String aircraftType,
    @JsonProperty("lat") Double latitude,
    @JsonProperty("lon") Double longitude,
    @JsonProperty("alt_baro") JsonNode altBaroRaw,
    @JsonProperty("alt_geom") Integer altitudeFt,
    @JsonProperty("gs") Double groundSpeed,
    @JsonProperty("track") Double heading,
    @JsonProperty("squawk") String squawk,
    @JsonProperty("emergency") String emergency,
    @JsonProperty("category") String category,
    @JsonProperty("mil") Boolean military
) {
  public boolean hasLocation() {
    return latitude != null && longitude != null;
  }

  public boolean isEmergency() {
    return emergency != null && !"none".equalsIgnoreCase(emergency);
  }

  public String getDisplayName() {
    return (callsign != null && !callsign.isBlank()) ? callsign.trim()
        : registration != null ? registration : hexCode;
  }

  public boolean hasAltitude() {
    return altitudeFt != null;
  }

  public String formattedAlt() {
    return hasAltitude() ? altitudeFt + " ft" : "GROUND";
  }

  public Integer altitudeFt() {
    if (altitudeFt != null) return altitudeFt;
    return (altBaroRaw != null && altBaroRaw.isInt()) ? altBaroRaw.intValue() : null;
  }
}