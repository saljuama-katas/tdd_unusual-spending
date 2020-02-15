package spending

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
