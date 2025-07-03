package ca.bazlur.flighttracker.sevice;

import ca.bazlur.flighttracker.model.Aircraft;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlightDataFunctions {

  private final FlightDataService flightDataService;

  public FlightDataFunctions(FlightDataService flightDataService) {
    this.flightDataService = flightDataService;
  }

  @Tool("Find aircraft near a specific location")
  public String findAircraftNearLocation(double latitude, double longitude, int radiusNm) {
    List<Aircraft> aircraft = flightDataService.findAircraftNear(latitude, longitude, radiusNm);
    return formatAircraftList(aircraft, "Aircraft near location");
  }

  @Tool("Find military aircraft currently in flight")
  public String findMilitaryAircraft() {
    List<Aircraft> aircraft = flightDataService.findMilitaryAircraft();
    return formatAircraftList(aircraft, "Military aircraft");
  }

  @Tool("Find aircraft by callsign")
  public String findAircraftByCallsign(String callsign) {
    List<Aircraft> aircraft = flightDataService.findByCallsign(callsign);
    return formatAircraftList(aircraft, "Aircraft with callsign " + callsign);
  }

  @Tool("Find aircraft in emergency situation")
  public String findEmergencyAircraft() {
    List<Aircraft> aircraft = flightDataService.findEmergencyAircraft();
    return formatAircraftList(aircraft, "Emergency aircraft");
  }

  private String formatAircraftList(List<Aircraft> aircraft, String title) {
    if (aircraft.isEmpty()) {
      return String.format("No %s found at this time.", title.toLowerCase());
    }

    StringBuilder result = new StringBuilder();
    result.append(String.format("%s (%d found):\n\n", title, aircraft.size()));

    aircraft.forEach(ac -> {
      result.append("✈️ ");

      // Flight info
      String displayName = ac.getDisplayName();
      result.append("Flight: ").append(displayName);

      if (ac.registration() != null && !displayName.equals(ac.registration())) {
        result.append(" (").append(ac.registration()).append(")");
      }

      // Aircraft type
      if (ac.aircraftType() != null) {
        result.append(" | Type: ").append(ac.aircraftType());
      }

      // Position
      if (ac.hasLocation()) {
        result.append(String.format(" | Position: %.4f, %.4f",
            ac.latitude(), ac.longitude()));
      }

      // Altitude and speed
      if (ac.hasAltitude()) {
        result.append(" | Alt: ").append(ac.altitudeFt()).append("ft");
      }

      if (ac.groundSpeed() != null) {
        result.append(" | Speed: ").append(ac.groundSpeed().intValue()).append("kts");
      }

      // Special status
      if (Boolean.TRUE.equals(ac.military())) {
        result.append(" | 🪖 MILITARY");
      }

      if (ac.isEmergency()) {
        result.append(" | 🚨 EMERGENCY: ").append(ac.emergency());
      }

      result.append("\n");
    });

    return result.toString();
  }
}