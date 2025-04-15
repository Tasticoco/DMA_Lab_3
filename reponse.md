# Laboratoire 4 
## Réponses aux questions

### Question 1.1

> Par rapport à un seul AP, que pouvez-vous dire sur la précision de la distance estimée ? Est-ce que la présence d’un obstacle (fenêtre, mur, personne) entre l’AP et le smartphone a une influence sur la précision ? Est-ce que faire une moyenne sur plusieurs mesures permet d’avoir une estimation plus fiable de la distance ?

La précision semble être assez stable lorsqu'on se trouve dans la même pièce, avec des fluctuation de +- 20 centimètres.

On a pu tout de même voir que les valeurs des distances elles-même sont tout de même pas super précise, du moins avec l'affichage donné. 

Ensuite, lorsqu'on sort de la salle ou se trouve la balise WifiRTT, les mesures commencent à fluctuer de quelques metres. Surtout si un objet solide se trouve entre le téléphone et le routeur (mur, porte, etc.)

Finalement, les mesures actuelles fluctuent très rapidement, faire une moyenne sur un set de ces mesures, comme la librairie que nous avions utilisé lors du laboratoire précédent, devrait améliorer les précisions, fondamentallement, une moyenne d'expériences similaires est toujours meilleures qu'une seule mesure. Même si cette sorte de lissage induit une latence. 

### Question 2.1

> Nous avons également placé des AP à différents endroits de l’étage B. La carte et la position de ces huit AP sont fournies dans le code. Pour activer une localisation sur l’étage B, il suffit de modifier la configuration placée dans la LiveData _mapConfig dans le WifiRttViewModel. Que pouvons-nous dire de la position obtenue en se promenant dans les couloirs de l’étage ? Doit-on tenir compte de tous les AP pour calculer la position ?

La localisation fonctionne bien si on ne prends pas tous les access points pour effectuer la trilatération pour éviter d'additionner les erreurs de tous les accès points. Au final, nous avons choisi de prendre les quatres AP les plus proches pour avoir une bonne stabilité.

### Question 2.2

> Pouvons-nous déterminer la hauteur du mobile par trilatération ? Si oui qu’est-ce que cela implique ? La configuration pour l’étage B contient la hauteur des AP et vous permet donc de faire des tests.

oui, d'utiliser 4 AP