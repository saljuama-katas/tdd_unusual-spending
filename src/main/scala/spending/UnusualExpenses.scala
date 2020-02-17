package spending

import spending.Category.Category


case class UnusualExpense(price: BigDecimal, category: Category)

trait UnusualExpensesAnalyzer {
  def analyzeExpenses(userId: Long): Seq[UnusualExpense]
}

trait UnusualExpensesNotifier {
  def notifyUser(userId: Long, unusualExpenses: Seq[UnusualExpense]): Unit
}
