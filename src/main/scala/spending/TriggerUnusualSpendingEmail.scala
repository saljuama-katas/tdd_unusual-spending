package spending


class TriggerUnusualSpendingEmail(val unusualExpensesAnalyzer: UnusualExpensesAnalyzer,
                                  val unusualExpensesNotifier: UnusualExpensesNotifier) {

  def trigger(userId: Long): Unit = {
    val unusualExpenses = unusualExpensesAnalyzer.analyzeExpenses(userId)
    if (unusualExpenses.nonEmpty)
      unusualExpensesNotifier.notifyUser(1L, unusualExpenses)
  }

}

import java.time.LocalDate

import spending.Category.Category

case class UnusualExpense(category: Category, price: BigDecimal)

trait UnusualExpensesAnalyzer {
  def analyzeExpenses(userId: Long): Seq[UnusualExpense]

  protected def monthlyExpensesByCategory(userId: Long, date: LocalDate, fetcher: FetchesUserPaymentsByMonth): Map[Category, BigDecimal] = {
    fetcher
      .fetch(userId, date.getYear, date.getMonthValue)
      .groupBy { _.category }
      .map { case (category, payments) ⇒ (category, payments.map { _.price }.sum) }
  }

  protected def findUnusualExpenses(current: Map[Category, BigDecimal], previous: Map[Category, BigDecimal]): Seq[UnusualExpense] = {
    def isAboveThreshold(current: BigDecimal, usual: BigDecimal): Boolean = current >= (usual * 1.50)
    current
      .filter { case (category, amount) ⇒ isAboveThreshold(amount, previous.getOrElse(category, 0.00)) }
      .map { case (category, amount) ⇒ UnusualExpense(category, amount) }
      .toSeq
  }
}

trait UnusualExpensesNotifier {
  def notifyUser(userId: Long, unusualExpenses: Seq[UnusualExpense]): Unit
}

