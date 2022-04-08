package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.personalAge
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.personalSex
import jiung.fastcampus.aop.part2.pview.model.History

class PersonalActivity:AppCompatActivity() {
    private val clearPersonalInfoButton: Button by lazy{
        findViewById(R.id.clearPersonalInfoButton)
    }

    private  val sexSpinnerTextView: TextView by lazy {
        findViewById(R.id.personalInfoSexTextView)
    }

    private val sexSpinner: Spinner by lazy{
        findViewById(R.id.sexSpinner)
    }

    private  val ageSpinnerTextView: TextView by lazy {
        findViewById(R.id.personalInfoAgeTextView)
    }

    private val ageSpinner: Spinner by lazy{
        findViewById(R.id.ageSpinner)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)

        spinnerSet(sexSpinner, sexSpinnerTextView)
        spinnerSet(ageSpinner, ageSpinnerTextView)



        clearPersonalInfoButton.setOnClickListener {
            personalSex = sexSpinnerTextView.text.toString()
            personalAge = ageSpinnerTextView.text.toString()

            setAppdataBase()
            startActivity(Intent(this, MainActivity::class.java))
        }



    }

    private fun spinnerSet(spinner: Spinner, textView: TextView){
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                textView.text = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setAppdataBase() {
        MainActivity.db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
        Thread(Runnable {
            MainActivity.db.historyDao().insertHistory(History(null, personalSex, personalAge, MainActivity.recommendDataAry[0], MainActivity.recommendDataAry[1], MainActivity.recommendDataAry[2], MainActivity.recommendDataAry[3], MainActivity.recommendDataAry[4], MainActivity.recommendDataAry[5], MainActivity.recommendDataAry[6]))
        }).start()
    }
}