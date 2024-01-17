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
    val date: LocalDateTime = LocalDateTime.now()
}

