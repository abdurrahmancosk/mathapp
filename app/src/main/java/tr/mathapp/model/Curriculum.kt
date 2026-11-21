package tr.mathapp.model

enum class BloomLevel(
    val title: String,
    val description: String,
    val emoji: String,
    val color: Long,
) {
    HATIRLA("Hatırla", "Bildiklerini hatırla: sayılar, işaretler, temel bilgiler.", "🧠", 0xFFE53935),
    ANLA("Anla", "Resimleri ve işlemleri yorumla, ne anlama geldiğini bul.", "💡", 0xFFFB8C00),
    UYGULA("Uygula", "Öğrendiklerini günlük hayat problemlerinde kullan.", "🛠️", 0xFFF9A825),
    COZUMLE("Analiz Et", "Karşılaştır, eksik parçayı bul, ilişkileri keşfet.", "🔍", 0xFF43A047),
    DEGERLENDIR("Değerlendir", "Bir çözümün ya da fikrin doğru olup olmadığına karar ver.", "⚖️", 0xFF1E88E5),
    OLUSTUR("Oluştur", "Kendi sayını, işlemini, grafiğini ya da örüntünü oluştur.", "🎨", 0xFF8E24AA),
}

/** [outcomes] lists the TYMM 2. sınıf matematik learning outcomes the topic's questions cover. */
enum class Topic(
    val title: String,
    val emoji: String,
    val color: Long,
    val summary: String,
    val outcomes: String,
) {
    SAYILAR("Sayılar", "🔢", 0xFF5C6BC0, "100'e kadar sayılar, basamak değeri, sıralama, yuvarlama, tahmin",
        "MAT.2.1.1, 2.1.2, 2.1.3, 2.1.4, 2.1.6"),
    TOPLAMA("Toplama", "➕", 0xFF00897B, "100'e kadar toplama, tahmin, eşitlik, problemler",
        "MAT.2.2.1, 2.2.2, 2.2.3, 2.2.6"),
    CIKARMA("Çıkarma", "➖", 0xFFEF6C00, "100'e kadar çıkarma, tahmin, toplamayla ilişkisi",
        "MAT.2.2.1, 2.2.2, 2.2.3"),
    CARPMA("Çarpma", "✖️", 0xFF8E24AA, "Tekrarlı toplama, 5'e kadar çarpım tablosu",
        "MAT.2.2.4, 2.2.5, 2.2.6"),
    BOLME("Bölme", "➗", 0xFF6D4C41, "20'ye kadar eşit paylaştırma ve gruplama",
        "MAT.2.2.4, 2.2.5"),
    KESIRLER("Kesirler", "🍕", 0xFFD81B60, "Bütün, yarım ve çeyrek", "MAT.2.1.7"),
    GEOMETRI("Geometri", "🔺", 0xFF039BE5, "Şekiller, cisimler, simetri, yön bulma, sıvı ölçme",
        "MAT.2.3.1, 2.3.3, 2.3.4, 2.3.5, 2.3.6, 2.3.7"),
    OLCME("Ölçme", "📏", 0xFF43A047, "Paralarımız, zaman, uzunluk ve kütle",
        "MAT.2.1.8, 2.1.9, 2.1.10, 2.1.11"),
    VERI("Veri", "📊", 0xFFF4511E, "Şekil grafiği, sıklık tablosu, araştırma sorusu", "MAT.2.4.1"),
    ORUNTU("Örüntüler", "🔁", 0xFF5E35B1, "Sayı ve şekil örüntüleri", "MAT.2.1.4, 2.1.5"),
}
