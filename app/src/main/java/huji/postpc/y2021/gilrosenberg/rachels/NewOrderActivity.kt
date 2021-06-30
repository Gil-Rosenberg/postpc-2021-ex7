package huji.postpc.y2021.gilrosenberg.rachels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NewOrderActivity : AppCompatActivity() {

    private lateinit var dataBase : DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBase = this.applicationContext as DataBase
        setContentView(R.layout.new_order)

        //finds views
        val costumerName: TextView = findViewById(R.id.name)
        val pickles: TextView = findViewById(R.id.howMuchPichles)
        val plus: TextView = findViewById(R.id.plus)
        val minus: TextView = findViewById(R.id.minus)
        val tahini: SwitchCompat = findViewById(R.id.tahini)
        val hummus: SwitchCompat = findViewById(R.id.hummus)
        val comments : EditText = findViewById(R.id.comments)
        val saveButton : FloatingActionButton = findViewById(R.id.sendOrder)

        //set view
        minus.visibility = View.GONE

        //set the name if he already order
        if (dataBase.costumerName != ""){
            costumerName.text = dataBase.costumerName
        }

        //listener to the save button
        saveButton.setOnClickListener{
            updateOrder(pickles, tahini, hummus, comments, costumerName)
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }

        plus.setOnClickListener {
            try {
                var numPickles : Int = pickles.text.toString().toInt()
                numPickles++
                plus.isEnabled = numPickles < 10
                if (numPickles >= 10){
                    plus.visibility = View.GONE
                }
                else{
                    minus.visibility = View.VISIBLE
                }
                pickles.text = numPickles.toString()
            } catch (e : Exception){
                Toast.makeText(this, "Invalid pickels number", Toast.LENGTH_SHORT).show()
            }
        }

        minus.setOnClickListener {
            try {
                var numPickles : Int = pickles.text.toString().toInt()
                numPickles--
                minus.isEnabled = numPickles > 0
                if (numPickles <= 0){
                    minus.visibility = View.GONE
                }
                else{
                    minus.visibility = View.VISIBLE
                }
                pickles.text = numPickles.toString()
            } catch (e : Exception){
                Toast.makeText(this, "Invalid pickles number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * This function updates all fields of the order
     */
    private fun updateOrder(
        howMuchPickles: TextView,
        tahini: SwitchCompat,
        hummus: SwitchCompat,
        comments: EditText,
        costumerName: TextView
    ) {
        val order = Order()
        order.pickles = howMuchPickles.text.toString().toInt()
        order.tahini = tahini.isChecked
        order.hummus = hummus.isChecked
        order.comment = comments.text.toString()
        order.name = costumerName.text.toString()
        dataBase.uploadOrUpdate(order)
    }
}