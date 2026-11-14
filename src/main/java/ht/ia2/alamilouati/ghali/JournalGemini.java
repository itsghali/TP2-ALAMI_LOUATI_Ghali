package ht.ia2.alamilouati.ghali;

import com.google.genai.Client;
import com.google.genai.types.ClientOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Capture les vrais corps JSON HTTP pour expliquer les outils dans le compte rendu.
 * Les entetes d'authentification ne sont jamais enregistres.
 */
public class JournalGemini {
    public static Client creerClient(String cle) {
        Path journal = Path.of("target/test7-echanges.jsonl");
        try {
            Files.createDirectories(journal.getParent());
            Files.writeString(journal, "");
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de creer le journal JSON.", e);
        }
        AtomicInteger numero = new AtomicInteger();
        OkHttpClient http = new OkHttpClient.Builder().addInterceptor(chaine -> {
            int appel = numero.incrementAndGet();
            Buffer contenu = new Buffer();
            if (chaine.request().body() != null) {
                chaine.request().body().writeTo(contenu);
            }
            enregistrer(journal, appel, "requete", contenu.readUtf8(), cle);
            Response reponse = chaine.proceed(chaine.request());
            // peekBody permet au SDK de lire ensuite la meme reponse normalement.
            enregistrer(journal, appel, "reponse", reponse.peekBody(2_000_000).string(), cle);
            return reponse;
        }).build();
        return Client.builder().apiKey(cle)
                .clientOptions(ClientOptions.builder().customHttpClient(http).build())
                .build();
    }

    private static synchronized void enregistrer(Path chemin, int numero, String direction,
                                                 String corps, String cle) throws IOException {
        JsonObject entree = new JsonObject();
        entree.addProperty("numero", numero);
        entree.addProperty("direction", direction);
        entree.add("json", JsonParser.parseString(corps.replace(cle, "[MASQUE]")));
        Files.writeString(chemin, entree + System.lineSeparator(), StandardOpenOption.APPEND);
    }
}
