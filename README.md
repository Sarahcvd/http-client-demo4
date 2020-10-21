![Java CI with Maven](https://github.com/Sarahcvd/http-client-demo/workflows/Java%20CI%20with%20Maven/badge.svg)

Dette prosjektet består av en HTTP-server og klient som kobles til en postgresql database. Vi har lagd tester for å se at planlagd funksjonalitet kjøres korrekt (tester mot: server, klient, querystring og database). Hovedklassen i prosjektet er server-klassen, denne kjøres for å starte servern (Forbindelse åpnes på port 8080 - via localhost). Klient-klassen brukes for å "snakke" med servern, data kan lastes opp og ned til serveren (html/txt objekter og css funksjonalitet). Det går ann å kommunisere med server i nettleseren. Workerdaoklassen lagrer bruker-input fra nettleser till databasen.

Vi har lagd threads i en uendelig "while-loop" for å holde serveren "åpen" sånn at den alltid er tilgjengelig for nye forbindelser. Kode for å stenge forbindensen er også lagd inn, "connection-close" for at nettsiden ikke står stille når brukeren prøver å kalle på serveren.

For å kommunisere med serveren:
Kjør mvn clean for å rense /target, kjør deretter mvh package for å opprette .jar filen som du skal kjøre. Konfigurasjonsfilen din skal du døpe til "pgr203.properties", den skal inneholde: 
* verdier for dataSource.url, dataSoure.username og dataSource.password som peker på en tom database

Etter å ha kjørt mvn package kan du kjøre serveren gjennom å skrive: java -jar *filepath til jar-fil*/http-client-demo-1.0-SNAPSHOT.jar

Når brukeren har opprettet forbindelse mot serveren så har denne mulighet til å lagre informasjon til arbeidere som outputtes i en HTML fil og lagres i en database. 

Prosjektet er bygget av Sarah Christine Van Dijk og Wali Gustav Björk
