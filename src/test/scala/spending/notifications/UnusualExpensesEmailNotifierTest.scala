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
        UnusualExpense(100.00, Category.entertainment),
        UnusualExpense(50.00, Category.travel)
      ))
    }

    "compose email subject with the sum of the expenses" in new Setup {
      emailsUserWrapper.email _ expects where { (_, subject, _) ⇒ subject.contains("150.00") }

      unusualExpensesEmailNotifier.notifyUser(1L, Seq(
        UnusualExpense(100.00, Category.entertainment),
        UnusualExpense(50.00, Category.travel)
      ))
    }

    "compose email body with an entry for each unusual expense" in new Setup {
      emailsUserWrapper.email _ expects where { (_, _, body) ⇒
        body.contains("100.00 on entertainment") &&
          body.contains("50.00 on travel")
      }

      unusualExpensesEmailNotifier.notifyUser(1L, Seq(
        UnusualExpense(100.00, Category.entertainment),
        UnusualExpense(50.00, Category.travel)
      ))
    }

  }
}
