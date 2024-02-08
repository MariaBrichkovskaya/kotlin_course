package user

import enums.Currency
import enums.Status
import wallet.Wallet
import java.math.BigDecimal
import java.util.UUID

data class User(val id: UUID, var email: String?, var fullName: String, var status: Status) {
    private val walletMutableSet = mutableSetOf<Wallet>()
    var wallets
        get() = walletMutableSet.filter { !it.isCold }.toMutableSet()
        set(value) {
            walletMutableSet.addAll(value)
        }

    init {
        val wallet = Wallet("qwerty", "qwerty", this)
        wallet.currencies += mapOf(Currency.BITCOIN to BigDecimal.TEN)
        wallets = mutableSetOf(wallet)
    }


    constructor(email: String, fullName: String) : this(
        UUID.randomUUID(),
        email,
        fullName,
        Status.NEW
    )

    constructor(email: String, fullName: String, status: Status) : this(
        UUID.randomUUID(),
        email,
        fullName,
        status
    )
}

val User.walletsAmount: Int
    get() = wallets.size



