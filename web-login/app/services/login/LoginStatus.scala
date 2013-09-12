package services.login

import com.blitz.idm.app.CustomEnumeration
import com.blitz.idm.EnumerationMacros._

sealed abstract class LoginStatus(private val _name: String) extends LoginStatus.Val {
  def name = _name
}

/**
 * Enumeration of the login statuses.
 */
object LoginStatus extends CustomEnumeration[LoginStatus] {
  INIT_ENUM_ELEMENTS()

  case object INITIAL extends LoginStatus("initial")
  case object SUCCESS extends LoginStatus("success")
  case object FAIL extends LoginStatus("fail")
  case object PROCESSING extends LoginStatus("processing")
}