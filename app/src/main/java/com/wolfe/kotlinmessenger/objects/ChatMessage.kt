package com.wolfe.kotlinmessenger.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChatMessage(val id: String, val fromId: String, val toId: String, val text: String, val timeStamp: Long): Parcelable {
    constructor() : this("","","","",-1)
}