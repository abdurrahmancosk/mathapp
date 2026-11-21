# 📚 Matematik Öğren - Android Uygulaması

Bloom'un Taksonomisi'ne göre 2. sınıf öğrencileri için eğitici matematik oyunları sunuyor.

## 🎯 Özellikler

### Bloom'un 6 Seviyesi
- **Hatırla (Remember)**: Sayı tanıma, basit soruları cevaplama
- **Anla (Understand)**: Matematik kavramlarını öğrenme
- **Uygula (Apply)**: Problem çözme
- **Analiz Et (Analyze)**: Karşılaştırma ve sınıflandırma
- **Değerlendir (Evaluate)**: Cevapları açıklama
- **Yarat (Create)**: Kendi problemleri oluşturma

### Matematik Konuları
- 🔢 Sayma (Counting)
- ➕ Toplama (Addition)
- ➖ Çıkarma (Subtraction)
- ✖️ Çarpma (Multiplication)
- ➗ Bölme (Division)
- 📐 Şekiller (Shapes)
- 📏 Ölçme (Measurement)
- 🔢 Çift-Tek (Even-Odd)
- 🍰 Kesirler (Fractions)

### Oyun Mekanikleri
- ⭐ Puan Sistemi
- 🏆 Badge/Başarı Sistemi
- 📊 İlerleme Takibi
- 🎮 Etkileşimli Sorular
- 🔥 Günlük Seri Sistemi

## 🛠️ Teknik Detaylar

### Kullanılan Teknolojiler
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room (SQLite)
- **Architecture**: MVVM + Repository Pattern
- **Minimum API Level**: 26
- **Target API Level**: 34

### Proje Yapısı
```
MathApp/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/example/mathapp/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── db/
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   └── Daos.kt
│   │   │   │   ├── model/
│   │   │   │   │   └── Models.kt
│   │   │   │   └── repository/
│   │   │   │       └── MathRepository.kt
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   ├── QuizScreen.kt
│   │   │   │   │   ├── ProgressScreen.kt
│   │   │   │   │   └── SettingsScreen.kt
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   └── viewmodel/
│   │   │   │       ├── HomeViewModel.kt
│   │   │   │       └── QuizViewModel.kt
│   │   │   └── ...
│   │   └── res/
│   │       └── values/
│   │           ├── strings.xml
│   │           ├── colors.xml
│   │           └── themes.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Android Studio (veya IntelliJ IDEA + Android Plugin)
- JDK 17+
- Android SDK 34+

### Adımlar

1. **Projeyi Klonlayın**
```bash
cd MathApp
```

2. **Android Studio'da Açın**
   - File → Open → MathApp klasörünü seçin

3. **Gradle Sync**
   - Android Studio otomatik olarak Gradle'ı sync edecek

4. **Emülatör/Cihaz Bağlayın**
   - Emülatör başlatın veya gerçek Android cihazı bağlayın

5. **Uygulamayı Çalıştırın**
   - Run → Run 'app' (Shift+F10) veya yeşil play tuşuna tıklayın

## 📱 Ekranlar

### 🏠 Anasayfa
- Tüm etkinlikleri liste halinde gösterir
- Konulara göre filtreleme
- Bloom seviyelerine göre filtreleme
- Öğrenci istatistiklerini gösterir

### 🎮 Quiz Ekranı
- Soru ve cevap seçenekleri
- Gerçek zamanda geri bildirim
- Açıklayıcı notlar
- İlerleme çubuğu

### 📈 İlerleme Ekranı
- Toplam puan ve istatistikler
- Bloom seviyeleri başarısı
- Konu başına ilerleme
- Doğruluk yüzdesini gösterir

### ⚙️ Ayarlar Ekranı
- Koyu mod toggle
- Ses efektleri
- Bildirimler
- Veri dışa aktarma/silme

## 📊 Veri Modeli

### Activity (Etkinlik)
```kotlin
data class Activity(
    val id: Int,
    val title: String,
    val description: String,
    val topic: MathTopic,
    val bloomLevel: BloomLevel,
    val difficulty: Int = 1
)
```

### Question (Soru)
```kotlin
data class Question(
    val id: Int,
    val activityId: Int,
    val question: String,
    val correctAnswer: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val explanation: String
)
```

### UserProgress (Kullanıcı İlerlemesi)
```kotlin
data class UserProgress(
    val id: Int,
    val activityId: Int,
    val questionId: Int,
    val isCorrect: Boolean,
    val score: Int,
    val completedAt: Long
)
```

## 🎨 Renkler (Çocuk Dostu Palet)

- **Mavi**: #64B5F6 (Anla Seviyesi)
- **Yeşil**: #81C784 (Uygula Seviyesi)
- **Turuncu**: #FFB74D (Analiz Seviyesi)
- **Kırmızı**: #EF5350 (Hatırla Seviyesi)
- **Mor**: #BA68C8 (Yarat Seviyesi)
- **Sarı**: #FFD54F (Değerlendir Seviyesi)

## 📚 Örnek Etkinlikler

### Bloom Seviyelerine Göre Organizasyon

**1. HATIRLA (Remember)**
- Sayıları Tanı (1-10)
- Basit Toplama (0-5)

**2. ANLA (Understand)**
- Toplamayı Anlama
- Çıkarmayı Anlama

**3. UYGULA (Apply)**
- Toplama Problemlerini Çöz
- Gerçek Hayat Problemleri

**4. ANALIZ ET (Analyze)**
- Çift ve Tek Sayıları Ayırt Et
- Şekilleri Karşılaştır

**5. DEĞERLENDIR (Evaluate)**
- Cevaplarını Açıkla
- Çözümleri Savun

**6. YARAT (Create)**
- Kendi Problemini Oluştur
- Matematik Hikayesi Yaz

## 🎯 Badge Sistemi

- 🎯 **İlk Adım** - İlk soruyu cevapla
- ⚡ **Hızlı Zekâ** - 10 soruyu doğru cevapla
- 🏆 **Math Şampiyonu** - 100 puan topla
- 🔍 **Kesif** - Tüm konuları keşfet
- 🧠 **İçgüdü Ustası** - %90 doğruluk oranı

## 🔄 Güncellemeler ve Geliştirmeler

Planlanan Özellikler:
- [ ] Çok Oyuncu Modu
- [ ] Leaderboard Sistemi
- [ ] Kullanıcı Profili
- [ ] Daha Fazla Matematik Konusu
- [ ] Ses/Konuşma Desteği
- [ ] Öğretmen Paneli
- [ ] İstatistik İhraç (PDF/Excel)
- [ ] Offline Mod
- [ ] Animasyonlar ve Efektler

## 📝 Lisans

Bu proje eğitim amaçlıdır. Özgür kullanım ve dağıtım yapabilirsiniz.

## 👨‍💻 Geliştirici

Kotlin & Jetpack Compose ile geliştirilmiştir.

## 📧 İletişim

Sorularınız için: [your-email@example.com]

---

**Matematik öğrenmeyi eğlenceli hale getirin! 🎓✨**
