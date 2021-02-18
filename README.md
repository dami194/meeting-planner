# Meeting Planner
Gestionnaire des salles de réunion d'une PME, via une API REST en Java.

Une petite entreprise compte mettre en place un utilitaire pour la gestion de ses salles qui devient trop complexe durant le COVID.  
Les locaux de l'entreprise comptent 12 salles qui peuvent être utilisées pour des réunions.  
Chaque salle a un nom, une capacité en nombre de places et une liste d'équipements.  

Il y a quatre types d'équipements : écran, tableau, pieuvre et webcam. Une salle contient au maximum un équipement de chaque type.
Chacune de ces salles peut être réservée pour une réunion à chacun des créneaux de réservation disponibles. Ces créneaux vont de 8h-9h à 19h-20h (sans interruption).

## Détails d'une réservation de salle

Par défaut, sans réservation effectuée, une salle est libre (réservable).
Elle peut être réservée par créneau de 1 heure : elle devient alors indisponible pour ce créneau dés qu'elle a été réservée pour une réunion.

En raison de la pandémie actuelle, une salle doit aussi être libre une heure avant sa prochaine réservation pour permettre aux agents de nettoyage de désinfecter les locaux.
Autrement dit, une salle réservée pour une réunion de 10h à 11h ne pourra pas être réservée de 11h à 12h.

En raison également de la pandémie actuelle, la capacité des salles a été réduite de 30% (cette réduction est paramétrable). 

L'entreprise dispose, en plus des équipements déjà présents dans les salles, d'équipements amovibles qu'il est possible d'utiliser dans n'importe quelle salle ne contenant pas les 
équipements correspondants. Il y a 4 pieuvres, 5 écrans, 4 webcams et 2 tableaux. Ces équipements sont bien entendu disponibles à chaque créneau mais dés qu'ils sont utilisés pour 
un créneau, ils ne sont plus disponibles pour ce créneau.

Il y a quatre types de réunions, qui nécessitent chacune un certain nombre d'équipements : 
- les réunions simples (RS) qui ne nécessitent aucun équipement
- les séances de partage et d'études de cas (SPEC) qui nécessitent un tableau
- les réunions couplées (RC), qui nécessitent un tableau, un écran et une pieuvre
- les visioconférences (VC), qui nécessitent un écran, une pieuvre et une webcam

## Conditions de réservation d'une salle

Pour résumer, une salle peut être réservée pour une réunion à un certain créneau et avec un certain nombre de personnes si : 

- elle est libre à ce créneau
- elle était libre au créneau précédent
- elle est assez grande pour accueillir le nombre de personnes attendues
- elle dispose des équipements prévus pour le type de réunion ou des équipements amovibles nécessaires sont disponibles à ce créneau

**Le but de l'API est de pouvoir réserver une ou des réunions en leur attribuant à chacune la meilleure salle adéquate**.

## Lancement et utilisation de l'API

Il s'agit d'une API REST développée en Java à l'aide de Spring Boot.

### Lancement

1) Télécharger le Zip du projet ou le cloner
2) Builder le projet : ouvrir un terminal et lancer la commande `mvn install` à la racine du projet
3) Pour lancer l'application, plusieurs choix s'offrent à vous : 
    - A l'aide de maven : taper la commande `mvn spring-boot:run` à la racine du projet
    - A l'aide d'un JDK : taper la commande `java -jar target/meeting-planner-0.0.1-SNAPSHOT.jar` à la racine du projet
    - A l'aide d'un IDE : ouvrir le projet dans un IDE et lancer la classe `MeetingPlannerApplication` en tant qu'application Java

### Utilisation

L'application ne comporte pas de front. Elle dispose cependant de plusieurs endpoints : 

- "/rooms" : pour consulter la liste des salles (GET)
- "/bookRoom" : pour réserver une réunion (POST)
- "/bookRooms" : pour réserver un ensemble de réunions (POST)

Un exemple de requête pour /bookRooms se situe dans src/test/resources du projet. Les créneaux sont de la forme "EIGHT_NINE" (pour 8h-9h).

## Idées d'amélioration

- Sauvegarder les réunions planifiées, pour pouvoir consulter les salles réservées et réunions planifiées par créneau
- Faire une IHM pour afficher un semainier avec les réunions déjà planifiées et les créneaux encore disponibles

## Note sur l'algorithme de choix de salle

Si plusieurs salles remplissent tous les critères de réservation (disponibilité, capacité, équipement), l'algorithme choisit en priorité la salle avec le moins d'équipement, plutôt que la salle avec le moins de capacité.
Par exemple : si une réunion de type SPEC (nécessitant seulement un tableau) peut se faire dans deux salles, une salle A avec un tableau et une capacité de 15 et une salle B avec un tableau, une webcam, un écran et une capacité de 5, la salle A sera choisie.
