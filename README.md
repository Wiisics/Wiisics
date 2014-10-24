# Wiisics Core #

Wiisics, Nintendo Wii kumandalarını İlkokul ve Lise Fizik derslerinin müfredatlarının pekiştirilmesinde kullanılmasına olanak sağlayacak bir altyapı projesidir.

### Bu repo'da neler var? ###

* Nintendo Wii kumandası (Wiimote) ile bağlantı kurma ve veri alıp verme işlemini yapacak olan Wiisics Core.
* Grafik gösterme vb. temel işlevleri sağlayacak yan sınıflar.

### Nasıl çalışır hale getirilir? ###

* Repository clone'lanır veya build indirilir.
* Sistemin çalışması için gerekli olan JSR082 Bluetooth Stack'i yüklenir. Mac ve Windows için Bluecove (http://bluecove.org/), Linux için Avetana Bluetooth'un (http://sourceforge.net/projects/avetanabt/) yüklenmesi yeterli olacaktır.
* Bluetooth Stack'in çalışması için gerekli olan driverlar indirilir: Mac ve Linux'ta standart driverlar bu işi görürken Windows'ta WIDCOMM driver'ı (http://www.broadcom.com/support/bluetooth/update.php) yüklenmelidir.
* Bütün yüklemeler tamamlandıktan sonra uygulama çalıştırılabilir, ancak an itibariyle Bluecove sadece x32 JVM'leri desteklediği için bu stack'in kullanılıyor olması durumunda Java VM'i -d32 argümanıyla çağırmalıdır (örn. 
```
#!Bash

java -d32 -jar Wiisics.jar
```
)

### Kiminle iletişime geçilmeli? ###

* Cem Gökmen, UAA '16
* Hakan Alpan, UAA '16
* Hüseyin Köse, UAA Science Department