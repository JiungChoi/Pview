package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class LoginActivity : AppCompatActivity() {
    private val findIdButton: AppCompatButton by lazy{
        findViewById(R.id.findIdButton)
    }

    private val findPwButton: AppCompatButton by lazy{
        findViewById(R.id.findPwButton)
    }

    private val loginButton: AppCompatButton by lazy{
        findViewById(R.id.loginButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findIdButton.paintFlags = findIdButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        findPwButton.paintFlags = findPwButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        loginButton.setOnClickListener {
            Intent(this, MainActivity::class.java).also { startActivity(it) }
        }

    }

}