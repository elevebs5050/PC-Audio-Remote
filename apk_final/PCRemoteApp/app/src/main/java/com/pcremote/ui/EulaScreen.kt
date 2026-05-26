package com.pcremote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EulaScreen(onAgree: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    Icons.Rounded.Gavel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "License Agreement",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ElevatedCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = """
                                END USER LICENSE AGREEMENT
                                
                                Last updated: May 2026
                                
                                PLEASE READ THIS AGREEMENT CAREFULLY BEFORE INSTALLING OR USING THIS SOFTWARE.
                                
                                1. DISCLAIMER OF WARRANTY
                                   THIS SOFTWARE IS PROVIDED "AS IS" AND "AS AVAILABLE", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
                                
                                2. LIMITATION OF LIABILITY
                                   IN NO EVENT SHALL THE AUTHOR (SEBAI MOHAMED SAFA) BE LIABLE FOR ANY CLAIM, DAMAGES, OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
                                
                                3. "VIBE CODED" NOTICE
                                   This software was created through a combination of developer guidance and AI-assisted code generation ("vibe coding"). While efforts have been made to ensure reasonable quality and security, this software may contain bugs, incomplete features, or unexpected behavior. Use at your own risk.
                                
                                4. ACCEPTANCE OF RISK
                                   By installing and using this software, you acknowledge that:
                                   - The author is NOT responsible for any damage to your system, data loss, or security breaches resulting from the use of this software.
                                   - This software allows remote control of your computer over a network. You are solely responsible for securing your network and ensuring that only authorized users have access.
                                   - TLS encryption is provided as a security measure but does not guarantee absolute protection against determined attackers.
                                
                                5. LICENSE
                                   You are granted a non-exclusive, non-transferable license to use this software for personal, non-commercial purposes. You may not redistribute, sell, or sublicense this software without prior written consent from the author.
                                
                                6. OPEN SOURCE
                                   The source code for this project is available for review. You are encouraged to examine it and compile it yourself if you have concerns about security or functionality.
                                
                                7. GOVERNING LAW
                                   This agreement shall be governed by the laws applicable in the jurisdiction of the author.
                                
                                By clicking "I Agree", you acknowledge that you have read, understood, and agree to be bound by the terms of this agreement.
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAgree,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonColors().let { ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp) }
            ) {
                Text("I AGREE & CONTINUE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
