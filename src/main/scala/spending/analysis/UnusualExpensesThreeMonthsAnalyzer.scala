package spending.analysis

import java.time.{Clock, LocalDate}

import spending.Category.Category
import spending.{FetchesUserPaymentsByMonth, UnusualExpense, UnusualExpensesAnalyzer}


class UnusualExpensesThreeMonthsAnalyzer(private val fetchesUserPaymentsByMonth: FetchesUserPaymentsByMonth)
                                        (implicit private val clock: Clock)
  extends UnusualExpensesAnalyzer {

  override def analyzeExpenses(userId: Long): Seq[UnusualExpense] = {
    val currentDate = LocalDate.now(clock)
    val currentExpensesByCategory = monthlyExpensesByCategory(userId, currentDate, fetchesUserPaymentsByMonth)
    val lastMonthsAveragesByCategory = averageExpensesInLastThreeMonthsByCategory(userId, currentDate)
    findUnusualExpenses(currentExpensesByCategory, lastMonthsAveragesByCategory)
  }

  private def averageExpensesInLastThreeMonthsByCategory(userId: Long, currentDate: LocalDate): Map[Category, BigDecimal] = {
    def mergeMapsAddingValues(map1: Map[Category, BigDecimal], map2: Map[Category, BigDecimal]): Map[Category, BigDecimal] = {
      (map1.keys ++ map2.keys)
        .toSet
        .map { category: Category ⇒ (category, map1.getOrElse(category, BigDecimal(0.00)) + map2.getOrElse(category, BigDecimal(0.00))) }
        .toMap
    }

    (1 to 3)
      .map { x ⇒ monthlyExpensesByCategory(userId, currentDate.minusMonths(x), fetchesUserPaymentsByMonth) }
      .reduce { (map1, map2) ⇒ mergeMapsAddingValues(map1, map2) }
      .map { case (category, amount) ⇒ (category, amount / 3) }
  }
}
