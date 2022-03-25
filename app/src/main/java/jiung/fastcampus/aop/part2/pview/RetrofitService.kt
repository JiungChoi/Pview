package jiung.fastcampus.aop.part2.pview

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*


public interface RetrofitService {
    // Http Method 'GET / POST / PUT / DELETE / HEAD 중에서 무슨 작업인지'
    // @GET( 전체 URL 에서 URI 제외한 EndPoint )

    @Multipart
    @POST("/api/pview/skin")
    fun postSkinImg(
        @Part imageFile : MultipartBody.Part
    ) : Call<getResoponseDto>

    @GET("{location}")
    fun getPosts(
        @Path("location") post: String?
    ): Call<getTokenDto?>?

    @POST("api/auth/login/email")
    fun login(@Body user: JsonObject): Call<getTokenDto>

    @POST("/api/pview/testjson")
    fun test(): Call<getResoponseDto>




}