# 🪜 Matematik Merdiveni

İlkokul 2. sınıf matematik dersi için Android uygulaması. Türkiye Yüzyılı Maarif Modeli (TYMM) 2. sınıf matematik konuları, Bloom taksonomisinin 6 basamağıyla etkinliklere dönüştürülmüştür.

## 📲 Telefona kurulum

1. Bu deponun **Releases** bölümünden **Matematik Merdiveni - son sürüm** sayfasını açın.
2. `MatematikMerdiveni.apk` dosyasını telefona indirin ve açın.
3. Telefon izin isterse "Bilinmeyen kaynaklardan yüklemeye izin ver" seçeneğini açın.

APK ayrıca her derlemede **Actions → son çalıştırma → Artifacts** bölümünde de bulunur.

## 📚 İçerik

10 konu × 6 Bloom basamağı = **60 etkinlik**. Sorular her seferinde rastgele üretildiği için etkinlikler tekrar tekrar oynanabilir.

| Konu | Kapsam |
|---|---|
| 🔢 Sayılar | 100'e kadar sayılar, okuma, basamak değeri, ritmik sayma, tek-çift |
| ➕ Toplama | 100'e kadar eldeli/eldesiz toplama, eksik toplanan |
| ➖ Çıkarma | 100'e kadar onluk bozarak/bozmadan çıkarma, eksik eksilen |
| ✖️ Çarpma | Tekrarlı toplama, eşit gruplar, çarpma |
| ➗ Bölme | Eşit paylaştırma, gruplama, çarpma-bölme ilişkisi |
| 🍕 Kesirler | Bütün, yarım, çeyrek |
| 🔺 Geometri | Üçgen, kare, dikdörtgen, daire; küp, küre, silindir, koni |
| 📏 Ölçme | Metre-santimetre, saat (tam/yarım), paralarımız |
| 📊 Veri | Nesne grafiği okuma, yorumlama ve oluşturma |
| 🔁 Örüntüler | Şekil ve sayı örüntüleri |

| Bloom basamağı | Etkinlik türü |
|---|---|
| 🧠 Hatırla | Temel bilgi ve işlemleri hatırlama |
| 💡 Anla | Resim ve işlemleri yorumlama |
| 🛠️ Uygula | Günlük hayat problemleri |
| 🔍 Analiz Et | Karşılaştırma, eksik parçayı bulma |
| ⚖️ Değerlendir | Bir çözümün/iddianın doğruluğuna karar verme |
| 🎨 Oluştur | Kendi sayısını, işlemini, grafiğini, örüntüsünü kurma |

## 🎮 Oyunlaştırma ve ilerleme

- Her etkinlik 5 soru; 3 doğru = 1 ⭐, 4 doğru = 2 ⭐, 5 doğru = 3 ⭐
- Bir basamaktan yıldız alınca bir sonraki basamak açılır
- Puan, günlük seri ve 9 rozet
- "İlerlemem" ekranında Bloom basamaklarına ve konulara göre başarı
- Tek öğrenci içindir; veriler yalnızca telefonda saklanır, internet gerekmez

## 🛠️ Geliştirme

Kotlin + Jetpack Compose. Android Studio ile açılabilir veya komut satırından derlenebilir:

```bash
./gradlew testDebugUnitTest assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`
