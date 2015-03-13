package controllers

import play.api._
import play.api.mvc._
import models._

import models._
import org.squeryl.PrimitiveTypeMode._
import controllers.lib.{Withs, FBApiCalls}


import play.api.libs.json._
import play.api.libs.functional.syntax._

object Users extends Controller {
  // not used
  def getFriendIDs() = Withs.withUserCreated { implicit request => {
    request.body.asJson match {
      case Some(json) => {
	println(request.body.toString)
	println(json)
      }
      case None =>
    }
    Ok("")
  } }





}
