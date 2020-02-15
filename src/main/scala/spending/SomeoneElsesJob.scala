package spending

/*
  Somebody Else's Job™ (from the README#Caveats)

  The contents of this file is to represent the contracts for code owned by someone else,
  do not write implementation of the following methods
*/

// Instances provided by factory method
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
