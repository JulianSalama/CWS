

package models

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._

class Photo(var blob: Option[Array[Byte]], 
	  @Column("album_id") var albumID: Long) 
extends LongKeyedEntity
with CreatedAtField 
with UpdatedAtField {

  def this() = this(None, 0L)

}

object Photo extends Util[Photo] {

  def deletePhotos(ids: Array[Long]) {
    Library.photos.deleteWhere(p => (p.id in ids.toSeq))
  }

  def getPhoto(id: Long): Option[Photo] = {
    first(from(Library.photos)(p => where(p.id === id) select(p)))
  }
  
  // order by updatedAt
  def getPhotoIDsForAlbum(album: Album) = {
    from(Library.photos)(p => where(p.albumID === album.id) select(p.id)  orderBy(p.createdAt desc)).toList
  }

}




