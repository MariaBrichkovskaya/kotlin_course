package coroutines

import coroutines.obsercable.CoroutineObserver
import coroutines.repositories.UserRepository
import coroutines.repositories.WalletRepository
import enums.Status
import exception.CoroutineException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import user.User
import wallet.Wallet
import kotlin.random.Random
import kotlin.random.nextInt


class CoroutinesTest {
    private val userRepository = UserRepository()
    private val walletRepository = WalletRepository()
    private val user = UserRepository.DEFAULT_USERS[0]
    private val wallet = WalletRepository.DEFAULT_WALLETS[0]
    private val supervisor = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Exception: $exception")
    }

    private suspend fun saveUser(user: User): User {
        return withContext(NonCancellable + exceptionHandler) {
            delay(1000)
            println(user)
            userRepository.saveUser(user)
        }
    }

    private suspend fun saveWallet(wallet: Wallet): Wallet {
        return withContext(NonCancellable + exceptionHandler) {
            val num = Random.nextInt(0..50)
            require(num in 0..25) {
                throw CoroutineException("Exception was thrown")
            }
            delay(1000)
            println(wallet)
            walletRepository.saveWallet(wallet)
        }
    }

    private suspend fun saveUserAndWallet(user: User, wallet: Wallet): Pair<User, Wallet> {
        val userDeferred = saveUser(user)
        val walletDeferred = saveWallet(wallet)
        userDeferred.wallets += walletDeferred
        return Pair(userDeferred, walletDeferred)
    }

    @Test
    fun testJobCancellation(): Unit = runBlocking {
        val job = coroutineScope.async {
            try {
                return@async saveUserAndWallet(user, wallet)
            } catch (e: CancellationException) {
                println(e.message)
            } catch (e: CoroutineException) {
                println(e.message)
            }
        }

        job.start()
        delay(1000)
        //job.cancel()
        //coroutineScope.cancel()
        coroutineContext.cancelChildren()
        println(job.await())
    }

    private fun processWallets(count: Int) {
        val wallets = walletRepository.getWallets(count * 50, 50 * (count + 1))
        for (wallet in wallets) {
            if (wallet.currencies.size > 2) {
                val user = wallet.user
                user.status = Status.APPROVED
                userRepository.changeUserStatus(wallet)
            }
        }
    }

    private suspend fun job1Logic() = runBlocking {
        launch(Dispatchers.Unconfined + supervisor) {
            var count = 0
            if (Random.nextInt(0..50) < 30) {
                throw RuntimeException("Exception occurred in Job 1")
            }
            while (count < 2) {
                try {

                    processWallets(count)
                    count++
                } catch (e: IndexOutOfBoundsException) {
                    println("I am waiting")
                    delay(1000)
                }
            }
        }

    }

    private fun job2Logic() = runBlocking {
        launch(Dispatchers.Unconfined + supervisor) {
            ensureActive()
            val observer = object : CoroutineObserver {
                override fun notify(user: User) {
                    println("Status was changed to ${user.status} for $user")
                }
            }
            println("job2 still alive")
            userRepository.addObserver(observer)
        }

    }

    @Test
    fun testParallelTasks(): Unit = runBlocking {
        val userInit = launch(Dispatchers.IO) { userRepository.init() }
        val walletInit = launch(Dispatchers.IO) { walletRepository.init() }
        val job1 = job1Logic()
        val job2 = job2Logic()

        job1.join()
        userInit.join()
        walletInit.join()
        job2.join()
    }


}