package jiung.fastcampus.aop.part2.pview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity



class StartAppActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_app)


        startAppActivity = this
        Handler().postDelayed({ startActivity(Intent(this, LoginActivity::class.java)) }, 2000L)
    }
    companion object{
        internal var startAppActivity: Activity? = null
    }

}