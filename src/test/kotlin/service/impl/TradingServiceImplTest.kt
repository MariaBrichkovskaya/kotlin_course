package service.impl

import enums.Currency
import enums.Status
import exception.BalanceException
import exception.InvalidUserStatusException
import exception.NoSuchCurrencyException
import exception.PassphraseMismatchException
import exchange.Exchange
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import transaction.SwapTransaction
import transaction.TradeTransaction
import user.User
import wallet.Wallet
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TradingServiceImplTest {
    private lateinit var tradingService: TradingServiceImpl
    private val senderUser = User("sender@example.com", "Sender", Status.APPROVED)
    private val receiverUser = User("receiver@example.com", "Receiver", Status.APPROVED)
    private val senderWallet = Wallet("Sender", "passphrase", senderUser)
    private val secondSenderWallet = Wallet("qwerty", "qwerty", senderUser)
    private val thirdSenderWallet = Wallet("qwerty", "qwerty", senderUser)
    private val receiverWallet = Wallet("Receiver", "passphrase", receiverUser)
    private val exchange = Exchange("TestExchange")
    private val fromCurrency = Currency.LITECOIN
    private val toCurrency = Currency.BITCOIN
    private val firstImplementation = FirstImplementation()
    private val secondImplementation = SecondImplementation()


    @BeforeEach
    fun setUp() {
        tradingService = TradingServiceImpl(mutableSetOf(exchange))
        senderUser.wallets = mutableSetOf(senderWallet, secondSenderWallet, thirdSenderWallet)
        senderWallet.currencies += Currency.LITECOIN to BigDecimal(100)
        exchange.exchangeRates += (fromCurrency to toCurrency) to BigDecimal(5)
        exchange.exchangeRates += (Currency.ETHEREUM to toCurrency) to BigDecimal(2)
    }

    @Test
    fun trade_shouldPerformTradeTransactionAndAddToTransactionHistory() {
        val amount = BigDecimal(100)
        val transaction = tradingService.trade(senderWallet, receiverWallet, fromCurrency, amount, toCurrency, exchange)
        assert(exchange.transactionHistory.contains(transaction))
    }

    @Test
    fun swap_shouldPerformSwapTransactionAndAddToTransactionHistory() {
        val amount = BigDecimal("100")
        val transaction =
            tradingService.swap(senderWallet, senderWallet.passphrase, fromCurrency, amount, toCurrency, exchange)
        assert(exchange.transactionHistory.contains(transaction))
    }


    @Test
    fun addExchange_shouldAddAnExchange() {
        val newExchange = Exchange("NewExchange")
        tradingService.addExchange(newExchange)
        assert(tradingService.getAllExchanges().contains(newExchange))
    }

    @Test
    fun getAllExchanges_shouldReturnAllExchanges() {
        assertEquals(tradingService.getAllExchanges().size, 1)
    }

    @Test
    fun trade_shouldThrowInvalidUserStatusExceptionIfSenderStatusIsNew() {
        senderUser.status = Status.NEW
        assertThrows<InvalidUserStatusException> {
            tradingService.trade(senderWallet, receiverWallet, fromCurrency, BigDecimal(100), toCurrency, exchange)
        }
    }

    @Test
    fun trade_shouldThrowBalanceException_whenSenderBalanceLessThanAmount() {
        assertThrows<BalanceException> {
            tradingService.trade(senderWallet, receiverWallet, fromCurrency, BigDecimal(1000), toCurrency, exchange)
        }
    }

    @Test
    fun swap_shouldThrowPassphraseException_whenPassphrasesMismatched() {
        assertThrows<PassphraseMismatchException> {
            tradingService.swap(senderWallet, "123", fromCurrency, BigDecimal(100), toCurrency, exchange)
        }
    }

    @Test
    fun swapTransaction_shouldReturnNoSuchCurrencyException_whenNoSuchCurrencyInWallet() {
        assertThrows<NoSuchCurrencyException> {
            tradingService.swap(
                senderWallet,
                senderWallet.passphrase,
                Currency.ETHEREUM,
                BigDecimal(100),
                toCurrency,
                exchange
            )
        }
    }

    @Test
    fun swapTransaction_shouldReturnNoSuitableExchangeException_whenNoSuitableExchange() {
        assertThrows<NoSuchCurrencyException> {
            tradingService.swap(
                senderWallet,
                senderWallet.passphrase,
                Currency.ETHEREUM,
                BigDecimal(100),
                Currency.BITCOIN,
                exchange
            )
        }
    }

    @Test
    fun `swap currencies in exchange rates`() {
        exchange.swapAllCurrencies()
        assertEquals(BigDecimal(5), exchange.exchangeRates[toCurrency to fromCurrency])
        assertEquals(BigDecimal(2), exchange.exchangeRates[toCurrency to Currency.ETHEREUM])
    }

    @Test
    fun `test delegates when first and third methods should be similar, second should be different`() {
        assertEquals(firstImplementation.first(), secondImplementation.first())
        assertEquals(firstImplementation.third(), secondImplementation.third())
        assertNotEquals(firstImplementation.second(), secondImplementation.second())
    }

    @Test
    fun `test sealed function with SwapTransaction`() {
        val transaction = SwapTransaction(
            senderWallet,
            fromCurrency,
            BigDecimal(10),
            toCurrency
        )
        assertEquals(senderWallet, tradingService.sealed(transaction))
    }

    @Test
    fun `test sealed function with TradeTransaction`() {
        val transaction = TradeTransaction(
            senderWallet,
            fromCurrency,
            BigDecimal(10),
            receiverWallet,
            toCurrency
        )
        assertEquals(receiverWallet, tradingService.sealed(transaction))
    }


    @Test
    fun `test getFilteredUsers with user with walletsAmount more than 2`() {
        val users = listOf(senderUser, receiverUser)
        assertEquals(1, tradingService.getFilteredUsers(users).size)
    }

    @Test
    fun `test fibonacci with n = 50`() {
        assertEquals(12586269025, tradingService.fibonacci(50))
    }

}
