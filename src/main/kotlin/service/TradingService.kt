package service

import enums.Currency
import exchange.Exchange
import transaction.Transaction
import user.User
import wallet.Wallet
import java.math.BigDecimal

interface TradingService {
    fun swap(
        sender: Wallet,
        passphrase: String,
        fromCurrency: Currency,
        amount: BigDecimal,
        toCurrency: Currency,
        exchange: Exchange
    ): Transaction

    fun addExchange(exchange: Exchange)
    fun trade(
        sender: Wallet,
        receiver: Wallet,
        fromCurrency: Currency,
        amount: BigDecimal,
        toCurrency: Currency,
        exchange: Exchange
    ): Transaction

    fun getAllExchanges(): Set<Exchange>
    fun sealed(transaction: Transaction): Wallet
    fun getFilteredUsers(users: List<User>): List<User>
    fun fibonacci(n: Int): Long
}