package ca.bazlur.flighttracker.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FlightDataResponse(
    @JsonProperty("aircraft") List<Aircraft> aircraft,
    @JsonProperty("resultCount") Integer total,
    @JsonProperty("now") Double timestamp,
    @JsonProperty("ptime") Double parseTime // optional
) {
  public List<Aircraft> getAircraft() {
    return aircraft != null ? aircraft : List.of();
  }
}