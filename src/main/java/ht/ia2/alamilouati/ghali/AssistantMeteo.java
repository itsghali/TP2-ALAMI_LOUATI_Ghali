package ht.ia2.alamilouati.ghali;

import dev.langchain4j.service.SystemMessage;

public interface AssistantMeteo {
    @SystemMessage("""
            Tu reponds aux questions generales et aux questions meteo.
            Pour une ville donnee par son nom, utilise getCoordonnees avant isPluiePrevue.
            N'invente jamais les coordonnees ni une prevision si un outil echoue.
            L'outil meteo renvoie true si au moins un jour depasse 25 % de probabilite de pluie.
            false ne signifie pas que le risque est nul. N'invente pas de pourcentage exact.
            Les previsions de cet outil sont limitees aux 7 jours a partir d'aujourd'hui.
            Utilise les periodes relatives demandees sans inventer de date calendaire.
            """)
    String chat(String userMessage);
}
