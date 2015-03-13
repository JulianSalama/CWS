package controllers.lib

import scala.util.DynamicVariable
import play.api.mvc._
import java.io.File

import models._
import controllers.lib.Params
object Current {
 
  val _request = new DynamicVariable[Request[AnyContent]](null)
  def request = _request.value

  val _user = new DynamicVariable[User](null)
  def user = _user.value

  val _params = new DynamicVariable[Params](null)
  def params = _params.value
  
}
