package huji.postpc.y2021.gilrosenberg.rachels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer


class InProgressActivity : AppCompatActivity() {
    private lateinit var dataBase : DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBase = this.applicationContext as DataBase
        setContentView(R.layout.activity_in_progress)

        //set observer to the liveData
        dataBase.orderLiveData.observe(this, Observer { order: Order ->
            if (order.status == Status.READY){
                val intent = Intent(this, DoneActivity::class.java)
                startActivity(intent)
            }
        })
    }
}