package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

@Suppress("DEPRECATION")
class StartAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_app)

        Handler().postDelayed({ startActivity(Intent(this, LoginActivity::class.java)) }, 2000L)
    }
}