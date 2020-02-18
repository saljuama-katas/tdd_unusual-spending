package spending


class TriggerUnusualSpendingEmail(val unusualExpensesAnalyzer: UnusualExpensesAnalyzer,
                                  val unusualExpensesNotifier: UnusualExpensesNotifier) {

  def trigger(userId: Long): Unit = {
    val unusualExpenses = unusualExpensesAnalyzer.analyzeExpenses(userId)
    if (unusualExpenses.nonEmpty)
      unusualExpensesNotifier.notifyUser(1L, unusualExpenses)
  }

}

import spending.Category.Category

case class UnusualExpense(price: BigDecimal, category: Category)

trait UnusualExpensesAnalyzer {
  def analyzeExpenses(userId: Long): Seq[UnusualExpense]
}

trait UnusualExpensesNotifier {
  def notifyUser(userId: Long, unusualExpenses: Seq[UnusualExpense]): Unit
}

