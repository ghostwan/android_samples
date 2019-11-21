# Am I home ?

- Créer les écrans :
    - un écran principale sépare en deux :
        - vue du statut : suis je ou non a la maison
        - vue de l'historique des évenements
    - un écran de setting
        - permet de paramétrer les méthodes de la localisation
        - permet de paramétrer la position pour le geofencing    
        - permet de paraméter l'ip de ce qu'il faut pinguer pour le ing
        
- Méthodes de localisation
    - Méthode manuel (tuile, widget, notif...) 
    - Méthode utilisant le géofencing
    - Méthode utilisant le ping
    
- Envoyer des notifications dans le cas des détection automatique
- Calculer le temps entre détection manuel et automatique
- Historique


## POC 1 - Boutons manuels + Preference

- stocker shared preference pour a la maison ✔︎
- ecran qui dit a la maison ou pas ✔︎
- créer une tuile pour dire a la maison si pas a la maison et sortie de la maison si a la maison ✔︎
- stocker l'historique des changements (wip)
    

## POC 2 - Geofencing + Room

- stocker donner dans une BD room
- Rajout de la détection par geofencing
- Comparer les valeurs manuels et automatiques

 
         