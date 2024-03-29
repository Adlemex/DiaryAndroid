package com.alex.materialdiary.sys.net.models.marks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mark(
    val value: Int = 0,
    val date: String = ""
) : Parcelable
