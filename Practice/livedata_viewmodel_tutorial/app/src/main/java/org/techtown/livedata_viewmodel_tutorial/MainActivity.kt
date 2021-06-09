package org.techtown.livedata_viewmodel_tutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import org.techtown.livedata_viewmodel_tutorial.databinding.ActivityMainBinding


// 메인 엑티비티
class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG: String = "로그"
    }

    lateinit var myNumberViewModel: MyNumberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        // 자동으로 완성된 엑티비티 메인 바인딩 클래스 인스턴스를 가져옴.
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        // 뷰 바인딩과 연결
        setContentView(activityMainBinding.root)

        myNumberViewModel = ViewModelProvider(this).get(MyNumberViewModel::class.java)

        myNumberViewModel.currentValue.observe(this, Observer {
            Log.d(TAG, "MainActivity - myNuberViewModel - currentValue  라이브 데이터 값 변경 : $it")
            number_TextView.text = it.toString()
        })
        plusbutton.setOnClickListener(this)
        minusbutton.setOnClickListener(this)

    }
        //클릭 이벤트
    override fun onClick(v: View?) {
        val userInput = userinput_editText.text.toString().toInt()

            // 뷰모델에 라이브데이터 값을 변경하는 메소드
            when(v) {
                plusbutton ->
                    myNumberViewModel.updateValue(actionType = ActionType.PLUS, userInput)
                minusbutton ->
                    myNumberViewModel.updateValue(actionType = ActionType.MINUS, userInput)
            }

    }
}