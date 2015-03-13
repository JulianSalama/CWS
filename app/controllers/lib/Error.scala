


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

object Error {

  def apply(message: String) = JsObject(Seq("error" -> JsString(message)))

}

