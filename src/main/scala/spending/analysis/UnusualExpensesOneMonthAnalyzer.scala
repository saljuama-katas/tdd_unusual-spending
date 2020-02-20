package spending.analysis

import java.time.{Clock, LocalDate}

import spending.{FetchesUserPaymentsByMonth, UnusualExpense, UnusualExpensesAnalyzer}


class UnusualExpensesOneMonthAnalyzer(private val fetchesUserPaymentsByMonth: FetchesUserPaymentsByMonth)
                                     (implicit private val clock: Clock)
  extends UnusualExpensesAnalyzer {

  override def analyzeExpenses(userId: Long): Seq[UnusualExpense] = {
    val currentDate = LocalDate.now(clock)
    val currentMonthExpensesByCategory = monthlyExpensesByCategory(userId, currentDate, fetchesUserPaymentsByMonth)
    val lastMonthExpensesByCategory = monthlyExpensesByCategory(userId, currentDate.minusMonths(1), fetchesUserPaymentsByMonth)
    findUnusualExpenses(currentMonthExpensesByCategory, lastMonthExpensesByCategory)
  }
}
