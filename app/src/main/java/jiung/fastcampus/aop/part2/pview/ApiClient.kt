package jiung.fastcampus.aop.part2.pview

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


object ApiClient {
    private const val BASE_URL = "http://118.67.131.29:5000/"
    private var retrofit: Retrofit? = null
    private var authToken: String? = " "

    fun setAuthToken(token: String){
        authToken = token
    }

    fun getAuthToken(): String {
        return "$authToken"
    }

    fun getApiClient(): Retrofit {

        //The gson builder
        val gson = GsonBuilder()
            .setLenient()
            .create()

        if (retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(provideOkHttpClient(AppInterceptor()))
                .build()
        }


        return retrofit!!
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient
            = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        build()
    }

    class AppInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", authToken)
                .build()
            return chain.proceed(newRequest)
        }
    }
}