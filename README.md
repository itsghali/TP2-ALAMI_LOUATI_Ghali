# TP2-ALAMI_LOUATI_Ghali

Le SDK du projet et les configurations partagées Test4/Test5/Test6 utilisent
`temurin-25` (JDK 25). La compilation Maven cible Java 25.

## Test 4 — embeddings

Recharger Maven, puis utiliser la configuration IntelliJ partagée
`Test4Embeddings`. Elle inclut la dépendance de portée `provided`, nécessaire
au modèle local original. Tous les fichiers se compilent avec `mvn compile`.
JDK 25 et variable d’environnement `GEMINI_KEY` nécessaires pour la comparaison Google.
Pour éviter l’avertissement d’accès natif sous JDK 25, ajouter l’option JVM
`--enable-native-access=ALL-UNNAMED` dans la configuration d’exécution.

Le programme compare huit couples identiques avec le modèle local
`BgeSmallEnV15EmbeddingModel` et Google `gemini-embedding-001`, configuré avec
`SEMANTIC_SIMILARITY`. Les phrases sont envoyées par groupe pour limiter les appels.
L’essai initial avec un timeout de 100 ms a échoué ; l’exécution à 30 secondes a réussi.

### Résultats observés le 14 novembre 2026

Résultats issus de l’exécution des deux modèles, affichés avec six décimales.

| Couple | Relation | BGE local | Google |
|---|---|---:|---:|
| 1 | Chat / félin au repos (français) | 0.647512 | 0.932272 |
| 2 | Chat dort / ne dort pas | 0.979863 | 0.934852 |
| 3 | Chat / prêt immobilier | 0.673606 | 0.709510 |
| 4 | Très chaud / très froid | 0.908444 | 0.853333 |
| 5 | Ordinateur en panne / PC ne fonctionne plus | 0.673904 | 0.955802 |
| 6 | Ordinateur en panne / oiseaux | 0.686117 | 0.706770 |
| 7 | Chat / félin au repos (anglais) | 0.782622 | 0.927214 |
| 8 | Chat / prêt immobilier (anglais) | 0.345091 | 0.684514 |

Sur les exemples français 1/3 et 5/6, BGE classe le couple sans rapport devant
la paraphrase. Google donne le classement attendu sur ces exemples. En anglais,
les deux modèles classent le couple similaire 7 devant le couple sans rapport 8.
Ces quelques exemples ne constituent pas une évaluation générale des modèles.

Les oppositions des couples 2 et 4 gardent des scores élevés avec les deux modèles :
la similarité des embeddings ne garantit pas l’accord logique entre deux phrases.
Comparer surtout les classements au sein de chaque modèle, sans seuil commun imposé.

L’usage de l’IA est documenté dans [IA.md](IA.md) ; les apprentissages personnels
restent à compléter par l’étudiant.

## Test 5 — RAG 1

Lancer `ht.ia2.alamilouati.ghali.Test5Rag1` depuis la racine du projet,
avec `GEMINI_KEY` définie. Le fichier `infos.txt` est lu depuis ce répertoire.
Recharger Maven, puis utiliser la configuration IntelliJ partagée `Test5Rag1`.
Elle exclut la dépendance `provided` à l’exécution pour laisser Easy RAG charger
son seul modèle par défaut. Aucun changement de profil Maven n’est nécessaire.
L’option JVM `--enable-native-access=ALL-UNNAMED` peut également être utilisée.

Le code fourni dans `rag.html` a été repris : package et nom de classe adaptés,
clé et modèle renseignés, température de 0,3, mémoire de 10 messages, ingestion
du document et recherche dans un magasin vectoriel en mémoire. Le code Test4
reste inchangé. Sa dépendance initiale est conservée en commentaire. Une déclaration
de portée `provided` permet de compiler Test4 sans ajouter son modèle au runtime
du RAG. La configuration de lancement Test4 l’inclut explicitement ; celle de
Test5 utilise le runtime normal. Cette séparation évite le conflit de modèles
sans exclure de classe de la compilation. Les anciens profils ont été retirés.

### Liens du Test5 consultés le 14 novembre 2026

| Page | Vérification |
|---|---|
| [Code fourni](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/rag.html) | HTTP 200, contenu lu |
| [Dépendance Easy RAG](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/dependance.html) | HTTP 200, contenu lu |
| [Configuration Logback, optionnelle](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/logbackpourdebug.html) | HTTP 200, contenu lu ; réglage fontbox WARN déjà présent |
| [Répertoire Maven lié par l’aide](https://repo.maven.apache.org/maven2/dev/langchain4j/langchain4j-easy-rag/) | HTTP 200 ; version `1.23.0-beta33` disponible et POM lu |

### Essais et réponses réellement obtenues

1. **Sans RAG**, reprise du modèle du Test1, question : « Comment s’appelle le chat de Pierre ? »

   > Il y a beaucoup de "Pierre" célèbres ou dans la vie de tous les jours !
   >
   > Pourriez-vous préciser de quel Pierre vous parlez (un auteur, un personnage de fiction, une personnalité, ou un ami à vous) ?

2. **Avec RAG**, fichier initial :

   ```text
   Le nom du chien de Jacques est Médor.
   Le nom du chat de Pierre est Felix.
   La capitale de la France est Paris.
   ```

   Même question sur le nom du chat. Réponse :

   > Le chat de Pierre s'appelle Felix.

3. Question : « Pierre appelle son chat. Qu’est-ce qu’il pourrait dire ? »

   > Il pourrait dire « Felix ».

4. Dernière ligne remplacée par `La capitale de la France est Marseille.`,
   puis nouvelle exécution avec la question « Quelle est la capitale de la France ? » :

   > D'après les informations fournies, la capitale de la France est Marseille.

Cette dernière réponse reprend l’information volontairement fausse du fichier.
Dans cet essai, le contexte RAG a donc primé sur les connaissances générales du
modèle. La capitale réelle reste Paris. Le RAG apporte du contexte, mais ne
garantit pas la véracité du document fourni ; une autre version du modèle peut
réagir différemment.

L’état final conserve Marseille dans `infos.txt` et la question sur la capitale
dans `Test5Rag1`, pour reproduire le dernier essai. Les deux autres questions
figurent en commentaire : les réutiliser pour refaire les essais sur Felix.
Les avertissements de bibliothèques n’ont pas empêché l’exécution réussie.

## Test 6 — RAG 2

Lancer la configuration IntelliJ partagée `Test6Rag2`, configurée avec
`temurin-25` (JDK 25), depuis la racine du projet. `GEMINI_KEY` doit être définie.
Le fichier `ml.pdf` doit rester à cette racine. Saisir une question, puis Entrée ;
une ligne vide est ignorée et `fin` termine la conversation, sans distinction
entre majuscules et minuscules.

`Test6Rag2` reprend Test5 et la boucle fournie par le professeur : le PDF remplace
`infos.txt`. Easy RAG charge et découpe le document, calcule les embeddings locaux
et les stocke en mémoire une fois au démarrage. Chaque question récupère les
segments pertinents. Le même assistant conserve une mémoire de 10 messages entre
les questions, avec une température de 0,3. Aucune nouvelle dépendance nécessaire.

### Liens analysés le 14 novembre 2026

Les quatre liens directs de la section Test6 ont été consultés, ainsi que le PDF
demandé et la page complémentaire sur la préparation des documents.

| Lien | Accès et apport pour Test6 |
|---|---|
| [Boucle de conversation](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/conversation.html) | HTTP 200 ; boucle `Scanner` reprise, ligne vide ignorée, arrêt par `fin` |
| [Code Easy RAG fourni](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/rag.html) | HTTP 200 ; ingestion, magasin en mémoire, retriever et mémoire conservés |
| [Exercice embeddings lié](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/index.html#embeddings) | HTTP 200 ; limites du modèle anglais sur du français ; modèle local conservé pour éviter les appels d’embeddings externes |
| [RAG naïf et préparation — TP4](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp4-rag/index.html#naif) | HTTP 200 ; distingue les étapes et explique les limites des réglages par défaut ; lecture de référence, aucun exercice TP4 réalisé |
| [Préparation des documents, complément lié depuis TP4](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp4-rag/aide/preparation.html) | HTTP 200 ; nettoyage, métadonnées et découpage par diapositive ; amélioration laissée au TP4 |
| [PDF machine learning](http://richard.grin.free.fr/emsi/casablanca-ia/supports/ml.pdf) | HTTP 200 ; fichier téléchargé, analysé et utilisé par Test6 |

Le PDF actuel est la **version 1.53 du 10 novembre 2026**, sur **27 pages PDF**.
Il est plus récent que la copie référencée dans l’analyse initiale du cours.
La lecture locale a extrait 64 896 caractères avec Tika. Empreinte SHA-256 :
`D77824FA500C57B982011C892960ED38A3D716CC052681336469EC96F1CDBB7E`.

La [documentation officielle Easy RAG](https://docs.langchain4j.dev/tutorials/rag/#easy-rag)
a également été consultée : chargement PDF avec Tika, modèle local quantifié et
découpage par défaut. Ces réglages ne sont pas remplacés par ceux du TP4.

### Conversation réellement obtenue sous JDK 25

Une ligne vide a d’abord été envoyée : nouvelle invitation à poser une question,
sans réponse du modèle.

**Question 1 :** `Quelle difference entre apprentissage supervise et auto-supervise selon ce support ?`

> D'après le support fourni, voici la différence entre les deux types d'apprentissage :
>
> * **Apprentissage supervisé :** Il utilise des données d'entrée accompagnées d'une étiquette ou d'une variable cible (par exemple, des photos de chiens et de chats étiquetées "chien" ou "chat" par des humains).
> * **Apprentissage auto-supervisé (SSL) :** Il se situe à mi-chemin entre le supervisé et le non supervisé. Le modèle apprend à partir de données non annotées, mais des « étiquettes » sont **générées automatiquement** à partir des données d'entraînement (comme dans le pré-entraînement des LLM pour prédire le prochain mot).

**Question 2, dans la même session :** `Donne un exemple du second type cite dans ta reponse.`

> D'après le support fourni dans la première réponse, le deuxième type cité (l'apprentissage auto-supervisé) a pour exemple :
>
> * **Le pré-entraînement des LLM pour prédire le prochain mot** (afin de comprendre le langage et les patterns).

`FIN` a ensuite terminé le programme avec un code de sortie **0**.
Les réponses sont cohérentes avec les diapositives sur les apprentissages
supervisé et auto-supervisé et les étapes d’apprentissage d’un LLM. La deuxième
question est comprise en référence à la première réponse. La question utilisée
est différente de l’exemple sur l’objectif du cours, exclu du bonus par l’énoncé.
Conserver la question 1 et sa réponse pour le corps du futur email de rendu.

Les sept fichiers Java du projet compilent ensemble sous Java 25. Les essais de
conversation ci-dessus ont été exécutés en ligne de commande sous JDK 25.
Le lancement interactif dans IntelliJ n’a pas pu être vérifié pendant cette
intervention : l’accès à la fenêtre a été refusé. La configuration de lancement
enregistrée fixe explicitement JDK 25, le répertoire du projet et l’exclusion des
dépendances `provided`, comme pour Test5.

Les avertissements de journalisation et de polices PDF n’ont pas bloqué ces essais.
Ils rappellent néanmoins qu’une extraction PDF peut perdre certains caractères.
Le modèle local anglais et le découpage fixe d’un PDF contenant plusieurs diapos
par page peuvent aussi réduire la pertinence : ces résultats ponctuels ne
garantissent pas toutes les réponses. Le RAG avancé et la préparation détaillée
des supports restent hors du Test6.
