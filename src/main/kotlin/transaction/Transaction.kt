package transaction

import enums.Currency
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

open class Transaction(
    val sender: Wallet,
    val fromCurrency: Currency,
    val amount: BigDecimal
) {
    val id: UUID = UUID.randomUUID()
    var date: LocalDateTime = LocalDateTime.now()

    constructor(sender: Wallet, fromCurrency: Currency, amount: BigDecimal, date: LocalDateTime) : this(
        sender,
        fromCurrency,
        amount
    ) {
        this.date = date
    }
}

