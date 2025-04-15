# Laboratoire 4 
## Réponses aux questions

### Question 1.1

> Par rapport à un seul AP, que pouvez-vous dire sur la précision de la distance estimée ? Est-ce
que la présence d’un obstacle (fenêtre, mur, personne) entre l’AP et le smartphone a une
influence sur la précision ? Est-ce que faire une moyenne sur plusieurs mesures permet d’avoir
une estimation plus fiable de la distance ?

La précision semble être assez stable lorsqu'on se trouve dans la même pièce, avec des fluctuation de +- 20 centimètres.

On a pu tout de même voir que les valeurs des distances elles-même sont tout de même pas super précise, du moins avec l'affichage donné. 

Ensuite, lorsqu'on sort de la salle ou se trouve la balise WifiRTT, les mesures commencent à fluctuer de quelques metres. Surtout si un objet solide se trouve entre le téléphone et le routeur (mur, porte, etc.)

Finalement, les mesures actuelles fluctuent très rapidement, faire une moyenne sur un set de ces mesures, comme la librairie que nous avions utilisé lors du laboratoire précédent, devrait améliorer les précisions, fondamentallement, une moyenne d'expériences similaires est toujours meilleures qu'une seule mesure. Même si cette sorte de lissage induit une latence. 