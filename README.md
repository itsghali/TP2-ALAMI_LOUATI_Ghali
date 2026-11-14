# TP2-ALAMI_LOUATI_Ghali

Le SDK du projet et les configurations partagées Test4/Test5/Test6/Test7/TestMeteo utilisent
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

## Test 7 — utilisation d’outils

Lancer `TestMeteo` pour appeler directement l’API météo, ou `Test7Outils` pour la
conversation. Les deux configurations IntelliJ fixent JDK 25, la racine du projet
et l’exclusion des dépendances `provided`. `GEMINI_KEY` est nécessaire pour
`Test7Outils` ; `TestMeteo` utilise Open-Meteo sans clé. Une ligne vide est
ignorée ; `fin` termine la conversation.

L’interface `AssistantMeteo` est dans un fichier distinct. L’assistant conserve
20 messages et utilise `MeteoTool` dans le package personnel `outil.meteo`.
Le code fourni de la méthode météo est conservé, avec le seuil de 25 %. La
méthode de géocodage fournie a été ajoutée avec ses annotations `@Tool` et `@P`.
Les erreurs d’outils et d’arguments sont transmises au modèle pour lui permettre
d’expliquer un échec, sans inventer une météo.

Le message système demande de géocoder les noms de villes et précise que
`false` signifie « aucun jour au-dessus de 25 % », pas « pluie impossible ».
Les essais ci-dessous ont été faits sous JDK 25 le 14 novembre 2026, d’abord sans
outils, puis avec le seul outil météo, puis après ajout du géocodage.

### Liens du Test7 analysés

| Source | Accès et contenu |
|---|---|
| [Support LangChain4j](http://richard.grin.free.fr/emsi/casablanca-ia/supports/langchain4j.pdf) | HTTP 200 ; version 4.6 du 9/11/2026, AiServices, annotations Tool/P et outils |
| [Support LLM](http://richard.grin.free.fr/emsi/casablanca-ia/supports/llm.pdf) | HTTP 200 ; version 2.22 du 3/11/2026, protocole functionCall/functionResponse et rôle de l’application |
| [Open-Meteo](https://open-meteo.com/) | HTTP 200 ; API sans clé pour cet usage de cours |
| [Documentation prévisions](https://open-meteo.com/en/docs) | HTTP 200 ; tableau daily et probabilités, prévisions jusqu’à 16 jours |
| [Appel Paris de l’énoncé](https://api.open-meteo.com/v1/forecast?latitude=48.85&longitude=2.35&daily=precipitation_probability_max&forecast_days=3) | HTTP 200 ; observation initiale : 78 %, 78 %, 90 % du 14 au 16 novembre 2026 |
| [Code météo fourni](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/aide/outil/Open-Meteo/codeoutil1.html) | HTTP 200 ; classe reprise avec adaptation du package uniquement pour cette étape |
| [Aide AiServices du Test5](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/rag.html) | HTTP 200 ; interface et construction de l’assistant |
| [Documentation géocodage](https://open-meteo.com/en/docs/geocoding-api) | HTTP 200 ; recherche et coordonnées de la première correspondance |
| [Code géocodage fourni](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/aide/outil/Open-Meteo/codeoutil2.html) | HTTP 200 ; méthode conservée et annotations complétées |
| [Configuration logging](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/index.html#configlogging) | Ancre interne consultée ; Logback INFO déjà configuré |
| [Questions imposées](http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/index.html#questions1) | Ancre interne consultée ; coordonnées, demain, autre sujet, ville connue et Zzyzx |

Le lien Gson contenu dans le code fourni a aussi été consulté :
[projet officiel](https://github.com/google/gson), puis métadonnées Maven Central.
La version publiée 2.15.0 a été déclarée explicitement dans le POM.

**Écart de documentation :** le code pédagogique et son commentaire annoncent une
limite de 7 jours ; l’API actuelle permet 16 jours. La limite de 7 du code fourni
est conservée. Le géocodage prend la première correspondance, conformément au code :
des noms ambigus peuvent donc désigner un autre lieu que celui souhaité.

### Vérification directe de l’outil

`TestMeteo` à Paris (48.85, 2.35), J+0 sur 3 jours : **true**.
À Zzyzx (35.14276, -116.10474), J+0 sur 3 jours : **false**.
Les deux branches ont donc été obtenues avec le seuil fourni de 25 %.

### Sans outils — question et réponse réelles

**Essai 1 — question exacte :** Je dois partir dans 2 jours à Paris ; j'y resterai 3 jours. Est-ce que je dois mettre un parapluie dans mes valises ?

> Comme nous sommes actuellement en **mai** (ou début juin) et que les prévisions météo à 2 jours sont assez fiables, le réflexe idéal est de regarder une application météo pour Paris, mais statistiquement et de saison : **oui, glissez un petit parapluie pliant dans votre valise.**
>
> Voici pourquoi :
> 1. **La météo parisienne est capricieuse :** Le fameux "temps de Paris" est souvent imprévisible. Même s'il fait beau le matin, une "averse surprise" (souvent courte mais intense) peut survenir en fin de journée.
> 2. **Le format voyage :** Un petit parapluie compact ne prend presque pas de place ni de poids dans la valise.
> 3. **Le plan B :** Si vous l'oubliez, pas de panique ! Paris regorge de petites pharmacies, de supermarchés (Monoprix) et de marchands ambulants qui en vendent pour quelques euros.
>
> **Mon conseil :** Emportez-le. S'il ne sert pas, tant mieux ! S'il pleut, vous serez bien content de ne pas avoir à courir vous abriter sous un porchère ou à acheter un parapluie bas de gamme hors de prix près des sites touristiques.
>
> Bon voyage à Paris !

Appels LLM : 1. Outils demandés : aucun.


Le modèle affirme être « en mai (ou début juin) » alors que l’essai est du
14 novembre. Son conseil général ne constitue donc pas une prévision météo
actualisée.

### Avec le seul outil météo — questions et réponses réelles

**Essai 1 — question exacte :** J'ai prévu d'aller aujourd'hui à la ville dont la latitude est 48.85 et la longitude est 2.35 pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?

> Oui, je vous conseille de mettre un parapluie dans votre valise, car de la pluie est prévue durant votre séjour de 3 jours qui commence aujourd'hui à Paris (latitude 48.85, longitude 2.35).

Appels LLM : 2. Outils demandés : `isPluiePrevue` {"latitude":48.85,"nbJours":3,"nbJoursDebut":0,"longitude":2.35}.

**Essai 2 — question exacte :** Finalement, je ne vais partir que demain. Est-ce que tu me conseilles de prendre un parapluie ?

> Oui, je vous conseille toujours de prendre un parapluie. De la pluie est prévue sur votre période de 3 jours, qui commencera demain.

Appels LLM : 2. Outils demandés : `isPluiePrevue` {"nbJours":3,"longitude":2.35,"latitude":48.85,"nbJoursDebut":1}.

**Essai 3 — question exacte :** Quelle est la capitale du Japon ?

> La capitale du Japon est Tokyo.

Appels LLM : 1. Outils demandés : aucun.

**Essai 4 — question exacte :** J'ai prévu d'aller aujourd'hui à Paris pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?

> Oui, je vous conseille de mettre un parapluie dans votre valise, car de la pluie est prévue à Paris pour les 3 prochains jours.

Appels LLM : 2. Outils demandés : `isPluiePrevue` {"nbJoursDebut":0,"latitude":48.8566,"nbJours":3,"longitude":2.3522}.

**Essai 5 — question exacte :** J'ai prévu d'aller aujourd'hui à Zzyzx pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?

> Non, pas besoin de parapluie pour Zzyzx (en Californie) : aucune pluie n'est prévue pour vos trois jours sur place (c'est une zone extrêmement aride, dans la vallée de la Mort). Vous pouvez l'oublier sans problème !

Appels LLM : 2. Outils demandés : `isPluiePrevue` {"nbJoursDebut":0,"latitude":35.143,"longitude":-116.404,"nbJours":3}.


Avant ajout du géocodage, le modèle a répondu pour Zzyzx, mais a deviné une
longitude de -116.404 au lieu de -116.10474 obtenue par l’API. Sa réponse « aucune
pluie » allait aussi au-delà de ce que prouve un booléen basé sur un seuil de 25 %.
Ces limites ont motivé les consignes du message système final ; le code de l’outil
fourni n’a pas été réécrit.

### Avec les deux outils — questions et réponses réelles

**Essai 1 — question exacte :** J'ai prévu d'aller aujourd'hui à Paris pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?

> Pour votre séjour de 3 jours à Paris qui commence aujourd'hui, je vous conseille de mettre un parapluie dans votre valise, car il y a un risque de pluie au cours de cette période.

Appels LLM : 3. Outils demandés : `getCoordonnees` {"ville":"Paris"} ; `isPluiePrevue` {"nbJours":3,"nbJoursDebut":0,"longitude":2.3488,"latitude":48.85341}.

**Essai 2 — question exacte :** Finalement, je ne vais partir que demain. Est-ce que tu me conseilles de prendre un parapluie ?

> Pour votre séjour de 3 jours qui commence demain à Paris, je vous conseille également de prendre un parapluie, car un risque de pluie est prévu durant cette période.

Appels LLM : 2. Outils demandés : `isPluiePrevue` {"nbJoursDebut":1,"nbJours":3,"longitude":2.3488,"latitude":48.85341}.

**Essai 3 — question exacte :** Quelle est la capitale du Japon ?

> La capitale du Japon est Tokyo.

Appels LLM : 1. Outils demandés : aucun.

**Essai 4 — question exacte :** J'ai prévu d'aller aujourd'hui à VilleInexistanteGhali987654 pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?

> Je suis désolé, mais je ne parviens pas à trouver cette ville, il m'est donc impossible de vous dire s'il va pleuvoir et de vous conseiller sur votre valise.

Appels LLM : 2. Outils demandés : `getCoordonnees` {"ville":"VilleInexistanteGhali987654"}.


La question générale reçoit « Tokyo » sans appel d’outil. La ville fictive
n’est pas localisée ; le modèle signale qu’il ne peut pas donner de prévision.

### Nouvelle session Zzyzx avec logging

Question exacte : J'ai prévu d'aller aujourd'hui à la ville de Zzyzx pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?

Réponse exacte :

> Pour votre séjour de 3 jours à Zzyzx qui commence aujourd'hui, les prévisions n'indiquent pas une forte probabilité de pluie (le seuil de 25 % n'est pas dépassé sur la période). Toutefois, cela ne signifie pas que le risque est totalement nul.

Trois requêtes Gemini et trois réponses ont été capturées, pour **1874
tokens** au total selon les compteurs `usageMetadata.totalTokenCount` des réponses.
Ce total inclut les contextes répétés envoyés à chaque requête.

`.logRequestsAndResponses(true)` est activé. Cette version de l’intégration
journalise les objets LangChain4j plutôt que les seuls corps JSON HTTP.
`JournalGemini` ajoute donc une capture des véritables corps JSON via le client
HTTP personnalisé du SDK Google, sans enregistrer les en-têtes d’authentification.
Le journal d’une nouvelle exécution est `target/test7-echanges.jsonl` (non suivi
par Git). Il est réinitialisé au lancement.

Extraits réels ci-dessous : signatures opaques, champs non utiles et historiques
répétés sont omis. Aucune valeur de résultat d’outil n’a été inventée.
Les annotations en gras du brouillon précisent l’émetteur, la nature de l’échange
et son utilité.

**Application → API Gemini — requête 1 : question et déclarations des deux outils.**

```json
{
  "tools": [
    {
      "functionDeclarations": [
        {
          "description": "Retourne true si la probabilité d'avoir de la pluie pour la ville dont on donne la latitude et la longitude\nest importante durant une période de nbJours jours. La période commence nbJourDebut jours après aujourd'hui.\nPar exemple, si nbJoursDebut = 1 et nbJours = 2, la période de 2 jours commence demain (1 jour après aujourd'hui).\nSi nbJoursDebut = 0 et nbJours = 3, la période de 3 jours commence aujourd'hui.\nRetourne false sinon.\n",
          "name": "isPluiePrevue",
          "parameters": {
            "description": "",
            "properties": {
              "latitude": {
                "description": "Latitude",
                "type": "NUMBER"
              },
              "longitude": {
                "description": "Longitude",
                "type": "NUMBER"
              },
              "nbJoursDebut": {
                "description": "Nombre de jours après aujourd'hui pour indiquer le début de la période",
                "type": "INTEGER"
              },
              "nbJours": {
                "description": "Nombre de jours de la période",
                "type": "INTEGER"
              }
            },
            "propertyOrdering": [
              "latitude",
              "longitude",
              "nbJoursDebut",
              "nbJours"
            ],
            "required": [
              "latitude",
              "longitude",
              "nbJoursDebut",
              "nbJours"
            ],
            "type": "OBJECT"
          }
        },
        {
          "description": "Retourne la latitude et la longitude de la premiere ville trouvee pour son nom en anglais. Utiliser ces coordonnees pour les previsions meteo ; une ville introuvable produit une erreur.",
          "name": "getCoordonnees",
          "parameters": {
            "description": "",
            "properties": {
              "ville": {
                "description": "Nom de la ville en anglais",
                "type": "STRING"
              }
            },
            "required": [
              "ville"
            ],
            "type": "OBJECT"
          }
        }
      ]
    }
  ],
  "contents": [
    {
      "parts": [
        {
          "text": "J'ai prévu d'aller aujourd'hui à la ville de Zzyzx pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?"
        }
      ],
      "role": "user"
    }
  ]
}
```

**API Gemini → application — réponse 1 : demande d’exécuter getCoordonnees.**

```json
{
  "candidates": [
    {
      "content": {
        "role": "model",
        "parts": [
          {
            "functionCall": {
              "name": "getCoordonnees",
              "args": {
                "ville": "Zzyzx"
              },
              "id": "call_2422799232120224213"
            }
          }
        ]
      }
    }
  ],
  "usageMetadata": {
    "promptTokenCount": 523,
    "candidatesTokenCount": 20,
    "totalTokenCount": 543
  }
}
```

**Application → API Gemini — requête 2 : résultat du géocodage exécuté par Java.**

```json
{
  "contents": [
    {
      "parts": [
        {
          "functionResponse": {
            "id": "call_2422799232120224213",
            "name": "getCoordonnees",
            "response": {
              "result": "[35.14276,-116.10474]"
            }
          }
        }
      ],
      "role": "user"
    }
  ]
}
```

**API Gemini → application — réponse 2 : demande d’exécuter isPluiePrevue avec les coordonnées obtenues.**

```json
{
  "candidates": [
    {
      "content": {
        "role": "model",
        "parts": [
          {
            "functionCall": {
              "name": "isPluiePrevue",
              "args": {
                "latitude": 35.14276,
                "nbJoursDebut": 0,
                "longitude": -116.10474,
                "nbJours": 3
              },
              "id": "call_7128965308433499889"
            }
          }
        ]
      }
    }
  ],
  "usageMetadata": {
    "promptTokenCount": 578,
    "candidatesTokenCount": 48,
    "totalTokenCount": 626
  }
}
```

**Application → API Gemini — requête 3 : résultat booléen de l’outil météo exécuté par Java.**

```json
{
  "contents": [
    {
      "parts": [
        {
          "functionResponse": {
            "id": "call_7128965308433499889",
            "name": "isPluiePrevue",
            "response": {
              "result": "false"
            }
          }
        }
      ],
      "role": "user"
    }
  ]
}
```

**API Gemini → application — réponse 3 : réponse finale en langage naturel.**

```json
{
  "candidates": [
    {
      "content": {
        "role": "model",
        "parts": [
          {
            "text": "Pour votre séjour de 3 jours à Zzyzx qui commence aujourd'hui, les prévisions n'indiquent pas une forte probabilité de pluie (le seuil de 25 % n'est pas dépassé sur la période). Toutefois, cela ne signifie pas que le risque est totalement nul."
          }
        ]
      }
    }
  ],
  "usageMetadata": {
    "promptTokenCount": 642,
    "candidatesTokenCount": 63,
    "totalTokenCount": 705
  }
}
```


Le rôle `user` des messages `functionResponse` vient du protocole Gemini :
c’est l’application qui envoie le résultat de la méthode Java. Les appels Java
aux API de géocodage et météo se produisent entre ces échanges ; le LLM ne les
exécute pas lui-même.

### Proposition d’économie de tokens

Un outil unique recevant le nom de ville et la période pourrait effectuer le
géocodage puis l’appel météo en Java et renvoyer un résultat compact. Cela éviterait
l’aller-retour intermédiaire avec le LLM et une répétition du contexte : deux
requêtes Gemini pourraient suffire au lieu des trois observées. Cette proposition
est décrite pour le rendu ; le nouvel outil n’est pas implémenté.
