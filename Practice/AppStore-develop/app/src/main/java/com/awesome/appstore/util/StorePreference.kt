package com.awesome.appstore.util

import android.app.Activity
import android.content.Context
import com.awesome.appstore.config.*

class StorePreference(private val context : Context) {

    fun saveIdSavePreference(id: String?) {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(StringTAG.ID, id)
        editor.apply()
    }

    fun loadIdSavePreference(): String? {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        return pref.getString(StringTAG.ID, "")
    }


    fun saveTokenPreference(token: String?) {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(StringTAG.TOKEN, token)
        editor.apply()
    }

    fun loadTokenPreference(): String? {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        return pref.getString(StringTAG.TOKEN, "")
    }

    fun savePasswordPreference(id: String?) {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(StringTAG.PASSWORD, id)
        editor.apply()
    }

    fun loadPasswordPreference(): String? {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        return pref.getString(StringTAG.PASSWORD, "")
    }

    fun saveLockScreenSetting(setting: String) {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(StringTAG.LOCK_SCREEN, setting)
        editor.apply()
    }

    fun loadLockScreenSetting(): String? {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        return pref.getString(StringTAG.LOCK_SCREEN, "")
    }

    fun savePushSetting(setting: String) {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(StringTAG.PUSH_SET, setting)
        editor.apply()
    }

    fun loadPushSetting(): String? {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        return pref.getString(StringTAG.PUSH_SET, "")
    }

    fun saveLockScreenEnable(setting: String) {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(StringTAG.LOCK_SCREEN_ENABLE, setting)
        editor.apply()
    }

    fun loadLockScreenEnable(): String? {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        return pref.getString(StringTAG.LOCK_SCREEN_ENABLE, "")
    }

    fun saveExpiredTime(setting: String) {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(StringTAG.EXPIRE_TIME, setting)
        editor.apply()
    }

    fun loadExpiredTime(): String? {
        val pref = context.getSharedPreferences(StringTAG.STORE_PREFERENCE, Activity.MODE_PRIVATE)
        return pref.getString(StringTAG.EXPIRE_TIME, "")
    }
}