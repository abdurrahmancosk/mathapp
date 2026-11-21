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

enum class Topic(
    val title: String,
    val emoji: String,
    val color: Long,
    val summary: String,
) {
    SAYILAR("Sayılar", "🔢", 0xFF5C6BC0, "100'e kadar sayılar, basamak değeri, tek ve çift"),
    TOPLAMA("Toplama", "➕", 0xFF00897B, "100'e kadar eldeli ve eldesiz toplama"),
    CIKARMA("Çıkarma", "➖", 0xFFEF6C00, "100'e kadar onluk bozarak ve bozmadan çıkarma"),
    CARPMA("Çarpma", "✖️", 0xFF8E24AA, "Tekrarlı toplama ve çarpma"),
    BOLME("Bölme", "➗", 0xFF6D4C41, "Eşit paylaştırma ve gruplama"),
    KESIRLER("Kesirler", "🍕", 0xFFD81B60, "Bütün, yarım ve çeyrek"),
    GEOMETRI("Geometri", "🔺", 0xFF039BE5, "Şekiller ve cisimler"),
    OLCME("Ölçme", "📏", 0xFF43A047, "Uzunluk, zaman ve paralarımız"),
    VERI("Veri", "📊", 0xFFF4511E, "Nesne grafiği okuma ve oluşturma"),
    ORUNTU("Örüntüler", "🔁", 0xFF5E35B1, "Sayı ve şekil örüntüleri"),
}
