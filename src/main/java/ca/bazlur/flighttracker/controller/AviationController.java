package ca.bazlur.flighttracker.controller;


import ca.bazlur.flighttracker.service.AviationAssistantService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/aviation")
public class AviationController {

  private static final Logger logger = LoggerFactory.getLogger(AviationController.class);

  private final AviationAssistantService assistantService;
  private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

  public AviationController(AviationAssistantService assistantService) {
    this.assistantService = assistantService;
  }

  @PostMapping("/ask")
  public ResponseEntity<String> askQuestion(@RequestBody @NotBlank String question,
                                            @RequestHeader(value = "X-Forwarded-For", defaultValue = "unknown") String clientIp) {

    // Rate limiting
    Bucket bucket = buckets.computeIfAbsent(clientIp, this::createBucket);
    if (!bucket.tryConsume(1)) {
      return ResponseEntity.status(429).body("Rate limit exceeded. Please try again later.");
    }

    try {
      logger.info("Processing aviation query from {}: {}", clientIp, question);
      String response = assistantService.processQuery(question);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Error processing query: {}", question, e);
      return ResponseEntity.status(500)
          .body("Sorry, I encountered an error processing your question. Please try again.");
    }
  }

  @GetMapping("/health")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("Aviation AI Assistant is ready for takeoff! ✈️");
  }

  @GetMapping("/examples")
  public ResponseEntity<String> examples() {
    String exampleQueries = """
        ## Try These Example Queries:
        
        - "What military aircraft are currently flying?"
        - "Show me flights near Toronto Pearson International Airport within 20 nautical miles"
        - "Are there any emergency aircraft right now?"
        - "Find flight BA123"
        - "What's flying within 30 nautical miles of New York?"
        
        **Tip:** Be specific about locations and use major airport names for best results!
        """;
    return ResponseEntity.ok(exampleQueries);
  }

  private Bucket createBucket(String key) {
    Bandwidth limit = Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1)));
    return Bucket4j.builder()
        .addLimit(limit)
        .build();
  }
}