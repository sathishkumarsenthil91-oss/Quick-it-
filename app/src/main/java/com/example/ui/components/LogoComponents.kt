package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Full "Quick it" Brand Logo: Speeding Q mark with "uick it" typography
 */
@Composable
fun QuickItFullLogo(
    modifier: Modifier = Modifier,
    height: Dp = 48.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_quick_it_logo),
        contentDescription = "Quick it Logo",
        modifier = modifier
            .height(height)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

/**
 * Aerodynamic Speeding Q Mark Only
 */
@Composable
fun QuickItMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_quick_it_mark),
        contentDescription = "Quick it Mark",
        modifier = modifier.size(size)
    )
}
