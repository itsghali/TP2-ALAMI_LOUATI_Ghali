
package ht.ia2.alamilouati.ghali;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiTokenUsage;
import dev.langchain4j.model.output.TokenUsage;

public class Test2Couts {

    public static void main(String[] args) {

        // 1. Initialisation du modele
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-3.5-flash-lite")
                .build();

        // 2. Meme question que Test1
        String question = "Traduis Bonjour en anglais";

        // 3. Appel au modele
        ChatResponse response = model.chat(
                UserMessage.from(question)
        );

        // 4. Affichage de la reponse
        System.out.println("Reponse : "
                + response.aiMessage().text());

        // 5. Recuperation des tokens
        TokenUsage usage = response.tokenUsage();

        int entree = usage.inputTokenCount();
        int sortie = usage.outputTokenCount();
        int total = usage.totalTokenCount();

        // Tokens de raisonnement Gemini
        int thinking = 0;

        if (usage instanceof GoogleGenAiTokenUsage) {
            GoogleGenAiTokenUsage geminiUsage =
                    (GoogleGenAiTokenUsage) usage;

            if (geminiUsage.thoughtsTokenCount() != null) {
                thinking = geminiUsage.thoughtsTokenCount();
            }
        }

        System.out.println("\n--- TOKENS ---");
        System.out.println("Tokens entree : " + entree);
        System.out.println("Tokens sortie : " + sortie);
        System.out.println("Tokens raisonnement : " + thinking);
        System.out.println("Total rapporte : " + total);

        // 6. Tarifs USD / 1 million de tokens
        double prixEntree = 0.30;
        double prixSortie = 2.50;

        // 7. Calcul des couts
        double coutEntree = entree * prixEntree / 1_000_000.0;

        double coutSortie = (sortie + thinking)
                * prixSortie / 1_000_000.0;

        double coutTotal = coutEntree + coutSortie;

        System.out.println("\n--- COUT EN DOLLARS ---");

        System.out.printf(java.util.Locale.US,
                "Cout entree : %.10f $%n", coutEntree);

        System.out.printf(java.util.Locale.US,
                "Cout sortie : %.10f $%n", coutSortie);

        System.out.printf(java.util.Locale.US,
                "Cout total : %.10f $%n", coutTotal);

        // 8. Nombre de requetes pour atteindre 1 dollar
        if (coutTotal > 0) {
            long nombreRequetes =
                    (long) Math.ceil(1.0 / coutTotal);

            System.out.println("\n--- RESULTAT ---");
            System.out.println("Requetes pour atteindre 1 $ : "
                    + nombreRequetes);
        }

    }
}
