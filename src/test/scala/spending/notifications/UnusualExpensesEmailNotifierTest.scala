package spending.notifications

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spending.{Category, UnusualExpense}

class UnusualExpensesEmailNotifierTest extends AnyWordSpec with Matchers with MockFactory {

  trait Setup {
    val emailsUserWrapper: EmailsUserWrapper = mock[EmailsUserWrapper]
    val unusualExpensesEmailNotifier = new UnusualExpensesEmailNotifier(emailsUserWrapper)
  }

  "An email notifier" must {

    "compose email addressed to the specified user" in new Setup {
      emailsUserWrapper.email _ expects(1L, *, *)

      unusualExpensesEmailNotifier.notifyUser(1L, Seq(
        UnusualExpense(Category.entertainment, 100.00, 75.00),
        UnusualExpense(Category.travel, 50.00, 45.00)
      ))
    }

    "compose email subject with the sum of the expenses" in new Setup {
      emailsUserWrapper.email _ expects where { (_, subject, _) ⇒ subject.contains("$150.00") && subject.contains("usual was $120.00") }

      unusualExpensesEmailNotifier.notifyUser(1L, Seq(
        UnusualExpense(Category.entertainment, 100.00, 75.00),
        UnusualExpense(Category.travel, 50.00, 45.00)
      ))
    }

    "compose email body with an entry for each unusual expense" in new Setup {
      emailsUserWrapper.email _ expects where { (_, _, body) ⇒
        body.contains("$100.00 on entertainment (usual was $75.00)") &&
          body.contains("$50.00 on travel (usual was $45.00)")
      }

      unusualExpensesEmailNotifier.notifyUser(1L, Seq(
        UnusualExpense(Category.entertainment, 100.00, 75.00),
        UnusualExpense(Category.travel, 50.00, 45.00)
      ))
    }

  }
}
