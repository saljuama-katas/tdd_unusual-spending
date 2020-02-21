package spending.notifications

import spending.{EmailsUser, UnusualExpense, UnusualExpensesNotifier}

class UnusualExpensesEmailNotifier(val emailsUserWrapper: EmailsUserWrapper) extends UnusualExpensesNotifier {

  override def notifyUser(userId: Long, unusualExpenses: Seq[UnusualExpense]): Unit = {
    emailsUserWrapper.email(userId, composeSubject(unusualExpenses), composeBody(unusualExpenses))
  }

  private def composeSubject(unusualExpenses: Seq[UnusualExpense]) = {
    val totalUnusualExpenses = unusualExpenses.map { _.unusual }.sum
    val totalUsualExpenses = unusualExpenses.map { _.usual }.sum
    s"Unusual spending detected of ${formatCurrency(totalUnusualExpenses)} detected!  (usual was ${formatCurrency(totalUsualExpenses)})"
  }

  private def composeBody(unusualExpenses: Seq[UnusualExpense]): String = {
    val emailHeader =
      """Hello card user!
        |
        |We have detected unusually high spending on your card in these categories:
        |
        |""".stripMargin
    val emailFooter =
      """
        |
        |Love,
        |
        |The Credit Card Company
        |""".stripMargin
    val unusualExpensesList = unusualExpenses
      .map { expense ⇒ s"* You spent ${formatCurrency(expense.unusual)} on ${expense.category} (usual was ${formatCurrency(expense.usual)})" }
      .mkString("\n")

    emailHeader + unusualExpensesList + emailFooter
  }

  private def formatCurrency(amount: BigDecimal): String = {
    val formatter = java.text.NumberFormat.getCurrencyInstance
    formatter.format(amount)
  }
}

trait EmailsUserWrapper {
  def email(userId: Long, subject: String, body: String): Unit = {
    EmailsUser.email(userId, subject, body)
  }
}