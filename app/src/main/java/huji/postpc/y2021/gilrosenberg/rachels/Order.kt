package huji.postpc.y2021.gilrosenberg.rachels

import java.util.*

enum class Status {
    WAITING, INPROGRESS, READY, DONE
}

/**
 * data class represent an order
 */
data class Order(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var pickles: Int = 0,
    var hummus: Boolean = false,
    var tahini: Boolean = false,
    var comment: String = "",
    var status: Status = Status.WAITING
)