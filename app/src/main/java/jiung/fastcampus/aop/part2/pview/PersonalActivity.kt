package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

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
            val intent = Intent(this, BodyActivity::class.java)
            intent
                .putExtra("sex", sexSpinnerTextView.text)
                .putExtra("age", ageSpinnerTextView.text)

            startActivity(intent)
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
}