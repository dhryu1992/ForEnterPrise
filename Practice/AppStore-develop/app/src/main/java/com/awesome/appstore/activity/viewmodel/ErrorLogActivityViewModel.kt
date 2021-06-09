package com.awesome.appstore.activity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.appstore.database.DatabaseStatus
import com.awesome.appstore.database.ErrorLog
import com.awesome.appstore.event.Event
import com.awesome.appstore.repository.Repository
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import javax.inject.Inject


class ErrorLogActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val executorService: ExecutorService,
) :
    ViewModel() {
    private val _errList: MutableLiveData<List<ErrorLog?>> = MutableLiveData()
    private val _onClickDelete: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _onClickSelectAll: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _onClickSubmit: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()

    var onClickSubmit: LiveData<Event<Boolean>> = _onClickSubmit
    var onClickSelectAll: LiveData<Event<Boolean>> = _onClickSelectAll
    var onClickDelete: LiveData<Event<Boolean>> = _onClickDelete
    var errList: LiveData<List<ErrorLog?>> = _errList

    init {
        getErrorLog()
    }

    fun getErrorLog() {
        viewModelScope.launch {
            val result = repository.getErrorLog()
            if (result.databaseStatus == DatabaseStatus.SUCCESS) {
                _errList.value = (result.data)
            }
        }
    }

    fun deleteAllErrorLog() {
        viewModelScope.launch {
            val result = repository.deleteAllErrorLog()
            if (result.databaseStatus == DatabaseStatus.SUCCESS) {
                getErrorLog()
            }
        }
    }

    fun onClickDelete() {
        _onClickDelete.value = Event(true)
    }

    fun deleteErrorLog(checkedLog: List<ErrorLog?>) {
        checkedLog.forEach {
            viewModelScope.launch {
                val result = repository.deleteErrorLog(it!!)
                if (result.databaseStatus == DatabaseStatus.SUCCESS) {
                    getErrorLog()
                }
            }
        }
    }

    fun submitErrorLog(checkedLog: List<ErrorLog?>) {
//       todo 에러로그 전송 프로토콜 적용
        viewModelScope.launch(Dispatchers.IO) {

        }

    }

    fun onClickSelectAll(){
        _onClickSelectAll.value = Event(true)
    }

    fun onClickSubmit(){
        _onClickSubmit.value = Event(true)
    }
}