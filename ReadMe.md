# context-free
---
## Voraussetzungen
Unterstützt werden aktuell die Plattformen Windows, Linux und Mac.
Benötigt wird Java in der Version 11 oder höher.

## Starten der Anwendung
Öffnen Sie zunächst Ihr Terminal/CMD Fenster und navigieren in den Ordner, 
in welchem sich die Datei context-free-1.0-SNAPSHOT.jar befindet.

Zum Starten der Anwendung benutzen Sie folgenden Befehl:
`java -jar context-free-1.0-SNAPSHOT.jar`
## Bedienungshinweise
* Eingabe von Terminalen und Nichtterminalen
	Um Terminale und Nichtterminale zu definieren, geben Sie diese Kommasepariert in die entsprechenden Felder ein
* Eingabe von Produktionen
	Um Produktionen zu definieren benutzen Sie folgendes Schema:
	  Nichtterminal -> {Terminal U Nichtterminal}
	Um mehrere Produktionen zusammenzufassen...
	  S -> a
	  S -> b
	...kann folgende Kurzschreibweise verwendet werden:
	  S -> a | b
* Eingabe einer epsilon-Produktion
	Um ein epsilon zu den Produktionen hinzuzufügen, klicken Sie in eines der Testfelder bei den Produktionen 
	und verwenden Sie den Shortcut ALT+e um ein epsilon einzufügen
* Anlegen eines Testwortes
	Klicken Sie auf den "+" Button in der Leiste "Testwörter". 
	Geben Sie ein beliebiges Wort ein, welches Sie testen möchten.
	Durch drücken der "Enter"-Taste fügen Sie das Wort hinzu.
	Wird es grün eingefärbt gehört es zur Sprache.
	Bei einer roten Einfärbung gehört es nicht zur Sprache.
* Generierung eines Syntaxbaums
	Ein Syntaxbaum kann ausschließlich durch ein Wort generiert werden, welches zur definierten Sprache gehört.
	Legen Sie ein neues Testwort an. Falls es grün markiert ist, können Sie darauf klicken.
	Der entsprechende Syntaxbaum zu diesem Testwort wird erzeugt.
