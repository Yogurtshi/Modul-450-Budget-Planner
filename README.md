# M450 Budget Planner

Team: Bilous Mariia, Holiuk Anna, Gyger Yoshi

## Projektbeschreibung

Der Benutzer kann seinen Kontostand eingeben. Anschliessend gibt er an, welche Transaktionen er hat und ob diese positiv oder negativ sind. Diese Transaktionen werden in Kategorien eingeteilt (z. B. Essen, Abos, Miete, Sparen, Einkommen) und nach ihrer Wiederholung klassifiziert (täglich, wöchentlich oder monatlich).

Das Projekt ist eine reine Konsolenanwendung ohne GUI, geschrieben in Java. Die Daten werden lokal in einer JSON-Datei gespeichert.

## Eingesetzte Technologien

| Bereich | Technologie |
|---|---|
| Sprache | Java 25 |
| Build-Tool | Maven |
| IDE | IntelliJ IDEA |
| JSON-Bibliothek | Gson 2.13.1 |
| Test-Framework | JUnit 5 (Jupiter) 5.11.4 |
| Mocking | Mockito 5.14.2 |
| Mutation Testing | PIT (Pitest) 1.17.4 + pitest-junit5-plugin 1.2.2 |
| Datenspeicherung | Lokale JSON-Datei (kein DB-Server) |

## Gewählte Testkategorien (M450 LB1)

- Fehlerbehandlung & Edge Cases
- Sammlungen & Listen
- Kombinatorische Logik
- Kontinuierliche Grenzwerte

**Testing-Technik (Freie Wahl):** Erweitertes Mocking & Code-Mutation
**Multiplikationsfaktor:** Faktor 3
**Automatisierungsgrad:** Nach Push (GitHub Actions CI)

## Architektur

Das Projekt folgt einer Trennung zwischen Datenmodell, Persistenz, Geschäftslogik und Konsolen-UI, um die Geschäftslogik unabhängig von Ein-/Ausgabe testen zu können.

```
src/main/java/org/m450/budgetplanner/
├── model/
│   ├── Customer.java
│   ├── Account.java
│   ├── PlannedTransaction.java
│   └── Category.java
├── model/enums/
│   └── RecurrenceType.java
├── storage/
│   └── JsonStorageService.java
├── service/
│   ├── CustomerService.java
│   ├── AccountService.java
│   ├── TransactionService.java
│   └── CategoryService.java
├── ui/
│   └── ConsoleMenu.java
└── Main.java
```

## Entitäten

| Entität | Felder |
|---|---|
| Customer | id, name, birthday |
| Account | id, name, balance, fk_customer |
| PlannedTransaction | id, amount, date, on_repeat (ENUM), fk_category, fk_account |
| Category | id, name |

**Beziehungen:** Ein Customer besitzt mehrere Accounts. Ein Account besitzt mehrere PlannedTransactions. Jede PlannedTransaction gehört zu genau einer Category.

## Klassenübersicht (CRUD + Helper)

Alle Model-Klassen besitzen Getter/Setter für jedes Feld. Zusätzlich definierte Kern- und Hilfsmethoden:

### Customer

| Methode                                                  | Typ           | Zweck                                                        |
|----------------------------------------------------------|---------------|--------------------------------------------------------------|
| register(name, birthday)                                 | Create/Helper | Neuen Customer anlegen                                       |
| getId/getName/getBirthday()                              | Read          | Felder auslesen                                              |
| setId/setName/setBirthday()                              | Read          | Felder auslesen                                              |
| editName(name)                                           | Update        | Namen ändern                                                 |
| editBirthday(birthday)                                   | Update        | Geburtsdatum ändern                                          |
| deleteCustomer(id)                                       | Delete        | Customer löschen                                             |
| findCustomerByName(name)                                 | Helper        | Ersetzt ursprüngliches `login()`, sucht bestehenden Customer |
| findCustomerById(id)                                     | Helper        | Sucht Customer anhand id                                     |
| isValidBirthday(birthday)                                | Helper        | Validierung: Datum plausibel (Edge Cases)                    |
| delete/create Account                                    | Create/Delete | Neue Account erstellen                                       |
| delete/create Transaction                                | Create/Delete | Neue Transaction erstellen                                   |
| filterByCategory(list, category)                         | Helper        | Filtert Liste nach Kategorie (Sammlungen & Listen)           |
| filterByCategoryAndRecurrence(list, category, on_repeat) | Helper        | Kombinierter Filter (Kombinatorische Logik)                  |
| login                                                    | Read          | Login in Benutzers UserAccount                               |


### Account

| Methode                                 | Typ | Zweck                                    |
|-----------------------------------------|---|------------------------------------------|
| setId/setName/setBalance/setFkCustomer() | Read | Felder auslesen                          |
| getId/getName/getBalance/getFkCustomer() | Read | Felder auslesen                          |
| editAccountName(name)                   | Update | Namen ändern                             |
| editBalance(balance)                    | Update | Kontostand ändern                        |
| isValidAmount(amount)                   | Helper | Zentrale Betragsvalidierung (Grenzwerte) |
| listAccounts(fk_customer)               | Helper | Alle Accoutns eines Benutzers            |

### PlannedTransaction

| Methode                                                             | Typ    | Zweck |
|---------------------------------------------------------------------|--------|---|
| getId/getAmount/getDate/getOnRepeat/getFkCategory/getFkAccount()    | Read   | Felder auslesen |
| setId/setAmount/setDate/setOnRepeat/setFkCategory/setFkAccount()    | Read   | Felder auslesen |
| listTransactions(fk_account)                                        | Helper | Alle Transaktionen eines Accounts |
| editAmount/editDate/editOnRepeat/editFkCategory(...)                | Update | Einzelne Felder ändern |
| isPositive()                                                        | Helper | Prüft Einnahme vs. Ausgabe |
| sumByCategory(list, category)                                       | Helper | Summiert Beträge einer Kategorie |
| isValidTransaction(amount, date, category)                          | Helper | Validierung vor dem Erstellen |

### Category

| Methode                                | Typ           | Zweck |
|----------------------------------------|---------------|---|
| getId/getName()                        | Read          | Felder auslesen |
| setId/setName()                        | Read          | Felder auslesen |
| listCategories()                       | Helper        | Alle Kategorien |
| editCategoryName(name)                 | Update/Helper | Namen ändern |
| deleteCategory(id)                     | Delete/Helper | Kategorie löschen |
| findCategoryById(id)                   | Helper        | Sucht Kategorie anhand id |
| findCategoryByName(name)               | Helper        | Sucht Kategorie anhand Namen |
| isNameUnique(name, existingCategories) | Helper        | Verhindert doppelte Kategorienamen |

## Minimal notwendiger Kern für die Bewertung

Um die vier gewählten Testkategorien abzudecken, reicht ein kleiner Kernbestand an Methoden. Der Rest der Helper-Methoden ist optional / nice-to-have.

| Testkategorie | Abgedeckt durch |
|---|---|
| Fehlerbehandlung & Edge Cases | `isValidAmount()`, `isValidBirthday()`, `isValidTransaction()` |
| Kontinuierliche Grenzwerte | `isValidAmount()` mit Toleranzbereichen (z. B. 19.99 ± 0.001) |
| Sammlungen & Listen | `filterByCategory()` mit leerer/gefüllter Liste |
| Kombinatorische Logik | `filterByCategoryAndRecurrence()` (AND-Verknüpfung) |
| Erweitertes Mocking (Faktor 3) | `JsonStorageService` mocken beim Testen der Service-Klassen |
| Code-Mutation (Faktor 3) | PIT über alle Kernmethoden laufen lassen |

## TDD-Workflow (Red – Green – Refactor)

1. **Red:** Test für eine noch nicht existierende Methode schreiben, Test schlägt fehl
2. **Green:** Minimale Implementierung schreiben, bis der Test grün ist
3. **Refactor:** Code aufräumen, ohne Testverhalten zu ändern

Empfohlene Reihenfolge: `Category` (keine Abhängigkeiten) → `Account` (Validierung, Grenzwerte) → `PlannedTransaction` (Sammlungen, Kombinatorik) → Mocking der `JsonStorageService` in den Service-Tests → PIT-Mutationstests über den gesamten Kern.

## Setup

### Voraussetzungen

- JDK 25 (z. B. Temurin oder OpenJDK)
- Maven (systemweit installiert für Terminal-Nutzung)
- IntelliJ IDEA

### Projekt öffnen

1. Repository klonen
2. In IntelliJ öffnen, Maven-Reimport abwarten
3. Unter *File → Project Structure → SDK* prüfen, dass JDK 25 aktiv ist

### Tests ausführen

```bash
mvn test
```

### Mutation Testing ausführen

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

## CI/CD

Automatisierungsgrad "Nach Push": GitHub Actions führt bei jedem Push automatisch `mvn test` aus (Workflow-Datei unter `.github/workflows/`).

## Bewertungskriterien (M450 LB1)

- **Fachliche Relevanz & Korrektheit:** Testideen decken reale Geschäftslogik ab (Validierung, Filterung, Kombination von Bedingungen)
- **Identifikation neuer Fehler:** Tests müssen von der Lehrperson eingebaute Fehler erkennen können
- **Automatisierungsgrad:** 3 Punkte durch CI-Ausführung nach jedem Push
