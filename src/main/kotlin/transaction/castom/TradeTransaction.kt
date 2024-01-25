package transaction.castom

import enums.Currency
import transaction.Transaction
import wallet.Wallet
import java.math.BigDecimal

class TradeTransaction(
    sender: Wallet,
    fromCurrency: Currency,
    amount: BigDecimal,
    val receiver: Wallet,
    toCurrency: Currency,
) : Transaction(sender, fromCurrency, amount)