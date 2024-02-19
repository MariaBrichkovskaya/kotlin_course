package wallet

import enums.Currency
import user.User
import java.math.BigDecimal
import java.util.UUID
import kotlin.properties.Delegates

data class Wallet(
    val id: UUID,
    var name: String,
    var isCold: Boolean,
    var passphrase: String,
    val user: User
) {
    var msg = ""
    var description: String by Delegates.observable("default")
    { _, old, new ->
        msg += "$old to $new "
    }
    var currencies = mutableMapOf<Currency, BigDecimal>()

    constructor(name: String, passphrase: String, user: User) : this(
        UUID.randomUUID(),
        name,
        false,
        passphrase,
        user
    )

    constructor(name: String, passphrase: String, user: User, currencies: MutableMap<Currency, BigDecimal>) : this(
        UUID.randomUUID(),
        name,
        false,
        passphrase,
        user
    ) {
        this.currencies = currencies
    }

    operator fun plus(wallet: Wallet) =
        Wallet(name, passphrase, user, createMergedCurrenciesMap(Pair(currencies, wallet.currencies)))

    private fun createMergedCurrenciesMap(
        pair: Pair<MutableMap<Currency, BigDecimal>, MutableMap<Currency, BigDecimal>>
    ): MutableMap<Currency, BigDecimal> {
        val mergedMap = mutableMapOf<Currency, BigDecimal>()

        for ((key, value) in pair.first) {
            mergedMap[key] = mergedMap.getOrDefault(key, BigDecimal.ZERO).add(value)
        }

        for ((key, value) in pair.second) {
            mergedMap[key] = mergedMap.getOrDefault(key, BigDecimal.ZERO).add(value)
        }
        return mergedMap
    }


}