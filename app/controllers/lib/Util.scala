package controllers.lib

import scala.util.{Random => R}

object Util {

  def randomString(length: Int) = {
    val n = if (length < 1) 1 else length
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val s = new Array[Char](n)
    for (i <- 0 until n)
      s(i) = chars.charAt(R.nextInt(chars.length))
    new String(s)
  }

}
