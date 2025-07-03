#!/bin/bash

# Flight Tracker AI - API Test Script
# This script tests the aviation assistant endpoints with curl

BASE_URL="http://localhost:8080"

echo "🛫 Testing Flight Tracker AI API..."
echo "=================================="

# Test 1: Health Check
echo "1. Testing health endpoint..."
curl -s -X GET "$BASE_URL/api/aviation/health" \
  -H "Content-Type: application/json" | jq -r '.'
echo -e "\n"

# Test 2: Ask about aircraft near a location
echo "2. Testing aircraft search near JFK Airport..."
curl -s -X POST "$BASE_URL/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.100" \
  -d "Show me aircraft near JFK Airport within 20 nautical miles"
echo -e "\n"

# Test 3: Ask about military aircraft
echo "3. Testing military aircraft query..."
curl -s -X POST "$BASE_URL/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.101" \
  -d "Are there any military aircraft currently in flight?"
echo -e "\n"

# Test 4: Ask about emergency situations
echo "4. Testing emergency aircraft query..."
curl -s -X POST "$BASE_URL/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.102" \
  -d "Are there any aircraft declaring emergency right now?"
echo -e "\n"

# Test 5: Ask about specific callsign
echo "5. Testing specific callsign search..."
curl -s -X POST "$BASE_URL/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.103" \
  -d "Find aircraft with callsign UAL123"
echo -e "\n"

# Test 6: Ask about aircraft near London Heathrow
echo "6. Testing aircraft near London Heathrow..."
curl -s -X POST "$BASE_URL/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.104" \
  -d "What aircraft are currently flying near London Heathrow within 15 nautical miles?"
echo -e "\n"

# Test 7: General aviation question
echo "7. Testing general aviation query..."
curl -s -X POST "$BASE_URL/api/aviation/ask" \
  -H "Content-Type: application/json" \
  -H "X-Forwarded-For: 192.168.1.105" \
  -d "What's the current air traffic situation over Europe?"
echo -e "\n"

# Test 8: Rate limiting test (multiple requests from same IP)
echo "8. Testing rate limiting..."
for i in {1..3}; do
  echo "Request $i:"
  curl -s -X POST "$BASE_URL/api/aviation/ask" \
    -H "Content-Type: application/json" \
    -H "X-Forwarded-For: 192.168.1.200" \
    -d "Quick test query $i"
  echo -e "\n"
done

echo "✅ API tests completed!"