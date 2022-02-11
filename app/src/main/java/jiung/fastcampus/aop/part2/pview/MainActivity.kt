package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    private val startButton: AppCompatButton by lazy{
        findViewById(R.id.startButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener {
            val intent = Intent(this, PersonalActivity::class.java)
            startActivity(intent)
        }
    }
}