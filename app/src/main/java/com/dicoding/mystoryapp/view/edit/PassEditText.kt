package com.dicoding.mystoryapp.view.edit

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.mystoryapp.R

class PassEditText : AppCompatEditText {

    private var _passMessage = MutableLiveData<String?>()
    var passMessage: LiveData<String?> = _passMessage

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                _passMessage.value = isValidPass(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun isValidPass(s: String): String? {

        if (s.length < 8) return resources.getString(R.string.pass_alert)
        return null
    }
}