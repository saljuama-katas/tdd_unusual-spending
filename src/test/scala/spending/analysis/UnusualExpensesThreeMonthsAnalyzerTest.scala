package spending.analysis

import java.time.{Clock, Instant, ZoneId}

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spending._

class UnusualExpensesThreeMonthsAnalyzerTest extends AnyWordSpec with Matchers with MockFactory {

  class SetupWithFixedCurrentDate(val date: String = "2020-01-10") {
    private def fakeClock: Clock = Clock.fixed(Instant.parse(date + "T00:00:00.00Z"), ZoneId.systemDefault())
    val fetchesUserPaymentsByMonth: FetchesUserPaymentsByMonth = mock[FetchesUserPaymentsByMonth]
    val unusualExpensesAnalyzer: UnusualExpensesAnalyzer = new UnusualExpensesThreeMonthsAnalyzer(fetchesUserPaymentsByMonth)(fakeClock)
  }

  "The three months unusual expenses analyzer" must {

    "fetch the expenses for the current and last 3 months" in new SetupWithFixedCurrentDate {
      (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 1).returning(Seq())
      (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 12).returning(Seq())
      (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 11).returning(Seq())
      (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 10).returning(Seq())

      unusualExpensesAnalyzer.analyzeExpenses(1L)
    }

    "return unusual expenses for a category" when {

      "having expenses this month but not in last months" in new SetupWithFixedCurrentDate {
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 10).returning(Seq())
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 11).returning(Seq())
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 12).returning(Seq())
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 1).returning(Seq(
          Payment(200.00, "because YOLO", Category.entertainment)
        ))

        private val result = unusualExpensesAnalyzer.analyzeExpenses(1L)

        result must contain(UnusualExpense(Category.entertainment, 200.00, 0.00))
      }

      "sum of payments this month goes above the threshold (150%) compared to last 3 months average" in new SetupWithFixedCurrentDate {
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 10).returning(Seq(
          Payment(25.00, "Rome", Category.travel),
          Payment(25.00, "Moscow", Category.travel)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 11).returning(Seq(
          Payment(50.00, "Rome", Category.travel),
          Payment(50.00, "Moscow", Category.travel)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 12).returning(Seq(
          Payment(75.00, "Rome", Category.travel),
          Payment(75.00, "Moscow", Category.travel)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 1).returning(Seq(
          Payment(50.00, "Madagascar", Category.travel),
          Payment(50.00, "Buenos Aires", Category.travel),
          Payment(50.00, "Amsterdam", Category.travel),
        ))

        private val result = unusualExpensesAnalyzer.analyzeExpenses(1L)

        result must contain(UnusualExpense(Category.travel, 150.00, 100.00))
      }
    }

    "NOT return unusual expenses for a category" when {

      "last month there were payments but not this month" in new SetupWithFixedCurrentDate {
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 10).returning(Seq(
          Payment(100.00, "I'm rich you know", Category.golf)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 11).returning(Seq(
          Payment(200.00, "I'm rich you know", Category.golf)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 12).returning(Seq(
          Payment(300.00, "I'm rich you know", Category.golf)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 1).returning(Seq())

        private val result = unusualExpensesAnalyzer.analyzeExpenses(1L)

        result.map { _.category } must  not contain Category.golf
      }

      "sum of payments this month does not reach the threshold (150%) compared to last month" in new SetupWithFixedCurrentDate {
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 10).returning(Seq(
          Payment(25.00, "Rome", Category.travel),
          Payment(25.00, "Moscow", Category.travel)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 11).returning(Seq(
          Payment(50.00, "Rome", Category.travel),
          Payment(50.00, "Moscow", Category.travel)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 12).returning(Seq(
          Payment(75.00, "Rome", Category.travel),
          Payment(75.00, "Moscow", Category.travel)
        ))
        (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 1).returning(Seq(
          Payment(50.00, "Madagascar", Category.travel),
          Payment(50.00, "Buenos Aires", Category.travel),
          Payment(49.99, "Amsterdam", Category.travel),
        ))

        private val result = unusualExpensesAnalyzer.analyzeExpenses(1L)

        result.map { _.category } must not contain Category.travel
      }
    }

    "analyze multiple categories" in new SetupWithFixedCurrentDate {
      (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 10).returning(Seq(
        Payment(25.00, "Rome", Category.travel),
        Payment(25.00, "Moscow", Category.travel),

        Payment(50.00, "Gaming", Category.entertainment),
        Payment(50.00, "Fun stuff", Category.entertainment),

        Payment(200.00, "I'm rich you know", Category.golf)
      ))
      (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 11).returning(Seq(
        Payment(50.00, "Rome", Category.travel),
        Payment(50.00, "Moscow", Category.travel),

        Payment(100.00, "Gaming", Category.entertainment),
        Payment(100.00, "Fun stuff", Category.entertainment),

        Payment(200.00, "I'm rich you know", Category.golf)
      ))
      (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2019, 12).returning(Seq(
        Payment(75.00, "Rome", Category.travel),
        Payment(75.00, "Moscow", Category.travel),

        Payment(150.00, "Gaming", Category.entertainment),
        Payment(150.00, "Fun stuff", Category.entertainment),

        Payment(200.00, "I'm rich you know", Category.golf)
      ))
      (fetchesUserPaymentsByMonth.fetch _).expects(1L, 2020, 1).returning(Seq(
        Payment(50.00, "Madagascar", Category.travel),
        Payment(50.00, "Buenos Aires", Category.travel),
        Payment(50.00, "Amsterdam", Category.travel),

        Payment(100.00, "Gaming", Category.entertainment),
        Payment(100.00, "Fun stuff", Category.entertainment),

        Payment(75.00, "Nom Nom Nom", Category.groceries)
      ))

      private val result = unusualExpensesAnalyzer.analyzeExpenses(1L)

      result.size mustBe 2
    }
  }
}
