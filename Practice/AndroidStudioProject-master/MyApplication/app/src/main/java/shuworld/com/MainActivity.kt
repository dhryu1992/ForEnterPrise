package shuworld.com

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import shuworld.com.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mbinding: ActivityMainBinding? = null
    private val binding get() = mbinding

    override fun onCreate(savedInstanceState: Bundle?) { // 앱이 최초로 실행되었을 때 수행.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // xml 화면 뷰를 연결
    }
}