package ht.ia2.alamilouati.ghali;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import ht.ia2.alamilouati.ghali.outil.meteo.MeteoTool;

import java.util.Scanner;

public class Test7Outils {
    public static AssistantMeteo creerAssistant(ChatModel modele) {
        return AiServices.builder(AssistantMeteo.class)
                .chatModel(modele)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .tools(new MeteoTool())
                // Une ville introuvable est signalee au LLM, qui peut repondre sans inventer.
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.sendExceptionMessageToLlm())
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.sendExceptionMessageToLlm())
                .build();
    }

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");
        if (cle == null || cle.isBlank()) {
            throw new IllegalStateException("La variable GEMINI_KEY n'est pas definie.");
        }
        ChatModel modele = GoogleGenAiChatModel.builder()
                .client(JournalGemini.creerClient(cle))
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.3)
                .logRequestsAndResponses(true)
                .build();
        AssistantMeteo assistant = creerAssistant(modele);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.println("Posez votre question : ");
                String question = scanner.nextLine();
                if (question.isBlank()) {
                    continue;
                }
                System.out.println("==================================================");
                if ("fin".equalsIgnoreCase(question)) {
                    break;
                }
                String reponse = assistant.chat(question);
                System.out.println("Assistant : " + reponse);
                System.out.println("==================================================");
            }
        }
    }
}
