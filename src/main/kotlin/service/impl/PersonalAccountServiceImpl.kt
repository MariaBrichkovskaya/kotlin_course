package service.impl

import enums.Currency
import enums.Status
import exchange.Exchange
import service.PersonalAccountService
import transaction.Transaction
import user.User
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID

object PersonalAccountServiceImpl : PersonalAccountService {
    override fun getBalance(vararg wallets: Wallet): MutableMap<Currency, BigDecimal> {
        val totalBalance = mutableMapOf<Currency, BigDecimal>()
        for (wallet in wallets) {
            for ((currency, amount) in wallet.currencies) {
                totalBalance[currency] = totalBalance.getOrDefault(currency, BigDecimal.ZERO) + amount
            }
        }

        for ((currency, amount) in totalBalance) {
            totalBalance += currency to amount
        }

        return totalBalance
    }

    override fun getTransactionsForPeriod(
        user: User,
        exchange: Exchange,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Transaction> {
        return exchange.transactionHistory
            .filter { transaction -> transaction.date.isAfter(from) && transaction.date.isBefore(to) }
            .filter { transaction -> transaction.sender.user.id == user.id }
    }

    override fun addWallet(user: User, wallet: Wallet) {
        user.wallets = mutableSetOf(wallet)
    }

    override fun creatMapsFromList(users: List<User>) {
        val firstMap: Map<UUID, User> = users.associateBy { it.id }
        val secondMap: Map<User, Status> = users.associateWith { it.status }

        println(firstMap)
        println(secondMap)
    }

    override fun destructurization(user: User): String {
        val (id, _, _, status) = user
        return "user with id $id have status $status"
    }

    override fun getEmailInUpperCaseAndWithoutDomain(user: User): User {
        val newEmail = user.email?.substringBefore('@')?.uppercase(Locale.getDefault()) ?: ""
        user.email = newEmail
        return user
    }
}