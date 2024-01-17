import enums.Currency
import enums.Status
import service.impl.PersonalAccountServiceImpl
import service.impl.TradingServiceImpl
import transaction.castom.SwapTransaction
import user.User
import wallet.Wallet
import java.math.BigDecimal

fun main() {
    userFunc()
}

private fun userFunc() {
    val user = User("123@example.com", "Masha", Status.APPROVED)
    val wallet1 = Wallet("Wallet 1", "passphrase1", user)
    val wallet2 = Wallet("Wallet 2", "passphrase2", user)
    val wallet3 = Wallet("Wallet 3", "passphrase3", user)
    val wallet4 = Wallet("Wallet 4", "passphrase4", user)
    wallet1.currencies = mutableMapOf(
        Currency.BITCOIN to BigDecimal(100),
        Currency.ETHEREUM to BigDecimal(168)
    )
    wallet2.currencies = mutableMapOf(
        Currency.BITCOIN to BigDecimal(10),
        Currency.LITECOIN to BigDecimal(165)
    )
    wallet3.currencies = mutableMapOf(
        Currency.LITECOIN to BigDecimal(40),
        Currency.ETHEREUM to BigDecimal(98)
    )
    wallet4.currencies = mutableMapOf(
        Currency.BITCOIN to BigDecimal(54)
    )

    user.wallets = mutableSetOf(wallet1, wallet2, wallet3, wallet4)
    val account = PersonalAccountServiceImpl()
    account.printBalance(*user.wallets.toTypedArray())

    val swap = SwapTransaction(wallet1, Currency.BITCOIN, BigDecimal(50), Currency.ETHEREUM)
    val trading: TradingServiceImpl

}
