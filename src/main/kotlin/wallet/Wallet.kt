package wallet

import enums.Currency
import user.User
import java.math.BigDecimal
import java.util.UUID

class Wallet(
    val id: UUID,
    var name: String,
    var isCold: Boolean,
    var passphrase: String,
    val user: User
) {
    var currencies = mutableMapOf<Currency, BigDecimal>()

    constructor(name: String, passphrase: String, user: User) : this(
        UUID.randomUUID(),
        name,
        false,
        passphrase,
        user
    )
}