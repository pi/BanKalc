package com.ps.bankalc
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.ViewModel
import com.ps.bankalc.ui.theme.BanKalcTheme
import kotlin.math.roundToInt

class MainModel : ViewModel() {
    var percents by mutableStateOf("16")
        private set

    var isBadPercents by mutableStateOf(false)
        private set

    var sum by mutableStateOf("10000")
        private set

    var isBadSum by mutableStateOf(false)
        private set

    var gain by mutableStateOf("")
        private set

    var months by mutableFloatStateOf(3f)
        private set

    var monthsLabel by mutableStateOf("")
        private set

    var capitalization by mutableStateOf(true)
        private set

    fun updatePercents(aPct: String): Boolean {
        percents = aPct

        isBadPercents = percents.trim().toDoubleOrNull() == null

        updateGain()

        return true
    }

    fun updateSum(aSum: String): Boolean {
        sum = aSum

        isBadSum = sum.trim().toDoubleOrNull() == null

        updateGain()

        return true
    }

    fun updateMonths(aMonths: Float): Boolean {
        months = aMonths.roundToInt().toFloat()

        updateGain()

        return true
    }

    fun updateCapitalization(value: Boolean): Boolean {
        capitalization = value

        updateGain()

        return true
    }

    fun updateGain() {
        if (isBadPercents || isBadSum) {
            gain = "-----"
            return
        }

        val p = percents.trim().toDouble()
        val s = sum.trim().toDouble()

        val m = months.toInt()
        var str = ""

        if (m >= 12)
            str = (m / 12).toString() + " г "

        if (m % 12 != 0)
            str += (m % 12).toString() + " мес"
        monthsLabel = "Срок: " + str.trim()

        var g: Double = 0.0

        if (capitalization) {
            (1..months.toInt()).forEach {
                g += ((s + g) * p / 1200.0)
            }
            gain = g.toInt().toString()
        } else {
            g = s * p * months / 1200.0
        }
        gain = g.roundToInt().toString()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var m = MainModel()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanKalcTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainUI(m)
                    }
                }
            }
        }
        m.updateGain()
    }
}

@Composable
fun MainUI(m: MainModel) {
    Column(modifier = Modifier.padding(10.dp)) {
        TextEntry("Годовой процент", m.percents, m.isBadPercents) { s -> m.updatePercents(s) }
        TextEntry("Сумма", m.sum, m.isBadSum) { s -> m.updateSum(s) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Капитализация")
            Spacer(Modifier.width(10.dp))
            Switch(
                m.capitalization,
                onCheckedChange = { v -> m.updateCapitalization(v) })
        }
        Text(m.monthsLabel)
        Slider(
            value = m.months,
            valueRange = 1f..36f,
            onValueChange = { v -> m.updateMonths(v) }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = m.gain,
                fontSize = 14.em,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TextEntry(prompt: String, aValue: String, err: Boolean, onChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = aValue,
        onValueChange = onChange,
        label = { Text(prompt) },
        isError = err,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    val m = MainModel()
    BanKalcTheme {
        MainUI(m)
    }
}
