package coroutines.repositories

import coroutines.obsercable.CoroutineObserver
import enums.Status
import kotlinx.coroutines.delay
import user.User
import wallet.Wallet
import kotlin.random.Random

class UserRepository {
    private val users: MutableList<User> = mutableListOf()

    companion object {
        val DEFAULT_USERS = listOf(
            User("Email1", "Name1"),
            User("Email2", "Name2")
        )
    }

    suspend fun init() {
        repeat(100) {
            val randomUser = DEFAULT_USERS.random()
            val randomDelay = Random.nextLong(100, 500)
            delay(randomDelay)
            users.add(randomUser)
            println("user $users.size")
        }
    }

    suspend fun saveUser(user: User): User {
        users.add(user)
        delay(Random.nextLong(100, 500))
        return user
    }

    private val userObservers = mutableListOf<CoroutineObserver>()


    fun addObserver(observer: CoroutineObserver) {
        userObservers.add(observer)
    }


    private fun notifyObservers(wallet: Wallet) {
        userObservers.forEach { it.notify(wallet.user) }
    }

    fun changeUserStatus(wallet: Wallet) {
        wallet.user.status = Status.APPROVED
        notifyObservers(wallet)
    }
}