package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ChatSession
import com.example.ui.theme.*
import com.example.ui.viewmodel.AssistantViewModel

@Composable
fun DashboardScreen(viewModel: AssistantViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var showSessionDialog by remember { mutableStateOf(false) }

    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val currentSessionId by viewModel.currentSessionId.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_scaffold"),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("dashboard_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { 
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send, 
                            contentDescription = "Chat", 
                            tint = if (selectedTab == 0) MaterialTheme.colorScheme.primary else NatSubText
                        ) 
                    },
                    label = { 
                        Text(
                            "Agent Chat", 
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else NatSubText, 
                            fontWeight = FontWeight.SemiBold
                        ) 
                    },
                    modifier = Modifier.testTag("nav_tab_chat"),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Person, 
                            contentDescription = "Asif Profile", 
                            tint = if (selectedTab == 1) MaterialTheme.colorScheme.primary else NatSubText
                        ) 
                    },
                    label = { 
                        Text(
                            "About Asif", 
                            color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else NatSubText, 
                            fontWeight = FontWeight.SemiBold
                        ) 
                    },
                    modifier = Modifier.testTag("nav_tab_profile"),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showSessionDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = PureWhite,
                    modifier = Modifier.testTag("fab_session_manager")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Session History")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> ChatScreen(viewModel = viewModel)
                1 -> ProfileScreen()
            }
        }

        // Multiple Sessions Manager Dialog
        if (showSessionDialog) {
            SessionManagerDialog(
                sessions = sessions,
                currentSessionId = currentSessionId,
                onSelectSession = { sessionId ->
                    viewModel.selectSession(sessionId)
                    showSessionDialog = false
                },
                onAddNewSession = { title ->
                    viewModel.createNewSession(title)
                },
                onDeleteSession = { session ->
                    viewModel.deleteSession(session)
                },
                onDismiss = { showSessionDialog = false }
            )
        }
    }
}

@Composable
fun SessionManagerDialog(
    sessions: List<ChatSession>,
    currentSessionId: String?,
    onSelectSession: (String) -> Unit,
    onAddNewSession: (String) -> Unit,
    onDeleteSession: (ChatSession) -> Unit,
    onDismiss: () -> Unit
) {
    var newSessionTitle by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("session_manager_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "🌿 Chat Sessions Library",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Create Session Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newSessionTitle,
                        onValueChange = { newSessionTitle = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_session_input"),
                        placeholder = { Text("Session Name (e.g. ⚽ Football)...", fontSize = 12.sp, color = NatSubText) },
                        maxLines = 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newSessionTitle.isNotBlank()) {
                                onAddNewSession(newSessionTitle)
                                newSessionTitle = ""
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("new_session_add_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = PureWhite)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Active Conversations",
                    fontSize = 13.sp,
                    color = NatSubText,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.heightIn(max = 240.dp)) {
                    if (sessions.isEmpty()) {
                        Text(
                            text = "No sessions found",
                            fontSize = 13.sp,
                            color = NatSubText,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        // Display list of sessions
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(sessions) { session ->
                                val isActive = session.id == currentSessionId
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                                        .clickable { onSelectSession(session.id) }
                                        .padding(horizontal = 12.dp, vertical = 9.dp)
                                        .testTag("session_item_${session.id}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = session.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (sessions.size > 1) {
                                        IconButton(
                                            onClick = { onDeleteSession(session) },
                                            modifier = Modifier
                                                .size(24.dp)
                                                .testTag("delete_session_${session.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Session",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
