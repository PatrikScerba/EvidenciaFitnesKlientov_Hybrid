# Galéria rozhrania aplikácie

 Táto stránka obsahuje vizuálnu dokumentáciu používateľského rozhrania aplikácie – jednotlivé obrazovky systému, formuláre a funkcie.

---
## Obsah
- [Hlavné menu](#hlavné-menu)
- [Správa klientov](#správa-klientov)
- [QR systém](#qr-systém)
- [Hybridný režim](#hybridný-režim)
- [História vstupov](#história-vstupov)
- [Systémové logy](#systémové-logy)
- [Späť na hlavné README.md](../README.md)
---

## Hlavné menu
Hlavné menu poskytuje prístup k hlavným funkciám aplikácie na správu klientov.
Umožňuje prechod do sekcií registrácie klienta, vyhľadávania, zoznamu klientov, histórie vstupov, skenera ID a záznamov logov.

![Hlavne_okno](../screenshots/01_Hlavne_okno.png)

---
## Správa klientov

### Registrácia klienta

Formulár na vytvorenie nového klienta v systéme.

![02_Registracia klienta.png](../screenshots/02_Registracia.png)

### Validácia formulára

Systém kontroluje správnosť zadaných údajov a upozorní na chybný vstup.

![Validácia](../screenshots/07_Validacia.png)

### Vyhľadávanie klienta

Vyhľadávanie klientov podľa mena alebo priezviska s podporou výberu pri viacerých výsledkoch.

![Vyhladavanie](../screenshots/08_Vyhladavanie.png)

### Detail klienta

Zobrazuje kompletné informácie o klientovi vrátane QR identifikátora,
ktorý sa automaticky generuje pri registrácii klienta.

![Detail_klienta](../screenshots/03_Detail_klienta.png)

### Úprava klienta

Formulár umožňuje zobraziť a upraviť údaje existujúceho klienta v systéme.
Používateľ môže aktualizovať osobné údaje klienta a uložiť vykonané zmeny.

![Uprava_klienta](../screenshots/04_Uprava_klienta.png)

### Oznámenia

Systém zobrazuje informačné a potvrdzovacie okná pri vykonávaní dôležitých operácií, ako je napríklad predĺženie permanentky alebo vymazanie klienta.

![Oznamenia](../screenshots/05_Oznamenia.png)

- [Späť na obsah](#obsah)
---

## Zoznam klientov

Tabuľkový prehľad všetkých klientov uložených v systéme vrátane údajov
o permanentke a dátume registrácie.

![Zoznam klientov](../screenshots/16_Zoznam_klientov.png)
- [Späť na obsah](#obsah)
---

## QR systém

### Obnovenie QR kódu

QR kód klienta je možné znovu vygenerovať v režime úpravy klienta.
Používa sa napríklad pri strate alebo kompromitovaní pôvodného identifikátora.

![15_Obnova_QR kódu.png](../screenshots/15_Obnova_QR.png)

### Tlač QR kódu

Pri použití funkcie vytlačiť QR aplikácia uloží QR kód klienta ako obrázok do výstupného priečinka aplikácie, odkiaľ je možné ho následne vytlačiť.

![18_vystup_QR kódu.png](../screenshots/18_vystup.png)

Ilustračný príklad uloženého QR kódu vo výstupnom priečinku aplikácie.

### Skenovanie vstupu

Simulácia vstupu klienta pomocou QR identifikátora.

![09_Simulacia_QR.png](../screenshots/09_Simulacia_QR.png)
- [Späť na obsah](#obsah)
---

## Hybridný režim

Aplikácia automaticky prejde do offline režimu pri nedostupnej databáze.

![10_Hybrid.png](../screenshots/10_Hybrid.png)

![11_Hybrid.png](../screenshots/11_Hybrid.png)

### Offline režim

- zobrazenie upozornenia pre zamestnanca
- obmedzenie niektorých funkcií
- čítanie dát z XML zálohy

![17_Hlasenie.png](../screenshots/17_Hlasenie.png)
- [Späť na obsah](#obsah)
---

## História vstupov

### Globálna história vstupov

Prehľad všetkých zaznamenaných vstupov klientov do systému.

![13_Historia_Vstupov.png](../screenshots/13_Historia_Vstupov.png)

### História vstupov klienta

Zobrazuje chronologický prehľad všetkých vstupov konkrétneho klienta do systému.
Obsahuje informácie o čase vstupu, výsledku kontroly a použitom režime systému
(online databáza alebo offline XML záloha).

![14_Historia_Klienta.png](../screenshots/14_Historia_klienta.png)

---

## Systémové logy

Prehľad interných systémových udalostí aplikácie.
Logy obsahujú informácie o stave aplikácie, pripojení k databáze,
chybových hláseniach a diagnostických správach pre monitoring systému.

![12_Systemovy_Log.png](../screenshots/12_Systemovy_Log.png)

---

### [Späť na obsah](#obsah)

Odkaz: [Späť na hlavné README.md](../README.md)




