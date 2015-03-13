package controllers

import play.api._
import play.api.mvc._
import models._

import models._
import org.squeryl.PrimitiveTypeMode._
import controllers.lib.{Withs, FBApiCalls}


import play.api.libs.ws._
import scala.concurrent.Future
import play.libs.Json
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.Await
import play.api.libs.concurrent.Execution.Implicits._


import play.api.libs.json._

import play.api.libs.functional.syntax._

import controllers.lib._

object Albums extends Controller {

 def editAlbum() = Withs.withUserCreated { implicit request => {
   val user = Current.user
   request.body.asJson match {
     case Some(json) => {
       (json \ "name").asOpt[String] match {
	 case Some(name) => {
	   val album: Album = (json \ "id").asOpt[Long] match {
	     case Some(id) => {
	       Album.findAlbumByAlbumID(id) match {
		 case Some(album) => {
		   album.name = name
		   album.userID = user.id
		   Library.albums.update(album)
		   album
		 }
		 case None => {
		   val newAlbum = new Album()
		   newAlbum.name = name
		   newAlbum.userID = user.id
		   Library.albums.insert(newAlbum)
		 }
	       }
	     }
	     case None => {
	       val newAlbum = new Album()
	       newAlbum.name = name
	       newAlbum.userID = user.id
	       Library.albums.insert(newAlbum)
	     }
	   }

	   // add friendIDs
	   (json \ "friendIDs").asOpt[Array[String]] match {
	     case Some(ids) => {
	       val aul = new AlbumUserLink()
	       aul.albumID = album.id
	       aul.userID = user.id
	       Library.albumUserLinks.insert(aul)
	       ids.map(id => {
		 User.findUserByFacebookID(id) match {
		   case Some(u) => {
		     val aul = new AlbumUserLink()
		     aul.albumID = album.id
		     aul.userID = u.id.toLong	
		     Library.albumUserLinks.insert(aul)
		   }
		   case None => 
		 }
	       })
	     }
	     case None =>
	   }
	   
	   // add photoIDs
	   val photoIDs: Array[Int] = ((json \ "localPhotoCount").asOpt[Int] match {
	     case Some(count) => { 
	       var i = 0
	       var list = new Array[Int](count)
	       for (i <- 0 to count-1) { 		 
		 val p = new Photo()
		 p.albumID = album.id
		 list(i) = Library.photos.insert(p).id.toInt	       
	       }
	       list
	     }
	     case None => new Array[Int](0)
	   })
	   
	   Ok(JsObject(Seq(
	     "albumID" -> JsNumber(album.id),
	     "photoIDs" -> JsArray(photoIDs.map(id => JsObject(Seq("id" -> JsNumber(id)))))
	   )))
	 }
	 case None => Ok(Error("no name was provided"))
       }
     }
     case None => Ok(Error("Something went wrong"))
   }
   
  } }

  def getAlbums() = Withs.withUserCreated { implicit request => {
    val user = Current.user
    
    val json = JsObject(Seq(
      "albums" -> JsArray(
	AlbumUserLink.getAlbumsForUser(user).map(
	  a => { JsObject(Seq(
	    "name"    -> JsString(a.name),
	    "id"      -> JsNumber(a.id),
	    "friends" -> JsArray(AlbumUserLink.getRestrictionsOnAlbum(a).map(
	      u => { 
		JsObject(Seq("fullName" -> JsString(u.fullname),
				  "id" -> JsString(u.id.toString),
				  "facebookID" -> JsString(u.facebookID)))
		   })),
	    "photos"  -> JsArray(Photo.getPhotoIDsForAlbum(a).map(p => JsObject(Seq("id" -> JsNumber(p)))))
	  ))}
	  ))))

    Ok(json)
  } }
  
    
}
