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
    result.append(String.format("%s (%d found):\n", title, aircraft.size()));

    aircraft.forEach(ac -> {
      // Main header for the aircraft
      result.append("\n✈️ ").append(ac.getDisplayName());
      if (ac.registration() != null && !ac.getDisplayName().equals(ac.registration())) {
        result.append(" (").append(ac.registration()).append(")");
      }
      result.append("\n");

      // Use a list to build details for cleaner formatting
      List<String> details = new java.util.ArrayList<>();

      // Type, Description, and Year
      StringBuilder typeInfo = new StringBuilder();
      if (ac.description() != null && !ac.description().isBlank()) {
        typeInfo.append(ac.description());
        if (ac.aircraftType() != null) {
          typeInfo.append(" (").append(ac.aircraftType()).append(")");
        }
      } else if (ac.aircraftType() != null) {
        typeInfo.append(ac.aircraftType());
      }
      if (ac.year() != null && !ac.year().isBlank()) {
        typeInfo.append(", built ").append(ac.year());
      }
      if (!typeInfo.isEmpty()) {
        details.add("Type: " + typeInfo);
      }

      // Operator
      if (ac.ownerOperator() != null && !ac.ownerOperator().isBlank()) {
        details.add("Operator: " + ac.ownerOperator());
      }

      // Position and Heading
      if (ac.hasLocation()) {
        String position = String.format("Position: %.4f, %.4f", ac.latitude(), ac.longitude());
        if (ac.heading() != null) {
          position += String.format(" | Heading: %.0f°", ac.heading());
        }
        details.add(position);
      }

      // Altitude and Vertical Speed
      if (ac.hasAltitude()) {
        String altitude = "Altitude: " + ac.formattedAlt();
        if (ac.geomRate() != null && ac.geomRate() != 0) {
          String direction = ac.geomRate() > 0 ? "Climbing" : "Descending";
          altitude += String.format(" (%s at %d fpm)", direction, Math.abs(ac.geomRate()));
        }
        details.add(altitude);
      }

      // Speed
      if (ac.groundSpeed() != null) {
        details.add(String.format("Speed: %.0f kts", ac.groundSpeed()));
      }

      // Squawk code
      if (ac.squawk() != null) {
        details.add("Squawk: " + ac.squawk());
      }

      // Status (Emergency/Military)
      List<String> statuses = new java.util.ArrayList<>();
      if (ac.isEmergency()) {
        statuses.add("🚨 EMERGENCY: " + ac.emergency());
      }
      if (Boolean.TRUE.equals(ac.military())) {
        statuses.add("🪖 MILITARY");
      }
      if (!statuses.isEmpty()) {
        details.add("Status: " + String.join(" | ", statuses));
      }

      // Append all details with indentation
      for (String detail : details) {
        result.append("   - ").append(detail).append("\n");
      }
    });

    return result.toString();
  }
}