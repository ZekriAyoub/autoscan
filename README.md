# Nom du projet : ScanMyCar 🚗

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) ![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white) ![Markdown](https://img.shields.io/badge/Markdown-000000?style=flat&logo=markdown&logoColor=white) ![JavaFX](https://img.shields.io/badge/JavaFX-1F72C1?style=flat&logo=java&logoColor=white)

## Description du Projet

ScanMyCar est une application de gestion des véhicules conçue pour les contrôleurs techniques. Grâce à la reconnaissance optique de caractères (OCR), elle permet de scanner les plaques d'immatriculation et d'accéder instantanément aux informations du véhicule, aux données du propriétaire et à l'historique des contrôles techniques. L'application permet également d'ajouter de nouveaux véhicules et leurs propriétaires à la base de données, facilitant ainsi la gestion et le suivi des inspections. On y retrouve également une option qui permet d'afficher des graphiques mettant en évidence l'état des véhicules enregistrés dans la base de données ainsi que le nombre de contrôles techniques effectués chaque mois de l'année. Un service d'aide est également disponible sur l'application. 


## Choix de l'Architecture

L'architecture retenue pour ce projet est _model-view-presenter_. 


## Plan de Tests Fonctionnels

Les tests fonctionnels élémentaires pour le projet sont les suivants :

### Test de lancement de l’application
- Démarrer l’application avec `mvn exec:java "-Dexec.mainClass=scanmycar.Main"`.
- Résultat attendu : L’interface utilisateur s’ouvre sans erreur.

### Test de scan d’une plaque d’immatriculation
- Prendre une image d’une plaque et lancer la reconnaissance OCR.
- Résultat attendu : Le numéro de la plaque est correctement détecté et affiché.

### Test d’ajout d’un véhicule
- Entrer les informations d’un véhicule (modèle, marque, année, etc.) et enregistrer.
- Résultat attendu : Le véhicule est bien ajouté à la base de données et s’affiche dans la liste.

### Test d'ajout d'un véhicule dont le propriétaire est déjà enregistré 
- Entrer les informations du véhicule + cocher la case `déjà enregistré` et enregistrer.
- Résultat attendu : Le véhicule est bien ajouté à la base de données et le propriétaire est relié à ce nouveau véhicule en plus.

### Test d’ajout d’un conducteur
- Ajouter un conducteur avec ses informations lors de l'ajout d'un véhicule.
- Résultat attendu : Le conducteur est bien enregistré et affiché.

### Test de consultation de l’historique des visites techniques
- Sélectionner un véhicule et afficher l’historique des visites techniques.
- Résultat attendu : La liste des visites passées s’affiche correctement.

### Test d'ajout d'une visite
- Ajouter une visite à l'historique de visites d'un véhicule.
- Résultat attendu : la visite est ajoutée à la base de donnée et l'état du vehicule est mis à jour.

### Test de génération d'un rapport de visite technique
- Cliquer sur le bouton `Imprimer Rapport`.
- Résultat attendu : le rapport d'inspection est bien généré au format pdf dans le dossier `Reports`.

### Test d'envoi de mail automatique
- Ajouter un nouveau véhicule et son propriétaire grace au formulaire.
- Rédultat attendu : Envoi automatique d'un mail au propriétaire pour confirmer son inscription.

### Test des performances et de la fluidité
- Effectuer plusieurs actions en parallèle (scan, ajout de véhicules, navigation).
- Résultat attendu : L’application reste fluide et réactive grâce au multithreading.

La couverture de test du `model` est de `83%`

## Installation et utilisation

Pour utiliser l'application, suivez les étapes suivantes : 

1. Clonez ce repository :
   ```bash
   git clone https://github.com/ZekriAyoub/autoscan.git
   ```
2. Assurez-vous d'avoir Maven installé, puis exécutez la commande suivante pour compiler le projet avec Maven :
   ```bash
   mvn clean compile
   ```
3. Renommez le fichier `.env.example` en `.env` et remplacez les valeurs des variables par : 
   ```bash
   EMAIL_USERNAME=scanmycarbot@gmail.com
   EMAIL_PASSWORD=rqioxqatpdlpbyfl
   ```      
4. Démarrez l'application en exécutant la commande :  
   ```bash
   mvn exec:java "-Dexec.mainClass=scanmycar.Main"
   ```

## Problèmes connus de l'application

Lors de la réalisation des tests fonctionnels, nous avons constatés les problèmes suivants : 

- Lors du scan, tesseract se trompe parfois dans la reconnaissance des caractères.

## Remarques 


Ce projet utilise un compte Gmail gratuit avec un mot de passe d’application pour l’envoi d’e-mails. La limite est d’environ 500 e-mails/jour, ce qui est suffisant dans le cadre de notre projet scolaire.

Dans un contexte professionnel, on recommande des alternatives plus robustes :

- SendGrid : 100 mails/jour gratuits (plan payant ensuite), API moderne, gestion de templates — parfait pour les e-mails transactionnels.

- Mailgun : Excellente délivrabilité, validation des mails, idéal pour les backends.

- Amazon SES : Très bon marché et scalable, mais nécessite une configuration technique (DNS, SPF, DKIM).

- Brevo : Respectueux du RGPD, 300 mails/jour gratuits, interface simple pour marketing et transactionnel.
