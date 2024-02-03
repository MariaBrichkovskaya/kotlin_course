package service.impl

import enums.Currency
import enums.Status
import exception.BalanceException
import exception.InvalidUserStatusException
import exception.NoSuchCurrencyException
import exception.NoSuitableExchangeException
import exception.PassphraseMismatchException
import exception.TransactionFailedException
import exchange.Exchange
import service.TradingService
import transaction.SwapTransaction
import transaction.TradeTransaction
import transaction.Transaction
import user.User
import user.walletsAmount
import wallet.Wallet
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.random.nextInt

class TradingServiceImpl(private val exchanges: MutableSet<Exchange>) : TradingService {
    companion object {
        private const val PASSPHRASE_MISMATCH_MESSAGE = "Passphrases are not equals"
        private const val INVALID_USER_STATUS_MESSAGE = "User status is %s"
        private const val NO_SUCH_CURRENCY_MESSAGE = "No such currency in wallet"
        private const val NOT_ENOUGH_MONEY_MESSAGE = "Not enough money on your balance"
        private const val TRANSACTION_FAILED_MESSAGE = "Transaction failed"
        private const val NO_SUITABLE_EXCHANGE_MESSAGE = "No suitable exchange"
    }

    override fun trade(
        sender: Wallet,
        receiver: Wallet,
        fromCurrency: Currency,
        amount: BigDecimal,
        toCurrency: Currency,
        exchange: Exchange
    ): Transaction {
        checkUserStatus(sender.user)
        checkUserStatus(receiver.user)

        checkBalance(sender, fromCurrency, amount)

        val transaction = TradeTransaction(
            sender = sender,
            receiver = receiver,
            amount = amount,
            fromCurrency = fromCurrency,
            toCurrency = toCurrency
        )

        changeCurrency(exchange, fromCurrency, amount, toCurrency, sender, receiver)

        exchange.transactionHistory.add(transaction)

        return transaction
    }

    override fun swap(
        sender: Wallet,
        passphrase: String,
        fromCurrency: Currency,
        amount: BigDecimal,
        toCurrency: Currency,
        exchange: Exchange
    ): Transaction {
        checkBalance(sender, fromCurrency, amount)
        checkPassPhrase(passphrase, sender)
        checkRandom()

        val transaction = SwapTransaction(
            sender = sender,
            fromCurrency = fromCurrency,
            amount = amount,
            toCurrency = toCurrency
        )

        changeCurrency(exchange, fromCurrency, amount, toCurrency, sender, sender)
        exchange.transactionHistory.add(transaction)

        return transaction
    }

    override fun addExchange(exchange: Exchange) {
        exchanges.add(exchange)
    }


    override fun getAllExchanges(): Set<Exchange> {
        return exchanges
    }

    private fun changeCurrency(
        exchange: Exchange,
        fromCurrency: Currency,
        amount: BigDecimal,
        toCurrency: Currency,
        sender: Wallet,
        receiver: Wallet
    ) {
        sender.currencies[fromCurrency] = sender
            .currencies[fromCurrency]!!
            .minus(amount)
        try {
            receiver.currencies[toCurrency] = receiver.currencies
                .getValue(toCurrency)
                .add(
                    amount.divide(exchange.exchangeRates[fromCurrency to toCurrency])
                        ?: throw NoSuitableExchangeException(NO_SUITABLE_EXCHANGE_MESSAGE)
                )
        } catch (e: NoSuchElementException) {
            receiver.currencies += toCurrency to amount
        }
    }

    private fun checkPassPhrase(passphrase: String, wallet: Wallet) {
        if (passphrase != wallet.passphrase)
            throw PassphraseMismatchException(PASSPHRASE_MISMATCH_MESSAGE)
    }

    private fun checkUserStatus(user: User) {
        if (user.status == Status.NEW || user.status == Status.BLOCKED)
            throw InvalidUserStatusException(INVALID_USER_STATUS_MESSAGE.format(user.status))
    }

    private fun checkBalance(wallet: Wallet, currency: Currency, amount: BigDecimal) {
        val balance: BigDecimal

        try {
            balance = wallet.currencies.getValue(currency)
        } catch (e: NoSuchElementException) {
            throw NoSuchCurrencyException(NO_SUCH_CURRENCY_MESSAGE)
        }

        if (balance < amount) {
            throw BalanceException(NOT_ENOUGH_MONEY_MESSAGE)
        }
    }

    private fun checkRandom() {
        val num = Random.nextInt(createRange())
        if (num in 27..50)
            throw TransactionFailedException(TRANSACTION_FAILED_MESSAGE)

    }

    private fun createRange(
        min: Int = 0,
        max: Int = 51
    ): IntRange {
        return min..max
    }

    override fun sealed(transaction: Transaction) =
        when (transaction) {
            is SwapTransaction -> transaction.sender
            is TradeTransaction -> transaction.receiver
        }

    override fun getFilteredUsers(users: List<User>): List<User> {
        return users.filter { it.walletsAmount > 2 }
    }

    override fun fibonacci(n: Int): Long {
        tailrec fun fibonacci(a: Long, b: Long, count: Int): Long {
            return if (count == 0) a
            else fibonacci(b, a + b, count - 1)
        }
        return fibonacci(0, 1, n)
    }
}
