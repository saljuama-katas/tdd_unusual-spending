package spending.analysis

import java.time.{Clock, LocalDate}

import spending.Category.Category
import spending.{FetchesUserPaymentsByMonth, UnusualExpense, UnusualExpensesAnalyzer}


class UnusualExpensesOneMonthAnalyzer(private val fetchesUserPaymentsByMonth: FetchesUserPaymentsByMonth)
                                     (implicit private val clock: Clock)
  extends UnusualExpensesAnalyzer {

  override def analyzeExpenses(userId: Long): Seq[UnusualExpense] = {
    val currentDate = LocalDate.now(clock)
    val pastMonthDate = currentDate.minusMonths(1)

    def monthlyExpensesByCategory(year: Int, month: Int): Map[Category, BigDecimal] =
      fetchesUserPaymentsByMonth.fetch(userId, year, month)
        .groupBy { _.category }
        .map { case (category, payments) ⇒ (category, payments.map { _.price }.sum) }

    val currentMonthExpensesByCategory = monthlyExpensesByCategory(currentDate.getYear, currentDate.getMonthValue)
    val lastMonthExpensesByCategory = monthlyExpensesByCategory(pastMonthDate.getYear, pastMonthDate.getMonthValue)

    def isAboveThreshold(currentMonth: BigDecimal, lastMonth: BigDecimal) = currentMonth >= (lastMonth * 1.50)

    currentMonthExpensesByCategory
      .filter { case (category, amount) ⇒ isAboveThreshold(amount, lastMonthExpensesByCategory.getOrElse(category, 0.00)) }
      .map { case (category, amount) ⇒ UnusualExpense(amount, category) }
      .toSeq
  }

}
