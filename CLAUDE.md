# Matematik Öğren - Proje Konfigürasyonu

## 📋 Proje Özeti

Bloom'un Taksonomisi'ne göre 2. sınıf öğrencileri için eğitici matematik oyunları Android uygulaması.

## 🏗️ Mimari Yapı

### MVVM + Repository Pattern
```
UI Layer (Screens)
    ↓
ViewModel Layer (HomeViewModel, QuizViewModel)
    ↓
Repository Layer (MathRepository)
    ↓
Data Layer (Database, Models)
```

## 📂 Önemli Dosyalar

### Core Files
- `MainActivity.kt` - Uygulamanın giriş noktası
- `data/db/AppDatabase.kt` - Room database konfigürasyonu
- `data/repository/MathRepository.kt` - İş mantığı ve veri işlemleri

### UI Screens
- `HomeScreen.kt` - Etkinlik listesi ve filtreleme
- `QuizScreen.kt` - Soru cevaplama ekranı
- `ProgressScreen.kt` - İlerleme gösterimi
- `SettingsScreen.kt` - Ayarlar

### Theme & UI
- `ui/theme/Theme.kt` - Material Design 3 temalaması
- `ui/theme/Type.kt` - Typography tanımları

## 🎨 Renkler

Çocuk dostu ve eğitici renk paleti:
- Mavi (#64B5F6) - Temel interaktif bileşenler
- Yeşil (#81C784) - Başarı, doğru cevaplar
- Kırmızı (#EF5350) - Hata, yanlış cevaplar
- Turuncu (#FFB74D) - Uyarılar, önemli bilgiler
- Mor (#BA68C8) - Yaratıcılık, Yarat seviyesi
- Sarı (#FFD54F) - Puanlar, rozetler

## 📊 Veri Tabanı

### Tablolar
- `activities` - Matematik etkinlikleri
- `questions` - Sorular
- `user_progress` - Kullanıcı cevapları ve ilerleme
- `user_stats` - Genel istatistikler
- `badges` - Başarılar

## 🔄 İş Akışı

1. **Kullanıcı Etkinlik Seçer**
   - HomeScreen etkinlikleri listeler
   - Konuya veya Bloom seviyesine göre filtreleme

2. **QuizScreen Başlar**
   - Soru gösterilir
   - Üç cevap seçeneği sunulur
   - Cevap kaydedilir
   - Geri bildirim ve açıklama gösterilir

3. **İlerleme Kaydedilir**
   - UserProgress tablosuna yazılır
   - İstatistikler güncellenir
   - Badge açılıp açılmadığı kontrol edilir

4. **ProgressScreen Güncellenir**
   - Toplam puan, doğruluk yüzdesi gösterilir
   - Bloom seviyeleri başarısı görüntülenir

## 🧪 Test Veriler

Repository'de `getSampleActivities()` ve `getSampleQuestions()` fonksiyonları var:
- 8 örnek etkinlik (tüm Bloom seviyeleri)
- Her etkinlik için 1-2 örnek soru
- 5 başarı rozeti

Uygulama ilk açıldığında bu veriler otomatik yüklenir.

## ⚙️ Gradle Konfigürasyonu

- **API Level**: minSdk=26, targetSdk=34, compileSdk=34
- **JVM Target**: 17
- **Kotlin Version**: 1.9.21
- **Compose Version**: 1.6.0
- **Room Version**: 2.6.1

## 🎯 Geliştirme Notları

### Code Style
- Kotlin naming conventions takip edilir
- Composable fonksiyonlar `@Composable` ile işaretlenir
- ViewModel'lar `AndroidViewModel` extend eder
- Flow kullanılarak reactive programming uygulanır

### Performance
- LazyColumn/LazyRow kullanılır listeler için
- State management düzgün yapılır (collectAsState)
- Database işlemleri coroutine'de yapılır

### Kullanıcı Deneyimi
- Animasyonlar QuizScreen'de kullanılır
- Renk kodlaması Bloom seviyelerine göre
- Material Design 3 kullanılır

## 🚀 Derleme ve Çalıştırma

```bash
# Full build
./gradlew clean build

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## 🐛 Bilinen Sorunlar ve Çözümler

### Gradle Senkronizasyon Sorunu
- File → Invalidate Caches → Restart

### Compose Hataları
- Kotlin compiler extension versiyonunu kontrol edin
- Compose UI ve Compiler versiyonları uyumlu olmalı

## 📈 İyileştirme Önerileri

1. **Daha Fazla Veri**
   - Konular başına 10+ soru ekle
   - Her seviye için 5+ etkinlik

2. **Oyun Mekanikleri**
   - Zaman limiti ekle
   - Combo sistemi (arka arkaya doğru cevaplar)
   - Zorluk seçeneği

3. **Sosyal Özellikler**
   - Leaderboard
   - Başarıları paylaşma
   - Çok oyuncu modu

4. **Öğretmen Araçları**
   - Öğretmen paneli
   - Öğrenci ilerleme raporu
   - Sınıf yönetimi

## 📞 Hızlı Referans

| Sınıf | Amaç |
|-------|------|
| `AppDatabase` | Room database tanımı |
| `MathRepository` | İş mantığı ve DAO erişimi |
| `HomeViewModel` | Ana ekran logic |
| `QuizViewModel` | Quiz ekran logic |
| `HomeScreen` | Etkinlik listesi UI |
| `QuizScreen` | Quiz deneyimi UI |

## ✅ Proje Kontrol Listesi

- [x] Temel proje yapısı
- [x] Database şeması
- [x] Repository pattern
- [x] ViewModel'lar
- [x] UI Screens (Compose)
- [x] Navigasyon
- [x] Örnek veriler
- [ ] Unit testleri
- [ ] Integration testleri
- [ ] Google Play yayını hazırlığı
- [ ] Lokalizasyon (İngilizce, diğer diller)
- [ ] Çevrimdışı mod
