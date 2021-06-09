package com.awesome.appstore.event

import com.awesome.appstore.event.NotificationType.NotificationType

data class NotificationEvent<T>(val notificationType: NotificationType, val contents:T) {
}