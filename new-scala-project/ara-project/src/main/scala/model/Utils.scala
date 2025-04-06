package model

object Utils {
  def toIntOption(s: String): Option[Int] = 
    if (s.nonEmpty && s.forall(_.isDigit)) Some(s.toInt) else None

  def toDoubleOption(s: String): Option[Double] = 
    if (s.nonEmpty && s.matches("-?\\d*\\.?\\d+")) Some(s.toDouble) else None
}