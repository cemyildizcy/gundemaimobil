package com.example.data

import com.example.data.Topic
import com.example.data.Source

object MockData {
    val topics = listOf(
        Topic("yapay_zeka", "Yapay Zekâ", "psychology"),
        Topic("teknoloji", "Teknoloji", "devices"),
        Topic("spor", "Futbol & Transfer", "sports_soccer"),
        Topic("turkiye", "Türkiye", "flag"),
        Topic("dunya", "Dünya", "public"),
        Topic("ekonomi", "Ekonomi & Finans", "payments"),
        Topic("girisimcilik", "Girişimcilik", "rocket_launch"),
        Topic("bilim", "Bilim", "biotech")
    )

    val sources = listOf(
        Source(
            id = "src_openai",
            name = "OpenAI Resmi",
            username = "@OpenAI",
            type = SourceType.OFFICIAL,
            platform = PlatformType.OFFICIAL,
            avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Yapay Zekâ Geliştiricisi",
            reliabilityLabel = "Resmi Kaynak"
        ),
        Source(
            id = "src_sam_altman",
            name = "Sam Altman",
            username = "@sama",
            type = SourceType.EXPERT,
            platform = PlatformType.X,
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "OpenAI CEO",
            reliabilityLabel = "Birincil Kaynak"
        ),
        Source(
            id = "src_baris_ozcan",
            name = "Barış Özcan",
            username = "BarisOzcan",
            type = SourceType.BLOG,
            platform = PlatformType.YOUTUBE,
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Teknoloji ve Hikaye Anlatıcısı",
            reliabilityLabel = "Alan Uzmanı"
        ),
        Source(
            id = "src_sdn",
            name = "ShiftDelete.Net",
            username = "@shiftdeletenet",
            type = SourceType.NEWS_SITE,
            platform = PlatformType.NEWS,
            avatarUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Teknoloji Yayıncılığı",
            reliabilityLabel = "Haber Kaynağı"
        ),
        Source(
            id = "src_fabrizio",
            name = "Fabrizio Romano",
            username = "@FabrizioRomano",
            type = SourceType.JOURNALIST,
            platform = PlatformType.X,
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Global Futbol Transferleri",
            reliabilityLabel = "Birincil Muhabir"
        ),
        Source(
            id = "src_yagiz",
            name = "Yağız Sabuncuoğlu",
            username = "@yagosabuncuoglu",
            type = SourceType.JOURNALIST,
            platform = PlatformType.X,
            avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Türk Futbolu & Transfer",
            reliabilityLabel = "Birincil Muhabir"
        ),
        Source(
            id = "src_gs_official",
            name = "Galatasaray SK",
            username = "@GalatasaraySK",
            type = SourceType.OFFICIAL,
            platform = PlatformType.OFFICIAL,
            avatarUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Spor Kulübü",
            reliabilityLabel = "Resmi Kaynak"
        ),
        Source(
            id = "src_coindesk",
            name = "CoinDesk Türkiye",
            username = "@CoinDesk",
            type = SourceType.NEWS_SITE,
            platform = PlatformType.NEWS,
            avatarUrl = "https://images.unsplash.com/photo-1621761191319-c6fb62004040?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Kripto & Finans",
            reliabilityLabel = "Haber Kaynağı"
        ),
        Source(
            id = "src_webrazzi",
            name = "Webrazzi",
            username = "@webrazzi",
            type = SourceType.NEWS_SITE,
            platform = PlatformType.BLOG,
            avatarUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Girişimcilik & Teknoloji",
            reliabilityLabel = "Haber Kaynağı"
        ),
        Source(
            id = "src_demis_hassabis",
            name = "Demis Hassabis",
            username = "@demishassabis",
            type = SourceType.EXPERT,
            platform = PlatformType.THREADS,
            avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Google DeepMind CEO",
            reliabilityLabel = "Birincil Kaynak"
        ),
        Source(
            id = "src_tcmb",
            name = "TCMB Resmi",
            username = "@merkez_bankasi",
            type = SourceType.OFFICIAL,
            platform = PlatformType.OFFICIAL,
            avatarUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Merkez Bankası",
            reliabilityLabel = "Resmi Kaynak"
        ),
        Source(
            id = "src_bilim_uzmani",
            name = "Evrim Ağacı",
            username = "@evrimagaci",
            type = SourceType.EXPERT,
            platform = PlatformType.BLOG,
            avatarUrl = "https://images.unsplash.com/photo-1532187863486-abf9d39d66e8?w=120&auto=format&fit=crop&q=60",
            fieldOfExpertise = "Popüler Bilim Yayıncılığı",
            reliabilityLabel = "Alan Uzmanı"
        )
    )

    val stories = listOf(
        Story(
            id = "story_ai_gpt5",
            category = "Yapay Zekâ",
            importance = ImportanceLevel.CRITICAL,
            status = VerificationStatus.OFFICIAL_STATEMENT,
            title = "OpenAI Yeni Nesil Yapay Zekâ Modeli 'GPT-5'i Resmen Duyurdu",
            summary = "OpenAI, akıl yürütme, kod yazma ve görsel analiz kabiliyetlerinde devrim yaratan yeni modeli GPT-5'i tanıttı. Model ilk etapta geliştiriciler için API üzerinden erişime açıldı.",
            contentWhat = "OpenAI CEO'su Sam Altman tarafından yapılan açıklamaya göre GPT-5, önceki modellere kıyasla %400 daha büyük bir bağlam penceresine (context window) sahip. Akıl yürütme (reasoning) gerektiren matematiksel ve mühendislik problemlerinde insan uzman düzeyine ulaştığı belirtiliyor. Ayrıca görsel ve işitsel verileri milisaniyeler seviyesinde gecikmeyle gerçek zamanlı analiz edebiliyor.",
            contentWhy = "Bu gelişme, yapay zekanın sadece metin tabanlı bir asistan olmaktan çıkıp karmaşık iş akışlarını otonom şekilde yönetebilen 'AI Agent' sistemlerine geçişini simgeliyor. Yazılım sektöründen finansa, eğitimden sağlık teknolojilerine kadar geniş bir alanda verimlilik artışını tetiklemesi ve iş yapış şekillerini kökten değiştirmesi bekleniyor.",
            aiComment = "GPT-5, yapay genel zeka (AGI) yolunda önemli bir kilometre taşıdır. Ancak yüksek işlem gücü maliyeti ve çevresel etkiler hala bir soru işareti olarak kalmaktadır. Geliştiricilerin bu modeli entegre etmesiyle birlikte birkaç ay içinde piyasada tamamen otonom yapay zeka çalışanları görmemiz işten bile değil.",
            consensusPoints = listOf(
                "Modelin akıl yürütme yetenekleri GPT-4o'dan belirgin derecede daha üstün.",
                "Bağlam penceresi genişletilmiş ve karmaşık PDF'leri hatasız okuyabiliyor.",
                "Geliştirici API fiyatları GPT-4o lansman fiyatına göre %30 daha ucuz."
            ),
            unresolvedPoints = listOf(
                "Modelin son kullanıcılara yönelik ChatGPT Plus arayüzüne ne zaman geleceği açıklanmadı.",
                "Eğitim verileri arasında telifli yayınların kullanılıp kullanılmadığı sorusu yanıtsız kaldı.",
                "Çıktılardaki halüsinasyon (uydurma) oranının ne derece azaldığı bağımsız testlerle henüz doğrulanmadı."
            ),
            coverUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=600&auto=format&fit=crop&q=80",
            firstTimestamp = "15 dakika önce",
            lastTimestamp = "2 dakika önce",
            sourcesCount = 4
        ),
        Story(
            id = "story_ai_deepmind",
            category = "Yapay Zekâ",
            importance = ImportanceLevel.HIGH,
            status = VerificationStatus.VERIFIED,
            title = "Google DeepMind, Kanser Teşhisinde Çığır Açan Med-Alpha Modelini Tanıttı",
            summary = "DeepMind, kanserli hücreleri standart taramalardan 18 ay önce tespit edebilen yeni medikal yapay zeka modeli Med-Alpha'yı doğruladı.",
            contentWhat = "Google DeepMind ekibi, tıp alanında devrim niteliğinde bir model yayınladı. Med-Alpha adı verilen bu model, mamografi ve MRI taramalarındaki mikro yapısal değişiklikleri inceleyerek kanser riskini henüz tümör oluşmadan 18 ay önce tespit edebiliyor. Klinik çalışmalarda başarı oranının %94.2 olduğu açıklandı.",
            contentWhy = "Erken teşhis, kanser tedavilerinde hayatta kalma oranını %90'ın üzerine çıkaran en kritik faktördür. Yapay zekanın radyoloji uzmanlarının gözden kaçırabileceği mikro anormallikleri saniyeler içinde saptaması, küresel sağlık sistemlerindeki yoğunluğu azaltacağı gibi milyonlarca hayatın kurtulmasını sağlayabilir.",
            aiComment = "Google'ın tıp alanındaki yapay zeka yatırımları meyvesini veriyor. Med-Alpha, AlphaFold'dan sonra biyoloji dünyasındaki en heyecan verici gelişmedir. Tabii ki bu modellerin doktorların yerini almasından ziyade, onlara asistanlık yapacak mükemmel birer ikinci göz olarak konumlandırılması gerekiyor.",
            consensusPoints = listOf(
                "Klinik testler 5 farklı ülkede 20,000 hasta üzerinde gerçekleştirildi.",
                "Yapay zekanın yanlış pozitif oranı uzman radyologlardan %12 daha düşük.",
                "Model, dünya genelindeki kamu hastanelerine ücretsiz API olarak sunulacak."
            ),
            unresolvedPoints = listOf(
                "Veri gizliliği ve hasta onaylarının sınırları tartışılmaktadır.",
                "Gelişmekte olan ülkelerdeki eski nesil görüntüleme cihazlarıyla uyumluluğu netleşmedi."
            ),
            coverUrl = "https://images.unsplash.com/photo-1530026405186-ed1ea0ac7a63?w=600&auto=format&fit=crop&q=80",
            firstTimestamp = "1 saat önce",
            lastTimestamp = "20 dakika önce",
            sourcesCount = 3
        ),
        Story(
            id = "story_transfer_osimhen",
            category = "Futbol & Transfer",
            importance = ImportanceLevel.CRITICAL,
            status = VerificationStatus.DEVELOPING,
            title = "Osimhen Transferinde Sıcak Gelişme: Galatasaray ve Napoli Anlaşmaya Çok Yakın",
            summary = "Galatasaray, dünya yıldızı forvet Victor Osimhen'i kiralamak için Napoli kulübü ile resmi görüşmelere başladı. İtalyan kaynaklar anlaşmanın bitmek üzere olduğunu bildiriyor.",
            contentWhat = "Süper Lig devi Galatasaray, Napoli'de kadro dışı kalan Nijeryalı golcü Victor Osimhen'i 1 yıllığına kiralamak için çılgın bir operasyon başlattı. Oyuncunun 11 milyon Euro'luk maaşının karşılanması konusunda sponsor desteği sağlandığı belirtilirken, İtalya'dan gelen haberler uçuş planlamasının dahi yapıldığını söylüyor.",
            contentWhy = "Bu kiralama işlemi, Türk futbol tarihinin en büyük sansasyonel transferlerinden biri olmaya aday. Osimhen'in güncel piyasa değerinin 100 milyon Euro barajında olması, Avrupa basınının da gözünü anında Süper Lig'e çevirmesini sağladı. Transferin tamamlanması Galatasaray'ın Avrupa Ligi iddialarını tamamen başka bir boyuta taşıyacaktır.",
            aiComment = "Kadro dışı kalma krizi olmasaydı Osimhen'i Türkiye'ye getirmek imkansızdı. Galatasaray yönetimi kriz anını muazzam bir fırsatçılıkla yönetti. Ancak Osimhen'in sözleşmesinde Ocak ayında Premier Lig ekipleri için bir çıkış maddesi bulunup bulunmayacağı, sarı kırmızılı taraftarların en büyük endişesi.",
            consensusPoints = listOf(
                "Galatasaray kiralama ücreti ödemeyecek, sadece maaş yükümlülüğünü üstlenecek.",
                "Oyuncu İstanbul'a gelmeye ve Galatasaray forması giymeye sıcak bakıyor.",
                "Napoli yönetimi kadroda düşünmediği oyuncunun ayrılmasına onay verdi."
            ),
            unresolvedPoints = listOf(
                "Ocak ayında dev takımlardan teklif gelmesi durumunda sözleşmenin feshedilme opsiyonu var mı?",
                "Maaşın ne kadarlık kısmının Galatasaray, ne kadarının sponsorlarca ödeneceği net açıklanmadı."
            ),
            coverUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600&auto=format&fit=crop&q=80",
            firstTimestamp = "45 dakika önce",
            lastTimestamp = "5 dakika önce",
            sourcesCount = 3
        ),
        Story(
            id = "story_tech_visionpro",
            category = "Teknoloji",
            importance = ImportanceLevel.HIGH,
            status = VerificationStatus.VERIFIED,
            title = "Apple, Daha Ucuz Bir Apple Vision Cihazı Üzerinde Çalışıyor",
            summary = "Apple'ın 3.500 dolarlık Vision Pro satışlarının ardından, yarı fiyatına satılacak ve iPhone gücüyle çalışacak daha hafif bir 'Apple Vision' gözlüğü üzerinde çalıştığı sızdırıldı.",
            contentWhat = "Bloomberg teknoloji muhabirleri ve tedarik zinciri analistlerinin sızıntılarına göre, Apple daha geniş kitlelere ulaşabilmek için 'Apple Vision' (Pro olmayan model) adında yeni bir başlık hazırlıyor. Cihazın ağırlığını düşürmek için harici bataryanın yanında, işlem gücünün bir kısmı doğrudan kullanıcının cebindeki iPhone'dan kablosuz olarak aktarılacak.",
            contentWhy = "Vision Pro'nun 3.500 dolarlık astronomik fiyatı, cihazın sadece teknoloji meraklıları ve kurumsal şirketlerle sınırlı kalmasına yol açtı. Apple'ın bu pazarda kalıcı bir ekosistem kurabilmesi için fiyatı 1.500 dolar bandına çekmesi ve günlük kullanıma uygun, hafif bir cihaz sunması şart.",
            aiComment = "Akıllı telefonların yerini alması beklenen uzamsal bilgisayarlar (spatial computing) için Vision Pro çok ağır ve pahalı bir prototipti. İşlem gücünü iPhone'a devretmek, Apple'ın hem pil süresini hem de ağırlığı düşürmek için alabileceği en mantıklı karardı. Bu hamle pazarın canlanmasını sağlayabilir.",
            consensusPoints = listOf(
                "Yeni cihazda daha ucuz ekran panelleri ve plastik gövde kullanılacak.",
                "Göz izleme ve el hareketleri kontrolü korunacak ancak dış ekran (EyeSight) kaldırılacak.",
                "Lansmanın 2027 ilk yarısında yapılması hedefleniyor."
            ),
            unresolvedPoints = listOf(
                "iPhone'un bataryasının bu veri aktarımıyla ne kadar hızlı tükeneceği bilinmiyor.",
                "Gözlüğün eski nesil iPhone modellerini destekleyip desteklemeyeceği belirsiz."
            ),
            coverUrl = "https://images.unsplash.com/photo-1593508512255-86ab42a8e620?w=600&auto=format&fit=crop&q=80",
            firstTimestamp = "2 saat önce",
            lastTimestamp = "40 dakika önce",
            sourcesCount = 3
        ),
        Story(
            id = "story_econ_tcmb",
            category = "Ekonomi & Finans",
            importance = ImportanceLevel.HIGH,
            status = VerificationStatus.OFFICIAL_STATEMENT,
            title = "Merkez Bankası Faiz Kararını Açıkladı: Faizler %50 Seviyesinde Sabit Tutuldu",
            summary = "Türkiye Cumhuriyet Merkez Bankası (TCMB) Para Politikası Kurulu, Temmuz ayı toplantısında politika faizini piyasa beklentilerine paralel olarak %50'de sabit bıraktı.",
            contentWhat = "TCMB tarafından yapılan açıklamada, enflasyondaki düşüş trendinin sürdüğü ancak hizmet enflasyonundaki katılık nedeniyle sıkı para politikası duruşunun kararlılıkla sürdürüleceği vurgulandı. Likidite fazlasını sterilize etmek amacıyla ek makroihtiyati tedbirlerin alınabileceği belirtildi.",
            contentWhy = "Faizin sabit tutulması, piyasalardaki sıkılaşmanın ve TL mevduat cazibesinin bir süre daha devam edeceği anlamına geliyor. Karar sonrası Dolar/TL kuru yatay seyrini korurken, borsa bankacılık hisselerinde hafif bir toparlanma gözlendi. Kredi faizlerinin yüksek seyri ise iç piyasadaki yavaşlamayı desteklemeye devam edecek.",
            aiComment = "Merkez Bankası pas geçerek enflasyonla mücadele kararlılığını yineledi. Sonbahar aylarında baz etkisiyle enflasyonda hızlı bir düşüş görecek olsak da, yapısal reformlar ve bütçe disiplini sağlanmadan faiz indirim döngüsüne girmek için henüz çok erken. Piyasalar ilk faiz indirimini Kasım veya Aralık ayında bekliyor.",
            consensusPoints = listOf(
                "Faiz kararı beklentilere %100 uyumlu gerçekleşti.",
                "Karar metninde ek sıkılaşma kapısı açık bırakılarak şahin duruş korundu.",
                "Enflasyon beklentilerinde yıl sonu hedefi olan %38-42 bandına bağlılık yinelendi."
            ),
            unresolvedPoints = listOf(
                "Asgari ücret artışı yapılmamasının iç talebe yansımasının ne zaman ölçüleceği net değil.",
                "Yabancı sermaye girişlerinin sürekliliği ve rezerv birikim hızı yakından takip edilecek."
            ),
            coverUrl = "https://images.unsplash.com/photo-1621761191319-c6fb62004040?w=600&auto=format&fit=crop&q=80",
            firstTimestamp = "3 saat önce",
            lastTimestamp = "1 saat önce",
            sourcesCount = 3
        ),
        Story(
            id = "story_ai_claude_desktop",
            category = "Yapay Zekâ",
            importance = ImportanceLevel.MEDIUM,
            status = VerificationStatus.VERIFIED,
            title = "Anthropic, Claude Yapay Zekâ Modelini Bilgisayarları Kontrol Edebilecek Şekilde Güncelledi",
            summary = "Anthropic, 'Computer Use' (Bilgisayar Kullanımı) özelliğini duyurdu. Claude artık bir insan gibi ekrana bakıp, imleci hareket ettirip, tıklamalar yapabiliyor ve yazı yazabiliyor.",
            contentWhat = "Anthropic, yapay zeka ajanlarında çığır açan bir API güncellemesi paylaştı. Claude 3.5 Sonnet, bilgisayar ekranının ekran görüntülerini alıp analiz ederek, yazılımcı veya ofis çalışanı gibi işletim sisteminde gezinebiliyor. Klasör açma, tarayıcıda form doldurma, Excel düzenleme ve yazılım hata ayıklama işlemlerini otonom gerçekleştirebiliyor.",
            contentWhy = "Bu özellik, yapay zekanın sadece metin üreten bir 'sohbet robotu' olmaktan çıkıp, işletim sistemleriyle doğrudan etkileşime geçen otonom bir 'operatör' haline geldiğinin en büyük kanıtı. Gelecekte rutin bilgisayar işlerinin neredeyse tamamının yapay zeka tarafından yapılmasının önünü açıyor.",
            aiComment = "Anthropic bu hamlesiyle OpenAI'ın önüne geçti. 'Computer Use' yapay zekanın uygulama bariyerlerini yıkan devrimsel bir yaklaşım. Tabii ki siber güvenlik, şifre çalınması veya yapay zekanın kontrolden çıkıp yanlış işlemler yapması (örneğin yanlışlıkla para transferi yapmak) gibi riskleri beraberinde getiriyor.",
            consensusPoints = listOf(
                "Özellik ilk etapta sadece geliştirici API'si üzerinden beta olarak sunuldu.",
                "Görsel analiz hızı saniyede 1-2 kare olarak optimize edildi.",
                "Güvenlik için finansal işlemler ve sosyal medya paylaşım sitelerinde kullanımı varsayılan olarak kısıtlandı."
            ),
            unresolvedPoints = listOf(
                "Normal kullanıcıların bilgisayarlarına yüklenebilecek bir masaüstü uygulamasına ne zaman entegre edileceği bilinmiyor.",
                "İşletim sistemi çökmelerinde veya takılmalarında Claude'un nasıl tepki vereceği tam test edilmedi."
            ),
            coverUrl = "https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=600&auto=format&fit=crop&q=80",
            firstTimestamp = "5 saat önce",
            lastTimestamp = "2 saat önce",
            sourcesCount = 4
        ),
        Story(
            id = "story_spor_fenerbahce",
            category = "Futbol & Transfer",
            importance = ImportanceLevel.MEDIUM,
            status = VerificationStatus.CLAIM,
            title = "Fenerbahçe Orta Sahaya İspanyol Yıldızı İstiyor: Görüşmeler Başladı",
            summary = "Fenerbahçe, Atletico Madrid'de forma giyen deneyimli orta saha oyuncusu için resmi teklif yaptı. Oyuncunun da transfere sıcak baktığı iddia ediliyor.",
            contentWhat = "Orta sahaya lider özellikli bir oyuncu arayan Fenerbahçe, teknik direktör Jose Mourinho'nun raporu doğrultusunda Atletico Madrid forması giyen tecrübeli orta saha için devreye girdi. Kiralama ve satın alma opsiyonları masada.",
            contentWhy = "Mourinho, merkez orta sahada oyunu iki yönlü oynayabilecek fizikli ve pas kalitesi yüksek bir oyuncu talep ediyordu. Bu transferin gerçekleşmesi durumunda Fenerbahçe hem ligdeki şampiyonluk yarışında hem de Avrupa arenasında orta saha direncini üst düzeye çıkaracaktır.",
            aiComment = "Mourinho faktörü transferde en büyük koz. Oyuncunun kariyerinin son döneminde Mourinho ile çalışmak istemesi çok doğal. Ancak Atletico Madrid'in bonservis inadı kırılmazsa, Fenerbahçe'nin alternatif isimlere yönelmesi gerekebilir.",
            consensusPoints = listOf(
                "Fenerbahçe idari menajeri oyuncunun menajeriyle İspanya'da bir araya geldi.",
                "Atletico Madrid kulübü 8 milyon Euro civarı bir bonservis bekliyor."
            ),
            unresolvedPoints = listOf(
                "Oyuncunun yıllık 4.5 milyon Euro maaş beklentisinin Fenerbahçe bütçesine uyumu netlik kazanmadı.",
                "Başka Premier Lig kulüplerinin de oyuncuya ilgisi olduğu iddia ediliyor."
            ),
            coverUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600&auto=format&fit=crop&q=80",
            firstTimestamp = "6 saat önce",
            lastTimestamp = "3 saat önce",
            sourcesCount = 2
        ),
        Story(
            id = "story_ai_gemini35",
            category = "Yapay Zekâ",
            importance = ImportanceLevel.HIGH,
            status = VerificationStatus.OFFICIAL_STATEMENT,
            title = "Google, En Güçlü Modeli Gemini 3.5'i Tanıttı",
            summary = "Google, yapay zeka yarışında çıtayı yükselterek çoklu ortam (multimodal) analizi ve akıl yürütme becerilerinde üstün başarı gösteren Gemini 3.5 modelini duyurdu.",
            contentWhat = "Google I/O etkinliği kapsamında duyurulan Gemini 3.5, özellikle ultra geniş bağlam kapasitesi (2 Milyon Token) ve video/ses verilerini sıfır gecikmeyle analiz etme becerisiyle öne çıkıyor. Google Asistan'ın yerini tamamen alacak olan model, akıllı telefonlarda tamamen yerleşik olarak çalışabilecek.",
            contentWhy = "Google, yapay zeka yarışında OpenAI ile başa baş liderliğini korumak istiyor. Gemini 3.5'in doğrudan Android işletim sisteminin çekirdeğine entegre edilmesi, milyarlarca kullanıcının günlük hayatta yapay zekayı doğal bir uzantı olarak kullanmasını sağlayacak.",
            aiComment = "Gemini 3.5'in 2M token bağlam penceresi hala sektördeki en büyük avantaj. Google, yazılım entegrasyonu gücü sayesinde bu modeli Android cihazlarda mükemmel çalışır hale getirdi. OpenAI'ın GPT-5 duyurusunun hemen ardından gelmesi, teknoloji devleri arasındaki rekabetin ne kadar sert olduğunu gösteriyor.",
            consensusPoints = listOf(
                "Model, tüm Google Workspace (Docs, Sheets, Gmail) uygulamalarına entegre edildi.",
                "Geliştiriciler için Google AI Studio üzerinden erişim ücretsiz deneme kotasıyla açıldı.",
                "Kodlama performansında GPT-4o'dan %15 daha yüksek başarı puanı elde etti."
            ),
            unresolvedPoints = listOf(
                "Gelişmiş özelliklerin Türkiye'de tamamen Türkçe dil desteğiyle ne zaman aktif olacağı kesinleşmedi.",
                "Bulut üzerinden çalışan versiyonların abonelik fiyatlandırması henüz açıklanmadı."
            ),
            coverUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=600&auto=format&fit=crop&q=80",
            firstTimestamp = "10 saat önce",
            lastTimestamp = "5 saat önce",
            sourcesCount = 3
        )
    )

    val timelineItems = listOf(
        // GPT-5 Timeline
        StoryTimelineItem("t1", "story_ai_gpt5", "10:05", "İlk iddialar sızdırıldı: OpenAI sunucularında hareketlilik var.", "src_sdn"),
        StoryTimelineItem("t2", "story_ai_gpt5", "10:18", "Sam Altman X üzerinden 'it's time' (vakit geldi) paylaşımını yaptı.", "src_sam_altman"),
        StoryTimelineItem("t3", "story_ai_gpt5", "10:42", "OpenAI resmi blogunda GPT-5 modeli detaylarıyla yayınlandı.", "src_openai"),
        StoryTimelineItem("t4", "story_ai_gpt5", "11:10", "Teknoloji analisti Barış Özcan YouTube canlı yayınında modeli inceledi.", "src_baris_ozcan"),

        // Osimhen Timeline
        StoryTimelineItem("t5", "story_transfer_osimhen", "09:12", "İtalyan gazeteci Fabrizio Romano, G.Saray'ın Osimhen için Napoli ile temas kurduğunu yazdı.", "src_fabrizio"),
        StoryTimelineItem("t6", "story_transfer_osimhen", "09:40", "Yağız Sabuncuoğlu transferin kiralık formülüyle bittiğini, uçak planlandığını bildirdi.", "src_yagiz"),
        StoryTimelineItem("t7", "story_transfer_osimhen", "10:15", "Galatasaray SK resmi hesabından 'görüşmelere başlandığı' KAP'a bildirildi.", "src_gs_official"),

        // Vision Pro Timeline
        StoryTimelineItem("t8", "story_tech_visionpro", "08:30", "Bloomberg, tedarik zincirinden aldığı ucuz ekran siparişi bilgisini sızdırdı.", "src_sdn"),
        StoryTimelineItem("t9", "story_tech_visionpro", "09:15", "Analistler Apple'ın ağırlık azaltmak için pili ve işlemciyi iPhone'a devredeceğini öngördü.", "src_webrazzi"),
        StoryTimelineItem("t10", "story_tech_visionpro", "10:45", "Teknoloji uzmanı Barış Özcan yeni gözlük konseptini detaylandıran video paylaştı.", "src_baris_ozcan")
    )

    val storySourceRelations = listOf(
        // GPT-5 Relations
        StorySourceRelation(
            storyId = "story_ai_gpt5",
            sourceId = "src_openai",
            postSnippet = "Geliştiriciler için yeni çağ başlıyor: GPT-5 API erişimi aktif hale getirildi. Kodlama ve analitikte %200 performans artışı.",
            timestamp = "10:42",
            originalUrl = "https://openai.com/blog/gpt-5"
        ),
        StorySourceRelation(
            storyId = "story_ai_gpt5",
            sourceId = "src_sam_altman",
            postSnippet = "GPT-5 bugün yayında. İnsanlığın en karmaşık problemlerini çözmek için tasarlanmış en güçlü aracımız. Deneyin ve yorumlarınızı paylaşın.",
            timestamp = "10:18",
            originalUrl = "https://x.com/sama/status/12345"
        ),
        StorySourceRelation(
            storyId = "story_ai_gpt5",
            sourceId = "src_baris_ozcan",
            postSnippet = "Yapay zeka devrimi vites büyüttü! GPT-5 resmi olarak duyuruldu. Canlı yayında kod yazdırıp, karmaşık belgeleri inceletiyoruz.",
            timestamp = "11:10",
            originalUrl = "https://youtube.com/watch?v=gpt5"
        ),
        StorySourceRelation(
            storyId = "story_ai_gpt5",
            sourceId = "src_sdn",
            postSnippet = "OpenAI bombayı patlattı! GPT-5 resmi olarak duyuruldu. İşte modelin özellikleri, fiyatı ve kullanıma sunulacağı tarih.",
            timestamp = "10:05",
            originalUrl = "https://shiftdelete.net/openai-gpt-5-ozellikleri"
        ),

        // Osimhen Relations
        StorySourceRelation(
            storyId = "story_transfer_osimhen",
            sourceId = "src_fabrizio",
            postSnippet = "BREAKING: Galatasaray are in advanced talks to sign Victor Osimhen on loan from Napoli! Salary coverage discussed. Here we go is close!",
            timestamp = "09:12",
            originalUrl = "https://x.com/FabrizioRomano/status/6789"
        ),
        StorySourceRelation(
            storyId = "story_transfer_osimhen",
            sourceId = "src_yagiz",
            postSnippet = "ÖZEL: Galatasaray, Victor Osimhen'i kadrosuna katmak üzere Napoli ile el sıkıştı. Oyuncu kiralık gelmeyi kabul etti. Bu gece İstanbul'da!",
            timestamp = "09:40",
            originalUrl = "https://x.com/yagosabuncuoglu/status/7112"
        ),
        StorySourceRelation(
            storyId = "story_transfer_osimhen",
            sourceId = "src_gs_official",
            postSnippet = "Profesyonel futbolcu Victor James Osimhen'in geçici transferi konusunda kulübü SSC Napoli ile resmi görüşmelere başlanmıştır.",
            timestamp = "10:15",
            originalUrl = "https://galatasaray.org/kap-osimhen"
        ),

        // Vision Pro Relations
        StorySourceRelation(
            storyId = "story_tech_visionpro",
            sourceId = "src_sdn",
            postSnippet = "Apple yarı fiyatına Vision Pro hazırlıyor! Cebimizdeki iPhone'un işlemcisini kullanacak cihazın hafifliği şaşırtacak.",
            timestamp = "08:30",
            originalUrl = "https://shiftdelete.net/ucuz-apple-vision"
        ),
        StorySourceRelation(
            storyId = "story_tech_visionpro",
            sourceId = "src_webrazzi",
            postSnippet = "Apple, Vision ekosistemini kurtarmak için daha ucuz bir model üzerinde çalışıyor. İşte tedarik zincirinden sızan ilk detaylar.",
            timestamp = "09:15",
            originalUrl = "https://webrazzi.com/apple-vision-ucuz-model"
        ),
        StorySourceRelation(
            storyId = "story_tech_visionpro",
            sourceId = "src_baris_ozcan",
            postSnippet = "3500 Dolarlık Vision Pro neden satmadı? Apple'ın yeni 1500 dolarlık uzamsal bilgisayar konseptini inceliyoruz.",
            timestamp = "10:45",
            originalUrl = "https://youtube.com/watch?v=visionpro-cheap"
        )
    )

    val gundemPackages = listOf(
        GundemPackage(
            id = "pkg_ai",
            title = "Yapay Zekâ Gündemi",
            description = "OpenAI, Anthropic, Google DeepMind, teknoloji yayınları ve seçilmiş global uzmanların tüm duyuruları ve analizleri.",
            sourcesCount = 5,
            dailyVolume = 12,
            followersCount = 42800,
            isFollowing = true,
            category = "Yapay Zekâ"
        ),
        GundemPackage(
            id = "pkg_transfer",
            title = "Süper Lig Transfer Gündemi",
            description = "Fabrizio Romano, Yağız Sabuncuoğlu, kulüplerin resmi hesapları ve en güvenilir spor muhabirlerinin anlık doğrulanmış transfer haberleri.",
            sourcesCount = 3,
            dailyVolume = 24,
            followersCount = 68400,
            isFollowing = false,
            category = "Futbol & Transfer"
        ),
        GundemPackage(
            id = "pkg_girisim",
            title = "Girişimcilik Dünyası",
            description = "Yatırım fonları, startup kurucuları, Webrazzi sızıntıları ve ekosistemdeki önemli satın alma/yatırım gelişmeleri.",
            sourcesCount = 2,
            dailyVolume = 6,
            followersCount = 18300,
            isFollowing = false,
            category = "Girişimcilik"
        ),
        GundemPackage(
            id = "pkg_finance",
            title = "Makro Ekonomi & Kripto",
            description = "TCMB kararları, FED sızıntıları, CoinDesk analizleri ve makro ekonomik göstergelerin tek bir akıştaki yansımaları.",
            sourcesCount = 2,
            dailyVolume = 8,
            followersCount = 32100,
            isFollowing = false,
            category = "Ekonomi & Finans"
        )
    )

    val notifications = listOf(
        NotificationEntity(
            id = "notif_1",
            title = "Kritik Gelişme 🚨",
            description = "Takip ettiğin Yapay Zekâ gündeminde OpenAI, yeni nesil modeli GPT-5'i resmen duyurdu!",
            type = "CRITICAL",
            timestamp = "15 dakika önce",
            isRead = false,
            storyId = "story_ai_gpt5"
        ),
        NotificationEntity(
            id = "notif_2",
            title = "Son Dakika Transfer ⚽",
            description = "Galatasaray, Victor Osimhen'i kiralamak için Napoli ile KAP görüşmelerine başladı!",
            type = "CRITICAL",
            timestamp = "45 dakika önce",
            isRead = false,
            storyId = "story_transfer_osimhen"
        ),
        NotificationEntity(
            id = "notif_3",
            title = "Resmi Açıklama 🏦",
            description = "Merkez Bankası politika faizini piyasa beklentilerine paralel olarak %50 seviyesinde sabit bıraktı.",
            type = "OFFICIAL_STATEMENT",
            timestamp = "3 saat önce",
            isRead = true,
            storyId = "story_econ_tcmb"
        ),
        NotificationEntity(
            id = "notif_4",
            title = "Doğrulanmış Bilgi 🧬",
            description = "Google DeepMind, kanseri taramalardan 18 ay önce saptayan Med-Alpha yapay zeka modelini test etti.",
            type = "RECENT_UPDATE",
            timestamp = "1 saat önce",
            isRead = true,
            storyId = "story_ai_deepmind"
        ),
        NotificationEntity(
            id = "notif_5",
            title = "Yapay Zeka Güncellemesi 🖥️",
            description = "Anthropic, Claude modelinin artık bilgisayarları bir insan gibi kontrol edebildiğini gösterdi.",
            type = "RECENT_UPDATE",
            timestamp = "5 saat önce",
            isRead = true,
            storyId = "story_ai_claude_desktop"
        ),
        NotificationEntity(
            id = "notif_6",
            title = "Teknoloji Sızıntısı 🕶️",
            description = "Apple'ın yarı fiyatına, işlem gücünü iPhone'dan alan yeni bir Apple Vision başlığı geliştirdiği sızdırıldı.",
            type = "RECENT_UPDATE",
            timestamp = "2 saat önce",
            isRead = true,
            storyId = "story_tech_visionpro"
        ),
        NotificationEntity(
            id = "notif_7",
            title = "Transfer İddiası 📌",
            description = "Fenerbahçe'nin Atletico Madrid orta sahası için İspanya'da temaslarda bulunduğu iddia edildi.",
            type = "RECENT_UPDATE",
            timestamp = "6 saat önce",
            isRead = true,
            storyId = "story_spor_fenerbahce"
        ),
        NotificationEntity(
            id = "notif_8",
            title = "Google I/O Lansmanı 🌟",
            description = "Google, yapay zeka entegrasyonlarını baştan tanımlayan Gemini 3.5 modelini resmi olarak tanıttı.",
            type = "OFFICIAL_STATEMENT",
            timestamp = "10 saat önce",
            isRead = true,
            storyId = "story_ai_gemini35"
        )
    )
}
