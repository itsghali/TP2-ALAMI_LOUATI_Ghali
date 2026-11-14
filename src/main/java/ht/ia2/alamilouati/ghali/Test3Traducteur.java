
package ht.ia2.alamilouati.ghali;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class Test3Traducteur {

    public static void main(String[] args) {

        // 1. Configuration du modele Gemini
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Cle API Gemini manquante."
            );
        }

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .build();

        // ========================================
        // TEST 3.1 : TEMPLATE AVEC UN PARAMETRE
        // ========================================

        PromptTemplate template1 = PromptTemplate.from(
                "Traduis le texte suivant en anglais : {{texte}}"
        );

        String texte1 = "Bonjour";

        Prompt prompt1 = template1.apply(
                Map.of("texte", texte1)
        );

        System.out.println("=== TEST 3.1 ===");
        System.out.println("Prompt : " + prompt1.text());

        String reponse1 = model.chat(
                prompt1.toUserMessage()
        ).aiMessage().text();

        System.out.println("Traduction : " + reponse1);


        // ========================================
        // TEST 3.2 : TEMPLATE AVEC DEUX PARAMETRES
        // ========================================

        PromptTemplate template2 = PromptTemplate.from(
                "Traduis le texte suivant en {{langue}} : {{texte}}"
        );

        String langue = "espagnol";
        String texte2 = "Bonjour";

        Prompt prompt2 = template2.apply(
                Map.of(
                        "langue", langue,
                        "texte", texte2
                )
        );

        System.out.println("\n=== TEST 3.2 ===");
        System.out.println("Prompt : " + prompt2.text());

        String reponse2 = model.chat(
                prompt2.toUserMessage()
        ).aiMessage().text();

        System.out.println("Traduction : " + reponse2);

    }
}