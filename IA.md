# Utilisation de l’IA

- **Problème rencontré :** IntelliJ signalait une vulnérabilité sur la dépendance `langchain4j-google-genai` dans `pom.xml` : « WS-2019-0379 — 6.5 — Transitive Input Validation ».

- **Raison de l’utilisation de l’IA :** identifier la dépendance à l’origine de l’alerte et comprendre comment corriger sa version dans Maven.

- **Prompt utilisé :** une capture d’écran d’IntelliJ montrant le `pom.xml` et le message « Vulnerability found in dependency: WS-2019-0379 6.5 Transitive Input Validation ».

- **Réponse de l’IA :** extrait pertinent de la conversation :

  > Corrigé dans [pom.xml](pom.xml).
  >
  > L’alerte concernait `commons-codec`, dépendance indirecte de Gemini. [Détail Mend](https://www.mend.io/vulnerability-database/WS-2019-0379).
  >
  > - Version remplacée : **1.11 → 1.19.0**.
  > - Version réellement utilisée vérifiée.
  > - Compilation réussie.
  >
  > Dans IntelliJ : **Reload All Maven Projects** pour actualiser l’alerte.

  La correction a consisté à ajouter un bloc `dependencyManagement` dans `pom.xml` pour fixer `commons-codec` à la version `1.19.0`.
