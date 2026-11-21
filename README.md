# 🪜 Matematik Merdiveni

İlkokul 2. sınıf matematik dersi için Android uygulaması. Türkiye Yüzyılı Maarif Modeli (TYMM) 2. sınıf matematik konuları, Bloom taksonomisinin 6 basamağıyla etkinliklere dönüştürülmüştür.

## 📲 Telefona kurulum

1. Bu deponun **Releases** bölümünden **Matematik Merdiveni - son sürüm** sayfasını açın.
2. `MatematikMerdiveni.apk` dosyasını telefona indirin ve açın.
3. Telefon izin isterse "Bilinmeyen kaynaklardan yüklemeye izin ver" seçeneğini açın.

APK ayrıca her derlemede **Actions → son çalıştırma → Artifacts** bölümünde de bulunur.

## 📚 İçerik

10 konu × 6 Bloom basamağı = **60 etkinlik**. Sorular her seferinde rastgele üretildiği için etkinlikler tekrar tekrar oynanabilir.

| Konu | Kapsam | TYMM çıktıları |
|---|---|---|
| 🔢 Sayılar | 100'e kadar sayılar, basamak değeri, sıralama, sayı doğrusu, yuvarlama, ileri-geri ritmik sayma, tahmin | MAT.2.1.1–2.1.4, 2.1.6 |
| ➕ Toplama | Eldeli/eldesiz toplama, 3 sayı ile toplama, deste-düzine, tahmin, eşitlik | MAT.2.2.1–2.2.3, 2.2.6 |
| ➖ Çıkarma | Onluk bozarak/bozmadan çıkarma, iki işlemli problemler, tahmin, toplamayla ilişkisi | MAT.2.2.1–2.2.3 |
| ✖️ Çarpma | Tekrarlı toplama, 5'e kadar çarpım tablosu, 0 ve 1 ile çarpma | MAT.2.2.4–2.2.6 |
| ➗ Bölme | 20'ye kadar sayıları 2–5'e kalansız bölme, ardışık çıkarma | MAT.2.2.4, 2.2.5 |
| 🍕 Kesirler | Bütün, yarım, çeyrek (kesir gösterimi olmadan) | MAT.2.1.7 |
| 🔺 Geometri | Şekiller; küp, prizmalar, küre, silindir; biçimin korunması, sıvı tahmini, yön bulma, simetri | MAT.2.3.1, 2.3.3–2.3.7 |
| 📏 Ölçme | TL ve kuruş; tam/yarım/çeyrek saat; zaman birimleri; m, cm, kg, g | MAT.2.1.8–2.1.11 |
| 📊 Veri | Şekil grafiği, sıklık tablosu, iki veri grubu, araştırma sorusu | MAT.2.4.1 |
| 🔁 Örüntüler | Tekrarlayan, artan-azalan sayı örüntüleri, sayıya dönüşen şekil örüntüleri | MAT.2.1.4, 2.1.5 |

Çıktı çıktı karşılaştırma: [docs/TYMM_UYUM.md](docs/TYMM_UYUM.md)

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
