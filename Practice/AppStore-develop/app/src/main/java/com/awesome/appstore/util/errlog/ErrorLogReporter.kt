package com.awesome.appstore.util.errlog

import android.content.Context
import com.awesome.appstore.config.StoreConfig
import com.awesome.appstore.database.ErrorLog
import com.awesome.appstore.database.StoreDatabase
import com.google.auto.service.AutoService
import com.orhanobut.logger.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.acra.ReportField
import org.acra.config.CoreConfiguration
import org.acra.config.ReportingAdministrator
import org.acra.data.CrashReportData
import org.json.JSONException
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@AutoService(ReportingAdministrator::class)
class ErrorLogReporter : ReportingAdministrator {

    override fun shouldSendReport(
        context: Context,
        config: CoreConfiguration,
        crashReportData: CrashReportData
    ): Boolean {
        Logger.d("error report sender create")
        val reportId =
            if (crashReportData.getString(ReportField.valueOf("REPORT_ID")) != null) crashReportData.getString(
                ReportField.valueOf("REPORT_ID")
            ) else StoreConfig.ERROR_LOG_FAIL
        val errorLog = ErrorLog( //기본 생성 에러로그의 해쉬 값을 아이디로 사용하나 로그정부 불러오기 실패시 에러 발생 시간으로 대체
            reportId,
            crashReportData.getString(ReportField.valueOf("APP_VERSION_CODE")),
            crashReportData.getString(ReportField.valueOf("ANDROID_VERSION")),
            crashReportData.getString(ReportField.valueOf("PHONE_MODEL")),
            crashReportData.getString(ReportField.valueOf("USER_CRASH_DATE")),
            crashReportData.getString(ReportField.valueOf("STACK_TRACE")),
            crashReportData.getString(ReportField.valueOf("FILE_PATH"))
        )
        Logger.d(errorLog)
        try {
            val storeDatabase: StoreDatabase = StoreDatabase.getInstance(context)
            GlobalScope.launch {
                storeDatabase.errorLogDao().insert(errorLog)
                Logger.d("ERROR LOG SAVE")
                val io = ErrorLogFileIo()
                io.writeErrLog(reportId, crashReportData.toJSON(), context)
                val readFromFile = io.readErrLog(reportId, context)
                Logger.d("readFileSuccess$readFromFile")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return true
    }
}