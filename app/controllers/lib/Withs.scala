package controllers.lib

import java.security.MessageDigest

import scala.collection.mutable.HashMap

import org.apache.commons.codec.binary.Hex

import play.api._
import play.api.mvc._
import play.api.libs.iteratee.Enumerator

import models._

import org.squeryl.PrimitiveTypeMode._


import controllers._
import models._

import views._

import play.api.libs.ws._
import scala.concurrent.Future
import play.libs.Json
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.Await
import play.api.libs.concurrent.Execution.Implicits._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.net.URLDecoder


object Withs extends Controller {

  def WithFacebookValidation(f: Request[AnyContent] => SimpleResult) =  {
    Action { implicit request => {
      if (request.body.asMultipartFormData.isDefined || request.method == "GET") {
	Current._params.withValue(Params(request)) {
	  println(Current.params.names)
	  val fullname = Current.params.get("fullName")
	  val fid = Current.params.get("facebookID").get
	  val token = Current.params.get("FBaccessToken").get
	  if (FBApiCalls.isTokenValid(token)) {
	    inTransaction {
	      val user: Option[User] = User.findUserByFacebookID(fid) match {
		case Some(user) => Some(user)
		case None => Some(User.createUser(fullname.get, fid))
	      }
	      user match {
		case Some(u) => {
		  Current._user.withValue(u) {
		    println("AM I HERE 3") 
		    f(request)
		  }
		}
		case None => Ok(Error("You are not logged in."))
	      }
	    }
	  } else {
	    Ok(Error("Facebook access token is invalid."))
	  }
	}
      } else {
	request.body.asJson match {
	  case Some(json) => {
	    (json \ "FBaccessToken").asOpt[String] match {
	      case Some(token) => {
		if (FBApiCalls.isTokenValid(token)) {
 		  f(request)
		} else {
		  Ok(Error("Facebook access token is invalid."))
		}
	      }
	      case None => {
		Ok(Error("Facebook access token was not found."))
	      }
	    }
	  }
	  case None => {
	    Ok(Error("Something went wrong."))
	  }
	}
      }
    } }
  }


  def withUserCreated(f: Request[AnyContent] => SimpleResult) = {
    WithFacebookValidation { implicit request => {
      val json = request.body.asJson.get 
      (json \ "facebookID").asOpt[String] match {
	case Some(s) => {
	  inTransaction {
	    val user: Option[User] = User.findUserByFacebookID(s) match {
	      case Some(user) => Some(user)
	      case None => { 
		(json \ "fullName").asOpt[String] match {
		  case Some(fullname) => Some(User.createUser(fullname, s))
		  case None => None
		} }
	    }
	    user match {
	      case Some(u) => {
		Current._user.withValue(u) {
		  Current._params.withValue(Params(request)) {
		    f(request)
		  }
		}
	      }
	      case None => Ok(Error("User was not found"))
	    }
	  }
	}
	case None => {
	  Ok(Error("User facebook ID was not found"))
	} 
      }
    } }
  }


}


object FBApiCalls {

  def isTokenValid(fbToken: String): Boolean = {
    /*
    *
    *
    * GET /debug_token?
    *    input_token={input-token}&
    *    access_token={app_id} | {app_secrete}
    * 
    **/
    val fbGetAppAccessToken = "https://graph.facebook.com/debug_token" 
    val future: Future[Boolean] = WS.url(fbGetAppAccessToken).withQueryString("input_token" -> fbToken, "access_token" -> "784824731572342|2b382e7f1badb17895ff142fa30edcd1").get().map {
      response => {
	val json = response.json
	val isValid: Seq[JsValue] = (json \\ "is_valid")
	isValid.head.asOpt[JsValue] match {
	  case Some(s) => s.toString == "true"
	  case None => false
	}
      }
    }    
    Await.result(future, 10 seconds): Boolean
  }

}
