package com.spacecar.imyself.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisclaimerScreen(onAccept: () -> Unit) {
    var isChecked by remember { mutableStateOf(false) }
    var isPolicyChecked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val openLink = { url: String ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚠️ Critical Disclaimer & Agreement",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "This application is strictly an experimental tracking utility and is not a medical, therapeutic, or professional habit-changing application.\n\n" +
                            "• No Medical Advice: The app does not provide medical advice, diagnosis, treatment, or clinical intervention.\n" +
                            "• No Liability: The developer accepts absolutely no liability or responsibility for any direct, indirect, physical, or psychological harm arising from the use of this application.\n" +
                            "• Personal Responsibility: By using this application, you acknowledge and agree that your recovery journey and any associated decisions are entirely at your own risk.\n\n" +
                            "• 18+ Age Requirement: You must be 18 years or older to use this app.\n" +
                            "• Not for EU: This app is not intended for users in the EU or GDPR-governed regions.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { openLink("https://github.com/saprecar/IMemyself/blob/main/PRIVACY_POLICY.md") }
                ) {
                    Text(
                        text = "📄 Read Full Privacy Policy",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { openLink("https://github.com/saprecar/IMemyself/blob/main/LICENSE.md") }
                ) {
                    Text(
                        text = "⚖️ Read Full License Agreement (EULA)",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPolicyChecked,
                        onCheckedChange = { isPolicyChecked = it }
                    )
                    Text(
                        text = "I have read and agree to the Privacy Policy and License Agreement.",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it }
                    )
                    Text(
                        text = "I acknowledge the cautions and accept all risks.",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onAccept,
                    enabled = isChecked && isPolicyChecked,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("I Agree, Proceed to App", fontSize = 16.sp)
                }
            }
    }
}
