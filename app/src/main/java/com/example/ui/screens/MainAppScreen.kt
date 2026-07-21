@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

import androidx.activity.compose.BackHandler

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var activeStoryId by remember { mutableStateOf<String?>(null) }
    var isChatbotOpen by remember { mutableStateOf(false) }
    var isSummaryOpen by remember { mutableStateOf(false) }

    // Handle System Back Button
    BackHandler(enabled = activeStoryId != null || isChatbotOpen || isSummaryOpen) {
        when {
            isSummaryOpen -> isSummaryOpen = false
            isChatbotOpen -> isChatbotOpen = false
            activeStoryId != null -> activeStoryId = null
        }
    }

    val stories by viewModel.stories.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                // Sleek X/Instagram-style ultra-thin bottom navigation
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = Color(0xFF2F3336),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .fillMaxWidth()
                            .height(58.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabs = listOf(
                            Triple("Gündem", Icons.Outlined.Feed, Icons.Filled.Feed),
                            Triple("Keşfet", Icons.Outlined.Explore, Icons.Filled.Explore),
                            Triple("Kaydet", Icons.Outlined.BookmarkBorder, Icons.Filled.Bookmark),
                            Triple("Bildirim", Icons.Outlined.Notifications, Icons.Filled.Notifications),
                            Triple("Profil", Icons.Outlined.Person, Icons.Filled.Person)
                        )

                        tabs.forEachIndexed { index, (label, outlineIcon, filledIcon) ->
                            val isSelected = selectedTab == index
                            val icon = if (isSelected) filledIcon else outlineIcon
                            val unreadCount = if (index == 3) notifications.count { !it.isRead } else 0

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        selectedTab = index
                                        activeStoryId = null // clear detail overlay on tab change
                                    }
                                    .padding(vertical = 4.dp)
                                    .testTag("nav_tab_$index"),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                BadgedBox(badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = Color(0xFFF4212E)) {
                                            Text(
                                                text = unreadCount.toString(),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) Color.White else Color(0xFF71767B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White else Color(0xFF71767B),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab_navigation"
                ) { tab ->
                    when (tab) {
                        0 -> GundemTab(
                            viewModel = viewModel,
                            onStoryClick = { activeStoryId = it },
                            onChatToggle = { isChatbotOpen = true },
                            onSummaryToggle = { isSummaryOpen = true }
                        )
                        1 -> KesfetTab(viewModel = viewModel)
                        2 -> SavedTab(viewModel = viewModel, onStoryClick = { activeStoryId = it })
                        3 -> NotificationsTab(viewModel = viewModel, onStoryClick = { activeStoryId = it })
                        4 -> ProfileTab(viewModel = viewModel, onLogout = onLogout)
                    }
                }
            }
        }

        // Animated Overlays for Detail Screen
        AnimatedVisibility(
            visible = activeStoryId != null,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            val storyId = activeStoryId
            if (storyId != null) {
                DetailScreen(
                    storyId = storyId,
                    viewModel = viewModel,
                    onBack = { activeStoryId = null }
                )
            }
        }

        // Animated Overlay for Chatbot Assistant
        AnimatedVisibility(
            visible = isChatbotOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            ChatbotScreen(
                viewModel = viewModel,
                onClose = { isChatbotOpen = false }
            )
        }

        // 2-Minute Summary Dialog
        if (isSummaryOpen) {
            val summary = viewModel.generate2MinuteSummary()
            SummaryDialog(
                summaryText = summary,
                onDismiss = { isSummaryOpen = false }
            )
        }
    }
}
@Composable
fun GundemTab(
    viewModel: MainViewModel,
    onStoryClick: (String) -> Unit,
    onChatToggle: () -> Unit,
    onSummaryToggle: () -> Unit
) {
    val stories by viewModel.stories.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val followedSourceIds by viewModel.sources.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val context = LocalContext.current

    // Local filters logic
    val filteredStories = remember(stories, searchQuery, selectedCategory, selectedFilter) {
        stories.filter { story ->
            // Category check
            val catMatch = selectedCategory == "Tümü" || story.category.contains(selectedCategory, ignoreCase = true)
            // Search query check
            val searchMatch = searchQuery.isBlank() || story.title.contains(searchQuery, ignoreCase = true) || story.summary.contains(searchQuery, ignoreCase = true)
            
            // Filter mode check
            val filterMatch = when (selectedFilter) {
                "Doğrulananlar" -> story.status == VerificationStatus.VERIFIED || story.status == VerificationStatus.OFFICIAL_STATEMENT
                "En Önemli" -> story.importance == ImportanceLevel.CRITICAL || story.importance == ImportanceLevel.HIGH
                "Sadece takip ettiklerim" -> {
                    val followedNames = followedSourceIds.filter { it.isFollowing }.map { it.name.lowercase() }.toSet()
                    followedNames.any { story.sourceName.lowercase().contains(it) || it.contains(story.sourceName.lowercase()) }
                }
                else -> true // En Yeni is default
            }

            catMatch && searchMatch && filterMatch
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Top welcome bar (Geometric Balance Header Layout)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: GündemAI Logo
                GundemAILogo(
                    iconSize = 34.dp,
                    innerSize = 11.dp,
                    textStyle = MaterialTheme.typography.titleLarge
                )

                // Right: Rounded Actions and Bordered Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // News Refresh Button
                    IconButton(
                        onClick = { viewModel.refreshNews() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Yenile",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Theme Toggle with slate/surface background
                    IconButton(
                        onClick = { viewModel.setTheme(!preferences.isDarkTheme) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = if (preferences.isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Tema Değiştir",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // AI Chat Button
                    IconButton(
                        onClick = onChatToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .testTag("ai_assistant_shortcut")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "GündemAI'ye Sor",
                            tint = DarkSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Bordered User Avatar
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF3B82F6).copy(alpha = 0.4f), CircleShape)
                            .background(BrandGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preferences.name.take(1).uppercase(),
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Spacer replacement in list spacing
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 2. Quick Recap Card (Geometric Balance Theme Layout)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(CardGradient)
                    .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.15f), RoundedCornerShape(28.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SON ZİYARETİNDEN BERİ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextSecondary,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val countText = if (filteredStories.size > 0) "${filteredStories.size} Yeni " else "Yepyeni "
                                Text(
                                    text = countText,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Gelişme",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF60A5FA)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Anlık Yapay Zekâ Analiz Desteği",
                                fontSize = 13.sp,
                                color = DarkTextSecondary
                            )
                        }
                        
                        Button(
                            onClick = onSummaryToggle,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("2_minute_summary_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "2 Dk Özetle", 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Spacer replacement in list spacing
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 3. Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Gündemde ara...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )
        }

        // 4. Horizontal Category List (Geometric Balance Pills)
        item {
            val categories = listOf("Tümü", "Yapay Zekâ", "Teknoloji", "Transfer", "Ekonomi")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF2563EB)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .clickable { viewModel.setSelectedCategory(cat) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("cat_chip_$cat")
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else DarkTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 5. Sorting/filtering chip row (Geometric Balance Badges)
        item {
            val filters = listOf("En Yeni", "En Önemli", "Doğrulananlar", "Sadece takip ettiklerim")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filt ->
                    val isSelected = selectedFilter == filt
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) Color(0xFF2563EB).copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF2563EB) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setSelectedFilter(filt) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("filter_chip_$filt")
                    ) {
                        Text(
                            text = filt,
                            color = if (isSelected) Color(0xFF60A5FA) else DarkTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Story lists / Empty State Views
        if (filteredStories.isEmpty()) {
            item {
                EmptyStateView(
                    title = "Aradığınız kriterde gelişme bulunamadı",
                    description = "Filtreleri sıfırlayarak veya yeni anahtar kelimeler arayarak başka gelişmeleri inceleyebilirsiniz.",
                    icon = Icons.Filled.Search,
                    buttonText = "Aramayı Temizle",
                    onButtonClick = { viewModel.setSearchQuery("") }
                )
            }
        } else {
            items(filteredStories, key = { it.id }) { story ->
                StoryCard(
                    story = story,
                    onStoryClick = { onStoryClick(story.id) },
                    onSaveToggle = { viewModel.toggleSaveStory(story.id) }
                )
            }
        }
    }
}

@Composable
fun StoryCard(
    story: Story,
    onStoryClick: () -> Unit,
    onSaveToggle: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStoryClick() }
            .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.15f), RoundedCornerShape(28.dp))
            .testTag("story_card_${story.id}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header Image with gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = story.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Gradient overlay representing Geometric Balance transitions
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xFF111A2E).copy(alpha = 0.85f))
                            )
                        )
                )

                // Tags in Image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        StatusBadge(status = story.status)
                        Spacer(modifier = Modifier.width(4.dp))
                        ImportanceBadge(level = story.importance)
                    }
                }

                // Title in Image Bottom
                Text(
                    text = story.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Short Summary Text inside Block Quote Style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .drawBehind {
                            val strokeWidth = 4.dp.toPx()
                            drawLine(
                                color = Color(0xFF3B82F6),
                                start = androidx.compose.ui.geometry.Offset(strokeWidth / 2, 0f),
                                end = androidx.compose.ui.geometry.Offset(strokeWidth / 2, size.height),
                                strokeWidth = strokeWidth
                            )
                        }
                        .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 12.dp)
                ) {
                    Text(
                        text = story.summary,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metadata Footer (Overlapping platform badges and clean actions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Overlapping Source Badges
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(width = 46.dp, height = 24.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color(0xFF111A2E), CircleShape)
                                    .background(Color(0xFF3B82F6))
                            ) {
                                Text("X", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .offset(x = 11.dp)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color(0xFF111A2E), CircleShape)
                                    .background(Color(0xFF8B5CF6))
                            ) {
                                Text("B", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .offset(x = 22.dp)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color(0xFF111A2E), CircleShape)
                                    .background(Color(0xFF475569))
                            ) {
                                Text("YT", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+${story.sourcesCount} Kaynak",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextSecondary
                        )
                    }

                    // Right: Actions with Bordered Detail Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onSaveToggle,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("save_button_${story.id}")
                        ) {
                            Icon(
                                imageVector = if (story.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Kaydet",
                                tint = if (story.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val shareIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, "${story.title}\n${story.originalUrl}\n\nGündemAI aracılığıyla paylaşıldı.")
                                    type = "text/plain"
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Haberi Paylaş"))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Paylaş",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        TextButton(
                            onClick = onStoryClick,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .height(28.dp)
                        ) {
                            Text(
                                "Detayları Gör",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF60A5FA)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KesfetTab(viewModel: MainViewModel) {
    val packages by viewModel.packages.collectAsState()
    val sources by viewModel.sources.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Gündemi Keşfet 🌟",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Yapay zeka asistanımızın derlediği hazır paketler ve güvenilir uzman kaynaklar.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        SectionHeader(title = "Popüler Gündem Paketleri", icon = Icons.Filled.AutoAwesome)

        packages.forEach { pkg ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("package_card_${pkg.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = pkg.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Button(
                            onClick = { viewModel.toggleFollowPackage(pkg.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (pkg.isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                contentColor = if (pkg.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("follow_pkg_button_${pkg.id}")
                        ) {
                            Text(text = if (pkg.isFollowing) "Takibi Bırak" else "Takip Et", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = pkg.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "• ${pkg.sourcesCount} Kaynak", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(text = "• Günde ~${pkg.dailyVolume} gelişme", fontSize = 11.sp, color = PremiumGold, fontWeight = FontWeight.Bold)
                        Text(text = "• ${pkg.followersCount / 1000}K Takipçi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(title = "Önerilen Uzman ve Kurumlar", icon = Icons.Filled.Group)

        sources.forEach { src ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("source_card_${src.id}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        AsyncImage(
                            model = src.avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = src.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                PlatformIcon(platform = src.platform)
                            }
                            Text(text = src.username, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = src.fieldOfExpertise, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Text(text = src.reliabilityLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkTertiary)
                        }
                    }

                    Button(
                        onClick = { viewModel.toggleFollowSource(src.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (src.isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                            contentColor = if (src.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("follow_src_button_${src.id}")
                    ) {
                        Text(text = if (src.isFollowing) "Takipte" else "Takip Et", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SavedTab(viewModel: MainViewModel, onStoryClick: (String) -> Unit) {
    val stories by viewModel.stories.collectAsState()
    val savedStories = remember(stories) { stories.filter { it.isSaved } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Kaydedilenler 🔖",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Daha sonra incelemek üzere arşivlediğiniz gelişmeler.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (savedStories.isEmpty()) {
            EmptyStateView(
                title = "Henüz kaydedilmiş gelişme yok",
                description = "Ana akıştaki gelişmelerin yanındaki kaydet ikonuna dokunarak favorilerinizi buraya ekleyebilirsiniz.",
                icon = Icons.Filled.Bookmark,
                buttonText = "Gündeme Göz At",
                onButtonClick = { /* Already handled by bottom navigation click */ }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(savedStories, key = { it.id }) { story ->
                    StoryCard(
                        story = story,
                        onStoryClick = { onStoryClick(story.id) },
                        onSaveToggle = { viewModel.toggleSaveStory(story.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationsTab(viewModel: MainViewModel, onStoryClick: (String) -> Unit) {
    val notifications by viewModel.notifications.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bildirim Merkezi 🔔",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Son büyük sızıntılar ve yapay zeka analizleri.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (notifications.any { !it.isRead }) {
                TextButton(
                    onClick = { viewModel.markAllNotificationsAsRead() },
                    modifier = Modifier.testTag("mark_all_read_button")
                ) {
                    Text("Tümünü Oku", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (notifications.isEmpty()) {
            EmptyStateView(
                title = "Bildirim kutunuz boş",
                description = "Herhangi bir sızıntı veya son dakika gelişmesi yaşandığında bildirimleriniz buraya düşecektir.",
                icon = Icons.Filled.NotificationsActive
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (!notif.isRead) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.markNotificationAsRead(notif.id)
                                if (notif.actionUrl != null) {
                                    onStoryClick(notif.actionUrl)
                                } else {
                                    Toast
                                        .makeText(context, notif.body, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            .testTag("notification_card_${notif.id}")
                    ) {
                        Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Unread indicator dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (notif.isRead) Color.Transparent else MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = notif.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (notif.isRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.body,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(notif.timestamp)),
                                        fontSize = 10.sp,
                                        color = SoftGray
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTab(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val preferences by viewModel.preferences.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Profil & Ayarlar ⚙️",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Profile details card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preferences.name.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = preferences.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Premium Gündem Üyesi", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(title = "Tercih Ayarları", icon = Icons.Filled.Settings)

        // Interests Summary
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Takip Ettiğiniz Alanlar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    preferences.selectedInterests.forEach { interest ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = interest, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // News Volume Summary
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Gündem Yoğunluğu", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Haber filtreleme katsayısı", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PremiumGold.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = preferences.volumeLevel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PremiumGold)
                }
            }
        }

        // App Theme Toggle Row
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Koyu Tema", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Arayüz parlaklığı", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = preferences.isDarkTheme,
                    onCheckedChange = { viewModel.setTheme(it) },
                    modifier = Modifier.testTag("theme_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Uygulama Bilgileri", icon = Icons.Filled.Info)

        Text(text = "GündemAI v1.0.0 (Demo Sürüm)", fontSize = 11.sp, color = SoftGray)
        Text(text = "Bu uygulama AI Studio platformu için özel olarak Jetpack Compose, Room ve Gemini API kullanılarak senior standartlarda kodlanmıştır.", fontSize = 11.sp, color = SoftGray, lineHeight = 16.sp, modifier = Modifier.padding(top = 4.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // Reset & Logout Button
        Button(
            onClick = {
                viewModel.resetOnboarding()
                onLogout()
                Toast.makeText(context, "Veriler sıfırlandı ve çıkış yapıldı.", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("reset_app_button")
        ) {
            Text("Verileri Sıfırla & Baştan Başla", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
