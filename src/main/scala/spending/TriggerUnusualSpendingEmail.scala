package spending

class TriggerUnusualSpendingEmail(val paymentFetcher: FetchesUserPaymentsByMonth,
                                  val emailSender: UserEmailsWrapper) {

  def trigger(userId: Long): Unit = {
    val currentMonthExpenses = paymentFetcher.fetch(userId, 2020, 2)
    val lastMonthExpenses = paymentFetcher.fetch(userId, 2020, 1)

    val lastMonth = lastMonthExpenses.map { _.price }.sum
    val currentMonth = currentMonthExpenses.map { _.price }.sum
    val ratio =
      if (lastMonth > BigDecimal(0.0) && currentMonth > BigDecimal(0.0))
        lastMonth / currentMonth
      else
        BigDecimal(0.0)

    if (ratio >= 1.5)
      emailSender.sendEmail(1L, "Unusual spending", "Hello card user!")
  }

}

trait UserEmailsWrapper {
  def sendEmail(userId: Long, subject: String, body: String): Unit = {
    EmailsUser.email(userId, subject, body)
  }
}