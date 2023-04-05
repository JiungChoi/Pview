package jiung.fastcampus.aop.part2.pview

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val startAppActivity : StartAppActivity = StartAppActivity.startAppActivity as StartAppActivity

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

        loginActivity = this
        startAppActivity.finish()

        findIdButton.paintFlags = findIdButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        findPwButton.paintFlags = findPwButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        LoginActivity.loginActivity as LoginActivity
        loginButton.setOnClickListener {
            logInToServer()
            Log.d("myTag Login", "Login 완료")
            Intent(this, MainActivity::class.java).also { startActivity(it) }
        }

    }

    // Login To Server && Get JWT
    private val id: String = "test@test.com"
    private val password: String = "test"
    private fun logInToServer() {
        //1. Json 문자열
        val user = ("{\"email\":\"${id}\","
                + "\"pw\":\"${password}\""
                + "}"
                )

        //2. Parser
        val jsonParser = JsonParser()


        //4. To JsonObject
        val jsonObj = jsonParser.parse(user) as JsonObject


        val apiClient = ApiClient

        val service = apiClient.getApiClient().create(RetrofitService::class.java)


        service.login(jsonObj).enqueue(object : Callback<getTokenDto> {
            override fun onResponse(call: Call<getTokenDto>, response: Response<getTokenDto>) {
                val result: getTokenDto? = response.body()

                apiClient.setAuthToken(result.toString())
                Log.d("myTag Login01", "successfully received token")
            }

            override fun onFailure(call: Call<getTokenDto>, t: Throwable) {
                Log.e("myTag Login02", "${t.localizedMessage}")
            }
        })
    }
    companion object{
        internal var loginActivity: Activity? = null
    }

}