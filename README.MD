# Etkinlik Haritası

Kocaeli Üniversitesi Yazılım Mühendisliği *YZM304 Mobil Programlama* dersi
proje ödevi olarak geliştirilmektedir.


## Özellikler ile Sorumluları

Ödev metninde belirtilen özellikler ile ekibin sorumluluk paylaşımı şu şekildedir:  

| Özellik                                                  | Sorumlu             | Dal                       |
| :------------------------------------------------------- | :------------------ | :------------------------ |
| Storage / Basic Data                                     | Mehmet Fadıl Boyacı |                           |
| Local Database (Room / CoreData)                         | Rukiye Berna Turan  | develop-local-database    |
| RESTFul API (CRUD)                                       | Mehmet Fadıl Boyacı |                           |
| UI (Compose / SwiftUI)                                   | Yusuf Kozan         | develop-ui                |
| Background Process / Task                                | Şükrü Fırat Sarp    |                           |
| Broadcast Receiver / NSNotificationCenter                | Yusuf Kozan         |                           |
| Sensor (Motion / Location / Environment)                 | Habil Tataroğulları | develop-sensor            |
| Connectivity (BLE / Wifi / Cellular Network / USB / NFC) | Habil Tataroğulları |                           |
| Authorization (OAuth / OpenID / JWT)                     | Rukiye Berna Turan  |                           |
| Cloud Service (AI)                                       | Şükrü Fırat Sarp    | develop-Cloud_Service(AI) |

---

## Sürüm Notları

### Local Database (Room / CoreData)

1. Hafta: Projenin local database ihtiyacını karşılamak üzere Room veritabanı çözümü seçildi ve yapı entegre edildi.  

    * User, Event ve Participation olmak üzere üç tabloya ait entity ve DAO iskeletleri oluşturuldu
    * Bu tablolar ileriki haftalarda kullanılacak ve ilişkili ekranlarla entegre edilecek
    * Room altyapısı `AppDatabase` ile tanımlandı
    * Singleton erişim için `AppDatabaseInstance` objesi eklendi  
    > Room, projede local cache yapısı olarak kullanılacak. Tüm yapı ileride geliştirilecek özellikler için hazırlandı.

### UI (Compose / SwiftUI)

1. Hafta: Sayfa kavramı oluşturuldu ve sayfalar arası geçiş yapıldı.

### Sensor (Motion / Location / Environment)

1. Hafta: GPS verisi okuma işlemi bir butona bağlanarak yapıldı.

### Cloud Service (AI)

1. Hafta: Gelecekte eklenecek yapay zekâlı asistan için konuşma tanıma
    özelliği yapıldı.