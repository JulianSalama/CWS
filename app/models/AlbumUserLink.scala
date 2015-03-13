
package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._

import org.squeryl.annotations.Column

class AlbumUserLink(@Column("album_id") var albumID: Long,
	    @Column("user_id") var userID: Long) 
extends LongKeyedEntity
with CreatedAtField 
with UpdatedAtField {

  def this() = this(0L, 0L)

  def user: Option[User] = User.findUserByUserID(userID)
    
  def album: Option[Album] = Album.findAlbumByAlbumID(albumID)
}


object AlbumUserLink extends Util[AlbumUserLink]{

  def getAlbumsForUser(user: User) = {
    val ids = from(Library.albumUserLinks)(aul => where(aul.userID === user.id) select(aul.albumID)).toList
    
    from(Library.albums)(a => where(a.id in ids) select(a) orderBy(a.createdAt desc)).toList

  }

  def getRestrictionsOnAlbum(album: Album) = {
    from(Library.users)(u => where(u.id in 
				   from(Library.albumUserLinks)(aul => where(aul.albumID === album.id) 
								select(aul.userID)).toList) select (u)).toList
  }
}




