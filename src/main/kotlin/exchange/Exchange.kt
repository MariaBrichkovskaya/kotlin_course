package exchange

import enums.Currency
import transaction.Transaction
import java.math.BigDecimal


data class Exchange(var name: String) {

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
