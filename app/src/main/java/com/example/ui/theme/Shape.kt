package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material Design 3 Shape System with soft, non-sharp rounded corners
 * applied across cards, dialogs, menus, chips, and text fields.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

val CardShape = RoundedCornerShape(20.dp)
val MenuShape = RoundedCornerShape(18.dp)
val DialogShape = RoundedCornerShape(28.dp)
val ChipShape = RoundedCornerShape(14.dp)
val TabShape = RoundedCornerShape(14.dp)
val InputShape = RoundedCornerShape(16.dp)
