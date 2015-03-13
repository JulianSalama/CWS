

package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import org.squeryl.annotations.Column

class User(val fullname: String,
	   @Column("facebook_id") val facebookID: String) 
extends LongKeyedEntity 
with CreatedAtField 
with UpdatedAtField {

  def this() = this("", "")
 
}

object User extends Util[User] {

  def createUser(fullname: String, facebookID: String) = {
    val user = new User(fullname, facebookID)
    Library.users.insert(user)
  }

  def findUserByFacebookID(facebookID: String): Option[User] = {
    first(from(Library.users)(u => where(u.facebookID === facebookID) select(u)))
  }

  def findUserByUserID(userID: Long): Option[User] = {
    first(from(Library.users)(u=> where (u.id === userID) select(u)))
  }
 
}





