package transaction.castom

import enums.Currency
import transaction.Transaction
import wallet.Wallet
import java.math.BigDecimal


class SwapTransaction(
    sender: Wallet,
    fromCurrency: Currency,
    amount: BigDecimal,
    val toCurrency: Currency
) : Transaction(sender, fromCurrency, amount) {

}