package jiung.fastcampus.aop.part2.pview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayout

@Suppress("DEPRECATION")
class BodyActivity:AppCompatActivity() {

    private val startCustomCosmeticsButton:AppCompatButton by lazy {
        findViewById(R.id.startCustomCosmeticsButton)
    }

    private val myPageTableLayout: TableLayout by lazy {
        findViewById(R.id.myPageTableLayout)
    }

    private val seekBar1: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar1)
    }

    private val seekBar2: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar2)
    }

    private val seekBar3: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar3)
    }

    private val seekBar4: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar4)
    }

    private val seekBar5: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar5)
    }

    private val seekBar6: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar6)
    }

    private val myPagePersonalInfoTextView: TextView by lazy {
        findViewById(R.id.myPagePersonalInfoTextView)
    }

    private val myPageTabLayout: TabLayout by lazy{
        findViewById(R.id.myPageTabLayout)
    }

    private val loadPictureButton: AppCompatButton by lazy{
        findViewById(R.id.loadPictureButton)
    }

    private val startPictureButton: AppCompatButton by lazy{
        findViewById(R.id.startPictureButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body)

        setPersonalInfo()
        setInitialComponents()

    }

    private fun setPersonalInfo(){
        val personalSex: String? = intent.getStringExtra("sex")
        val personalAge: Int? = intent.getStringExtra("age")?.toInt()

        myPagePersonalInfoTextView.text = "$personalSex , ${personalAge}세"
    }

    private fun setInitialComponents(){
        setTab()
        setPictureButton()
    }

    private fun setTab(){
        myPageTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.text){
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

    private fun setPictureButton(){
        loadPictureButton.setOnClickListener{
            when{
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED-> {
                    //권한이 잘 부여 되었을 때 갤러리에서 사진을 선택하는 기능
                    navigatePhoto()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //교육용 팝업 확인 후 권한 팝업을 띄우는 기능
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                }
            }
        }
        startPictureButton.setOnClickListener {
            val intent = Intent(this, PictureActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showPermissionContextPopup(){
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해서는 권한이 필요합니다.")
            .setPositiveButton("동의하기"){_, _->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기"){_, _-> }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
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
                    setSeekBar()
                } else {
                    Toast.makeText(this, "사진을 가져오지 못 했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못 했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSeekBar(){
        5.also { seekBar1.progress = it }
        4.also { seekBar2.progress = it }
        1.also { seekBar3.progress = it }
        2.also { seekBar4.progress = it }
        0.also { seekBar5.progress = it }
        2.also { seekBar6.progress = it }
        true.also { myPageTableLayout.isVisible = it }
        true.also { startCustomCosmeticsButton.isVisible = it }
    }
}