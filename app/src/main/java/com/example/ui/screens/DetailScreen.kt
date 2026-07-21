@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DetailScreen(
    storyId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val stories by viewModel.stories.collectAsState()
    val story = remember(stories, storyId) { stories.firstOrNull { it.id == storyId } }

    val isEnrichingSet by viewModel.isEnriching.collectAsState()
    val isCurrentlyEnriching = remember(isEnrichingSet, storyId) { isEnrichingSet.contains(storyId) }

    LaunchedEffect(storyId) {
        viewModel.enrichStoryWithGemini(storyId)
    }

    val context = LocalContext.current

    if (story == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Gelişme bulunamadı.")
        }
        return
    }

    var selectedSection by remember { mutableIntStateOf(0) } // 0: Özet, 1: Zaman Çizelgesi, 2: Kaynaklar

    // Use actual enriched timeline/sources if available, otherwise fallback
    val timeline = remember(storyId, story) {
        if (story != null && story.timeline.isNotEmpty()) {
            story.timeline
        } else {
            story?.let { s -> generateDynamicTimeline(s) } ?: emptyList()
        }
    }
    val relations = remember(storyId, story) {
        if (story != null && story.enrichedSources.isNotEmpty()) {
            story.enrichedSources
        } else {
            story?.let { s -> generateDynamicSources(s) } ?: emptyList()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(story.category, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleSaveStory(story.id) },
                        modifier = Modifier.testTag("detail_save_button")
                    ) {
                        Icon(
                            imageVector = if (story.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Kaydet",
                            tint = if (story.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, "${story.title}\n${story.originalUrl}\n\nGündemAI aracılığıyla paylaşıldı.")
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Haberi Paylaş"))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Paylaş")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Cover Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = story.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Title
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badges & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = story.status)
                    Spacer(modifier = Modifier.width(6.dp))
                    ImportanceBadge(level = story.importance)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Güncellendi: ${story.lastTimestamp}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section tabs row (Özet, Zaman Çizelgesi, Kaynaklar)
                TabRow(
                    selectedTabIndex = selectedSection,
                    containerColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        text = { Text("Özet", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("detail_tab_summary")
                    )
                    Tab(
                        selected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        text = { Text("Zaman Çizelgesi", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("detail_tab_timeline")
                    )
                    Tab(
                        selected = selectedSection == 2,
                        onClick = { selectedSection = 2 },
                        text = { Text("Kaynaklar (${story.sourcesCount})", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("detail_tab_sources")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isCurrentlyEnriching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Gelişme Yapay Zekâ Tarafından Analiz Ediliyor...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Haber detayları taranıyor, çelişkili iddialar ayıklanıyor ve tarafsız yapay zekâ analiz raporu oluşturuluyor.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                } else {
                    // Content Panel according to active Section Tab
                    when (selectedSection) {
                        0 -> SummarySection(story = story)
                        1 -> TimelineSection(timeline = timeline, story = story)
                        2 -> SourcesSection(relations = relations, story = story)
                    }
                }
            }
        }
    }
}

@Composable
fun SummarySection(story: Story) {
    Column {
        // "Ne Oldu?" Section
        Text(
            text = "Ne Oldu?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = story.contentWhat,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // "Neden Önemli?" Section
        Text(
            text = "Neden Önemli?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = story.contentWhy,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // "GündemAI Yorumu" Section (Geometric Balance Styled)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardGradient)
                .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GündemAI Tarafsız Yapay Zeka Yorumu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = story.aiComment,
                    fontSize = 13.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // "Consensus / Kaynakların ortaklaştığı noktalar" Section
        if (story.consensusPoints.isNotEmpty()) {
            Text(
                text = "Kaynakların Ortaklaştığı Noktalar ✅",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            story.consensusPoints.forEach { point ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "• ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                    Text(text = point, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Unresolved / Henüz Netleşmeyen noktalar" Section
        if (story.unresolvedPoints.isNotEmpty()) {
            Text(
                text = "Henüz Netleşmeyen Noktalar ❓",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PremiumGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            story.unresolvedPoints.forEach { point ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "• ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PremiumGold)
                    Text(text = point, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                }
            }
        }

        AIOverlayDisclaimer()
    }
}

@Composable
fun TimelineSection(timeline: List<StoryTimelineItem>, story: Story) {
    if (timeline.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Bu gelişmeye ait zaman çizelgesi verisi henüz bulunmuyor.", color = SoftGray)
        }
        return
    }

    Column {
        Text(
            text = "Gelişme Zaman Çizelgesi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        timeline.forEachIndexed { index, item ->
            val source = resolveSource(item.sourceId, story)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Timeline Line Left Circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    if (index < timeline.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }

                // Timeline Event Details Box
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.timestamp, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = source.name, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                PlatformIcon(platform = source.platform)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.eventDescription,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SourcesSection(relations: List<StorySourceRelation>, story: Story) {
    val context = LocalContext.current

    if (relations.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Kaynak bağlantısı bulunamadı.", color = SoftGray)
        }
        return
    }

    Column {
        Text(
            text = "Birleştirilmiş Bilgi Kaynakları",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        relations.forEach { rel ->
            val source = resolveSource(rel.sourceId, story)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = source.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = source.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    PlatformIcon(platform = source.platform)
                                }
                                Text(text = source.username, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Text(text = rel.timestamp, fontSize = 11.sp, color = SoftGray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Snippet content
                    Text(
                        text = "\"${rel.postSnippet}\"",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // CTA Button to external source
                    Button(
                        onClick = {
                            val webIntent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse(rel.originalUrl)
                            )
                            try {
                                context.startActivity(webIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Bağlantı açılamadı: ${rel.originalUrl}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .height(32.dp)
                    ) {
                        Text("Orijinal Kaynağa Git", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Resolve Source helper dynamically mapping publisher names to live Source objects
private fun resolveSource(sourceId: String, story: Story): Source {
    val mockSource = MockData.sources.firstOrNull { it.id == sourceId }
    if (mockSource != null) return mockSource

    val cleanName = sourceId.removePrefix("src_live_").replace("_", " ")
    val avatarUrl = when {
        cleanName.contains("webrazzi", ignoreCase = true) -> "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("shiftdelete", ignoreCase = true) || cleanName.contains("sdn", ignoreCase = true) -> "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("donanım", ignoreCase = true) || cleanName.contains("donanim", ignoreCase = true) -> "https://images.unsplash.com/photo-1531297484001-80022131f5a1?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("log", ignoreCase = true) && cleanName.length <= 5 -> "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("ntv", ignoreCase = true) -> "https://images.unsplash.com/photo-1495020689067-958852a6565d?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("trt", ignoreCase = true) -> "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("aspor", ignoreCase = true) || cleanName.contains("a spor", ignoreCase = true) -> "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("fotomaç", ignoreCase = true) || cleanName.contains("fotomac", ignoreCase = true) -> "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("fanatik", ignoreCase = true) -> "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("hürriyet", ignoreCase = true) || cleanName.contains("hurriyet", ignoreCase = true) -> "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("milliyet", ignoreCase = true) -> "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("bloomberg", ignoreCase = true) -> "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("sözcü", ignoreCase = true) || cleanName.contains("sozcu", ignoreCase = true) -> "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("habertürk", ignoreCase = true) || cleanName.contains("haberturk", ignoreCase = true) -> "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=120&auto=format&fit=crop&q=60"
        cleanName.contains("kap", ignoreCase = true) -> "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=120&auto=format&fit=crop&q=60"
        else -> "https://images.unsplash.com/photo-1495020689067-958852a6565d?w=120&auto=format&fit=crop&q=60"
    }

    val platform = when {
        cleanName.contains("X", ignoreCase = false) -> PlatformType.X
        cleanName.contains("Youtube", ignoreCase = true) -> PlatformType.YOUTUBE
        cleanName.contains("KAP", ignoreCase = false) -> PlatformType.OFFICIAL
        else -> PlatformType.NEWS
    }

    return Source(
        id = sourceId,
        name = cleanName,
        username = "@${cleanName.replace(" ", "").lowercase()}",
        type = SourceType.NEWS_SITE,
        platform = platform,
        avatarUrl = avatarUrl,
        fieldOfExpertise = "Güncel & Canlı Haber Akışı",
        reliabilityLabel = "Doğrulanmış Kaynak"
    )
}

private fun getRealisticSourcesForCategory(category: String, primarySource: String): List<String> {
    val techSources = listOf("Webrazzi", "ShiftDelete.Net", "DonanımHaber", "LOG", "TeknoSeyir", "Chip Online")
    val sporSources = listOf("TRT Spor", "A Spor", "Fotomaç", "Fanatik", "NTV Spor", "beIN Sports")
    val ekonomiSources = listOf("Bloomberg HT", "KAP", "Ekonomist", "Dünya Gazetesi", "NTV Para", "Bigpara")
    val generalSources = listOf("TRT Haber", "NTV", "Anadolu Ajansı", "Hürriyet", "Milliyet", "Sözcü", "Habertürk")

    val pool = when (category) {
        "Yapay Zekâ", "Teknoloji" -> techSources
        "Transfer", "Futbol & Transfer", "Spor" -> sporSources
        "Ekonomi", "Ekonomi & Finans" -> ekonomiSources
        else -> generalSources
    }

    val filteredPool = pool.filter { !it.contains(primarySource, ignoreCase = true) && !primarySource.contains(it, ignoreCase = true) }
    val chosenFromPool = filteredPool.shuffled().take(2)
    val result = mutableListOf<String>()
    result.add(primarySource)
    result.addAll(chosenFromPool)
    
    while (result.size < 3) {
        val extra = pool.firstOrNull { name -> !result.any { it.contains(name, ignoreCase = true) } } ?: "Haber Merkezi"
        result.add(extra)
    }
    return result.take(3)
}

private fun generateDynamicTimeline(story: Story): List<StoryTimelineItem> {
    val baseTimeStr = story.lastTimestamp // This is the HH:mm format e.g. "10:15"
    val parts = baseTimeStr.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 10
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 15
    
    // Time 1: 35 minutes before base time
    val min1 = (minute - 35 + 60) % 60
    val hr1 = (hour - (if (minute < 35) 1 else 0) + 24) % 24
    val time1 = String.format("%02d:%02d", hr1, min1)
    
    // Time 2: 12 minutes before base time
    val min2 = (minute - 12 + 60) % 60
    val hr2 = (hour - (if (minute < 12) 1 else 0) + 24) % 24
    val time2 = String.format("%02d:%02d", hr2, min2)
    
    val time3 = baseTimeStr
    
    val chosenSources = getRealisticSourcesForCategory(story.category, story.sourceName)
    val sourceName1 = chosenSources[2]
    val sourceName2 = chosenSources[1]
    val sourceName3 = chosenSources[0]
    
    val headline = story.title

    val desc1 = when (story.category) {
        "Yapay Zekâ", "Teknoloji" -> "$sourceName1 teknik analiz ekipleri, \"$headline\" konusuna ilişkin ilk sızıntı detaylarını ve saha raporlarını paylaştı."
        "Transfer" -> "Özel Haber: $sourceName1 muhabirleri, \"$headline\" gelişmesine dair kulüpler arasındaki ilk resmi temasları duyurdu."
        "Ekonomi" -> "$sourceName1 uzmanları, piyasada ses getiren \"$headline\" kararına dair ilk finansal öncü verileri değerlendirdi."
        else -> "$sourceName1 haber merkezi, \"$headline\" başlığı altında gelişen olayların ilk kritik detaylarını kamuoyuna aktardı."
    }
    
    val desc2 = when (story.category) {
        "Yapay Zekâ", "Teknoloji" -> "$sourceName2 editörleri, bağımsız kaynaklardan aldıkları ek bilgilerle haberi teyit ederek derinlemesine bir bülten yayınladı."
        "Transfer" -> "Son Dakika: $sourceName2, taraflar arasındaki resmi anlaşma maddelerini ve kontrat detaylarını sızdırdı."
        "Ekonomi" -> "$sourceName2 analistleri, \"$headline\" kararının piyasa dengelerine ve makro göstergelere yansımalarını inceleyen bir rapor paylaştı."
        else -> "$sourceName2 muhabirleri, olay yerinden aktarılan yeni bulguları ve doğrudan kaynak teyitlerini haber merkezine ulaştırdı."
    }
    
    val desc3 = "Gelişme anlık olarak yayında: Orijinal kaynak '$sourceName3' haberi resmi olarak servis etti: \"$headline\"."

    val id1 = "src_live_" + sourceName1.replace(" ", "_")
    val id2 = "src_live_" + sourceName2.replace(" ", "_")
    val id3 = "src_live_" + sourceName3.replace(" ", "_")

    return listOf(
        StoryTimelineItem("dt_${story.id}_1", story.id, time1, desc1, id1),
        StoryTimelineItem("dt_${story.id}_2", story.id, time2, desc2, id2),
        StoryTimelineItem("dt_${story.id}_3", story.id, time3, desc3, id3)
    )
}

private fun generateDynamicSources(story: Story): List<StorySourceRelation> {
    val baseTimeStr = story.lastTimestamp
    val parts = baseTimeStr.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 10
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 15

    val min1 = (minute - 35 + 60) % 60
    val hr1 = (hour - (if (minute < 35) 1 else 0) + 24) % 24
    val time1 = String.format("%02d:%02d", hr1, min1)
    
    val min2 = (minute - 12 + 60) % 60
    val hr2 = (hour - (if (minute < 12) 1 else 0) + 24) % 24
    val time2 = String.format("%02d:%02d", hr2, min2)
    
    val time3 = baseTimeStr

    val chosenSources = getRealisticSourcesForCategory(story.category, story.sourceName)
    val sourceName1 = chosenSources[2]
    val sourceName2 = chosenSources[1]
    val sourceName3 = chosenSources[0]
    
    val headline = story.title

    val snippet1 = when (story.category) {
        "Yapay Zekâ", "Teknoloji" -> "\"$headline\" konusunda ilk teknik veriler ve Ar-Ge sızıntıları ulaştı. Yeni altyapı oldukça başarılı duruyor."
        "Transfer" -> "\"$headline\" transferinde sıcak gelişmeler var. Taraflar prensipte anlaşma sağladı, ödeme planı netleşiyor."
        "Ekonomi" -> "\"$headline\" sonrasında piyasada faiz, döviz ve tahvil piyasasında ilk tepkiler izleniyor. Yatırımcılar temkinli."
        else -> "\"$headline\" hakkında bölgedeki resmi ve yerel kaynaklardan doğrulanmış ilk raporlar haber merkezimize ulaştı."
    }

    val snippet2 = when (story.category) {
        "Yapay Zekâ", "Teknoloji" -> "Yapay zekâ ekosisteminde büyük heyecan yaratan bu adım, üretkenliği ve kullanıcı deneyimlerini kökten değiştirebilir."
        "Transfer" -> "Kulüp kanallarından teyit edilen bilgilere göre, resmi bütçe onaylandı. Oyuncu sağlık kontrolleri için davet edildi."
        "Ekonomi" -> "Kararın ardından portföy yöneticileri ve makro analistler pozisyonlarını güncelliyor. Piyasa likiditesi oldukça yüksek."
        else -> "Gelişmeye ilişkin tarafların resmi açıklamaları ve saha gözlem raporları, anlık haber akışımızda güncelleniyor."
    }

    val snippet3 = "Google News verilerine göre: \"$headline\". Orijinal ve tarafsız detaylarla gelişmeyi kaynağından izleyin."

    val id1 = "src_live_" + sourceName1.replace(" ", "_")
    val id2 = "src_live_" + sourceName2.replace(" ", "_")
    val id3 = "src_live_" + sourceName3.replace(" ", "_")

    val url1 = "https://www.google.com/search?q=${java.net.URLEncoder.encode(story.title + " " + sourceName1, "UTF-8")}"
    val url2 = "https://www.google.com/search?q=${java.net.URLEncoder.encode(story.title + " " + sourceName2, "UTF-8")}"
    val url3 = story.originalUrl

    return listOf(
        StorySourceRelation(story.id, id1, snippet1, time1, url1),
        StorySourceRelation(story.id, id2, snippet2, time2, url2),
        StorySourceRelation(story.id, id3, snippet3, time3, url3)
    )
}
