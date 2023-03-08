package com.srignan.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var euro: TextView? = null
    private var peso: TextView? = null
    private var yuan: TextView? = null
    private var rupee: TextView? = null
    private var convertEuro = 0.95
    private var convertPeso = 18.05
    private var convertYuan = 6.96
    private var convertRupee = 81.95
    private var dollar: EditText? = null
    private var STATE_DOLLARS = "dollars"
    private var minUpVelocity = -3000
    private var minDownVelocity = 3000
    private var dollars = 0f

    private var gestureDetector: GestureDetector? = null
    private var mTouchPosition = 0f
    private var mReleasePosition = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If we have a saved state then we can restore the dollar amount
        if (savedInstanceState != null) {
            dollars = savedInstanceState.getFloat(STATE_DOLLARS, 0f)
            setAmount()
        }
        setContentView(R.layout.activity_main)
        gestureDetector = GestureDetector(this, GestureListener())
        dollar = findViewById(R.id.dollar)
        euro = findViewById(R.id.euro)
        peso = findViewById(R.id.peso)
        yuan = findViewById(R.id.yuan)
        rupee = findViewById(R.id.rupee)
        dollar?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val tempUSD = tryParse(dollar?.text.toString())

                // makes sure than the edit text doesn't contain a negative number
                if (tempUSD < 0) {
                    dollars = tempUSD
                    setAmount()
                } else {
                    dollars = tempUSD
                    setEditAmount()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(STATE_DOLLARS, dollars)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            mTouchPosition = event.y
        }
        if (event.action == MotionEvent.ACTION_UP) {
            mReleasePosition = event.y
            if (mTouchPosition - mReleasePosition > 0) {
                // user scroll up
                dollars += 0.10.toFloat()
            } else {
                //user scroll down
                dollars -= 0.10.toFloat()
            }
        }
        setAmount()
        gestureDetector!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    // detects is the user flings up or down
    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            // user fling up
            if (velocityY < minUpVelocity) {
                dollars += 0.9.toFloat()
            }

            // user fling down
            if (velocityY > minDownVelocity) {
                dollars -= 0.9.toFloat()
            }
            setAmount()
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }



    // parses the number in the text if it's possible
    fun tryParse(text: String): Float {
        return try {
            text.toFloat()
        } catch (e: NumberFormatException) {
            0f
        }
    }

    // updates all text/edit text boxes
    fun setAmount() {
        if (dollars < 0) {
            dollars = 0f
        }
        val currDollars = String.format("%.02f", dollars)
        val currEuros = String.format("%.02f", dollars * convertEuro)
        val currPesos = String.format("%.02f", dollars * convertPeso)
        val currYuan = String.format("%.02f", dollars * convertYuan)
        val currRupee = String.format("%.02f", dollars * convertRupee)
        dollar!!.setText(currDollars)
        euro!!.text = "$currEuros euros"
        peso!!.text = "$currPesos pesos"
        yuan!!.text = "$currYuan yuan"
        rupee!!.text = "$currRupee rupee"
    }

    // updates all text boxes except the edit text box, to prevent stack overflow
    fun setEditAmount() {
        if (dollars < 0) {
            dollars = 0f
        }
        val currEuros = String.format("%.02f", dollars * convertEuro)
        val currPesos = String.format("%.02f", dollars * convertPeso)
        val currYuan = String.format("%.02f", dollars * convertYuan)
        val currRupee = String.format("%.02f", dollars * convertRupee)
        euro!!.text = "$currEuros euros"
        peso!!.text = "$currPesos pesos"
        yuan!!.text = "$currYuan yuan"
        rupee!!.text = "$currRupee rupee"
    }
}
