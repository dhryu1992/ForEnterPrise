package com.awesome.appstore.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.event.Event
import com.awesome.appstore.network.NetworkStatus
import com.awesome.appstore.protocol.request.Push
import com.awesome.appstore.protocol.request.RequestSetting
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.util.StorePreference
import com.awesome.appstore.util.lock.LockManager
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val preference: StorePreference,
    private val lockManager: LockManager
) : ViewModel() {
    private val _onClickExit: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var onClickExit: LiveData<Event<Boolean>> = _onClickExit

    private val _setPattern: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var setPattern: LiveData<Event<Boolean>> = _setPattern

    private val _setPin: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var setPin: LiveData<Event<Boolean>> = _setPin

    private val _lockScreen: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    var lockScreen: LiveData<Boolean> = _lockScreen

    private val _pushAlarm: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    var pushAlarm: LiveData<Boolean> = _pushAlarm

    private val _alertLock: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var alertLock: LiveData<Event<Boolean>> = _alertLock

    private val _lockScreenEnable: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var lockScreenEnable: LiveData<Boolean> = _lockScreenEnable

    private val _lockScreenDisableMsg: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var lockScreenDisableMsg: LiveData<Event<Boolean>> = _lockScreenDisableMsg

    private val _tokenExpireDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    var tokenExpireDialog: LiveData<Event<Boolean>> = _tokenExpireDialog

    init {
        _lockScreen.value = preference.loadLockScreenSetting() == "1"
        _pushAlarm.value = preference.loadPushSetting() == "1"
        _lockScreenEnable.value = preference.loadLockScreenEnable() == "1"
    }

    fun onClickExit() {
        _onClickExit.value = Event(true)
    }

    fun setPatternLock() {
        _setPattern.value = Event(true)
    }

    fun setPinLock() {
        _setPin.value = Event(true)
    }

    fun setLockScreen() {
        lockManager.load()
        Logger.d(lockManager.locks)
        if (lockManager.isEmpty()) {
            _alertLock.value = Event(true)
            _lockScreen.value = false
        } else {
            _lockScreen.value = lockScreen.value != true
            preference.saveLockScreenSetting(
                if (_lockScreen.value == true) "1"
                else "0"
            )
        }
    }

    fun setPush() {
        _pushAlarm.value = pushAlarm.value != true
        preference.savePushSetting(
            if (_pushAlarm.value == true) "1"
            else "0"
        )
        viewModelScope.launch {
            val push = if (preference.loadPushSetting() == "1") "Y" else "N"
            val result = repository.updateUserSetting(RequestSetting(Push(push)))
            if (result.networkStatus == NetworkStatus.ERROR && result.message == StringTAG.ERROR_CODE_401){
                _tokenExpireDialog.postValue(Event(true))
            }
        }
    }

    fun showLockScreenDisable(){
        if (lockScreenEnable.value == false){
            _lockScreenDisableMsg.value = Event(true)
        }
    }

}