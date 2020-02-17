package spending

class TriggerUnusualSpendingEmail(val unusualExpensesAnalyzer: UnusualExpensesAnalyzer,
                                  val unusualExpensesNotifier: UnusualExpensesNotifier) {

  def trigger(userId: Long): Unit = {
    val unusualExpenses = unusualExpensesAnalyzer.analyzeExpenses(userId)
    if (unusualExpenses.nonEmpty)
      unusualExpensesNotifier.notifyUser(1L, unusualExpenses)
  }

}
