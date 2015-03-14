
package controllers

import play.api._
import play.api.mvc._

import org.squeryl.PrimitiveTypeMode._

import play.api.libs.ws._
import scala.concurrent.Future
import play.libs.Json
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.Await
import play.api.libs.concurrent.Execution.Implicits._

import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.apache.commons.io.FileUtils

import models.{Photo, Library}
import controllers.lib.{Withs, Current, Error, Completed}

import com.sksamuel.scrimage._

object Photos extends Controller  {

  def getPhoto(id: Long) = Withs.withUserCreated  { request => {
    Photo.getPhoto(id) match {
      case Some(s) => {
	Ok(s.blob.get).as("image/png")
      }
      case None => Ok(Error("Photo was not found."))
    }
  } }

  def getPhoto2(id: Long, fbtoken: Option[String], fbid: Option[String]) = Withs.WithFacebookValidation  { request => {
    Photo.getPhoto(id) match {
      case Some(s) => {
	Ok(s.blob.get).as("image/png")
      }
      case None => Ok(Error("Photo was not found."))
    }
  } }

  def getThumbnail(id: Long, fbtoken: Option[String], fbid: Option[String]) = Withs.WithFacebookValidation { request => {
    Photo.getPhoto(id) match {
      case Some(s) => {
	val image = Image(s.blob.get)
	val width = image.width.toDouble
	val height = image.height.toDouble
	val maxLenght = 300.0
	if (width > height) {
	  val ratio = height / width
	  Ok(image.fit(maxLenght.toInt, (maxLenght * ratio).toInt).write(com.sksamuel.scrimage.Format.PNG)).as("image/png") //withCompression(50).write(out)	
	} else {
	  val ratio = width/height
	  Ok(image.fit((maxLenght * ratio).toInt, maxLenght.toInt).write(com.sksamuel.scrimage.Format.PNG)).as("image/png") //.withCompression(50).write(out)	
	}
      }
      case None => Ok(Error("Photo was not found."))
    }
  } }

  def deletePhotos() = Withs.withUserCreated { request => {
    request.body.asJson match {
      case Some(json) => {
	(json \ "ids").asOpt[Array[Long]] match {
	  case Some(ids) => {
	    Photo.deletePhotos(ids)
	    Ok(Completed("photo deleted"))
	  }
	  case None => Ok(Error("Could not delete photo"))
	}
      }
      case None =>
    }
    Ok(Completed("photo deleted"))
  } }


  def doUpload(id: Long) = Withs.WithFacebookValidation { request => { 
    Current.params.getData("picture") match {
      case Some(arr) => {
	Photo.getPhoto(id) match {
	  case Some(p) => {
	    p.blob = Some(arr)
	    Library.photos.update(p)
	    Ok(Completed(""))
	  }
	  case None => Ok(Error("Photo was not found"))
	}
      }
      case None => Ok(Error("Uploaded picture was not found"))
    }

  } }


}


/*
*/


