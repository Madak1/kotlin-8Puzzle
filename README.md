# 8 Puzzle

Játék leírása 
- A játék egy 3x3-as négyzetrácsos táblán játszódik, nyolc darab számozott kockával. A játékosnak sorrendbe 
kell tenni a számokat, méghozzá úgy, hogy függőlegesen vagy vízszintesen arrébb tolja az egyik kockát 
valamelyik irányba.

Követelmények 
- Az alkalmazásnak rendelkeznie kell egy igényes és egyszerűen kezelhető menüvel, mely lehetőséget biztosít a felhasználónak ki-bekapcsolni a háttérzenét, megnézni a ranglistát, valamint engedélyezni a döntögetéssel való irányítást. A játék elindítása előtt, a játékos választhat a felajánlott játékmódokból. A kockák helyzet változása animációval történjen. Az alkalmazásnak végig reszponzívnak kell lennie. Ezeken kívül a játéknak rendelkeznie kell ranglistával, ahol megjelenítődnek az eddigi eredmények.

Szolgáltatások 
- Eredmények elmentése és rangsor előállítása 
- Döntögetéssel való irányítás lehetősége
- Háttérzene
- Kamera használata egyéni pálya létrehozásához.
- Játékidő folyamatos jelzése
- Animációk

Megvalósítás 
- Az alkalmazás 5 db Activity-ből fog megvalósulni. Az első a főmenü, ahol a felhasználónk lehetősége van a 
játékot elindítani, megnézni a ranglistát, vagy pedig bemenni a beállításokba. A beállításokon belül lehetőség 
van a háttérzenét, és a döntögetéssel való irányítást ki/be kapcsolni, valamint találhatunk ott egy gombot ami 
elmenti a felhasználó beállításait, majd vissza lép a főmenübe. A ranglista gombra kattintva egy újabb Activity-re vált a program, ahol láthatjuk az eddigi pontszámokat. Ha a játékot akarnánk elindítani, akkor átjutunk a 
Start Activity-be. Ennél a játékmódot állíthatjuk be. 3 féle játékmód lesz elérhető. Az eredeti, hogy számokat kell sorba rakni, a másodiknál egy random képet kell 
kirakni és végül az utolsó, amikor egy fényképet készíthetünk, majd azzal a képpel játszhatunk. Ezután 
bekerülünk a Game Activity-be, ahol megjelenik az időnk, egy megállítás gomb, a játéktábla valamint az exit 
gomb, amivel visszajuthatunk a főmenübe. Ha a játéknak vége,a program bekéri a játékos nevet, majd a lépések és az idő alapján generál egy pontot és 
felkerül az illető a ranglistára.
