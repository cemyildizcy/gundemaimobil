@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.MainViewModel
import com.example.ui.components.*

@Composable
fun LandingScreen(
    viewModel: MainViewModel,
    onOnboardingComplete: () -> Unit
) {
    var onboardingStep by remember { mutableStateOf(-1) } // -1 is Landing Page

    // Local Wizard States
    var userName by remember { mutableStateOf("Cem") }
    var selectedInterests by remember { mutableStateOf(setOf("Yapay Zekâ")) }
    var volumeLevel by remember { mutableStateOf("Dengeli") }
    var notificationFreq by remember { mutableStateOf(setOf("Sabah özeti", "Akşam özeti")) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = onboardingStep,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "onboarding_navigation"
            ) { step ->
                when (step) {
                    -1 -> LandingPage(
                        onStartClick = { onboardingStep = 0 }
                    )
                    0 -> OnboardingStep1(
                        userName = userName,
                        onNameChange = { userName = it },
                        selectedInterests = selectedInterests,
                        onInterestsChange = { selectedInterests = it },
                        onNext = { onboardingStep = 1 }
                    )
                    1 -> OnboardingStep2(
                        volumeLevel = volumeLevel,
                        onVolumeChange = { volumeLevel = it },
                        onBack = { onboardingStep = 0 },
                        onNext = { onboardingStep = 2 }
                    )
                    2 -> OnboardingStep3(
                        notificationFreq = notificationFreq,
                        onFreqChange = { notificationFreq = it },
                        onBack = { onboardingStep = 1 },
                        onFinish = {
                            viewModel.setOnboardingPreferences(
                                name = userName,
                                interests = selectedInterests,
                                volume = volumeLevel,
                                frequencies = notificationFreq
                            )
                            onOnboardingComplete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LandingPage(
    onStartClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Logo
        GundemAILogo(
            modifier = Modifier.padding(8.dp),
            iconSize = 38.dp,
            innerSize = 14.dp,
            textStyle = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Hero title & Subtitle
        Text(
            text = "Gündemi takip etme.\nGündem seni bulsun.",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            lineHeight = 44.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Güvenilir uzmanları, kurumları, haberleri ve önemli gelişmeleri tek ekranda takip et. GündemAI aynı olaya ait içerikleri birleştirir, Türkçe özetler ve yalnızca gerçekten önemli gelişmelerde seni haberdar eder.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // CTA Buttons
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("get_started_button"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ücretsiz Başla",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Filled.ArrowForward, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color(0xFF2F3336))
        ) {
            Text(
                text = "Demoyu İncele",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Mock Phone Screen Preview
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Günaydın Cem 👋", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Son ziyaretinden beri: 12 yeni", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = DarkSecondary, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Preview
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DarkSecondary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("YAPAY ZEKÂ", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DarkSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DarkError.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("KRİTİK", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DarkError)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "OpenAI Yeni Nesil Yapay Zekâ Modeli 'GPT-5'i Resmen Duyurdu",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Akıl yürütme, kod yazma ve görsel analiz kabiliyetlerinde devrim yaratan yeni modeli GPT-5 tanıtıldı...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Features List
        Text(
            text = "GündemAI Nasıl Çalışır?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(16.dp))

        val features = listOf(
            Triple(Icons.Filled.FilterList, "Farklı Platformlar, Tek Akış", "X, Threads, YouTube, bloglar, haber siteleri ve resmi kurum açıklamalarını tek bir akışta birleştirir."),
            Triple(Icons.Filled.Group, "Tekrarlananları Tek Bir Gelişmede Birleştirme", "Aynı gelişmeyle ilgili tekrar eden yüzlerce paylaşımı tek bir hikaye kartı altında toplar."),
            Triple(Icons.Filled.AutoAwesome, "\"Ne Oldu?\" ve \"Neden Önemli?\" Özetleri", "Yapay zeka, karmaşık haber metinlerini tarafsızca analiz eder ve en önemli bilgileri saniyeler içinde özetler."),
            Triple(Icons.Filled.NotificationsActive, "Akıllı ve Kişiselleştirilebilir Bildirimler", "Sadece gerçekten önem verdiğiniz konulardaki büyük gelişmelerde anlık bildirim alırsınız.")
        )

        features.forEach { (icon, title, desc) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = desc, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Testimonials
        Text(
            text = "Kullanıcı Yorumları",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(16.dp))

        val testimonials = listOf(
            Triple("Selim Y.", "Yazılım Geliştirici", "Yapay zeka gelişmelerini takip etmek için 15 farklı kanalı gezerdim. GündemAI her şeyi birleştiriyor ve zaman tasarrufu sağlıyor."),
            Triple("Melis K.", "Girişimci & Yatırımcı", "Aynı olayın farklı kaynaklardaki analizlerini kronolojik zaman çizgisiyle görmek benzersiz bir özellik."),
            Triple("Efe T.", "Teknoloji Meraklısı", "Sabah ve akşam bültenleri harika. Sadece gerçekten önemli gelişmelerde telefonuma bildirim geliyor, bilgi kirliliği yok.")
        )

        testimonials.forEach { (name, job, text) ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "\"$text\"", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = job, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Footer
        Divider()
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "© 2026 GündemAI. Tüm Hakları Saklıdır.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Hakkımızda | Gizlilik Politikası | Kullanım Koşulları | Kaynak Politikası",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
fun OnboardingStep1(
    userName: String,
    onNameChange: (String) -> Unit,
    selectedInterests: Set<String>,
    onInterestsChange: (Set<String>) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Profilini Oluştur",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sana hitap edebilmemiz ve sadece ilgilendiğin gelişmeleri derleyebilmemiz için bilgileri girin.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Name input
            Text(
                text = "Adınız",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = userName,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_input"),
                placeholder = { Text("Örn: Cem", color = Color(0xFF71767B)) },
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1D9BF0),
                    unfocusedBorderColor = Color(0xFF2F3336),
                    focusedContainerColor = Color(0xFF0F1419),
                    unfocusedContainerColor = Color(0xFF0A0F1D),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Interests Selection
            Text(
                text = "İlgi Alanlarını Seç (En az 1 adet)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Grid Layout for interests
            val topics = MockData.topics
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .height(280.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(topics) { topic ->
                    val isSelected = selectedInterests.contains(topic.name)
                    Surface(
                        onClick = {
                            val next = selectedInterests.toMutableSet()
                            if (isSelected) {
                                next.remove(topic.name)
                            } else {
                                next.add(topic.name)
                            }
                            onInterestsChange(next)
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = when (topic.id) {
                                    "yapay_zeka" -> Icons.Filled.AutoAwesome
                                    "teknoloji" -> Icons.Filled.Devices
                                    "spor" -> Icons.Filled.SportsSoccer
                                    "turkiye" -> Icons.Filled.Flag
                                    "dunya" -> Icons.Filled.Public
                                    "ekonomi" -> Icons.Filled.Payments
                                    "girisimcilik" -> Icons.Filled.RocketLaunch
                                    else -> Icons.Filled.Biotech
                                },
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = topic.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 24.dp)) {
            Button(
                onClick = onNext,
                enabled = selectedInterests.isNotEmpty() && userName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("step1_next_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.White.copy(alpha = 0.3f),
                    disabledContentColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Text("Devam Et", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OnboardingStep2(
    volumeLevel: String,
    onVolumeChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gündem Yoğunluğu",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hangi sıklıkla gelişmeleri almak istersiniz? GündemAI akıllı asistan filtrelemeyi buna göre yapacaktır.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(36.dp))

            val volumes = listOf(
                Pair("Sakin", "Yalnızca kritik ve son dakika gelişmeleri bülteninize eklenir. Günde ortalama 2-3 kart görürsünüz."),
                Pair("Dengeli", "Sektörde öne çıkan önemli gelişmeler ve kritik olaylar derlenir. Günde ortalama 8-12 kart görürsünüz."),
                Pair("Yoğun", "Takip ettiğiniz alanlardaki tüm sızıntılar, uzman yorumları ve paylaşımlar akışınızda listelenir.")
            )

            volumes.forEach { (title, desc) ->
                val isSelected = volumeLevel == title
                Surface(
                    onClick = { onVolumeChange(title) },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onVolumeChange(title) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("step2_next_button"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("Devam Et", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OnboardingStep3(
    notificationFreq: Set<String>,
    onFreqChange: (Set<String>) -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bildirim Tercihleri",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Zamanınızı çalmayan akıllı bildirimler hazırlıyoruz. Hangi bildirimleri almak istersiniz?",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(36.dp))

            val options = listOf(
                Pair("Son dakika bildirimleri", "Kritik öneme sahip ani gelişmelerde anında bildirim."),
                Pair("Sabah gündem özeti", "Her sabah saat 08:30'da güne başlarken 2 dakikalık yapay zeka sesli/yazılı özeti."),
                Pair("Akşam gündem özeti", "Her akşam saat 19:30'da günün tüm birleştirilmiş gelişmelerinin özeti."),
                Pair("Haftalık rapor", "Her Pazar günü en çok okuduğunuz alanların haftalık trend analizi.")
            )

            options.forEach { (title, desc) ->
                val isSelected = notificationFreq.contains(title)
                Surface(
                    onClick = {
                        val next = notificationFreq.toMutableSet()
                        if (isSelected) {
                            next.remove(title)
                        } else {
                            next.add(title)
                        }
                        onFreqChange(next)
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                val next = notificationFreq.toMutableSet()
                                if (isSelected) {
                                    next.remove(title)
                                } else {
                                    next.add(title)
                                }
                                onFreqChange(next)
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("onboarding_finish_button"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("Gündemimi Oluştur 🚀", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
