

package models

import java.sql.Timestamp
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

object Util {

  def now = new Timestamp(System.currentTimeMillis)

}

trait Util [T <: LongKeyedEntity] {

  protected def first(q: Query[T]): Option[T] = {
    val l = q.toList
    if (l.isEmpty)
      return None
    Some(l.head)
  }

  protected def all(q: Query[T]): List[T] = {
    q.toList
  }
  
}

