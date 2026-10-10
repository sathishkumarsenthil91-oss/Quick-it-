package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

// ----------------------------------------------------
// 1. AI Prompt Library Screen
// ----------------------------------------------------
@Composable
fun PromptsScreen(
    prompts: List<PromptItem>,
    onBack: () -> Unit,
    onAddPrompt: (String, String, String) -> Unit,
    onCopyId: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var promptBody by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Productivity") }
    val clipboard = LocalClipboardManager.current

    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SubScreenHeader("AI Prompt Library", "Pre-tuned neural prompts & chains", onBack) {
                ClayButton(onClick = { showDialog = true }, gradientColors = listOf(RoyalPurple, DeepViolet)) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(prompts) { prm ->
                    ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), elevation = 6.dp) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    HubbyAiMascot(size = 32.dp, animateEyes = true)
                                    Column {
                                        Text(prm.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepViolet)
                                        Text("${prm.category} • ${prm.model}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                                UniqueIdChip(displayId = prm.displayId, onCopied = onCopyId)
                            }

                            // Prompt Code Preview Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PearlWhite)
                                    .border(1.dp, GlassBorderPurple, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = prm.promptText,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextPrimary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                ClayButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(prm.promptText))
                                        onCopyId("Copied prompt text")
                                    },
                                    gradientColors = listOf(PastelPurple, RoyalPurple),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Copy Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Save AI Prompt", fontWeight = FontWeight.Bold, color = DeepViolet) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                    OutlinedTextField(value = promptBody, onValueChange = { promptBody = it }, label = { Text("Prompt Instructions") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), minLines = 3)
                }
            },
            confirmButton = {
                ClayButton(onClick = {
                    if (title.isNotBlank()) {
                        onAddPrompt(title, promptBody, category)
                        showDialog = false
                    }
                }) { Text("Save", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel", color = TextSecondary) } },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// ----------------------------------------------------
// 2. Personal Notes Screen
// ----------------------------------------------------
@Composable
fun NotesScreen(
    notes: List<NoteItem>,
    onBack: () -> Unit,
    onAddNote: (String, String, List<String>, String) -> Unit,
    onCopyId: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SubScreenHeader("Personal Notes", "Rich-text thoughts & attachments", onBack) {
                ClayButton(onClick = { showDialog = true }, gradientColors = listOf(RoyalPurple, DeepViolet)) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Note", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(notes) { note ->
                    ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), elevation = 6.dp) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(note.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepViolet)
                                UniqueIdChip(displayId = note.displayId, onCopied = onCopyId)
                            }
                            Text(note.content, fontSize = 13.sp, lineHeight = 19.sp, color = TextPrimary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(note.updatedAt, fontSize = 11.sp, color = TextMuted)
                                Surface(shape = RoundedCornerShape(8.dp), color = SoftLavender) {
                                    Text(note.folderName, fontSize = 10.sp, color = RoyalPurple, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Note", fontWeight = FontWeight.Bold, color = DeepViolet) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), minLines = 4)
                }
            },
            confirmButton = {
                ClayButton(onClick = {
                    if (title.isNotBlank()) {
                        onAddNote(title, content, listOf("Vault", "Idea"), "General")
                        showDialog = false
                    }
                }) { Text("Save Note", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel", color = TextSecondary) } },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// ----------------------------------------------------
// 3. Daily Planner Screen
// ----------------------------------------------------
@Composable
fun PlannerScreen(
    tasks: List<TaskItem>,
    onBack: () -> Unit,
    onToggleTask: (String) -> Unit,
    onAddTask: (String, String, TaskPriority, String) -> Unit,
    onCopyId: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }

    val completedCount = tasks.count { it.isCompleted }
    val progress = if (tasks.isNotEmpty()) completedCount.toFloat() / tasks.size else 0f

    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SubScreenHeader("Daily Planner", "Reminders & focus calendar", onBack) {
                ClayButton(onClick = { showDialog = true }, gradientColors = listOf(RoyalPurple, DeepViolet)) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Task", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Progress Banner
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = 4.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Daily Completion", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepViolet)
                            Text("$completedCount / ${tasks.size} done", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = RoyalPurple,
                            trackColor = SoftLavender
                        )
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks) { task ->
                    ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = 3.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { onToggleTask(task.id) },
                                colors = CheckboxDefaults.colors(checkedColor = RoyalPurple)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (task.isCompleted) TextMuted else TextPrimary
                                )
                                Text("Due: ${task.dueTime}", fontSize = 11.sp, color = TextSecondary)
                            }
                            UniqueIdChip(displayId = task.displayId, onCopied = onCopyId)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Schedule Task", fontWeight = FontWeight.Bold, color = DeepViolet) },
            text = {
                OutlinedTextField(value = taskTitle, onValueChange = { taskTitle = it }, label = { Text("Task Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            },
            confirmButton = {
                ClayButton(onClick = {
                    if (taskTitle.isNotBlank()) {
                        onAddTask(taskTitle, "Today, 5:00 PM", TaskPriority.HIGH, "General")
                        showDialog = false
                    }
                }) { Text("Add Task", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel", color = TextSecondary) } },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// ----------------------------------------------------
// 4. Coding Projects Screen
// ----------------------------------------------------
@Composable
fun CodingProjectsScreen(
    projects: List<CodingProject>,
    onBack: () -> Unit,
    onCopyId: (String) -> Unit
) {
    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SubScreenHeader("Coding Projects", "GitHub links, code & docs", onBack)

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(projects) { prj ->
                    ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), elevation = 6.dp) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(prj.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepViolet)
                                UniqueIdChip(displayId = prj.displayId, onCopied = onCopyId)
                            }
                            Text(prj.description, fontSize = 13.sp, color = TextPrimary)
                            Text("Repo: ${prj.repoUrl}", fontSize = 11.sp, color = RoyalPurple, fontWeight = FontWeight.Medium)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                prj.languages.forEach { lang ->
                                    Surface(shape = RoundedCornerShape(8.dp), color = SoftLavender) {
                                        Text(lang, fontSize = 10.sp, color = DeepViolet, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. Learning Hub Screen
// ----------------------------------------------------
@Composable
fun LearningHubScreen(
    subjects: List<LearningSubject>,
    onBack: () -> Unit,
    onCopyId: (String) -> Unit
) {
    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SubScreenHeader("Learning Hub", "Interactive subjects & AI progress", onBack) {
                HubbyAiMascot(size = 32.dp, animateEyes = true)
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(subjects) { sub ->
                    ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), elevation = 6.dp) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(sub.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepViolet)
                                UniqueIdChip(displayId = sub.displayId, onCopied = onCopyId)
                            }
                            Text("Topic: ${sub.currentTopic}", fontSize = 12.sp, color = TextSecondary)
                            LinearProgressIndicator(
                                progress = { sub.progress },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = SpeedCyan,
                                trackColor = SoftLavender
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${sub.completedModules}/${sub.totalModules} Modules", fontSize = 11.sp, color = TextMuted)
                                Text("${(sub.progress * 100).toInt()}% Mastery", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepViolet)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. Profile & Settings Screen
// ----------------------------------------------------
@Composable
fun ProfileScreen(
    user: UserProfile,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onCopyId: (String) -> Unit
) {
    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SubScreenHeader("Personal Profile", "Vault security & account settings", onBack)

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), elevation = 8.dp) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(SpeedCyan, RoyalPurple, DeepViolet))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(user.avatarInitials, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }

                            Text(user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepViolet)
                            Text(user.email, fontSize = 13.sp, color = TextSecondary)

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                UniqueIdChip(displayId = user.displayId, onCopied = onCopyId)
                                Surface(shape = RoundedCornerShape(10.dp), color = SoftLavender) {
                                    Text(user.plan, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalPurple, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), elevation = 4.dp) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Cloud Storage & Synchronization", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepViolet)
                            Text("• Allocated: 100 GB Cloud Storage", fontSize = 12.sp, color = TextPrimary)
                            Text("• Supabase Database: Connected (RLS Enabled)", fontSize = 12.sp, color = TextPrimary)
                            Text("• Google OAuth: Authenticated & Synced", fontSize = 12.sp, color = TextPrimary)
                            Text("• Cross-Platform: Web (Next.js) + Android (Capacitor/Native)", fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }

                item {
                    ClayCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), elevation = 4.dp) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Vault Security & Sync Status", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepViolet)
                                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF10B981).copy(alpha = 0.15f)) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                        Text("Always Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                    }
                                }
                            }
                            Text("Your personal hub vault is permanently active on this device with zero login friction. All files, chats, prompts, and notes remain encrypted and isolated under your unique User ID.", fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubScreenHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.85f))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepViolet)
            }
            Column {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepViolet)
                Text(subtitle, fontSize = 11.sp, color = TextSecondary)
            }
        }
        trailing?.invoke()
    }
}
