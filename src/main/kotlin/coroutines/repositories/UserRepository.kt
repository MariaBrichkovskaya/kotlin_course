package coroutines.repositories

import kotlinx.coroutines.delay
import user.User
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


}