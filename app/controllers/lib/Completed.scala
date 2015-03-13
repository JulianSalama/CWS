
package controllers.lib

import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.functional.syntax._


object Completed {

  def apply(message: String) = JsObject(Seq("completed" -> JsString(message)))

}
