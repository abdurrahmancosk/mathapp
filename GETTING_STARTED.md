# 🚀 Başlangıç Rehberi

## ✅ Gereksinimler

- **Android Studio** (Flamingo veya daha yeni)
- **JDK 17 veya daha yeni**
- **Android SDK 34** (SDK 26+ de çalışır ama 34 tavsiye edilir)

## 1️⃣ Projeyi Açın

### Android Studio İle
1. Android Studio'yu açın
2. **File** → **Open**
3. `MathApp` klasörünü seçin
4. **OK** tıklayın

### Komut Satırından
```bash
cd MathApp
./gradlew build
```

## 2️⃣ Gradle Senkronizasyonu

Android Studio açıldıktan sonra:
1. **File** → **Sync Now**
2. Gradle bitene kadar bekleyin (1-2 dakika)

Eğer sorun yaşarsanız:
1. **File** → **Invalidate Caches**
2. **Invalidate and Restart**

## 3️⃣ Emülatör Ayarla

### Yeni Emülatör Oluştur
1. **Tools** → **Device Manager**
2. **Create Device**
3. **Pixel 6** seçin (veya başka bir model)
4. **API Level 34** seçin
5. **Finish**

### Emülatörü Başlat
1. Device Manager'da emülatörün üzerine hover yapın
2. **Play** (▶️) butonuna tıklayın
3. Emülatör başlaması 1-2 dakika alır

## 4️⃣ Uygulamayı Çalıştırın

1. Run configuration'ı seçin (sağ üstte, genelde "app" yazılı)
2. **Run** butonuna tıklayın (▶️) veya **Shift+F10**
3. İlk derleme 2-3 dakika alır
4. Uygulama emülatörde açılacak

## 🎮 İlk Kullanım

Uygulama açıldığında:

### Anasayfa
- Tüm etkinlikleri göreceksiniz
- 8 örnek etkinlik önceden yüklenmiş

### Bir Etkinlik Seçin
- Herhangi bir etkinlik kartına tıklayın
- Quiz ekranı açılacak

### Quiz Oynayın
- Soruya cevap seçeneklerinden birini tıklayın
- Cevap kaydedilecek ve geri bildirim gösterilecek
- Açıklama okuyun
- Sonraki soruya geçin

### İlerlemeyi Takip Edin
- Alt menüden **İlerleme** sekmesine tıklayın
- İstatistiklerinizi göreceksiniz

## 📱 Gerçek Cihazda Test

### Cihazı Bağlayın
1. Android cihazınızı USB ile bilgisayara bağlayın
2. Cihazda **Developer Mode** açın:
   - **Settings** → **About Phone**
   - **Build Number** 7 kez tıklayın
3. **USB Debugging** açın

### Çalıştırın
1. Run butonuna tıklayın
2. Android Studio cihazı otomatik tespit edecek
3. Cihazda app açılacak

## 🔧 Sorun Giderme

### Gradle Build Hataları
```bash
# Cache temizle
./gradlew clean

# Tekrar build et
./gradlew build
```

### Kotlin Compiler Hatası
Eğer "Kotlin compiler error" alırsanız:
1. `app/build.gradle.kts` dosyasını kontrol edin
2. Kotlin versiyonunun `1.9.21` olduğundan emin olun

### Emülatör Yavaş Çalışıyor
1. Emülatör ayarlarında **GPU acceleration** aç
2. **Tools** → **Emulator** → **Extended Controls**
3. **Performance** → **GPU** → **Enabled**

### Hiçbir şey göremiyorum
Uygulama ilk açıldığında veritabanını oluşturuyor.
1-2 dakika bekleyin.

## 📊 Proje Yapısı

```
MathApp/
├── app/
│   └── src/main/
│       ├── kotlin/com/example/mathapp/
│       │   ├── MainActivity.kt ← Giriş noktası
│       │   ├── data/
│       │   │   ├── db/ ← Database
│       │   │   ├── model/ ← Veri modelleri
│       │   │   └── repository/ ← İş mantığı
│       │   └── ui/
│       │       ├── screens/ ← Ekranlar
│       │       ├── theme/ ← Renkler ve stil
│       │       └── viewmodel/ ← Logic
│       └── res/ ← Kaynaklar
└── build.gradle.kts ← Proje ayarları
```

## 🎯 Sonraki Adımlar

### Yeni Etkinlik Ekle
1. `MathRepository.kt` aç
2. `getSampleActivities()` fonksiyonuna Activity ekle
3. `getSampleQuestions()` fonksiyonuna sorular ekle

### Renk Değiştir
1. `ui/theme/Theme.kt` aç
2. Renkleri düzenle

### Yeni Ekran Ekle
1. `ui/screens/` klasörüne yeni dosya oluştur
2. `@Composable` fonksiyon yaz
3. `MainActivity.kt`'da navigasyonu ekle

## 📚 Kaynaklar

- [Android Developer Docs](https://developer.android.com/)
- [Jetpack Compose Tutorial](https://developer.android.com/compose/tutorial)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## ✨ İyi Şanslar!

Sorularınız varsa README.md dosyasını kontrol edin.

**Matematik öğrenmeyi eğlenceli hale getirin! 🎓**
