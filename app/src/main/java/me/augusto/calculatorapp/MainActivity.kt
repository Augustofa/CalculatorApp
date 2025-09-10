package me.augusto.calculatorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var resultView: TextView;

    private var firstValue: Double = 0.0
    private var secondValue: Double = 0.0
    private var currentOperator: String = ""
    private var isDisplayEmpty: Boolean = true
    private var digitAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        resultView = findViewById<TextView>(R.id.resultText)

        val buttonIds = listOf(
            R.id.clearBtn, R.id.eraseBtn, R.id.moduloBtn, R.id.divideBtn,
            R.id.btn7, R.id.btn8, R.id.btn9, R.id.multiplyBtn,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.minusBtn,
            R.id.btn1, R.id.btn2, R.id.btn3, R.id.sumBtn,
            R.id.squareBtn, R.id.btn0, R.id.pointBtn, R.id.equalsBtn
        )

        buttonIds.forEach { id ->
            findViewById<Button>(id)?.setOnClickListener(this)
        }
    }

    override fun onClick(view : View) {
        if(isDisplayingError()) onClearClicked()
        when(view.id){
            R.id.clearBtn -> {
                onClearClicked()
            }
            R.id.eraseBtn -> {
                onEraseClicked()
            }
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9 -> {
                onDigitClicked((view as Button).text.toString())
            }
            R.id.sumBtn, R.id.multiplyBtn, R.id.divideBtn, R.id.moduloBtn -> {
                onOperatorClicked((view as Button).text.toString())
            }
            R.id.minusBtn -> {
                onMinusClicked()
            }
            R.id.pointBtn -> onPointClicked()
            R.id.squareBtn -> onSquareClicked()
            R.id.equalsBtn -> onEqualsClicked()
        }
    }

    private fun onClearClicked() {
        resultView.text = "0"
        firstValue = 0.0
        secondValue = 0.0
        currentOperator = ""
        isDisplayEmpty = true
        digitAdded = false
    }

    private fun onEraseClicked() {
        var displayedText = resultView.text.toString()
        if(displayedText.length > 1){
            resultView.text = displayedText.substring(0, displayedText.length - 1)
        }else{
            resultView.text = "0"
        }
    }

    private fun onDigitClicked(digit : String){
        if(isDisplayEmpty || resultView.text.toString() == "0"){
            resultView.text = digit
            isDisplayEmpty = false
            digitAdded = true
        }else{
            resultView.append(digit)
        }
    }

    private fun onOperatorClicked(operator: String) {
        firstValue = getDisplayedValue()
        currentOperator = operator
        resultView.text = "0"
        isDisplayEmpty = true
    }

    private fun onMinusClicked() {
        if(isDisplayEmpty){
            setDisplayedValue("-")
            isDisplayEmpty = false
        }else{
            onOperatorClicked("-")
        }
    }

    private fun onSquareClicked() {
        currentOperator = "sq"
        onEqualsClicked()
        if(!isDisplayingError()){
            secondValue = getDisplayedValue()
        }
    }

    private fun onEqualsClicked() {
        if(currentOperator.isNotEmpty()){
            if(digitAdded){
                secondValue = getDisplayedValue()
            }
            val result = calculateResult()
            firstValue = result
            isDisplayEmpty = false
            digitAdded = false
            if(abs(result).rem(1).equals(0.0)){
                setDisplayedValue(result.toInt().toString())
            }else{
                setDisplayedValue(result.toString())
            }
        }
    }

    private fun onPointClicked() {
        if(!resultView.text.contains(".")){
            resultView.append(".")
            isDisplayEmpty = false
        }
    }

    private fun calculateResult() : Double {
        return when(currentOperator){
            "+" -> (firstValue + secondValue)
            "-" -> (firstValue - secondValue)
            "*" -> (firstValue * secondValue)
            "/" -> {
                if(secondValue != 0.0){
                    (firstValue / secondValue)
                }else{
                    Double.NaN
                }
            }
            "sq" -> (secondValue * secondValue)
            "%" -> (firstValue % secondValue)
            else -> 0.0
        }
    }

    private fun isDisplayingError() : Boolean {
        return resultView.text.toString().equals("TOO BIG") || resultView.text.toString().equals("NaN")
    }

    private fun getDisplayedValue() : Double {
        val value = resultView.text.toString()
        if(value == "-") return 0.0
        return value.toDouble()
    }

    private fun setDisplayedValue(value : String) {
        if(value.length >= 10){
            resultView.text = "TOO BIG"
        }else{
            resultView.text = value
        }
    }
}
