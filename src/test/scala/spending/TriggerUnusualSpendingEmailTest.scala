package spending

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec


class TriggerUnusualSpendingEmailTest extends AnyWordSpec with Matchers with MockFactory {

  trait Setup {
    val fetchesUserPaymentsByMonth: FetchesUserPaymentsByMonth = mock[FetchesUserPaymentsByMonth]
    val mailerWrapper: UserEmailsWrapper = mock[UserEmailsWrapper]
    val unusualSpendingEmail = new TriggerUnusualSpendingEmail(fetchesUserPaymentsByMonth, mailerWrapper)
  }

  "Trigger Unusual Spending Email" when {

    "triggering the process of unusual spending" must {
      "not send an email" when {
        "the user has no payments in the current and past month" in new Setup {
          (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 2).returning(Seq())
          (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 1).returning(Seq())

          unusualSpendingEmail.trigger(1L)
        }
      }

      "send an email" when {
        "expenses in the current month are >= 50% compared to last month" in new Setup {
          (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 2).returning(Seq(Payment(100.22, "yo", Category.entertainment)))
          (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 1).returning(Seq(Payment(150.33, "yo", Category.entertainment)))
          (mailerWrapper.sendEmail _).expects(where { (userId, subject, body) ⇒
            userId == 1L && subject.contains("Unusual spending") && body.contains("Hello card user!")
          })

          unusualSpendingEmail.trigger(1L)
        }
      }
    }
  }

}
