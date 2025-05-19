# Etkinlik Haritası

Kocaeli Üniversitesi Yazılım Mühendisliği *YZM304 Mobil Programlama* dersi
proje ödevi olarak geliştirilmektedir.


## Özellikler ile Sorumluları

Ödev metninde belirtilen özellikler ile ekibin sorumluluk paylaşımı şu şekildedir:  

| Özellik                                                  | Sorumlu             | Dal                       |
| :------------------------------------------------------- | :------------------ | :------------------------ |
| Storage / Basic Data                                     | Mehmet Fadıl Boyacı | devolop-storage_basicdata |
| Local Database (Room / CoreData)                         | Rukiye Berna Turan  | develop-local-database    |
| RESTFul API (CRUD)                                       | Mehmet Fadıl Boyacı | develop-restfulApi        |
| UI (Compose / SwiftUI)                                   | Yusuf Kozan         | develop-ui                |
| Background Process / Task                                | Şükrü Fırat Sarp    |                           |
| Broadcast Receiver / NSNotificationCenter                | Yusuf Kozan         |                           |
| Sensor (Motion / Location / Environment)                 | Habil Tataroğulları | develop-sensor            |
| Connectivity (BLE / Wifi / Cellular Network / USB / NFC) | Habil Tataroğulları | develop-connectivity      |
| Authorization (OAuth / OpenID / JWT)                     | Rukiye Berna Turan  |                           |
| Cloud Service (AI)                                       | Şükrü Fırat Sarp    | develop-Cloud_Service(AI) |

---

## Kurulum Notları

* Google Maps API ile bağlantı sağlanabilmesi için, derlemeden önce kök dizinde `secrets.properties`
    adlı bir dosya oluşturun. API anahtarınızı dosyaya aşağıdaki şekilde yerleştirin:  
    ```
    MAPS_API_KEY=[API ANAHTARINIZ]
    ```

---

## Sürüm Notları

### Storage / Basic Data

1. Hafta: QR kod tarayıcı özelliği için kamera izin yönetimi eklenmiştir.  
    Geleneksel `onRequestPermissionsResult` yöntemi kaldırılmış ve yerine Jetpack Compose ile uyumlu
    `rememberLauncherForActivityResult` API’si kullanılmıştır ve gerekli kütüphaneler eklenmiştir.

### Local Database (Room / CoreData)

1. Hafta: Projenin local database ihtiyacını karşılamak üzere Room veritabanı çözümü seçildi ve yapı entegre edildi.  

    * User, Event ve Participation olmak üzere üç tabloya ait entity ve DAO iskeletleri oluşturuldu
    * Bu tablolar ileriki haftalarda kullanılacak ve ilişkili ekranlarla entegre edilecek
    * Room altyapısı `AppDatabase` ile tanımlandı
    * Singleton erişim için `AppDatabaseInstance` objesi eklendi  
    > Room, projede local cache yapısı olarak kullanılacak. Tüm yapı ileride geliştirilecek özellikler için hazırlandı.  

2. Hafta: Room altyapısının stabil şekilde çalıştığını doğrulamak amacıyla,
    ViewModel ve Repository entegrasyonundan önce bir Android instrumented test eklendi.

### RESTFul API (CRUD)

2. Hafta: Event entity'si için gerekli API işlemleri gerçekleştirildi.
    API'den veri çekme, ekleme, silme ve güncelleme işlemleri eklendi.
    Bu işlemleri yöneten fonksiyonlar içeren bir repository sınıfı oluşturuldu.

### UI (Compose / SwiftUI)

1. Hafta: Sayfa kavramı oluşturuldu ve sayfalar arası geçiş yapıldı.  
2. Hafta: Google Maps API kullanılarak Keşfet sayfasına harita kondu.  
    Yakındaki etkinliklerin görüntülenmesi için çekmece eklendi.

### Sensor (Motion / Location / Environment)

1. Hafta: GPS verisi okuma işlemi bir butona bağlanarak yapıldı.
2. Hafta: Konum bilgisi 5 saniye aralıklarla otomatik bir şekilde alınmaya başlandı.

### Connectivity (BLE / Wifi / Cellular Network / USB / NFC)

2. Hafta: NFC kartlarının ID'leri okunup ekrana yazılmaya başlandı.

### Cloud Service (AI)

1. Hafta: Yapay zekâlı asistan için Text-to-Speech desteği eklendi.  

    Gelecek süreçte Gemini Cloud entegrasyonu yapılacak.