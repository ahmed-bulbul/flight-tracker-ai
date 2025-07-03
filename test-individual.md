# Flight Tracker AI - Individual Curl Tests

Test the aviation assistant API endpoints individually with these curl commands:

## 1. Health Check
```bash
curl -X GET "http://localhost:8080/api/aviation/health" \
  -H "Content-Type: application/json"
```

## 2. Aircraft Near JFK Airport
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.100" \
  -d "Show me aircraft near JFK Airport within 20 nautical miles"
```

## 3. Military Aircraft Query
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.101" \
  -d "Are there any military aircraft currently in flight?"
```

## 4. Emergency Aircraft Query
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.102" \
  -d "Are there any aircraft declaring emergency right now?"
```

## 5. Specific Callsign Search
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.103" \
  -d "Find aircraft with callsign UAL123"
```

## 6. Aircraft Near London Heathrow
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.104" \
  -d "What aircraft are currently flying near London Heathrow within 15 nautical miles?"
```

## 7. General Aviation Query
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.105" \
  -d "What's the current air traffic situation over Europe?"
```

## 8. Rate Limiting Test
Test multiple requests from the same IP (should hit rate limit after 60 requests/minute):
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.200" \
  -d "Rate limit test query"
```

## 9. Error Handling Test
Test with malformed request:
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.106" \
  -d ""
```

## 10. Complex Query Test
```bash
curl -X POST "http://localhost:8080/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.107" \
  -d "Find all aircraft within 50 nautical miles of Frankfurt Airport that are flying above 30,000 feet and tell me about any interesting patterns"
```

## Notes:
- Replace `localhost:8080` with your actual server address
- The `X-Forwarded-For` header is used for rate limiting per IP
- Rate limit is 60 requests per minute per IP
- Make sure Ollama is running with the llama3.1:8b model
- The application needs to be running (`./gradlew bootRun`)