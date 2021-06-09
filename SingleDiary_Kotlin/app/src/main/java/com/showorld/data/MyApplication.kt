package com.showorld.data

import android.app.Application
import android.content.ContentValues.TAG
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MyApplication : Application() {
    val TAG = "MyApplication"
    lateinit var requestQueue: RequestQueue

    override fun onCreate() {

        super.onCreate()

        if (requestQueue == null) {
            requestQueue =
                Volley.newRequestQueue(applicationContext, object : HurlStack() {
                    @Throws(IOException::class)
                    protected override fun createConnection(url: URL?): HttpURLConnection? {
                        val connection: HttpURLConnection = super.createConnection(url)
                        connection.instanceFollowRedirects = false
                        return connection
                    }
                })
        }
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    interface OnResponseListener {
        fun processResponse(requestCode: Int, responseCode: Int, response: String?)
    }

    fun send(
        requestCode: Int, requestMethod: Int, url: String, params: Map<String, String>,
        listener: OnResponseListener?
    ) {
        val request: StringRequest = object : StringRequest(
            requestMethod,
            url,
            Response.Listener { response ->
                Log.d(TAG, "Response for $requestCode -> $response")
                listener?.processResponse(requestCode, 200, response)
            },
            Response.ErrorListener { error ->
                Log.d(TAG, "Error for " + requestCode + " -> " + error.message)
                listener?.processResponse(requestCode, 400, error.message)
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return params
            }
        }
        request.setShouldCache(false)
        request.retryPolicy = DefaultRetryPolicy(
            10 * 1000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        if (requestQueue != null) {
            requestQueue.add<String>(request)
            Log.d(TAG, "Request sent : $requestCode")
            Log.d(TAG, "Request url : $url")
        } else {
            Log.d(TAG, "Request queue is null.")
        }
    }

}
