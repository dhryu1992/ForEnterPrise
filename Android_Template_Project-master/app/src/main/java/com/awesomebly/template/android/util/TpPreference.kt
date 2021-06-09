package com.awesomebly.template.android.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.awesomebly.template.android.util.config.Constants.Companion.Name.PREFERENCE_NAME
import com.awesomebly.template.android.util.config.Constants.Companion.Preference.PREFERENCE_SAMPLE
import javax.inject.Inject

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : TpPreference
 * Date : 2021-05-03, 오후 3:40
 * History
seq   date          contents      programmer
01.   2021-05-03                    차태준
02.
03.
 */

class TpPreference @Inject constructor(private val preference: SharedPreferences) {
    fun saveNamePreference(sample: String?) {
        preference.edit().run {
            putString(PREFERENCE_SAMPLE, sample)
            apply()
        }
    }

    fun loadNamePreference(): String {
        return preference.getString(PREFERENCE_SAMPLE, "")!!
    }
}