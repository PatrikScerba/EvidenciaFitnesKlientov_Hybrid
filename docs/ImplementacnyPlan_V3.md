# Implementačný plán – Verzia 3.0.0 (Hybrid)

## 1. DB vrstva
- Pripojenie k MySQL
- CRUD operácie pre klientov (základ)
- Vyhľadávanie klienta podľa ID

## 2. XML vrstva
- Zápis klientov do XML
- Načítanie klientov z XML
- Vyhľadávanie klienta podľa ID v XML

## 3. Logika ( Hybrid)
- Detekcia režimu (online/offline)
- Hybridné čítanie (zoznam + detail)
- Registrácia klienta povolená len v online režime

## 4. Ďalšie kroky 
- Úprava klienta (update) len v online režime
- Vymazanie klienta (delete) len v online režime
- Modul vstupov (kontrola vstupu, evidencia)
- QR modul (generovanie, uloženie, tlač)
- Logovanie udalostí (vstupy, chyby, režim)