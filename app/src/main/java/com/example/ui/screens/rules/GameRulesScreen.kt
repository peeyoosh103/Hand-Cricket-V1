package com.example.ui.screens.rules

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StadiumBackground
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel
import java.util.Locale

enum class AudioSection {
    NONE,
    BATTING,
    BOWLING,
    ALL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRulesScreen(
    viewModel: GameViewModel
) {
    val context = LocalContext.current

    // Exact rules text as specified in requirements
    val battingRulesText = "Batting me aap 1 se 6 tak koi number choose karte hain. CPU bowler bhi ek number choose karta hai. Dono numbers different hone par aapko utne runs milte hain. Same number hone par aap out ho jaate hain. Target chase karke ya higher score banakar match jeet sakte hain."
    val bowlingRulesText = "Bowling me aap 1 se 6 tak koi number choose karte hain. CPU batsman bhi ek number choose karta hai. Dono numbers same hone par batsman out ho jaata hai. Numbers different hone par batsman ke chosen number ke runs score me add hote hain. Aapka goal wickets lekar runs rokna hai."

    // Voice Explanation State
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    var activeAudioSection by remember { mutableStateOf(AudioSection.NONE) }
    var isPlaying by remember { mutableStateOf(false) }

    // Initialize Android TextToSpeech for Voice Explanation
    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale("hi", "IN"))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale("hi"))
                    }
                    tts?.setPitch(1.05f)
                    tts?.setSpeechRate(0.95f)
                    isTtsReady = true
                }
            }

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isPlaying = true
                }

                override fun onDone(utteranceId: String?) {
                    isPlaying = false
                    activeAudioSection = AudioSection.NONE
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    isPlaying = false
                    activeAudioSection = AudioSection.NONE
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    isPlaying = false
                    activeAudioSection = AudioSection.NONE
                }
            })

            ttsEngine = tts
        } catch (e: Exception) {
            isTtsReady = false
        }

        onDispose {
            try {
                tts?.stop()
                tts?.shutdown()
            } catch (e: Exception) {
                // Ignore cleanup error
            }
        }
    }

    fun playAudio(section: AudioSection) {
        val engine = ttsEngine ?: return
        if (!isTtsReady) return

        try {
            engine.stop()
            activeAudioSection = section
            isPlaying = true

            val spokenText = when (section) {
                AudioSection.BATTING -> "बल्लेबाज़ी के नियम: $battingRulesText"
                AudioSection.BOWLING -> "गेंदबाज़ी के नियम: $bowlingRulesText"
                AudioSection.ALL -> "हैंड क्रिकेट के नियम। बल्लेबाज़ी: $battingRulesText गेंदबाज़ी: $bowlingRulesText"
                AudioSection.NONE -> ""
            }

            if (spokenText.isNotEmpty()) {
                engine.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null, "rules_utterance_${System.currentTimeMillis()}")
            }
        } catch (e: Exception) {
            isPlaying = false
            activeAudioSection = AudioSection.NONE
        }
    }

    fun stopAudio() {
        try {
            ttsEngine?.stop()
        } catch (e: Exception) {
            // Ignore
        }
        isPlaying = false
        activeAudioSection = AudioSection.NONE
    }

    // Android System Back Navigation
    BackHandler {
        stopAudio()
        viewModel.navigateBack()
    }

    val pulseTransition = rememberInfiniteTransition(label = "audio_pulse")
    val audioPulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "audio_scale"
    )

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "GAME RULES",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = CricketGold
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CricketGold.copy(alpha = 0.2f),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(listOf(CricketGold, CricketGoldLight)),
                                    width = 1.dp
                                )
                            ) {
                                Text(
                                    text = "OFFICIAL GUIDE",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 8.sp,
                                        color = CricketGoldLight
                                    )
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                stopAudio()
                                viewModel.navigateBack()
                            },
                            modifier = Modifier.testTag("rules_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    actions = {
                        // Quick audio play/stop toggle in top bar
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    stopAudio()
                                } else {
                                    playAudio(AudioSection.ALL)
                                }
                            },
                            modifier = Modifier.testTag("rules_audio_header_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isPlaying) "Stop Audio" else "Play All Audio Rules",
                                tint = if (isPlaying) CricketWicketRed else CricketPitchLightGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Hero Info Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rules_hero_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(CricketPitchGreen, CricketRoyalBlue)),
                        width = 1.5.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CricketGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📖", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    text = "Hand Cricket Rulebook",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Complete batting & bowling rules with voice narration",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Audio Status & Master Control Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = ScoreboardBg,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    if (isPlaying) listOf(CricketPitchLightGreen, CricketGold) else listOf(StadiumCardBorder, StadiumCardBorder)
                                ),
                                width = 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (isPlaying) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .scale(audioPulseScale)
                                                .clip(CircleShape)
                                                .background(CricketPitchLightGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = if (isPlaying) {
                                                when (activeAudioSection) {
                                                    AudioSection.BATTING -> "Playing Batting Rules Voice..."
                                                    AudioSection.BOWLING -> "Playing Bowling Rules Voice..."
                                                    AudioSection.ALL -> "Playing All Rules Explanation..."
                                                    AudioSection.NONE -> "Voice Playing..."
                                                }
                                            } else "Voice Explanation (Hindi TTS)",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPlaying) CricketPitchLightGreen else TextPrimary
                                            )
                                        )
                                        Text(
                                            text = if (isPlaying) "Tap Stop or Pause to control" else "Listen to complete rules in Hindi voice",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (isPlaying) {
                                        Button(
                                            onClick = { stopAudio() },
                                            colors = ButtonDefaults.buttonColors(containerColor = CricketWicketRed),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("rules_stop_audio_button")
                                        ) {
                                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Stop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { playAudio(AudioSection.ALL) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("rules_play_all_audio_button")
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Play All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 1: BATTING RULES (50 Words)
                // ==========================================
                val isBattingPlaying = isPlaying && (activeAudioSection == AudioSection.BATTING || activeAudioSection == AudioSection.ALL)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rules_batting_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBattingPlaying) StadiumCardNavy.copy(alpha = 0.95f) else StadiumCardNavy
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            if (isBattingPlaying) listOf(CricketPitchLightGreen, CricketGold)
                            else listOf(CricketPitchGreen, CricketPitchGreen.copy(alpha = 0.3f))
                        ),
                        width = if (isBattingPlaying) 2.dp else 1.2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Header Row with Audio Play Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(listOf(CricketPitchGreen, Color(0xFF047857)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏏", fontSize = 22.sp)
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Batting",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp,
                                                color = Color.White
                                            )
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = CricketPitchGreen.copy(alpha = 0.2f),
                                            border = CardDefaults.outlinedCardBorder().copy(
                                                brush = Brush.linearGradient(listOf(CricketPitchLightGreen, CricketPitchGreen)),
                                                width = 1.dp
                                            )
                                        ) {
                                            Text(
                                                text = "50 WORDS",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 8.sp,
                                                    color = CricketPitchLightGreen
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Scoring runs & avoiding dismissal",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Individual Audio Control Button
                            FilledTonalIconButton(
                                onClick = {
                                    if (activeAudioSection == AudioSection.BATTING && isPlaying) {
                                        stopAudio()
                                    } else {
                                        playAudio(AudioSection.BATTING)
                                    }
                                },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = if (isBattingPlaying) CricketPitchLightGreen else ScoreboardBg,
                                    contentColor = if (isBattingPlaying) Color.Black else TextPrimary
                                ),
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("rules_play_batting_audio_button")
                            ) {
                                Icon(
                                    imageVector = if (isBattingPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Listen to Batting Rules",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Exact 50-Word Batting Rules Text
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = ScoreboardBg.copy(alpha = 0.7f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    listOf(StadiumCardBorder, StadiumCardBorder.copy(alpha = 0.5f))
                                ),
                                width = 1.dp
                            )
                        ) {
                            Text(
                                text = "“$battingRulesText”",
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.5.sp
                                )
                            )
                        }

                        // Quick Highlight Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RuleChip(
                                title = "Different Numbers",
                                subtitle = "= Runs Scored",
                                icon = "➕",
                                color = CricketPitchLightGreen,
                                modifier = Modifier.weight(1f)
                            )
                            RuleChip(
                                title = "Same Number",
                                subtitle = "= Wicket (OUT)",
                                icon = "🚨",
                                color = CricketWicketRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ==========================================
                // SECTION 2: BOWLING RULES (50 Words)
                // ==========================================
                val isBowlingPlaying = isPlaying && (activeAudioSection == AudioSection.BOWLING || activeAudioSection == AudioSection.ALL)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rules_bowling_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBowlingPlaying) StadiumCardNavy.copy(alpha = 0.95f) else StadiumCardNavy
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            if (isBowlingPlaying) listOf(CricketGold, CricketPitchLightGreen)
                            else listOf(Color(0xFF4338CA), CricketRoyalBlue)
                        ),
                        width = if (isBowlingPlaying) 2.dp else 1.2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Header Row with Audio Play Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(listOf(Color(0xFF4338CA), CricketRoyalBlue))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎯", fontSize = 22.sp)
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Bowling",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp,
                                                color = Color.White
                                            )
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF93C5FD).copy(alpha = 0.2f),
                                            border = CardDefaults.outlinedCardBorder().copy(
                                                brush = Brush.linearGradient(listOf(Color(0xFF93C5FD), CricketRoyalBlue)),
                                                width = 1.dp
                                            )
                                        ) {
                                            Text(
                                                text = "50 WORDS",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 8.sp,
                                                    color = Color(0xFF93C5FD)
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Taking wickets & defending target",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Individual Audio Control Button
                            FilledTonalIconButton(
                                onClick = {
                                    if (activeAudioSection == AudioSection.BOWLING && isPlaying) {
                                        stopAudio()
                                    } else {
                                        playAudio(AudioSection.BOWLING)
                                    }
                                },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = if (isBowlingPlaying) CricketGold else ScoreboardBg,
                                    contentColor = if (isBowlingPlaying) Color.Black else TextPrimary
                                ),
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("rules_play_bowling_audio_button")
                            ) {
                                Icon(
                                    imageVector = if (isBowlingPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Listen to Bowling Rules",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Exact 50-Word Bowling Rules Text
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = ScoreboardBg.copy(alpha = 0.7f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    listOf(StadiumCardBorder, StadiumCardBorder.copy(alpha = 0.5f))
                                ),
                                width = 1.dp
                            )
                        ) {
                            Text(
                                text = "“$bowlingRulesText”",
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.5.sp
                                )
                            )
                        }

                        // Quick Highlight Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RuleChip(
                                title = "Same Number",
                                subtitle = "= Wicket Taken",
                                icon = "🎯",
                                color = CricketGold,
                                modifier = Modifier.weight(1f)
                            )
                            RuleChip(
                                title = "Different Numbers",
                                subtitle = "= CPU Runs Added",
                                icon = "🏏",
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RuleChip(
    title: String,
    subtitle: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = StadiumDeepNavy,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(StadiumCardBorder, color.copy(alpha = 0.4f))),
            width = 1.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = icon, fontSize = 16.sp)
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}
