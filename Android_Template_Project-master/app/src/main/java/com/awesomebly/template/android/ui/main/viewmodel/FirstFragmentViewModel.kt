package com.awesomebly.template.android.ui.main.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomebly.template.android.R
import com.awesomebly.template.android.database.entity.TpEntity
import com.awesomebly.template.android.database.handler.DatabaseStatus
import com.awesomebly.template.android.repository.DatabaseRepository
import com.awesomebly.template.android.repository.NetworkRepository
import com.awesomebly.template.android.ui.base.BaseViewModel
import com.awesomebly.template.android.util.Utils
import com.awesomebly.template.android.util.event.Event
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : MainFragmentViewModel
 * Date : 2021-05-03, 오전 11:51
 * History
seq   date          contents      programmer
01.   2021-05-03    init            차태준
02.
03.
 */
@HiltViewModel
class FirstFragmentViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val utils: Utils
) : BaseViewModel() {
    private val _tpList = MutableLiveData<List<TpEntity?>>()
    val tpList: LiveData<List<TpEntity?>> = _tpList
    private val _btnDbSave = MutableLiveData<Event<Boolean>>()
    val btnDbSave: LiveData<Event<Boolean>> = _btnDbSave

    /**
     * Insert tp
     * 입력 받은 이름과 현재 시간으로 TpEntity insert
     * @param name
     */
    fun insertTp(name: String) {
        viewModelScope.launch {
            Logger.d(databaseRepository.insertTp(TpEntity(name = name, date = utils.getCurrentDateToString())))
            selectAllTp()
        }
    }

    fun onClickDbSaveBtn() {
        _btnDbSave.value = Event(true)
    }

    /**
     * Select all tp
     * 저장된 TpEntity 모두 조회 하여 tpList에 할당
     */
    fun selectAllTp() {
        viewModelScope.launch {
            databaseRepository.selectTpAll().let {
                when (it.databaseStatus) {
                    DatabaseStatus.SUCCESS -> _tpList.postValue(it.data as List<TpEntity?>)
                    DatabaseStatus.ERROR -> Toast.makeText(
                        appContext,
                        appContext.getString(R.string.toast_msg_db_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * On click db clear btn
     * TpEntity 모두 삭제 하고 tpList에 반영
     */
    fun onClickDbClearBtn() {
        viewModelScope.launch {
            databaseRepository.deleteAllTp().let {
                when (it.databaseStatus) {
                    DatabaseStatus.SUCCESS -> {
                        Toast.makeText(appContext, appContext.getString(R.string.toast_msg_db_clear), Toast.LENGTH_SHORT).show()
                        selectAllTp()
                    }
                    DatabaseStatus.ERROR -> Toast.makeText(
                        appContext,
                        appContext.getString(R.string.toast_msg_db_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}