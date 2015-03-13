

package models
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.annotations.Column

class Album(var name: String,
	    @Column("user_id") var userID: Long) 
extends LongKeyedEntity 
with CreatedAtField 
with UpdatedAtField {

  def this() = this("", 0L)
  
}

object Album extends Util[Album] {

  def findAlbumByAlbumID(albumID: Long):Option[Album] = {
    first(from(Library.albums)(a => where(a.id === albumID) select(a)))
  }

}
 



