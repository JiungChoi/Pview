package jiung.fastcampus.aop.part2.pview

import android.util.Log
import com.google.gson.annotations.SerializedName


class getTokenDto(token: String) {
    // @SerializedName()으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    // @SerializedName()로 변수명을 일치시켜주면 클래스 변수명이 달라도 알아서 매핑시켜줌

    @SerializedName("Authorization") private var authorization: String? = token

    override fun toString(): String {
        return "${authorization?.split(" ")!!.last()}"
    }
}