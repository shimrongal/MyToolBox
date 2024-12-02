package dev.gs.mytoolbox.ui.custom_views

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import dev.gs.mytoolbox.R

class ViewPassword @JvmOverloads constructor(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrSet, defStyleAttr) {

    private val etPassword: EditText
    private val ibVisibility: ImageButton

    init {
        inflate(context, R.layout.layout_password, this)
        etPassword = findViewById(R.id.etPassword)
        ibVisibility = findViewById(R.id.ibPasswordVisibility)
    }

    fun getPassword(): Editable {
        return etPassword.text
    }

    fun setOnVisibilityClick(listener: OnClickListener) {
        ibVisibility.setOnClickListener(listener)
    }

    fun setInputType(isSelected: Boolean) {
        etPassword.inputType = if (isSelected) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }
}