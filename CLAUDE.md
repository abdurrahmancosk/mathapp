# Matematik Merdiveni

2. sınıf matematik (TYMM konuları × Bloom basamakları) Android uygulaması. Kotlin + Jetpack Compose, tek modül (`app`).

## Yapı

- `model/Curriculum.kt` – `Topic` (10 konu) ve `BloomLevel` (6 basamak)
- `model/QuestionGenerator.kt` – her konu × basamak için rastgele soru üreteci; tüm içerik burada
- `model/Question.kt` – `ChoiceQuestion` (çoktan seçmeli) ve `BuildQuestion` (iki sayı seçiciyle cevap oluşturma)
- `data/Progress.kt` – ilerleme, rozetler, SharedPreferences ile kayıt
- `ui/AppViewModel.kt` – gezinme yığını, soru oturumu, sonuç hesaplama
- `ui/screens/` – Compose ekranları

## Komutlar

```bash
./gradlew testDebugUnitTest assembleDebug
```

`QuestionGeneratorTest` her etkinlikten yüzlerce soru üretip şıkların ve çözümlerin geçerliliğini kontrol eder; içerik değişikliğinden sonra mutlaka çalıştırın.

## Kurallar

- Türkçe ekler sayılara bağlı değişir ("3'ten", "4'e"); sayıya ek getiren cümle kurmaktan kaçının ya da sabit sayılarla yazın.
- Yeni emoji eklerken Android 8 desteğini düşünün; çok yeni emojiler eski cihazlarda kutu olarak görünebilir.
- CI (`.github/workflows/build.yml`) testleri çalıştırır, APK'yı Artifacts'a ve `latest` sürümüne yükler.
