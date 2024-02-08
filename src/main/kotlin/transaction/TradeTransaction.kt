package transaction

import enums.Currency
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class TradeTransaction(
    override val sender: Wallet,
    override val fromCurrency: Currency,
    override val amount: BigDecimal,
    val receiver: Wallet,
    var toCurrency: Currency,
) : Transaction {
    override val id: UUID = UUID.randomUUID()
    override var date: LocalDateTime = LocalDateTime.now()

    constructor(
        sender: Wallet,
        fromCurrency: Currency,
        amount: BigDecimal,
        receiver: Wallet,
        toCurrency: Currency, date: LocalDateTime
    ) : this(
        sender,
        fromCurrency, amount, receiver, toCurrency
    ) {
        this.date = date
    }
}