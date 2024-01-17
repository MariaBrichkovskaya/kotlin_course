package service.impl

import enums.Currency
import exchange.Exchange
import service.PersonalAccountService
import transaction.Transaction
import user.User
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime

class PersonalAccountServiceImpl : PersonalAccountService {
    override fun printBalance(vararg wallets: Wallet) {
        val totalBalance = mutableMapOf<Currency, BigDecimal>()
        for (wallet in wallets) {
            for ((currency, amount) in wallet.currencies) {
                totalBalance[currency] = totalBalance.getOrDefault(currency, BigDecimal.ZERO) + amount
            }
        }
        for ((currency, amount) in totalBalance) {
            println("Currency: $currency balance: $amount ")
        }
    }

    override fun getTransactionsForPeriod(
        user: User,
        exchange: Exchange,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Transaction> {
        return exchange.transactionHistory
            .filter { transaction -> transaction.date.isAfter(from) && transaction.date.isBefore(to) }
            .filter { transaction -> transaction.sender.id == user.id }
    }

    override fun addWallet(user: User, wallet: Wallet) {
        user.wallets.add(wallet)
    }
}