package jiung.fastcampus.aop.part2.pview

import android.util.Log
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import org.json.JSONObject


//class getResoponseDto(rank:String, skindata: JsonObject, recommand: JsonObject) {

class getResoponseDtoDetail(email:String, wrinkle:String, skin_tone:String, pore_detect:String, dead_skin:String, oilly:String, pih:String) {
    // @SerializedName()으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    // @SerializedName()로 변수명을 일치시켜주면 클래스 변수명이 달라도 알아서 매핑시켜줌

//    @SerializedName("wrinkle") private var wrinkle: String? = token
//    @SerializedName("skin_tone") private var skin_tone: String? = token
//    @SerializedName("pore_detect") private var pore_detect: String? = token
//    @SerializedName("dead_skin") private var dead_skin: String? = token
//    @SerializedName("oilly") private var oilly: String? = token
//    @SerializedName("pih") private var pih: String? = token

    @SerializedName("email") private var email: String? = email
    @SerializedName("wrinkle") private var wrinkle: String? = wrinkle
    @SerializedName("skin_tone") private var skin_tone: String? = skin_tone
    @SerializedName("pore_detect") private var pore_detect: String? = pore_detect
    @SerializedName("dead_skin") private var dead_skin: String? = dead_skin
    @SerializedName("oilly") private var oilly: String? = oilly
    @SerializedName("pih") private var pih: String? = pih

    // @SerializedName("skindata") private var skindata:JsonObject? = skindata
    // @SerializedName("recommand") private var recommand: JsonObject? = recommand

    override fun toString(): String {
        return "$email,$wrinkle,$skin_tone,$pore_detect,$dead_skin,$oilly,$pih"
    }
}