package jiung.fastcampus.aop.part2.pview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayout
import jiung.fastcampus.aop.part2.pview.extensions.loadCenterCrop
import java.util.*
import java.util.Random as Random

class ReferenceActivity : AppCompatActivity() {

    private val myPagePreviewImageView: ImageView by lazy {
        findViewById(R.id.myPagePreviewImageView)
    }

    private val myPagePersonalSkinLankTextView: TextView by lazy {
        findViewById(R.id.myPagePersonalSkinLank)
    }

    private val myPagePersonalSkinAgeTextView: TextView by lazy {
        findViewById(R.id.myPagePersonalSkinAge)
    }

    private val myPageScrollView: ScrollView by lazy {
        findViewById(R.id.myPageScrollView)
    }

    private val seekBar1: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar1)
    }

    private val skinLankTextView1: TextView by lazy {
        findViewById(R.id.skinLankTextView1)
    }

    private val seekBar2: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar2)
    }

    private val skinLankTextView2: TextView by lazy {
        findViewById(R.id.skinLankTextView2)
    }

    private val seekBar3: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar3)
    }

    private val skinLankTextView3: TextView by lazy {
        findViewById(R.id.skinLankTextView3)
    }

    private val seekBar4: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar4)
    }

    private val skinLankTextView4: TextView by lazy {
        findViewById(R.id.skinLankTextView4)
    }

    private val seekBar5: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar5)
    }

    private val skinLankTextView5: TextView by lazy {
        findViewById(R.id.skinLankTextView5)
    }

    private val seekBar6: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar6)
    }

    private val skinLankTextView6: TextView by lazy {
        findViewById(R.id.skinLankTextView6)
    }

    private val myPagePersonalInfoTextView: TextView by lazy {
        findViewById(R.id.myPagePersonalInfoTextView)
    }

    private val myPageTabLayout: TabLayout by lazy {
        findViewById(R.id.myPageTabLayout)
    }

    private val loadPictureButton: AppCompatButton by lazy {
        findViewById(R.id.loadPictureButton)
    }

    private val startPictureButton: AppCompatButton by lazy {
        findViewById(R.id.startPictureButton)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setPersonalInfo()
        setInitialComponents()

    }

    private var personalSex: String? = null
    private var personalAge: Int? = null
    private var personalSkinLank: Int? = null
    private var personalSkinAge: Int? = null

    private fun setPersonalInfo() {
        intent.getStringExtra("url").also {
            if (it != null) {
                myPagePreviewImageView.loadCenterCrop(url = it.toString(), corner = 4f) // 선택한 이미지 url 불러와서 이미지뷰 이미지를 바꿈
                skinCareResult()
            } else {

            }
        }

        intent.getStringExtra("sex").also {
            if (it != null) {
                this.personalSex = it
            }
        }

        intent.getStringExtra("age").also {
            if (it != null) {
                this.personalAge = it.toInt()
            }
        }

        changeSkinPicture()
    }

    private fun changeSkinPicture(){
        personalSkinLank = getRandomNumber(100)
        personalSkinAge = when (personalSkinLank?.div(10)) {
            0 -> this.personalAge?.minus(5)
            1 -> this.personalAge?.minus(4)
            2 -> this.personalAge?.minus(3)
            3 -> this.personalAge?.minus(1)

            5 -> this.personalAge?.plus(2)
            6 -> this.personalAge?.plus(3)
            7 -> this.personalAge?.plus(4)
            8 -> this.personalAge?.plus(5)
            9 -> this.personalAge?.plus(8)
            10 -> this.personalAge?.plus(10)
            else -> this.personalAge
        }

        myPagePersonalInfoTextView.text = "${this.personalSex} , ${this.personalAge}세"
        myPagePersonalSkinLankTextView.text = "상위 $personalSkinLank%"
        myPagePersonalSkinAgeTextView.text = "피부 나이 만 ${personalSkinAge}세"
    }

    private fun setInitialComponents() {
        setTab()
        setPictureButton()
    }

    private fun setTab() {
        myPageTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "얼굴" -> {

                    }

                    "바디" -> {

                    }

                    "손" -> {

                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun setPictureButton() {
        loadPictureButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //권한이 잘 부여 되었을 때 갤러리에서 사진을 선택하는 기능
                    navigatePhoto()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //교육용 팝업 확인 후 권한 팝업을 띄우는 기능
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
        }

        startPictureButton.setOnClickListener {
            val intent = Intent(this, PictureActivity::class.java)
            intent
                .putExtra("sex", personalSex)
                .putExtra("age", personalAge.toString())
            startActivity(intent)
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해서는 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 부여 된 것입니다.
                    navigatePhoto()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                //
            }
        }
    }

    private fun navigatePhoto() {
        val intent =
            Intent(Intent.ACTION_GET_CONTENT) //ACTION_GET_CONTENT 스트링은 이 인덴트는 SAF 기능으로 안드로이드 내장해 있는 액티비티 실행
        intent.type = "image/*" // png jpg 등등 이미지 파일 필터링
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            // 정상적으로 데이터를 받아오지 못 했을 때
            return
        }

        when (requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    myPagePreviewImageView.loadCenterCrop(url = selectedImageUri.toString(), corner = 4f)
                    changeSkinPicture()
                    skinCareResult()
                } else {
                    Toast.makeText(this, "사진을 가져오지 못 했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못 했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun skinCareResult(){
        //TODO 결과가 나왔을 때 이미지뷰 옆에 동나이 기준 순위를 알려주는 기능 : personalSkinAge
        setLankOfPeer()
        setSeekBar() //seekBar를 personalSkinAge에 맞게 조절하고(반 랜덤) 컴포넌트 리스너를 등록하는 과정

    }

    private fun setLankOfPeer(){


        when(personalSkinAge){
        }
    }


    /*
    seekBar1 : 피부톤 -> 랜덤
    seekBar2 : 모공 -> 피부나이 && 랜덤
    seekBar3 : 각질 -> 피부나이
    seekBar4 : 색소침착 -> 피부나이
    seekBar5 : 주름 -> 피부나이
    seekBar6 : 유분 -> 랜덤
    */

    private fun setSeekBar() {
        // seekBar1 : 피부톤은 0~100 사이의 랜덤
        Random().nextInt(100).also {
            seekBar1.progress = it.div(10)
            skinLankTextView1.text = "상위 ${it.toString()}%"
        }

        seekBar1.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        seekBar2.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        seekBar3.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        seekBar4.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        seekBar5.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        seekBar6.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar?.progress != null) {
                        when {
                            seekBar.progress > 7 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_100)
                            }
                            seekBar.progress > 4 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_80)
                            }
                            seekBar.progress > 2 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_50)
                            }
                            seekBar.progress > 0 -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_30)
                            }
                            else -> {
                                seekBar.thumb = getDrawable(R.drawable.ic_thumb_arrow_10)
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        5.also { seekBar1.progress = it }
        4.also { seekBar2.progress = it }
        1.also { seekBar3.progress = it }
        2.also { seekBar4.progress = it }
        0.also { seekBar5.progress = it }
        2.also { seekBar6.progress = it }
        true.also { myPageScrollView.isVisible = it }
    }

    private fun getRandomNumber(num: Int): Int {
        return Random().nextInt(num)
    }

    companion object {

    }


}