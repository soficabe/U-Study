package com.example.u_study.data.models

import androidx.annotation.StringRes
import com.example.u_study.R

enum class Language(val code: String, @StringRes val langName: Int) {
    ITALIAN("it", R.string.language_italian),
    ENGLISH("en", R.string.language_english)

}
