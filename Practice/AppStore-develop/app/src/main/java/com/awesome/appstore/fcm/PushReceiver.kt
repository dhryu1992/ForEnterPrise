package com.awesome.appstore.fcm

import android.content.Context
import android.content.Intent
import com.awesome.appstore.AppStoreApplication
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.repository.Repository
import com.awesomebly.push.lib.pushlistener.receiver.AwesomeReceiver
import com.awesomebly.pushlib.vo.PushMessage
import com.orhanobut.logger.Logger
import dagger.android.AndroidInjection
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class PushReceiver : AwesomeReceiver() {

    @Inject
    lateinit var repository: Repository

    lateinit var context: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        this.context = context!!
        super.onReceive(context, intent)
    }

    override fun pushReceiveListener(push: PushMessage) {
        super.pushReceiveListener(push)
        GlobalScope.launch {
            val appInfo = push.targetPkg?.let { repository.selectAppInfoByPackageName(it).data }
            // todo   지금 해당 앱이 실행중인지 체크하는 로직 필요
            Logger.d(appInfo)
            if (appInfo != null) {
                val pushCount = appInfo.packageName.let { repository.getPushCount(it).data?.apply { count += 1 } }
                pushCount?.let { repository.insertPushCount(it) }
                Intent().also { intent ->
                    intent.action = StringTAG.ACTION_APP_LIST_REFRESH
                    intent.setPackage(context.packageName)
                    context.sendBroadcast(intent)
                }
            }
        }
    }

    override fun registerFailListener(msg: String) {
        super.registerFailListener(msg)
        Logger.d(msg)
    }

    override fun tokenReceiveListener(token: String) {
        super.tokenReceiveListener(token)
        Logger.d(token)
    }
}

