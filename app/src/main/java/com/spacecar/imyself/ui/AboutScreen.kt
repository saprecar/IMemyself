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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

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
