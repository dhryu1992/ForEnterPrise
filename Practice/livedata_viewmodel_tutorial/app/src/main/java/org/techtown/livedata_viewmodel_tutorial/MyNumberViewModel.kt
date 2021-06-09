package org.techtown.livedata_viewmodel_tutorial

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class ActionType {
    PLUS, MINUS
}


// 데이터의 변경
// 뷰모델은 데이터의 변경사항을 알려주는 라이브 데이터를 가지고있고


class MyNumberViewModel : ViewModel() {
    // 뮤터블 라이브 데이터 - 수정가능한 녀석
    // 라이브 데이터 - 수정 불가.

    // 내부에서 설정하는 자료형은 뮤터블로
    // 변경 가능하도록 설정
    private val _currentValue = MutableLiveData<Int>()

    val currentValue: LiveData<Int>
        get() = _currentValue

    // 초기값 설정
    init {
        Log.d(TAG, "MyNumberViewModel - 생성자 호출 ")
        _currentValue.value = 0 // 일반적인 라이브데이터인 경우에는 값 설정 불가. 뮤터블이니 가능

    }

    fun updateValue(actionType: ActionType, input: Int) {
        when(actionType) {
            ActionType.PLUS ->
                _currentValue.value = _currentValue.value?.plus(input)

            ActionType.MINUS ->
                _currentValue.value = _currentValue.value?.minus(input)
        }
    }






}