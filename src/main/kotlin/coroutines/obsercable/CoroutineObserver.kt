package coroutines.obsercable

import user.User


interface CoroutineObserver {
    fun notify(user: User)
}