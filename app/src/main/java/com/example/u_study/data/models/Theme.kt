package com.example.u_study.data.models

import androidx.annotation.StringRes
import com.example.u_study.R

enum class Theme(@StringRes val themeName: Int) {
    Light(R.string.light),
    Dark(R.string.dark),
    System(R.string.system)
}