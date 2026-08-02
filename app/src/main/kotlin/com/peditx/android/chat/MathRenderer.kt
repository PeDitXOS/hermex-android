package com.peditx.hermex.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

/**
 * Simple math/LaTeX renderer — extracts inline $...$ and block $$...$$ segments
 * and renders them in a monospace font. Full LaTeX rendering requires a WebView;
 * this is a lightweight fallback that makes math readable.
 */
object MathRenderer {
    private val inlinePattern = Regex("\$([^$]+)\$")
    private val blockPattern = Regex("\$\$([^$]+)\$\$")

    fun hasMath(text: String): Boolean = text.contains("$")

    @Composable
    fun RenderMathText(text: String, modifier: Modifier = Modifier) {
        if (!hasMath(text)) {
            Text(text, modifier = modifier)
            return
        }
        val annotated = buildAnnotatedString {
            var remaining = text
            while (remaining.isNotEmpty()) {
                val blockMatch = blockPattern.find(remaining)
                val inlineMatch = inlinePattern.find(remaining)
                val firstMatch = listOfNotNull(blockMatch, inlineMatch).minByOrNull { it.range.first }
                if (firstMatch == null) {
                    append(remaining)
                    break
                }
                append(remaining.substring(0, firstMatch.range.first))
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp)) {
                    append(firstMatch.groupValues[1])
                }
                remaining = remaining.substring(firstMatch.range.last + 1)
            }
        }
        Text(annotated, modifier = modifier)
    }
}
