package controllers

import play.api._
import play.api.mvc._
import models._

import models._
import org.squeryl.PrimitiveTypeMode._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}
