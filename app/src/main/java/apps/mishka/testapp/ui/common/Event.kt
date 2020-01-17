package apps.mishka.testapp.ui.common

// Event is class that allow to ignore false LiveData's Observer invocations,
// that are happening during Fragment lifecycle
class Event<T>(private val content: T) {
    var isHandled = false
        private set

    fun getContentIfNotHandled():T?{
        if(isHandled){
            return null
        } else {
            isHandled = true
            return content
        }
    }

    fun peekContent(): T = content
}

fun <T> T.toEvent(): Event<T> {
    return Event(this)
}
