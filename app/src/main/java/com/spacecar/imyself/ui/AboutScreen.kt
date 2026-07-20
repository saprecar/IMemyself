package com.spacecar.imyself.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.spacecar.imyself.data.BackupManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(viewModel: TrackingViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val backupManager = BackupManager(context, viewModel.repository)
                backupManager.exportToUri(uri)
                Toast.makeText(context, "Backup Exported Successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val backupManager = BackupManager(context, viewModel.repository)
                val success = backupManager.importFromUri(uri)
                if (success) {
                    Toast.makeText(context, "Backup Restored! Please restart app.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to restore backup.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val openLink = { url: String ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About & Legal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "IMemyself / Control habit",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "An experimental offline habit tracker designed for deep psychological tracking, patterns, and unyielding perseverance.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Legal & Policies", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            ListItem(
                headlineContent = { Text("Privacy Policy & Terms") },
                supportingContent = { Text("Read our full strict Privacy Policy") },
                modifier = Modifier.clickable { openLink("https://github.com/saprecar/IMemyself/blob/main/PRIVACY_POLICY.md") }
            )
            
            ListItem(
                headlineContent = { Text("License Agreement (EULA)") },
                supportingContent = { Text("View the custom proprietary License") },
                modifier = Modifier.clickable { openLink("https://github.com/saprecar/IMemyself/blob/main/LICENSE.md") }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Data & Backup (Offline)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            ListItem(
                headlineContent = { Text("Export Backup") },
                supportingContent = { Text("Save your data to a plain text JSON file") },
                modifier = Modifier.clickable { exportLauncher.launch("IMyself_Backup.json") }
            )

            ListItem(
                headlineContent = { Text("Import Backup") },
                supportingContent = { Text("Restore data from a JSON file (Overwrites current data)") },
                modifier = Modifier.clickable { importLauncher.launch(arrayOf("application/json")) }
            )
            
            Text(
                text = "✓ Auto-backup runs silently in the background every 24 hours.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Support & Contact", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            ListItem(
                headlineContent = { Text("Contact Developer") },
                supportingContent = { Text("getspacecar@gmail.com") },
                modifier = Modifier.clickable { 
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:getspacecar@gmail.com"))
                    context.startActivity(intent)
                }
            )

            ListItem(
                headlineContent = { Text("GitHub Repository") },
                supportingContent = { Text("View source code, report issues, or contact via GitHub") },
                modifier = Modifier.clickable { openLink("https://github.com/saprecar/IMemyself") }
            )

            Button(
                onClick = { openLink("https://buymeacoffee.com/spacecar") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDD00), contentColor = Color.Black)
            ) {
                Text("☕ Buy me a coffee", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Version 1.0 (Experimental)\nNot intended for EU/GDPR regions.\nRequires age 18+.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}
