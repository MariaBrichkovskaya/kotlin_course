package coroutines

import coroutines.repositories.UserRepository
import coroutines.repositories.WalletRepository
import enums.Status
import exception.CoroutineException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
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

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Exception: $exception")
    }

    private suspend fun saveUserAndWallet(user: User, wallet: Wallet): Pair<User, Wallet> {
        val userDeferred =
            withContext(NonCancellable + exceptionHandler)

            {
                delay(1000)
                println(user)
                userRepository.saveUser(user)
            }

        val walletDeferred =
            withContext(NonCancellable + exceptionHandler)
            {
                val num = Random.nextInt(0..50)
                require(num in 0..25) {
                    throw CoroutineException("Exception was thrown")
                }
                delay(1000)
                println(wallet)
                walletRepository.saveWallet(wallet)
            }
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


    @Test
    fun `test parallel tasks`() = runBlocking {
        var count = 0
        val userInit = coroutineScope.launch(Dispatchers.Unconfined) { userRepository.init() }
        val walletInit = launch {
            walletRepository.init()

        }
        val job1 = coroutineScope.launch {
            while (count < 2) {
                try {
                    val wallets = walletRepository.getWallets(count * 50, 50 * (count + 1))
                    count++
                    for (wallet in wallets) {
                        if (wallet.currencies.size > 2) {
                            val user = wallet.user
                            user.status = Status.APPROVED
                            println("job ${wallet.id}")
                        }
                    }
                } catch (e: IndexOutOfBoundsException) {
                    println("я жду")
                    delay(1000)
                }
            }
        }

        job1.join()
        userInit.join()
        walletInit.join()
    }

}