package com.awesome.appstore.repository


import androidx.lifecycle.LiveData
import com.awesome.appstore.BuildConfig
import com.awesome.appstore.database.*
import com.awesome.appstore.network.NetworkHandler
import com.awesome.appstore.network.NetworkResult
import com.awesome.appstore.network.NetworkService
import com.awesome.appstore.protocol.request.*
import com.awesome.appstore.protocol.response.*
import com.orhanobut.logger.Logger
import java.lang.Exception


class Repository(
    private val networkService: NetworkService,
    private val storeDatabase: StoreDatabase,
    private val networkHandler: NetworkHandler,
    private val databaseHandler: DatabaseHandler
) {

    suspend fun getUserAuth(userId: String, password: String): NetworkResult<ResponseUserAuth?> {
        return try {
            val userAuth: RequestUserAuth.UserAuth = RequestUserAuth.UserAuth(userId, password)
            val requestUserAuth: RequestUserAuth = RequestUserAuth(userAuth)
            val response = networkService.getUserAuth(requestUserAuth)
            networkHandler.handleSuccess(response)
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }


    suspend fun insertNotice(notice: Notice): DatabaseResult<Long?> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.noticeDao().insert(notice))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun selectAllNoticeList(): DatabaseResult<List<Notice?>> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.noticeDao().getNoticeAllList())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }


    suspend fun getAppList(): NetworkResult<ResponseAppInfo?> {
        return try {
            val response =
                if (BuildConfig.IS_MOCK) networkService.getAppListMock()
                else networkService.getAppList(1, 1000)
            networkHandler.handleSuccess(response!!)
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun insertAppInfo(appInfo: AppInfo): DatabaseResult<Long?> {
        return try {
            if (getPushCount(appInfo.packageName).data == null) {
                insertPushCount(PushCount(appInfo.packageName))
            }
            databaseHandler.handleSuccess(storeDatabase.appInfoDao().insertAppInfo(appInfo))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun selectAllAppInfo(): DatabaseResult<List<AppInfo?>> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.appInfoDao().allAppInfo())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun selectInstalledAppInfo(): DatabaseResult<List<AppInfo?>> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.appInfoDao().installedAppInfo())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun selectEssentialAppInfo(): DatabaseResult<List<AppInfo?>> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.appInfoDao().essentialAppInfo())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun selectUpdateAppInfo(): DatabaseResult<List<AppInfo?>> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.appInfoDao().updateAppInfo())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun selectAllLauncherIndex(): DatabaseResult<List<LauncherIndex?>> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.launcherIndexDao().getIndexAll())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun insertLauncherIndex(index: LauncherIndex) {
        try {
            storeDatabase.launcherIndexDao().insert(index)
        } catch (exception: Exception) {
            databaseHandler.handleException<Exception>(exception)
        }
    }

    suspend fun deleteLauncherIndexByPkgName(pkgName: String) {
        try {
            storeDatabase.launcherIndexDao().deleteByPkgName(pkgName)
        } catch (exception: Exception) {
            databaseHandler.handleException<Exception>(exception)
        }
    }

    suspend fun getAppDetail(appId: Long): NetworkResult<ResponseAppDetail?> {
        return try {
            val response =
                if (BuildConfig.IS_MOCK) networkService.getAppDetailContentsMock()
                else networkService.getAppDetailContents(appId)
            networkHandler.handleSuccess(response!!)
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun getNoticeList(page: Int, pageSize: Int): NetworkResult<ResponseNotice?> {
        return try {
            val response =
                if (BuildConfig.IS_MOCK) networkService.getNoticeListMock()
                else networkService.getNoticeList("reg_dt", page, pageSize)
            networkHandler.handleSuccess(response!!)
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun selectAppInfoByPackageName(pkgName: String): DatabaseResult<AppInfo> {
        return try {
            databaseHandler.handleSuccess(
                storeDatabase.appInfoDao().selectAppInfoByPackageName(pkgName)
            )
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun deleteAppInfo(appInfo: AppInfo) {
        try {
            databaseHandler.handleSuccess(
                storeDatabase.appInfoDao().deleteAppInfo(appInfo)
            )
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun appStatusChangeCallback(appStatusChange: RequestAppStatusChange): NetworkResult<Common> {
        return try {
            networkHandler.handleSuccess(
                networkService.appStatusChange(appStatusChange)
            )
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun getReviewList(page: Int, pageSize: Int, appId: Long): NetworkResult<ResponseReview> {
        return try {
            val result = if (BuildConfig.IS_MOCK) networkService.getReviewListMock()
            else networkService.getReviewList(page, pageSize, appId)
            networkHandler.handleSuccess(result!!)
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun updateNoticeReadState(request: RequestNoticeReadStateChange): NetworkResult<Common> {
        return try {
            networkHandler.handleSuccess(
                networkService.updateNoticeReadState(request)
            )
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun getErrorLog(): DatabaseResult<List<ErrorLog?>> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.errorLogDao().getErrorLogAllList())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun deleteAllErrorLog(): DatabaseResult<Unit> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.errorLogDao().deleteAllErrorLog())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun deleteErrorLog(err: ErrorLog): DatabaseResult<Unit> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.errorLogDao().deleteErrorLog(err))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun insertErrorLog(err: ErrorLog): DatabaseResult<Unit> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.errorLogDao().insert(err))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun writeReview(review: RequestWriteReview): NetworkResult<Common> {
        return try {
            networkHandler.handleSuccess(networkService.writeReview(review))
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun deleteReview(reviewId: Int): NetworkResult<Common> {
        return try {
            networkHandler.handleSuccess(networkService.deleteReview(reviewId))
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun editReview(review: RequestEditReview): NetworkResult<Common> {
        return try {
            networkHandler.handleSuccess(networkService.editReview(review))
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun updateUserSetting(setting: RequestSetting): NetworkResult<Common> {
        return try {
            networkHandler.handleSuccess(networkService.updateSetting(setting))
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun getSettings(): NetworkResult<ResponseSetting> {
        return try {
            networkHandler.handleSuccess(networkService.getSettings())
        } catch (exception: Exception) {
            networkHandler.handleException(exception)
        }
    }

    suspend fun getAllPushCount(): DatabaseResult<List<PushCount?>> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.pushCountDao().getAllPushCount())
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun getPushCount(pkgName: String): DatabaseResult<PushCount?> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.pushCountDao().getPushCount(pkgName))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

    suspend fun insertPushCount(pushCount: PushCount): DatabaseResult<Long> {
        return try {
            databaseHandler.handleSuccess(storeDatabase.pushCountDao().insert(pushCount))
        } catch (exception: Exception) {
            databaseHandler.handleException(exception)
        }
    }

}