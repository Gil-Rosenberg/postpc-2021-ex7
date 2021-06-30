package huji.postpc.y2021.gilrosenberg.rachels

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EditActivity : AppCompatActivity() {
    private lateinit var dataBase: DataBase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        dataBase = this.applicationContext as DataBase
        val order: Order? = dataBase.orderLiveData.value
        //finds views
        val hello: TextView = findViewById(R.id.hello)
        val plus: TextView = findViewById(R.id.plus)
        val minus: TextView = findViewById(R.id.minus)
        val progressView: TextView = findViewById(R.id.howMuchPichles)
        val tahini: SwitchCompat = findViewById(R.id.tahini)
        val hummus: SwitchCompat = findViewById(R.id.hummus)
        val commentContent : EditText = findViewById(R.id.comments)
        val deleteButton : FloatingActionButton = findViewById(R.id.delete)
        val saveButton : FloatingActionButton = findViewById(R.id.save)

        setViews(order, hello, tahini, hummus, commentContent, progressView, minus, plus)

        plus.setOnClickListener {
            if (order != null){
                try {
                    var numPickles : Int = order.pickles
                    numPickles++
                    plus.isEnabled = numPickles < 10
                    if (numPickles >= 10){
                        plus.visibility = View.GONE
                    }
                    else{
                        minus.visibility = View.VISIBLE
                    }
                    order.pickles = numPickles
                    progressView.text = order.pickles.toString()
                } catch (e : Exception){
                    Toast.makeText(this, "Invalid pickles number", Toast.LENGTH_SHORT).show()
                }
            }
        }

        minus.setOnClickListener {
            if (order != null){
                try {
                    var numPickles : Int = order.pickles.toInt()
                    numPickles--
                    plus.isEnabled = numPickles > 0
                    if (numPickles <= 0){
                        minus.visibility = View.GONE
                    }
                    else{
                        minus.visibility = View.VISIBLE
                    }
                    order.pickles = numPickles
                    progressView.text = order.pickles.toString()
                } catch (e : Exception){
                    Toast.makeText(this, "Invalid pickles number", Toast.LENGTH_SHORT).show()
                }
            }
        }

        deleteButton.setOnClickListener{
            dataBase.deleteOrder()
        }

        saveButton.setOnClickListener{
            if (order != null) {
                order.pickles = progressView.text.toString().toInt()
                order.tahini = tahini.isChecked
                order.hummus = hummus.isChecked
                order.comment = commentContent.text.toString()
                dataBase.uploadOrUpdate(order)
                Toast.makeText(this, "All changes have been saved", Toast.LENGTH_SHORT).show()
            }
        }

        dataBase.orderLiveData.observe(this, Observer { it->
            if (it == null){
                val intent = Intent(this, NewOrderActivity::class.java)
                startActivity(intent)
            }
            else if (it.status == Status.INPROGRESS){
                val intent = Intent(this, InProgressActivity::class.java)
                startActivity(intent)
            }
        })
    }

    /**
     * This function sets the views after order was changed
     */
    private fun setViews(
        order: Order?,
        hello: TextView,
        tahini: SwitchCompat,
        hummus: SwitchCompat,
        comments: EditText,
        howMuchPickles: TextView,
        minus: TextView,
        plus: TextView
    ) {
        val helloStr = "Hi " + (order?.name ?: "")
        hello.text = helloStr
        tahini.isChecked = order?.tahini ?: false
        hummus.isChecked = order?.hummus ?: false
        val comment = order?.comment
        comments.setText(comment)
        howMuchPickles.text = order?.pickles.toString()

        try {
            val numPickles: Int = howMuchPickles.text.toString().toInt()
            if (numPickles <= 0) {
                minus.visibility = View.GONE
            }
            if (numPickles >= 10) {
                plus.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.d("failed convert", "failed to convert string to int")
        }
    }
}