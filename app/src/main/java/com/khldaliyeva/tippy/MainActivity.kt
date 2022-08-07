package com.khldaliyeva.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var etBillAmount: EditText
    private lateinit var sbTipPercent: SeekBar
    private lateinit var tvTipFeedback: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        etBillAmount = findViewById(R.id.etBillAmount)
        sbTipPercent = findViewById(R.id.sbTipPercent)
        tvTipFeedback = findViewById(R.id.tvTipFeedback)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        sbTipPercent.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipFeedback(INITIAL_TIP_PERCENT)

        sbTipPercent.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipFeedback(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        etBillAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }
        })
    }

    private fun computeTipAndTotal() {
        if(etBillAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        val baseAmount = etBillAmount.text.toString().toDouble()
        val tipPercent = sbTipPercent.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }

    private fun updateTipFeedback(tipPercent: Int) {
        val feedback = when(tipPercent) {
            in 0..9 -> "\uD83D\uDE22"
            in 10..14 -> "\uD83D\uDE10"
            in 15..19 -> "\uD83D\uDE42"
            in 20..24 -> "\uD83D\uDE04"
            else -> "\uD83D\uDE03"
        }
        tvTipFeedback.text = feedback

        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / sbTipPercent.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        tvTipFeedback.setTextColor(color)
    }
}