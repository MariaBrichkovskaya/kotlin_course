package exchange

import enums.Currency
import transaction.Transaction
import java.math.BigDecimal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


data class Exchange(var name: String) {

    val description: String by DelegatedName()

    private inner class DelegatedName : ReadOnlyProperty<Exchange, String> {
        override fun getValue(thisRef: Exchange, property: KProperty<*>): String =
            "This is exchange with name ${thisRef.name}"

    }

    var exchangeRates = mutableMapOf<Pair<Currency, Currency>, BigDecimal>()

    var transactionHistory = mutableListOf<Transaction>()

    fun swapAllCurrencies() {
        val newExchangeRates = mutableMapOf<Pair<Currency, Currency>, BigDecimal>()
        for ((currencyPair, rate) in exchangeRates) {
            val newCurrencyPair = currencyPair.swapCurrencies()
            newExchangeRates[newCurrencyPair] = rate
        }
        exchangeRates = newExchangeRates
    }
}

fun Pair<Currency, Currency>.swapCurrencies(): Pair<Currency, Currency> {
    return second to first
}
