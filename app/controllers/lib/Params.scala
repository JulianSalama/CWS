
package controllers.lib

import java.io.File
import org.apache.commons.io.FileUtils

import scala.collection.mutable.{HashMap,ListBuffer}
import scala.xml._

import play.api._
import play.api.mvc._
import play.api.libs.Files.TemporaryFile

object Params {

  def apply(request: Request[AnyContent]) = new RequestParams(request)

}

abstract class Params {
  protected def data: Option[Map[String, Seq[String]]]

  protected def trim(s: String): String = {
    s.replaceAll("""\s+$""", "").replaceAll("""^\s+""", "")
  }

  def has(key: String) = getAll(key).isDefined

  def names: Set[String] = {
    data match {
      case Some(dm) => dm.keySet
      case None => Set[String]()
    }
  }

  def getAll(key: String): Option[Seq[String]] = {
    data match {
      case Some(dm) => dm.get(key) match {
        case Some(vs) => Some(vs.map(s => trim(s)))
        case None => None
      }
      case _ => None
    }
  }

  def get(key: String): Option[String] = {
    getAll(key) match {
      case Some(vs) => if (!vs.isEmpty) Some(vs.head) else None
      case _ => None
    }
  }

  def get(key: String, default: String): String = {
    get(key) match {
      case Some(v) => v
      case None => default
    }
  }

  def getLong(key: String): Option[Long] = {
    get(key) match {
      case Some(s) => Some(s.toLong)
      case _ => None
    }
  }

  def getLong(key: String, default: Long): Long = {
    get(key) match {
      case Some(s) => s.toLong
      case _ => default
    }
  }

  def isMultipart: Boolean

  def getFile(key: String): Option[MultipartFormData.FilePart[TemporaryFile]]
  def getFiles(key: String): Seq[MultipartFormData.FilePart[TemporaryFile]]

  // Get a blob from a multipart/form-data post as a byte array.
  def getData(key: String): Option[Array[Byte]]

  def toMap: Map[String, Seq[String]] = data.get

  def toXML: xml.NodeSeq = {
    <params>{names.map {
      case n: String => {
        <param name={n}>{getAll(n) match {
          case Some(vs) => vs.map(v => <value>{v}</value>)
          case None => NodeSeq.Empty
        }}</param>
      }
    }}</params>
  }

  override def toString = data.toString

}

class RequestParams(val request: Request[AnyContent]) extends Params {
  private var _data: Option[Map[String, Seq[String]]] = None

  override protected def data: Option[Map[String, Seq[String]]] = {
    _data match {
      case Some(d) => Some(d)
      case None => {
        _data = Some(request.queryString ++ (request.body.asFormUrlEncoded match {
          case Some(bd) => bd
          case None => request.body.asMultipartFormData match {
            case Some(bd) => bd.dataParts
            case _ => Map[String, Seq[String]]()
          }
        }))
        _data
      }
    }
  }

  override def isMultipart = request.body.asMultipartFormData.isDefined

  override def getFiles(key: String): Seq[MultipartFormData.FilePart[TemporaryFile]] = {
    val lb = ListBuffer.empty[MultipartFormData.FilePart[TemporaryFile]]
    request.body.asMultipartFormData match {
      case Some(bd) => {
        for (bdf <- bd.files) {
          if (bdf.key == key) {
            if (bdf.ref.file.length > 10 * 1024 * 1024)
              throw new Exception("Uploaded file size is too large (limit is %s Mb)".format("10"))
            lb += bdf
          }
        }
      }
      case _ =>
    }
    lb.toSeq
  }

  override def getFile(key: String): Option[MultipartFormData.FilePart[TemporaryFile]] = {
    request.body.asMultipartFormData match {
      case Some(bd) => {
        bd.file(key) match {
          case Some(f) => {
            if (f.ref.file.length > 10 * 1024 * 1024)
              throw new Exception("Uploaded file size is too large (limit is %s Mb)".format("10"))
            Some(f)
          }
          case _ => None
        }
      }
      case None => throw new Exception("Error: Current.formParams.getFile() called in request where form is not enctype=\"multipart/form-data\".")
    }
  }

  override def getData(key: String): Option[Array[Byte]] = {
    getFile(key) match {
      case Some(file) => {
	val f = new File("/tmp/CWS_upload%s".format(Util.randomString(10)))
        val tempFile = new TemporaryFile(f).file
        file.ref.moveTo(tempFile, true)
        Some(FileUtils.readFileToByteArray(tempFile))
      }
      case _ => None
    }
  }

}

