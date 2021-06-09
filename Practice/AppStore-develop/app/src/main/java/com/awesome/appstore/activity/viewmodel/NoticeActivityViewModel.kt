package com.awesome.appstore.activity.viewmodel

import androidx.lifecycle.*
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.database.DatabaseStatus
import com.awesome.appstore.database.Notice
import com.awesome.appstore.event.Event
import com.awesome.appstore.network.NetworkResult
import com.awesome.appstore.network.NetworkStatus
import com.awesome.appstore.protocol.request.RequestNoticeReadStateChange
import com.awesome.appstore.protocol.response.Paging
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.util.StorePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import kotlin.random.Random

class NoticeActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val preference: StorePreference
) :
    ViewModel() {
    private val _onClickExit: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _noticeList: MutableLiveData<List<Notice?>> = MutableLiveData<List<Notice?>>()
    private val _tokenExpireDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _pagingInfo: MutableLiveData<Paging> = MutableLiveData()

    var pagingInfo: LiveData<Paging> = _pagingInfo
    var tokenExpireDialog: LiveData<Event<Boolean>> = _tokenExpireDialog
    var noticeList: LiveData<List<Notice?>> = _noticeList

    init {
        getNoticeList(1)
    }

    var onClickExit: LiveData<Event<Boolean>> = _onClickExit

    fun onClickExit() {
        _onClickExit.value = Event(true)
    }

    fun getNoticeList(page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
//            _noticeList.postValue(repository.selectAllNoticeList().data)
            val result = repository.getNoticeList(page, 10)
            when (result.networkStatus) {
                NetworkStatus.SUCCESS -> {
                    if (page > 1) delay(1000)
                    _pagingInfo.postValue(result.data?.data?.paging)
                    _noticeList.postValue(result.data?.data?.notice)
                }
                NetworkStatus.ERROR -> {
                    if (result.message == StringTAG.ERROR_CODE_401) {
                        _tokenExpireDialog.postValue(Event(true))
                    }
                }
            }
        }


    }

    fun setNoticeReadState(notice: Notice) {
        viewModelScope.launch {
            if (notice.isRead == "N") {
                notice.isRead = "Y"
//                val result = repository.insertNotice(notice)
//                if (result.databaseStatus == DatabaseStatus.SUCCESS) {
//                    getNoticeList()
//                }
                val netResult =
                    repository.updateNoticeReadState(RequestNoticeReadStateChange(RequestNoticeReadStateChange.RequestNoticeRead(notice.noticeId)))
                if (netResult.networkStatus == NetworkStatus.ERROR && netResult.message == StringTAG.ERROR_CODE_401) {
                    _tokenExpireDialog.postValue(Event(true))
                }
            }
        }
    }
}