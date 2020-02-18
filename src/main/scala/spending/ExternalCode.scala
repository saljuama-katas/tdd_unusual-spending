package spending

/*
  Somebody Else's Job™ (from the README#Caveats)

  The contents of this file is to represent the contracts for code owned by someone else,
  do not write implementation of the following methods
*/

// Instance provided by a factory method
trait FetchesUserPaymentsByMonth {
  def fetch(userId: Long, year: Int, month: Int): Seq[Payment]
}

// Static invocation
trait EmailsUserInterface {
  def email(userId: Long, subject: String, body: String): Unit
}
object EmailsUser extends EmailsUserInterface {
  override def email(userId: Long, subject: String, body: String): Unit = ???
}

/*
  Domain classes (from README#Requirements)

  These can't be modified
 */

// yes enums in scala are weird
object Category extends Enumeration {
  type Category = Value
  // values extracted from the README but not necessarily limited to this
  val entertainment, restaurants, golf, groceries, travel = Value
}
import Category._

case class Payment(price: BigDecimal, description: String, category: Category)
