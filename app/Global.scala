import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter}
import org.squeryl.{Session, SessionFactory}
import play.api.GlobalSettings
import play.api.Application
import org.squeryl.PrimitiveTypeMode._
import play.api.Play

object Global extends GlobalSettings {

  override def onStop(app: Application) {
  }

  override def onStart(app: Application) {
    Class.forName(Play.current.configuration.getString("db.default.driver").get);
    
    SessionFactory.concreteFactory = Some(()=>
        Session.create(
	java.sql.DriverManager.getConnection(Play.current.configuration.getString("db.default.url").get,
		Play.current.configuration.getString("db.default.user").get,
		Play.current.configuration.getString("db.default.password").get),
	new PostgreSqlAdapter))

  }


}
