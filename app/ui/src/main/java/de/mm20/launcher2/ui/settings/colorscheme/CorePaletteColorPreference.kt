package de.mm20.launcher2.ui.settings.colorscheme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.SettingsSuggest
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import de.mm20.launcher2.ui.component.BottomSheetDialog
import de.mm20.launcher2.ui.component.colorpicker.HctColorPicker
import de.mm20.launcher2.ui.component.colorpicker.rememberHctColorPickerState
import de.mm20.launcher2.ui.component.preferences.SwitchPreference

@Composable
fun CorePaletteColorPreference(
    title: String,
    value: Int?,
    onValueChange: (Int?) -> Unit,
    defaultValue: Int,
    modifier: Modifier = Modifier,
    autoGenerate: (() -> Int?)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }

    PlainTooltipBox(tooltip = { Text(title) }) {
        ColorSwatch(
            color = Color(value ?: defaultValue),
            modifier = modifier
                .size(48.dp)
                .tooltipTrigger()
                .clickable { showDialog = true },
        )
    }

    if (showDialog) {
        var currentValue by remember { mutableStateOf(value) }
        BottomSheetDialog(onDismissRequest = {
            onValueChange(currentValue)
            showDialog = false
        }) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                SwitchPreference(
                    icon = Icons.Rounded.SettingsSuggest,
                    title = "Use system default",
                    value = currentValue == null,
                    onValueChanged = {
                        currentValue = if (it) null else defaultValue
                    }
                )
                AnimatedVisibility(
                    currentValue != null,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                    ),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                    )
                ) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        val colorPickerState = rememberHctColorPickerState(
                            initialColor = Color(value ?: defaultValue),
                            onColorChanged = {
                                currentValue = it.toArgb()
                            }
                        )
                        HctColorPicker(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            state = colorPickerState,
                        )

                        if (autoGenerate != null) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            TextButton(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .align(Alignment.End),
                                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
                                onClick = {
                                    val autoGenerated = autoGenerate()
                                    currentValue = autoGenerated
                                    if (autoGenerated != null) {
                                        colorPickerState.setColor(Color(autoGenerated))
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.AutoFixHigh, null,
                                    modifier = Modifier
                                        .padding(ButtonDefaults.IconSpacing)
                                        .size(ButtonDefaults.IconSize)
                                )
                                Text("From primary color")
                            }
                        }
                    }
                }
            }
        }
    }
}