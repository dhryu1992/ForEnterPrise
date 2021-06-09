package com.awesome.appstore.activity.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.database.DatabaseStatus
import com.awesome.appstore.database.PushCount
import com.awesome.appstore.event.Event
import com.awesome.appstore.network.NetworkStatus
import com.awesome.appstore.protocol.request.RequestAppStatusChange
import com.awesome.appstore.protocol.request.RequestEditReview
import com.awesome.appstore.protocol.request.RequestWriteReview
import com.awesome.appstore.protocol.response.Paging
import com.awesome.appstore.protocol.response.Review
import com.awesome.appstore.repository.Repository
import com.awesome.appstore.util.FileDirectoryInfo
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import kotlin.math.round

class DetailActivityViewModel @Inject constructor(
    private val repository: Repository,
    private val packageUtil: PackageUtil,
    private val fileDirectoryInfo: FileDirectoryInfo,
    private val preference: StorePreference
) : ViewModel() {
    private val _onClickExit = MutableLiveData<Event<Boolean>>()
    private val _appDelete: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _appExecute: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _appUpdate: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _appInfo: MutableLiveData<AppInfo> = MutableLiveData()
    private val _appDetailImgUrl: MutableLiveData<List<String?>> = MutableLiveData()
    private val _appDetailContent: MutableLiveData<String?> = MutableLiveData()
    private val _downloadProgress: MutableLiveData<Int> = MutableLiveData(-1)
    private val _appDownloadComplete: MutableLiveData<String> = MutableLiveData<String>()
    private val _appStatusUpdateComplete: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _starPoint: MutableLiveData<Float> = MutableLiveData(0.0f)
    private val _reviewCount: MutableLiveData<Int> = MutableLiveData(0)
    private val _onClickEvaluation: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private val _reviewList: MutableLiveData<List<Review?>> = MutableLiveData()
    private val _isExistMyReview: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _reviewUpdateDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _reviewDeleteDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _tokenExpireDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _pagingInfo: MutableLiveData<Paging> = MutableLiveData()
    private val _toastMsg: MutableLiveData<String> = MutableLiveData()

    var toastMsg: LiveData<String> = _toastMsg
    var pagingInfo: LiveData<Paging> = _pagingInfo
    var tokenExpireDialog: LiveData<Event<Boolean>> = _tokenExpireDialog
    var reviewDeleteDialog: LiveData<Event<Boolean>> = _reviewDeleteDialog
    var reviewUpdateDialog: LiveData<Event<Boolean>> = _reviewUpdateDialog
    var isExistMyReview: LiveData<Boolean> = _isExistMyReview
    var reviewList: LiveData<List<Review?>> = _reviewList
    var onClickEvaluation: LiveData<Event<Boolean>> = _onClickEvaluation
    var reviewCount: LiveData<Int> = _reviewCount
    var starPoint: LiveData<Float> = _starPoint
    var appStatusUpdateComplete: LiveData<Event<Boolean>> = _appStatusUpdateComplete
    var appDownloadComplete: LiveData<String> = _appDownloadComplete
    var downloadProgress: LiveData<Int> = _downloadProgress
    var appDelete: LiveData<Event<Boolean>> = _appDelete
    var appExecute: LiveData<Event<Boolean>> = _appExecute
    var appUpdate: LiveData<Event<Boolean>> = _appUpdate
    var appInfo: LiveData<AppInfo> = _appInfo
    var appDetailImgUrl: LiveData<List<String?>> = _appDetailImgUrl
    var appDetailContent: LiveData<String?> = _appDetailContent
    var onClickExit: LiveData<Event<Boolean>> = _onClickExit
//                    if ((result as ResponseAppDetail.AppDetail).common.status == "200") {
//                        val detail = (result as ResponseAppDetail.AppDetail).data
//                        _appDetailImgUrl.value = detail.imgUrl
//                        _appDetailContent.value = detail.content
//                    }


    var isReset = true
    lateinit var myReview: Review

    var token = preference.loadTokenPreference()


    private suspend fun getAppDetail(appInfo: AppInfo) {
//            storeService.getAppDetail(object : StoreService.GetCallback {
//                override fun onSuccess(result: Any) {
//                    super.onSuccess(result)
//                }
//            })
        repository.getAppDetail(appInfo.appId).let { result ->
            when (result.networkStatus) {
                NetworkStatus.SUCCESS -> {
                    _appDetailImgUrl.value = result.data?.data?.files?.filter { it.type == "SCS" }?.map { it.url }
                    Logger.d(result.data)
                    val desc = "${result.data?.data?.app?.descr} \n ============================= \n ${result.data?.data?.version?.descr}"
                    _starPoint.value = result.data?.data?.app?.starPoint?.toFloat()
                    _reviewCount.value = result.data?.data?.app?.reviewCount
                    _appDetailContent.value = desc
                }
                NetworkStatus.ERROR -> {
                    if (result.message == StringTAG.ERROR_CODE_401) {
                        showTokenExpireDialog()
                    }
                    Logger.d("Failed to retrieve app details.")
                }
            }

        }

    }

    private suspend fun setApp(appInfo: AppInfo) {
        _appInfo.value = appInfo
    }

    fun AppRefresh() {
        viewModelScope.launch {
            val result = repository.selectAppInfoByPackageName(_appInfo.value?.packageName!!)
            when (result.databaseStatus) {
                DatabaseStatus.SUCCESS -> {
                    _appInfo.postValue(result.data)
                }
            }
        }
    }

    fun getReviewList(page: Int, pageSize: Int = 5) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getReviewList(page, pageSize, appInfo.value?.appId!!)
            when (result.networkStatus) {
                NetworkStatus.SUCCESS -> {
                    Logger.d(result.data?.data)
                    if (page != 1) delay(1000)
                    _pagingInfo.postValue(result.data?.data?.paging)
                    _reviewList.postValue(result.data?.data?.reviews as List<Review?>)
                    if (page == 1) {
                        val review = result.data.data.reviews.filter { it?.reviewer?.sawonNum == preference.loadIdSavePreference() }
                        Logger.d(review)
                        val isNotEmpty = review?.isNotEmpty()
                        _isExistMyReview.postValue(isNotEmpty)
                        Logger.d(isNotEmpty)
                        if (isNotEmpty) myReview = review[0]!!
                    }
                }
                NetworkStatus.ERROR -> {
                    if (result.message == StringTAG.ERROR_CODE_401) {
                        showTokenExpireDialog()
                    }
                    Logger.d("failed to load review")
                }
            }
        }
    }

    fun onClickExit() {
        _onClickExit.value = Event(true)
    }

    fun onClickEvaluation() {
        Logger.d("onClickEvaluation")
        _onClickEvaluation.value = Event(true)
    }

    fun onClickAppInstall() {
        apkDownload(appInfo.value!!)
    }

    fun onClickAppDelete() {
        _appDelete.value = Event(true)
    }

    fun onClickAppExecute() {
        _appExecute.value = Event(true)
        viewModelScope.launch {
            _appStatusUpdateComplete.postValue(Event(true))
        }
    }
    
    fun pushCountReset(){
        viewModelScope.launch {
            repository.insertPushCount(PushCount(appInfo.value?.packageName!!))
        }
    }

    fun onClickAppUpdate() {
        _appUpdate.value = Event(true)
    }

    fun onClickReviewEdit(type: String) {
        Logger.d(type)
        when (type) {
            "D" -> {
                _reviewDeleteDialog.value = Event(true)
            }
            "U" -> {
                _reviewUpdateDialog.value = Event(true)
                Logger.d(myReview)
            }
        }
    }

    fun deleteReview() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteReview(myReview.reviewId.toInt())
            if (result.networkStatus == NetworkStatus.ERROR && result.message == StringTAG.ERROR_CODE_401) {
                showTokenExpireDialog()
            }
            if (result.networkStatus == NetworkStatus.SUCCESS) {
                getReviewList(1)
            }
        }
    }

    fun editReview(title: String, content: String, starPoint: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val review = RequestEditReview.Review(reviewId = myReview.reviewId, subject = title, content = content, starPoint = starPoint.toDouble())
            val result = repository.editReview(RequestEditReview(review))
            if (result.networkStatus == NetworkStatus.ERROR && result.message == StringTAG.ERROR_CODE_401) {
                showTokenExpireDialog()
            }
            if (result.networkStatus == NetworkStatus.SUCCESS) {
                getReviewList(1)
            }
        }
    }

    fun setAppInfo(appInfo: AppInfo) {
        viewModelScope.launch {
            setApp(appInfo)
            getAppDetail(appInfo)
            getReviewList(1)
        }
    }

    private fun apkDownload(model: AppInfo) {
        var id = PRDownloader.download(
            model.downloadUrl,
            fileDirectoryInfo.internalDownLoads,
            "${model.packageName}.apk"
        )
            .setHeader("Authorization", "Bearer $token")
            .setHeader("Accept", "application/json")
            .build()
            .setOnProgressListener {
//                Logger.d("currentBytes = ${it.currentBytes}, totalBytes = ${it.totalBytes}")
                _downloadProgress.value = round(
                    (it.currentBytes.toDouble().div(it.totalBytes.toDouble())).times(100)
                ).toInt()
            }
            .setOnCancelListener {
                Logger.d("OnCancel")
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Logger.d(fileDirectoryInfo.internalDownLoads)
                    Logger.d("onDownloadComplete ==> file isExist? : ${File(fileDirectoryInfo.internalDownLoads + "/${model.packageName}.apk").exists()}")
                    _downloadProgress.value = -1
                    _appDownloadComplete.value = model.packageName
                }

                override fun onError(error: com.downloader.Error?) {
                    Logger.d(error?.serverErrorMessage)
                    _toastMsg.postValue("다운로드중 문제가 발생했습니다. 잠시후 다시 시도해 주세요")
                }
            })
    }

    fun appStatusUpdate(type: String) {
        val packInfo = packageUtil.getInstalledPackageInfo(appInfo.value?.packageName!!)
        viewModelScope.launch {
            val app: AppInfo
            val result = repository.selectAppInfoByPackageName(appInfo.value?.packageName!!)
            when (result.databaseStatus) {
                DatabaseStatus.SUCCESS -> {
                    app = result.data!!
                    when (type) {
                        "I" -> {
                            app.installDate = packInfo?.firstInstallTime.toString()
                            app.installStatus = "Y"
                        }
                        "U" -> {
                            app.updateStatus = "N"
                            app.updateDate = packInfo?.lastUpdateTime.toString()
                        }
                        "D" -> {
                            app.installStatus = "N"
                            app.installDate = ""
                            app.updateDate = ""
                            app.updateStatus = "N"
                            viewModelScope.launch {
                                repository.insertPushCount(PushCount(app.packageName))
                            }
                        }
                    }
                    val appResult = repository.appStatusChangeCallback(
                        RequestAppStatusChange(
                            RequestAppStatusChange.StatusChange(
                                preference.loadIdSavePreference()!!,
                                app.appId,
                                app.versionId,
                                type
                            )
                        )
                    )
                    if (appResult.networkStatus == NetworkStatus.ERROR && appResult.message == StringTAG.ERROR_CODE_401) {
                        showTokenExpireDialog()
                    }
                    if (repository.insertAppInfo(app).databaseStatus == DatabaseStatus.SUCCESS) {
                        _appStatusUpdateComplete.postValue(Event(true))
                    }
                }
            }
        }
    }

    fun writeReview(title: String, content: String, starPoint: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val review = RequestWriteReview.Review(appId = appInfo.value?.appId!!, subject = title, content = content, starPoint = starPoint)
            val result = repository.writeReview(RequestWriteReview(review))
            when (result.networkStatus) {
                NetworkStatus.SUCCESS -> {
                    _toastMsg.postValue("리뷰가 등록되었습니다.")
                    getReviewList(1)
                }
                NetworkStatus.ERROR -> {
                    if (result.message == StringTAG.ERROR_CODE_401) {
                        showTokenExpireDialog()
                    }
                }
            }
        }
    }

    fun showTokenExpireDialog() {
        _tokenExpireDialog.value = Event(true)
    }
}