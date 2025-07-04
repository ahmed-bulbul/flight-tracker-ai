package ca.bazlur.flighttracker.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AviationAssistantService {
  private static final Logger logger = LoggerFactory.getLogger(AviationAssistantService.class);
  private final AviationAiService aviationAiService;

  public AviationAssistantService(@Value("${langchain4j.ollama.chat-model.base-url:http://localhost:11434}") String baseUrl,
                                  @Value("${langchain4j.ollama.chat-model.model-name:llama3.1:8b}") String modelName,
                                  @Value("${langchain4j.ollama.chat-model.temperature:0.2}") Double temperature,
                                  FlightDataFunctions flightDataFunctions) {

    var chatLanguageModel = OllamaChatModel.builder()
        .baseUrl(baseUrl)
        .modelName(modelName)
        .temperature(temperature)
        .build();

    aviationAiService = AiServices.builder(AviationAiService.class)
        .chatModel(chatLanguageModel)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
        .tools(flightDataFunctions)
        .build();
  }

  public String processQuery(String userQuery) {
    try {
      return aviationAiService.processQuery(userQuery);
    } catch (Exception e) {
      logger.error("error processing query", e);
      return "I'm currently experiencing technical difficulties. Please try again later.";
    }
  }
}