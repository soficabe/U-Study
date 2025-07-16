package com.example.u_study.data.models

import androidx.annotation.StringRes
import com.example.u_study.R

enum class Language(@StringRes val langName: Int) {
    ITALIAN(R.string.language_italian),
    ENGLISH(R.string.language_english)

}
