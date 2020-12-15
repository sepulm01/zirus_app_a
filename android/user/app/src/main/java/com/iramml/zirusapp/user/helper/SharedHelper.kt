package com.iramml.zirusapp.user.helper

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.Context;


class SharedHelper {
    companion object {
        private var sharedPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null

        @SuppressLint("CommitPrefEdits")
        fun putKey(context: Context, Key: String?, Value: String?) {
            sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE)
            editor = sharedPreferences!!.edit()
            editor!!.putString(Key, Value)
            editor!!.apply()
        }

        fun getKey(contextGetKey: Context, Key: String?): String? {
            sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE)
            return sharedPreferences!!.getString(Key, "")
        }

        fun clearSharedPreferences(context: Context) {
            sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE)
            sharedPreferences!!.edit().clear().apply()
        }
    }
}