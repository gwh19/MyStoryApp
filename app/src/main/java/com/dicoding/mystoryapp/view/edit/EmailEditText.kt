package com.dicoding.mystoryapp.view.edit

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.mystoryapp.R

class EmailEditText : AppCompatEditText {

    private var _emailmessage = MutableLiveData<String?>()
    var emailMessage: LiveData<String?> = _emailmessage

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
               _emailmessage.value = isValidEmail(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun isValidEmail(s: String): String? {

        if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) return resources.getString(R.string.email_alert)
        return null
    }
}