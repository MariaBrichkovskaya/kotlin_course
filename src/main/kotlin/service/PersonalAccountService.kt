package service

import exchange.Exchange
import transaction.Transaction
import user.User
import wallet.Wallet
import java.time.LocalDateTime

interface PersonalAccountService {
    fun printBalance(vararg wallets: Wallet)
    fun getTransactionsForPeriod(
        user: User,
        exchange: Exchange,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Transaction>

    fun addWallet(user: User, wallet: Wallet)
}