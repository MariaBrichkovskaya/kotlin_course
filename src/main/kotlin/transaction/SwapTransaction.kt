package transaction

import enums.Currency
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class SwapTransaction(
    override val sender: Wallet,
    override val fromCurrency: Currency,
    override val amount: BigDecimal,
    val toCurrency: Currency
) : Transaction {
    override val id: UUID = UUID.randomUUID()
    override var date: LocalDateTime = LocalDateTime.now()
}