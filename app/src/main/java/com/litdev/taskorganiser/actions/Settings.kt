package com.litdev.taskorganiser.actions

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Settings(var phone: String, var useSMS: Boolean)
{

}
