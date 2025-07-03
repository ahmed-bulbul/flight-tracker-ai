package ca.bazlur.flighttracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Aircraft(
    @JsonProperty("hex") String hexCode,
    @JsonProperty("flight") String callsign,
    @JsonProperty("r") String registration,
    @JsonProperty("t") String aircraftType,
    @JsonProperty("desc") String description, // New field
    @JsonProperty("ownOp") String ownerOperator, // New field
    @JsonProperty("year") String year, // New field
    @JsonProperty("category") String category,
    @JsonProperty("mil") Boolean military,

    @JsonProperty("lat") Double latitude,
    @JsonProperty("lon") Double longitude,
    @JsonProperty("alt_baro") JsonNode altBaroRaw,
    @JsonProperty("alt_geom") Integer altitudeGeom, // Renamed for clarity
    @JsonProperty("gs") Double groundSpeed,
    @JsonProperty("track") Double heading,
    @JsonProperty("nav_heading") Double navHeading,
    @JsonProperty("geom_rate") Integer geomRate,

    @JsonProperty("type") String type,
    @JsonProperty("squawk") String squawk,
    @JsonProperty("emergency") String emergency,
    @JsonProperty("dbFlags") Integer dbFlags,
    @JsonProperty("nav_qnh") Double navQnh,
    @JsonProperty("nav_altitude_mcp") Integer navAltitudeMcp,
    @JsonProperty("version") Integer version,
    @JsonProperty("messages") Long messages,
    @JsonProperty("seen") Double seen,
    @JsonProperty("seen_pos") Double seenPos,
    @JsonProperty("rssi") Double rssi,

    @JsonProperty("nic") Integer nic,
    @JsonProperty("rc") Integer rc,
    @JsonProperty("nic_baro") Integer nicBaro,
    @JsonProperty("nac_p") Integer nacP,
    @JsonProperty("nac_v") Integer nacV,
    @JsonProperty("sil") Integer sil,
    @JsonProperty("sil_type") String silType,
    @JsonProperty("gva") Integer gva,
    @JsonProperty("sda") Integer sda,

    @JsonProperty("alert") Integer alert,
    @JsonProperty("spi") Integer spi,
    @JsonProperty("mlat") JsonNode mlat,
    @JsonProperty("tisb") JsonNode tisb
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
    return altitudeFt() != null;
  }

  public String formattedAlt() {
    return hasAltitude() ? altitudeFt() + " ft" : "GROUND";
  }

  /**
   * Returns the best available altitude. It prefers the geometric altitude (`alt_geom`)
   * but will fall back to the barometric altitude (`alt_baro`) if it's a valid integer.
   *
   * @return The altitude in feet, or null if no valid altitude is available.
   */
  public Integer altitudeFt() {
    if (altitudeGeom != null) {
      return altitudeGeom;
    }
    // This correctly handles cases where alt_baro is "ground" (returns null)
    // or an integer value.
    return (altBaroRaw != null && altBaroRaw.isInt()) ? altBaroRaw.intValue() : null;
  }
}