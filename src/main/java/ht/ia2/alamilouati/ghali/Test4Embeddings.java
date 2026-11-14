package ht.ia2.alamilouati.ghali;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel.TaskTypeEnum;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Test4Embeddings {

    public static void main(String[] args) {
        // Memes couples pour les deux modeles : similaires, opposes et sans rapport.
        String[][] couples = {
                {"Le chat dort sur le canapé.", "Un félin se repose sur le sofa."},
                {"Le chat dort sur le canapé.", "Le chat ne dort pas sur le canapé."},
                {"Le chat dort sur le canapé.", "La banque accorde un prêt immobilier."},
                {"Il fait très chaud aujourd'hui.", "Il fait très froid aujourd'hui."},
                {"Mon ordinateur est en panne.", "Mon PC ne fonctionne plus."},
                {"Mon ordinateur est en panne.", "Les oiseaux chantent dans le jardin."},
                {"The cat is sleeping on the couch.", "A feline is resting on the sofa."},
                {"The cat is sleeping on the couch.", "The bank approves a home loan."}
        };

        EmbeddingModel modeleLocal = new BgeSmallEnV15EmbeddingModel();
        afficherSimilarites("BgeSmallEnV15 (local, entraine en anglais)", modeleLocal, couples);

        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Cle API Gemini manquante : definir GEMINI_KEY.");
        }

        EmbeddingModel modeleGoogle = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .taskType(TaskTypeEnum.SEMANTIC_SIMILARITY)
                // L'essai a 100 ms a echoue (timeout) ; delai augmente apres test.
                .timeout(Duration.ofSeconds(30))
                .maxRetries(0)
                .build();

        afficherSimilarites("Google gemini-embedding-001", modeleGoogle, couples);
    }

    private static void afficherSimilarites(String nom, EmbeddingModel modele, String[][] couples) {
        // Un appel groupe evite un appel distant pour chaque phrase.
        List<TextSegment> textes = new ArrayList<>();
        for (String[] couple : couples) {
            textes.add(TextSegment.from(couple[0]));
            textes.add(TextSegment.from(couple[1]));
        }

        System.out.println("\n=== " + nom + " ===");
        List<Embedding> embeddings = modele.embedAll(textes).content();

        for (int i = 0; i < couples.length; i++) {
            double similarite = CosineSimilarity.between(
                    embeddings.get(2 * i), embeddings.get(2 * i + 1));

            System.out.println("\nCouple " + (i + 1));
            System.out.println("Phrase 1 : " + couples[i][0]);
            System.out.println("Phrase 2 : " + couples[i][1]);
            System.out.printf(Locale.ROOT, "Similarite cosinus : %.6f%n", similarite);
        }
    }
}
