package huji.postpc.y2021.gilrosenberg.rachels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

/**
 * This class is the source of truth for the app, and responsible of the connection to firebase.
 */
class DataBase : Application() {
    var id: String? = null
    var costumerName: String? = null
    private lateinit var firestore : FirebaseFirestore
    lateinit var sp: SharedPreferences
    private val ordersLiveDataMutable: MutableLiveData<Order> = MutableLiveData()
    val orderLiveData: LiveData<Order>
        get() = ordersLiveDataMutable

    override fun onCreate() {
        super.onCreate()
        sp = this.getSharedPreferences("order_data", Context.MODE_PRIVATE)
        FirebaseApp.initializeApp(this)
        firestore = FirebaseFirestore.getInstance()

        initializeFromSp()
        if (id!!.isNotEmpty()){
            firebaseObserver()
        }
    }

    /**
     * If order exists, this function takes the id of order from the sp and create a listener
     * to changes from firebase.
     */
    private fun initializeFromSp() {
        id = sp.getString("id", "").toString()
        costumerName = sp.getString("name", "").toString()
        if (id!!.isNotEmpty()){
            firestore.collection("orders").document(id.toString()).get().addOnSuccessListener { result ->
                ordersLiveDataMutable.value = result?.toObject(Order::class.java)
            }
        }
    }

    /**
     * This function upload/update order to the firebase and set the values in the live data and
     * the share preference
     */
    public fun uploadOrUpdate(order: Order){
        if (id.isNullOrEmpty()){
            id = order.id
            firebaseObserver()
        }
        if (costumerName.isNullOrEmpty()){
            costumerName = order.name
        }
        firestore.collection("orders").document(order.id).set(order).addOnSuccessListener {
            Toast.makeText(this, "Order updated", Toast.LENGTH_SHORT).show()
        }
        ordersLiveDataMutable.value = order
        val editor = sp.edit()
        editor.putString("id", id)
        editor.putString("name", order.name)
        editor.apply()
    }

    /**
     * create fire base observer on the order
     */
    private fun firebaseObserver() {
        id?.let {
            if(it.isNotEmpty()){
                firestore.collection("orders").document(it).addSnapshotListener { result: DocumentSnapshot?, e: FirebaseException? ->
                    if (result != null) {
                        ordersLiveDataMutable.value = result.toObject(Order::class.java)
                    }
                }
            }
        }
    }


    /**
     * this function delete order from the database ans set the values in live data an sp
     */
    fun deleteOrder(){
        if(id != null){
            Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show()
            val editor = sp.edit()
            editor.remove("id")
            editor.clear()
            editor.apply()
            id = null
            ordersLiveDataMutable.value = null
        }
    }
}
