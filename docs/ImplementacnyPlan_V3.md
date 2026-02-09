## 1. DB vrstva
- Pripojenie k MySQL
- CRUD operácie pre klientov
- Vyhľadávanie podľa mena / priezviska

## 2. XML vrstva
- Zápis klientov do XML
- Načítanie klientov z XML
- Vyhľadávanie klienta podľa ID v XML
- Parsovanie XML → objekt
- Fallback čítanie dát pri výpadku DB

## 3. Logika (Hybrid)
- Detekcia režimu (online / offline)
- Hybridné čítanie klientov (DB / XML)
- Detail klienta (online / offline)
- Registrácia klienta len v online režime
- Aktualizácia klienta (update) len v online režime
- Vymazanie klienta (delete) len v online režime

## 4. Vstupy a permanentky
- Aktivácia permanentky (pridelenie klientovi)
- Predĺženie permanentky
- Kontrola platnosti permanentky
- Evidencia vstupov klientov
- História vstupov
- Zápis vstupov do DB / XML
- Validácia vstupu

## 5. Logovanie
- AppLog – systémové udalosti a chyby (súbor) `app_log.txt`
- Systémové udalosti
- Chyby
- Výnimky
- Offline režim info


- VstupLog – logovanie vstupov klientov (súbor) `vstupy_log.txt`
- Log vstupov klientov
- Čas operácie + klient (ID, meno) + stav vstupu + dôvod

## 6. UI moduly

- Hlavné okno
- Registrácia klienta
- Detail klienta
- Vyhľadávanie klientov
- Zoznam klientov
- História vstupov

## 7. QR modul
- Generovanie QR
- Uloženie QR obrázka do priečinka `qr_kody`
- Prepojenie na klienta (UUID / token)
- Zobrazenie QR v detaile
- Príprava QR na tlač (TlacServis)
- Export QR do priečinka `vystup`

## 8. Stabilizácia a finalizácia systému
- Overenie online/offline režimov
- Overenie XML fallback mechanizmu
- Overenie funkčnosti QR tlače
- Ošetrenie výnimiek
- Rozšírenie logovania
- Refactor čistenie
- UI dizajn ladenie
- Zarovnanie layoutov





