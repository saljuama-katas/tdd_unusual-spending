package spending

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec


class TriggerUnusualSpendingEmailTest extends AnyWordSpec with Matchers with MockFactory {

  trait Setup {
    val unusualExpensesAnalyzer: UnusualExpensesAnalyzer = mock[UnusualExpensesAnalyzer]
    val unusualExpensesNotifier: UnusualExpensesNotifier = mock[UnusualExpensesNotifier]
    val unusualSpendingEmail = new TriggerUnusualSpendingEmail(unusualExpensesAnalyzer, unusualExpensesNotifier)
  }

  "Triggering the process for a user" must {
    val userId: Long = 1L

    "NOT send an user notification when the user does not have unusual expenses" in new Setup {
      (unusualExpensesAnalyzer.analyzeExpenses _).expects(userId).returning(Seq())
      (unusualExpensesNotifier.notifyUser _).expects(*, *).never

      unusualSpendingEmail.trigger(userId)
    }

    "send an user notification when the user has unusual expenses" in new Setup {
      private val unusualExpenses = Seq(UnusualExpense(10.00, Category.entertainment))

      (unusualExpensesAnalyzer.analyzeExpenses _).expects(userId).returning(unusualExpenses)
      (unusualExpensesNotifier.notifyUser _).expects(userId, unusualExpenses).once

      unusualSpendingEmail.trigger(userId)
    }
  }
}
