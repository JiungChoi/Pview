package jiung.fastcampus.aop.part2.pview

import android.util.Log
import com.google.gson.annotations.SerializedName


class getResoponseDto(token: String) {
    // @SerializedName()으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    // @SerializedName()로 변수명을 일치시켜주면 클래스 변수명이 달라도 알아서 매핑시켜줌

    @SerializedName("wrinkle") private var wrinkle: String? = token
    @SerializedName("skin_tone") private var skin_tone: String? = token
    @SerializedName("pore_detect") private var pore_detect: String? = token
    @SerializedName("dead_skin") private var dead_skin: String? = token
    @SerializedName("oilly") private var oilly: String? = token
    @SerializedName("pih") private var pih: String? = token


    override fun toString(): String {

        return "wrinkle : $wrinkle\n" +
                "skin_tone$skin_tone\n" +
                "pore_detect$pore_detect\n" +
                "dead_skin$dead_skin\n" +
                "oilly$oilly\n" +
                "pih$pih"
    }
}