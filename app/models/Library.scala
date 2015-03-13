
package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp


object Library extends Schema {
  
  val users = table[User]("user")
  on(users)(user => declare(
    user.id is (primaryKey, autoIncremented("user_id_seq")),
    user.facebookID is (unique)
  ))

  val photos = table[Photo]("photo")
  on(photos)(photo => declare(
      photo.id is (primaryKey, autoIncremented("photo_id_seq"))
  ))

  val albums = table[Album]("album")
  on(albums)(album => declare(
    album.id is (primaryKey, autoIncremented("album_id_seq"))
  ))

  val albumUserLinks = table[AlbumUserLink]("album_user_link")
  on(albumUserLinks)(aul => declare(
    aul.id is (primaryKey, autoIncremented("album_user_link_id_seq"))
  ))
  
  override def callbacks = Seq (
    beforeInsert[CreatedAtField] call(c => {
      c.createdAt = Util.now
    } ),
    beforeInsert[UpdatedAtField] call(u => {
      u.updatedAt = Util.now
    } ),
    beforeUpdate[UpdatedAtField] call(u => {
      u.updatedAt = Util.now
    } )
  )

}
