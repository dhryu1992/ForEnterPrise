package com.awesome.appstore.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.awesome.appstore.event.NotificationEvent
import com.awesome.appstore.event.NotificationType.NotificationType
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LogoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode =ThreadMode.MAIN)
    fun <T> appExitEvent(notificationType: NotificationType, notificationEvent: NotificationEvent<T>){

        when(notificationType){
            NotificationType.APP_OGOUT-> logoutProcess()

        }
    }

    private fun logoutProcess() {
       Logger.d("logoutProcess")
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}