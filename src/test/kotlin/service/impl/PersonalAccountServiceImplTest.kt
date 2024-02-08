package service.impl

import enums.Currency
import enums.Status
import exchange.Exchange
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.PersonalAccountService
import transaction.TradeTransaction
import user.User
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersonalAccountServiceImplTest {
    private val USER_EMAIL = "test@test.com"
    private val USER_FULL_NAME = "Test User"
    private val USER_STATUS = Status.APPROVED

    private lateinit var personalAccountService: PersonalAccountService
    private lateinit var user: User
    private lateinit var exchange: Exchange
    private lateinit var wallet: Wallet
    private lateinit var wallet2: Wallet

    @BeforeEach
    fun setUp() {
        user = User(USER_EMAIL, USER_FULL_NAME, USER_STATUS)
        exchange = Exchange("TestExchange")
        wallet = Wallet("TestWallet", "passphrase", user)
        wallet2 = Wallet("TestWallet2", "passphrase2", user)
        personalAccountService = PersonalAccountServiceImpl
    }

    @Test
    fun testGetBalance() {
        wallet.currencies[Currency.LITECOIN] = BigDecimal(100)
        wallet.currencies[Currency.BITCOIN] = BigDecimal(200)
        wallet2.currencies[Currency.LITECOIN] = BigDecimal(500)
        wallet2.currencies[Currency.ETHEREUM] = BigDecimal(300)
        val expected = mapOf(
            Currency.LITECOIN to BigDecimal(600),
            Currency.BITCOIN to BigDecimal(200),
            Currency.ETHEREUM to BigDecimal(300)
        )

        val actual = personalAccountService.getBalance(wallet, wallet2)

        assertEquals(expected, actual)
    }

    @Test
    fun testGetTransactionsForPeriod() {
        val transaction1 = TradeTransaction(
            wallet,
            Currency.BITCOIN,
            BigDecimal(50),
            wallet2,
            Currency.ETHEREUM,
            LocalDateTime.now().minusDays(2)
        )
        val transaction2 = TradeTransaction(
            wallet,
            Currency.LITECOIN,
            BigDecimal(70),
            wallet2,
            Currency.ETHEREUM,
            LocalDateTime.now().minusDays(1)
        )
        val transaction3 =
            TradeTransaction(wallet, Currency.ETHEREUM, BigDecimal(100), wallet2, Currency.BITCOIN, LocalDateTime.now())
        exchange.transactionHistory.addAll(listOf(transaction1, transaction2, transaction3))
        val from = LocalDateTime.now().minusDays(2)
        val to = LocalDateTime.now().plusDays(1)

        val transactions = personalAccountService.getTransactionsForPeriod(user, exchange, from, to)

        assertEquals(2, transactions.size)
        assertTrue(transactions.all { it.sender == wallet })
        assertTrue(transactions.all { it.date.isAfter(from) && it.date.isBefore(to) })
    }

    @Test
    fun testAddWallet() {
        personalAccountService.addWallet(user, wallet)

        assertEquals(2, user.wallets.size)
        assertTrue(user.wallets.contains(wallet))
    }

    @Test
    fun `init method should add default wallet`() {
        val user = User("qwerty", "qwerty")

        assertEquals(1, user.wallets.size)
    }

    @Test
    fun `test destructurization`() {
        val result = personalAccountService.destructurization(user)
        assertEquals("user with id ${user.id} have status APPROVED", result)
    }

    @Test
    fun `test getEmailInUpperCaseAndWithoutDomain`() {
        val modifiedUser = personalAccountService.getEmailInUpperCaseAndWithoutDomain(user)
        assertEquals("TEST", modifiedUser.email)
    }

    @Test
    fun `compare speed of sequence and stream`() {
        val list1 = createUsers()
        val list2 = createUsers()
        val timeOfSequenceOperation = getTimeOfSequenceOperation(list2)
        val timeOfSimpleListOperation = getTimeOfSimpleListOperation(list1)
        println(timeOfSimpleListOperation)
        println(timeOfSequenceOperation)

        /* когда количество пользователей огромное(например миллион), значительно быстрее справляется Sequence,
        но когда количество сравнительно небольшое (10 000), операции работают примерно за одно время
        (чаще всего быстрее работают операции над обычным списком),
        поэтому невозможно предугадать что будет быстрее*/
        assertTrue(timeOfSequenceOperation < timeOfSimpleListOperation)
    }

    private fun createUsers(): MutableList<User> {
        val list = mutableListOf<User>()
        for (i in 0..1000000) {
            list += User(USER_EMAIL, USER_FULL_NAME)
            if (i % 7 == 0) {
                list[i].status = Status.BLOCKED
            }
            if (i % 5 == 0) {
                list[i].email = "Abcde"
            }
        }
        return list
    }

    private fun getTimeOfSequenceOperation(list: MutableList<User>): Long {
        val sequence = list.asSequence()
        val startTimeSequence = System.currentTimeMillis()
        sequence.filter { it.status == Status.APPROVED }
            .map { it.fullName }
            .any { it.startsWith("A", ignoreCase = true) }
        val endTimeSequence = System.currentTimeMillis()
        return endTimeSequence - startTimeSequence
    }


    private fun getTimeOfSimpleListOperation(list: MutableList<User>): Long {
        val startTimeList = System.currentTimeMillis()
        list.filter { it.status == Status.APPROVED }
            .map { it.fullName }
            .any { it.startsWith("A", ignoreCase = true) }
        val endTimeList = System.currentTimeMillis()
        return endTimeList - startTimeList
    }
}
