package bharadwaj.juno.music.ui.component

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import bharadwaj.juno.music.R
import bharadwaj.juno.music.models.MediaMetadata
import bharadwaj.juno.music.utils.ComposeToImage
import bharadwaj.juno.music.utils.ShareTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ShareStoryDialog(
    mediaMetadata: MediaMetadata,
    lyricText: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var selectedTemplate by remember { mutableStateOf(ShareTemplate.MINIMAL) }
    var isGeneratingImage by remember { mutableStateOf(false) }

    // Palette extraction for Gradient/Glass previews
    var paletteColors by remember { mutableStateOf<Pair<Color, Color>?>(null) }
    LaunchedEffect(mediaMetadata.thumbnailUrl) {
        if (mediaMetadata.thumbnailUrl != null) {
            withContext(Dispatchers.IO) {
                try {
                    val loader = ImageLoader(context)
                    val req = ImageRequest.Builder(context)
                        .data(mediaMetadata.thumbnailUrl)
                        .allowHardware(false)
                        .build()
                    val result = loader.execute(req)
                    val bmp = result.image?.toBitmap()
                    if (bmp != null) {
                        val palette = Palette.from(bmp).generate()
                        val vibrant = palette.getVibrantColor(0xFF512DA8.toInt())
                        val darkVibrant = palette.getDarkVibrantColor(0xFF1A237E.toInt())
                        paletteColors = Pair(Color(vibrant), Color(darkVibrant))
                    }
                } catch (_: Exception) {}
            }
        }
    }

    val isInstagramInstalled = remember {
        try {
            context.packageManager.getPackageInfo("com.instagram.android", 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header row with Close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(if (lyricText.isNullOrEmpty()) R.string.share else R.string.share_lyrics),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = stringResource(R.string.close),
                            tint = Color.White
                        )
                    }
                }

                // 9:16 Aspect Ratio Preview Card
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val cardHeight = maxHeight
                    val cardWidth = cardHeight * (9f / 16f)
                    val finalWidth = if (cardWidth > maxWidth) maxWidth else cardWidth
                    val finalHeight = finalWidth * (16f / 9f)

                    Surface(
                        modifier = Modifier
                            .size(finalWidth, finalHeight)
                            .shadow(16.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF121212)
                    ) {
                        StoryCardPreview(
                            mediaMetadata = mediaMetadata,
                            lyricText = lyricText,
                            template = selectedTemplate,
                            paletteColors = paletteColors
                        )
                    }
                }

                // Template Chips Selector
                Text(
                    text = "Templates",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlign = TextAlign.Start
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShareTemplate.values().forEach { template ->
                        val isSelected = selectedTemplate == template
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTemplate = template },
                            label = {
                                Text(
                                    text = template.name.lowercase().replace('_', ' ')
                                        .replaceFirstChar { it.uppercase() }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = Color.White.copy(alpha = 0.08f),
                                labelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = Color.Transparent,
                                borderColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Row
                if (isGeneratingImage) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Instagram Option
                        ShareActionButton(
                            iconRes = R.drawable.ic_launcher_foreground,
                            label = if (isInstagramInstalled) "Instagram" else "Stories",
                            onClick = {
                                isGeneratingImage = true
                                coroutineScope.launch {
                                    try {
                                        val bitmap = ComposeToImage.createStoryImage(
                                            context,
                                            mediaMetadata.thumbnailUrl,
                                            mediaMetadata.title,
                                            mediaMetadata.artists.joinToString { it.name },
                                            mediaMetadata.album?.title,
                                            lyricText,
                                            selectedTemplate
                                        )
                                        val filename = "juno_story_${System.currentTimeMillis()}"
                                        val uri = ComposeToImage.saveBitmapAsFile(context, bitmap, filename)

                                        if (isInstagramInstalled) {
                                            val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                                                setDataAndType(uri, "image/png")
                                                setPackage("com.instagram.android")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(intent)
                                        } else {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "image/png"
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share Story"))
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isGeneratingImage = false
                                    }
                                }
                            }
                        )

                        // Share Sheet
                        ShareActionButton(
                            iconRes = R.drawable.share,
                            label = "Share",
                            onClick = {
                                isGeneratingImage = true
                                coroutineScope.launch {
                                    try {
                                        val bitmap = ComposeToImage.createStoryImage(
                                            context,
                                            mediaMetadata.thumbnailUrl,
                                            mediaMetadata.title,
                                            mediaMetadata.artists.joinToString { it.name },
                                            mediaMetadata.album?.title,
                                            lyricText,
                                            selectedTemplate
                                        )
                                        val filename = "juno_share_${System.currentTimeMillis()}"
                                        val uri = ComposeToImage.saveBitmapAsFile(context, bitmap, filename)

                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "image/png"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            putExtra(Intent.EXTRA_TEXT, "https://share.junomusic.fun/watch?v=${mediaMetadata.id}")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Story"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isGeneratingImage = false
                                    }
                                }
                            }
                        )

                        // Save to Gallery
                        ShareActionButton(
                            iconRes = R.drawable.download,
                            label = "Save",
                            onClick = {
                                isGeneratingImage = true
                                coroutineScope.launch {
                                    try {
                                        val bitmap = ComposeToImage.createStoryImage(
                                            context,
                                            mediaMetadata.thumbnailUrl,
                                            mediaMetadata.title,
                                            mediaMetadata.artists.joinToString { it.name },
                                            mediaMetadata.album?.title,
                                            lyricText,
                                            selectedTemplate
                                        )
                                        val filename = "Juno_${mediaMetadata.title.replace(" ", "_")}_${System.currentTimeMillis()}"
                                        val uri = ComposeToImage.saveToGallery(context, bitmap, filename)
                                        if (uri != null) {
                                            Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isGeneratingImage = false
                                    }
                                }
                            }
                        )

                        // Copy Link
                        ShareActionButton(
                            iconRes = R.drawable.link,
                            label = "Copy Link",
                            onClick = {
                                val link = "https://share.junomusic.fun/watch?v=${mediaMetadata.id}"
                                clipboardManager.setText(AnnotatedString(link))
                                Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShareActionButton(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .width(64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StoryCardPreview(
    mediaMetadata: MediaMetadata,
    lyricText: String?,
    template: ShareTemplate,
    paletteColors: Pair<Color, Color>?
) {
    val context = LocalContext.current
    val hasLyrics = !lyricText.isNullOrBlank()

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(mediaMetadata.thumbnailUrl)
            .crossfade(true)
            .build()
    )

    when (template) {
        ShareTemplate.MINIMAL -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = if (hasLyrics) 16.dp else 48.dp)
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(if (hasLyrics) 120.dp else 180.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = mediaMetadata.title,
                            color = Color.White,
                            fontSize = if (hasLyrics) 16.sp else 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mediaMetadata.artists.joinToString { it.name },
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = if (hasLyrics) 13.sp else 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!mediaMetadata.album?.title.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mediaMetadata.album.title,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = if (hasLyrics) 11.sp else 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (hasLyrics && lyricText != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lyricText,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    BrandingBlock(textColor = Color.White.copy(alpha = 0.6f))
                }
            }
        }
        ShareTemplate.DARK -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = if (hasLyrics) 16.dp else 48.dp)
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(if (hasLyrics) 130.dp else 200.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = mediaMetadata.title,
                            color = Color.White,
                            fontSize = if (hasLyrics) 16.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mediaMetadata.artists.joinToString { it.name },
                            color = Color(0xFF8E8E93),
                            fontSize = if (hasLyrics) 13.sp else 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (hasLyrics && lyricText != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lyricText,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    BrandingBlock(textColor = Color(0xFF8E8E93))
                }
            }
        }
        ShareTemplate.GRADIENT -> {
            val startColor = paletteColors?.first ?: Color(0xFF4A148C)
            val endColor = paletteColors?.second ?: Color(0xFF1A237E)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(startColor, endColor)))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = if (hasLyrics) 16.dp else 48.dp)
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(if (hasLyrics) 120.dp else 180.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = mediaMetadata.title,
                            color = Color.White,
                            fontSize = if (hasLyrics) 16.sp else 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mediaMetadata.artists.joinToString { it.name },
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = if (hasLyrics) 13.sp else 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (hasLyrics && lyricText != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lyricText,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    BrandingBlock(textColor = Color.White)
                }
            }
        }
        ShareTemplate.GLASS -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(30.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = if (hasLyrics) 32.dp else 64.dp)
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(if (hasLyrics) 90.dp else 140.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = mediaMetadata.title,
                                color = Color.White,
                                fontSize = if (hasLyrics) 15.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mediaMetadata.artists.joinToString { it.name },
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = if (hasLyrics) 12.sp else 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (hasLyrics && lyricText != null) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lyricText,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        BrandingBlock(textColor = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
        ShareTemplate.LYRICS_FOCUS -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF16171B))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    if (hasLyrics && lyricText != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lyricText,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = mediaMetadata.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = mediaMetadata.artists.joinToString { it.name },
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 48.dp)
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = mediaMetadata.title,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mediaMetadata.artists.joinToString { it.name },
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(1.dp))
                    }

                    BrandingBlock(textColor = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun BrandingBlock(
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "JUNO",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}
