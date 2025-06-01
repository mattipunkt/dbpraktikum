# Relationales Datenbank Prakitkum 

## Setup
### Voraussetzungen
- Java OpenJDK 24
- PostgreSQL-Datenbank
- Geduld

**1. Starten der Datenbank (mit Docker)**; *die Zugangsdaten für die DB sind in der Compose festgelegt*
```shell
# im Root des Projektes
docker compose up
```

**2. Starten des Projektes (am besten mit IntelliJ)**
- Als erstes den abgegebenen Ordner "source.zip" entpacken, Projekt in IntelliJ öffnen
- Dazu IntelliJ die richtige JDK installieren und Maven-Abhängigkeiten herunterladen lassen
- Main-Funktion in `dev.numerouno.Main` ausführen.

*oder*:
Die mitabgegebene JAR-Datei ausführen. **Benötigt mindestens Java JRE 17**

***Anmerkung***: Wir würden die erste Methode bevorzugen, JAR-Dateien zu packen ist immer so eine Sache mit den Abhängigkeiten... Wenn hier unerwartete Fehler auftreten (bspw. RuntimeExceptions, die das Programm beenden) bitte die IntelliJ-Variante verwenden.
```shell
java -jar dbpraktikum-1.0.jar
```

### Der Import kann einige Zeit in Anspruch nehmen!
Auf einem Intel i7-1260P mit 32GB RAM unter Linux dauerte der Import von allen Dateien knapp 10 Minuten.


## Team
- Matti Weidlich
- Marisa Weiß
