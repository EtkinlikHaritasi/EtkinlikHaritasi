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
| Background Process / Task                                | Şükrü Fırat Sarp    | develop-Background-Task   |
| Broadcast Receiver / NSNotificationCenter                | Yusuf Kozan         |                           |
| Sensor (Motion / Location / Environment)                 | Habil Tataroğulları | develop-sensor            |
| Connectivity (BLE / Wifi / Cellular Network / USB / NFC) | Habil Tataroğulları | develop-connectivity      |
| Authorization (OAuth / OpenID / JWT)                     | Rukiye Berna Turan  | develop-auth              |
| Cloud Service (AI)                                       | Şükrü Fırat Sarp    | develop-Cloud_Service(AI) |

---

## Kurulum Notları

* Google Maps API ve Gemini API ile bağlantı sağlanabilmesi için, derlemeden önce
    kök dizinde `secrets.properties` adlı bir dosya oluşturun. API anahtarlarınızı
    dosyaya aşağıdaki şekilde yerleştirin:  
    ```
    MAPS_API_KEY=[API ANAHTARINIZ]
    GEMINI_API_KEY=[API ANAHTARINIZ]
    ```

---

## Sürüm Notları

### Storage / Basic Data

1. Hafta: QR kod tarayıcı özelliği için kamera izin yönetimi eklenmiştir.  
    Geleneksel `onRequestPermissionsResult` yöntemi kaldırılmış ve yerine Jetpack Compose ile uyumlu
    `rememberLauncherForActivityResult` API’si kullanılmıştır ve gerekli kütüphaneler eklenmiştir.  

5. Hafta: FileUtils dosyası ile file system'e png kayıt eden fonksiyon ve gerekli permission eklendi.

### Local Database (Room / CoreData)

1. Hafta: Projenin local database ihtiyacını karşılamak üzere Room veritabanı çözümü seçildi ve yapı entegre edildi.  

    * User, Event ve Participation olmak üzere üç tabloya ait entity ve DAO iskeletleri oluşturuldu
    * Bu tablolar ileriki haftalarda kullanılacak ve ilişkili ekranlarla entegre edilecek
    * Room altyapısı `AppDatabase` ile tanımlandı
    * Singleton erişim için `AppDatabaseInstance` objesi eklendi  
    > Room, projede local cache yapısı olarak kullanılacak. Tüm yapı ileride geliştirilecek özellikler için hazırlandı.  

2. Hafta: Room altyapısının stabil şekilde çalıştığını doğrulamak amacıyla,
    ViewModel ve Repository entegrasyonundan önce bir Android instrumented test eklendi.  

3. Hafta: Event verileri için EventDao, EventRepository, EventViewModel, EventViewModelFactory
    sınıfları oluşturuldu.

### RESTFul API (CRUD)

2. Hafta: Event entity'si için gerekli API işlemleri gerçekleştirildi.
    API'den veri çekme, ekleme, silme ve güncelleme işlemleri eklendi.
    Bu işlemleri yöneten fonksiyonlar içeren bir repository sınıfı oluşturuldu.  

3. Hafta: User ve Participatüon entityleri için api işlemleri yapıldı ve bu entityler için
    repositoryler oluşturuldu. Tüm entitylerin api işlemlerinin ilk versiyonları oluşturuldu sonraki versiyonlarda detaylandırılacak.  

4. Hafta: Entity sınıflarında kullanışlılık için değişiklikler yapılmış, giriş yapmayı
    kolaylaştıracak düzenlemeler eklenmiştir.

### UI (Compose / SwiftUI)

1. Hafta: Sayfa kavramı oluşturuldu ve sayfalar arası geçiş yapıldı.  
2. Hafta: Google Maps API kullanılarak Keşfet sayfasına harita kondu.  
    Yakındaki etkinliklerin görüntülenmesi için çekmece eklendi.  

3. Hafta: Bilet sayfası artık hem NFC hem QR okuma yapabiliyor. Şimdilik biraz kararsız.  

4. Hafta: Haritada bir yere uzun dokunarak yeni etkinlik oluşturulabiliyor.  
    Daha önceden eklenmiş etkinlikler haritada görüntüleniyor.  

5. Hafta: Giriş ve kayıt için kullanıcı arayüzü yapıldı.
    Bilet özellikleri tamamlandı. Etkinliğe katılıp ayrılma ve yol tarifi
    özellikleri eklendi.

### Background Process / Task

4. Hafta: Etkinliğe kalan süre bildirimi ve etkinlik müşteri hizmetlerinde veri tabanındaki
    etkinliklere göre öneri yapma eklendi.

5. Hafta: Hatalar giderildi.

### Sensor (Motion / Location / Environment)

1. Hafta: GPS verisi okuma işlemi bir butona bağlanarak yapıldı.
2. Hafta: Konum bilgisi 5 saniye aralıklarla otomatik bir şekilde alınmaya başlandı.  
5. Hafta: QR oluşturucu eklendi.

### Connectivity (BLE / Wifi / Cellular Network / USB / NFC)

2. Hafta: NFC kartlarının ID'leri okunup ekrana yazılmaya başlandı.  

4. Hafta: Bir noktadan diğer bir noktaya yol tarifi veren API bağlandı.  

5. Hafta: Yakın cihazları arayan ve bağlantı kurabilen kod yazıldı.

### Authorization (OAuth / OpenID / JWT)

4. Hafta: Firebase Authentication ile kullanıcı girişi entegre edildi.  
    Giriş yapılan kullanıcıdan JWT token alınıp uygulama içinde kullanılacak şekilde yapılandırıldı.  

5. Hafta: Üye olma özelliği yapıldı.

### Cloud Service (AI)

1. Hafta: Yapay zekâlı asistan için Text-to-Speech desteği eklendi.  

    Gelecek süreçte Gemini Cloud entegrasyonu yapılacak.

2. Hafta: Gemini entegrasyonuna başlandı.  
    Arka planda sürekli mikrofon dinlemesi yapılıyor.

5. Hafta: Hatalar giderildi.