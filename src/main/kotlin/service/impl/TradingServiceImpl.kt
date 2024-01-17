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
import transaction.Transaction
import transaction.castom.SwapTransaction
import transaction.castom.TradeTransaction
import user.User
import wallet.Wallet
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.random.nextInt

class TradingServiceImpl(private val exchanges: MutableSet<Exchange>) : TradingService {
    //val exchanges = mutableSetOf<Exchange>()
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
                    exchange.exchangeRates[fromCurrency to toCurrency]?.multiply(amount)
                        ?: throw NoSuitableExchangeException("No suitable exchange")
                )
        } catch (e: NoSuchElementException) {
            receiver.currencies += toCurrency to amount
        }
    }

    private fun checkPassPhrase(passphrase: String, wallet: Wallet) {
        if (passphrase != wallet.passphrase)
            throw PassphraseMismatchException("Passphrases are not equals")
    }

    private fun checkUserStatus(user: User) {
        if (user.status == Status.NEW || user.status == Status.BLOCKED)
            throw InvalidUserStatusException("User status is ${user.status}")
    }

    private fun checkBalance(wallet: Wallet, currency: Currency, amount: BigDecimal) {
        val balance: BigDecimal
        try {
            balance = wallet.currencies.getValue(currency)
        } catch (e: NoSuchElementException) {
            throw NoSuchCurrencyException("No such currency in wallet")
        }
        if (balance < amount) {
            throw BalanceException("Not Enough money on your balance")
        }
    }

    private fun checkRandom() {
        val num = Random.nextInt(0..51)
        if (num in 27..50)
            throw TransactionFailedException("Transaction failed")

    }
}
