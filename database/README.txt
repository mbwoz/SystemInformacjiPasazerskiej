System Informacji Pasażerskiej

Projekt modeluje tygodniowy rozkład jazdy pociągów po Polsce. 


Cześć II:

W stosunku do przedstawionego w pierwszej części projektu wprowadziliśmy 
następujące zmiany:

    - wagony: liczba miejsc dla rowerów oraz dostosowanie wagonów dla niepełnosprawnych.
    
    
W celu uruchomienia aplikacji należy:

    1. postawić przykladową bazę danych, zawierającą wszystkie potrzebne tabele,
    funkcje, trigger, itd. wraz z przykładowymi danymi. Całość znajduje się 
    w pliku create.sql
    
    2. aplikacja komunikująca się z bazą danych wykorzystuje technologię JavaFX,
    dlatego do uruchomienia aplikacji potrzebne będą odpowiednie moduły. Ze strony
    https://gluonhq.com/products/javafx/ w pierwszej sekcji LTS należy wybrać
    odpowienią paczkę JavaFX SDK i rozpakować. Do uruchomienia będziemy 
    potrzebowali ścieżki do folderu lib z pobranej paczki.
    
    3. w folderze z dostarczonym plikiem JAR aplikacji należy wykonać poniższe
    polecenie w celu uruchomienia aplikacji, podając ścieżkę do folderu lib
    z paczki pobranej w poprzednim punkcie. 
    
    java --module-path /path/to/javafx/lib --add-modules=javafx.controls,javafx.fxml -jar SystemInformacjiPasazerskiej-1.0-SNAPSHOT-jar-with-dependencies.jar
    
    4. po uruchomieniu aplikacji wyświetli się ekran połączenia z bazą. 
    Aplikacja wykorzystuje PostgreSQL JDBC Driver. Wszystkie podane na ekranie
    dane służą połączeniu z odpowiednią bazą i uzupełniają odpowiednie pola
    potrzebne do nawiązania połączenia. Więcej informacji odnośnie podawanych
    informacji można znaleźć na stronie 
    https://jdbc.postgresql.org/documentation/head/connect.html
    
    5. po nawiązaniu połączenia aplikacja przekieruje nas do ekranu głównego 
    aplikacji, skąd możemy wykonywać odpowiednie operacje. W pierwszej zakładce
    znajduje się wyszukiwarka dostępna dla standardowego użytkownika. 
    Zakładki druga i trzecia zawierają sekcje dla administratora pozwalające
    na modyfikację różnych elementów zawartych w bazie.


Część I:

W zaprojektowanej bazie danych zawarliśmy następujące informacje:

    - wagony: przedstawia wagony jakie są dostępne do naszej dyspozycji. 
    
    - sklady: przedstawia składy (zestawy wagonów), które będziemy wypuszczali na trasy.
    Skład zawiera również informacje o tym czy istnieje możliwość przewożenia przesyłek
    tym składem. 
    
    - sklady_wagony: łączy dwie powyższe tabele. Definuje jakie wagony należą do
    konkretnych składów. Każdy skład może zawierać kilka wagonów tego samego
    modelu, stąd atrybut liczba_wagonow. 
    
    - stacje: zawiera informacje o stacjach na jakich zatrzymują się nasze pociągi.
    
    - odcinki: stanowią "atomowe" połączenia pomiędzy kolejnymi stacjami na trasie,
    po której poruszają się nasze pociągi. Relacja ta przechowuje ten sam odcinek,
    ale o różnym kierunku, jako dwa osobne rekordy. Zdecydowaliśmy się na tę
    redundancję, ponieważ w dłuższej perspektywie jest ona korzystniejsza niż
    wykorzystanie flagi, oznaczającej kierunek odcinka na trasie (dany odcinek
    może być wielokrotnie używany na podobnych trasach, a co za tym idzie,
    wielokrotnie musielibyśmy używać flag).
    
    - trasy: trasy (zestawy odcinków), po których poruszają się nasze pociągi.
    Dodatkowo przechowuje informacje o tym czy dana trasa jest trasą przyspieszoną.
    
    - trasy_odcinki: łączy dwie powyższe relacje. Każdej trasie przypisuje wszystkie
    jej odcinki składowe.
    
    - pociagi: przechowuje informacje o pociągach, w tym po jakiej trasie się
    poruszają (jeden pociąg o danym id może jeździć tylko po konkretnej trasie). 
    
    - rozklady: zawiera id_kursu dla danego pociągu w konkretnym dniu tygodnia 
    (określa dzień wyjazdu ze stacji początkowej, ponieważ w trakcie przejazdu
    może nastąpić zmiana dnia).
    
    - postoje: łączy informacje o kursie z postojami, tj. konkretnymi stacjami
    na trasie (godziną przyjazdu i odjazdu) oraz składem jaki wyjeżdża z danej
    stacji (pozwala to na zmianę składu na stacji).
    
W niektórych tabelach bazy pojawiają się również redundantne w stosunku do 
id, nazwy (np. stacji). Ze względów wydajnościowych do połączeń wykorzystujemy
id, zaś do prezentacji danych nazwy.


Przkładowe ograniczenia jakie planujemy nałożyć na tabele:
    
    - Na stacji w jednym czasie nie może przebywać więcej pociągów niż jest torów.
    
    - Całkowita długość składu zatrzymującego się na stacji nie może przekraczać
    długości peronu.
    
    - Dla danego pociągu dany dzien_tygodnia występuje co najwyżej raz.
    
    - Kolejne postoje powinny mieć odpowiednie zależności czasowe
    (ostrzeżenie o dodaniu pociągu o czasie przejazdu powyżej 24h).
    
    - Postój powinien zajmować więcej czasu gdy następuje zmiana składu.
    
    - Odpowiedni kurs, trasa i stacje powinny sobie odpowiadać.
    
    
W projekcie bierze udział:
Jakub Fedak
Filip Synowiec
Michał Woźny

Wkład każdego w projekcie jest równomierny.


Pliki projektu można również znaleźć na githubie: https://github.com/mwoztcs/SystemInformacjiPasazerskiej
