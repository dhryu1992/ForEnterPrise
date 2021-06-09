package com.awesomebly.template.android.ui.main.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesomebly.template.android.R
import com.awesomebly.template.android.database.entity.TpEntity
import com.awesomebly.template.android.network.handler.NetworkStatus
import com.awesomebly.template.android.network.response.ResponsePosts
import com.awesomebly.template.android.repository.DatabaseRepository
import com.awesomebly.template.android.repository.NetworkRepository
import com.awesomebly.template.android.ui.base.BaseViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Company : Awesomebly (http://www.awesomebly.com)
 * Project : Awesomebly Android Template Project
 * Created by 차태준
 * FileName : MainFragmentViewModel
 * Date : 2021-05-04, 오전 10:31
 * History
seq   date          contents      programmer
01.   2021-05-03    init            차태준
02.
03.
 */
@HiltViewModel
class SecondFragmentViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : BaseViewModel() {

    private val _post = MutableLiveData<ResponsePosts>()
    val post: LiveData<ResponsePosts> = _post
    /**
     * Get post
     * Api 샘플 서버로 부터 해당 아이디 값에 맞는 json 데이터를 수신해 post에 할당한다.
     * 해당 샘플 데이터의 id 범위는 1~100
     * @param id
     * 100을 초과할 경우 1..100 사이의 랜덤 값으로 전환
     */
    fun getPost(id: Int) {
        viewModelScope.launch {
            val postId = if (id > 100) (1..100).random() else id
            networkRepository.getPostById(postId.toLong()).let {
                when (it.networkStatus) {
                    NetworkStatus.SUCCESS -> it.data?.let { post ->
                        Logger.d(post)
                        _post.value = (post)
                    }
                    NetworkStatus.ERROR ->
                        Toast.makeText(
                            appContext,
                            appContext.getText(R.string.toast_msg_net_error),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }
    }
}