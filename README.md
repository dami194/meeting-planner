# Meeting Planner
Gestionnaire des salles de réunion d'une PME, via une API REST en Java.

Une petite entreprise compte mettre en place un utilitaire pour la gestion de ses salles qui devient trop complexe durant le COVID.
Les locaux de l'entreprise comptent 12 salles qui peuvent être utilisées pour des réunions. Chaque salle a un nom, une capacité en nombre de places et une liste d'équipements.
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
- elle dispose des équipements prévus pour le type de réunion ou des équipements amovibles nécessaires sont disponibles

**Le but de l'API est de pouvoir réserver une ou des réunions en leur attribuant à chacune la meilleure salle adéquate**.

## Lancement et utilisation de l'API

