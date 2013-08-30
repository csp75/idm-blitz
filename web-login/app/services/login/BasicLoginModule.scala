package services.login

import play.api.mvc.{AnyContent, Request}

/**
 */
trait BasicLoginModule extends LoginModule {

  object Obligations extends Enumeration {
    type Obligations = Value

    val CHANGE_PASSWORD = Value("change_password")

    class ObligationsVal(obligation: Value)  {
    }

    implicit def valueToObligations(obligation: Value) = new ObligationsVal(obligation)
  }

  /**
   * Changes a subject's password.
   * @param curPswd the current subject's password.
   * @param newPswd the new subject's password.
   * @param lc the current login context.
   * @param request the current HTTP request.
   * @return the result of the operation. True if password has been changed successfully and false otherwise.
   *         Errors can be obtained from the current login context.
   */
  def changePassword(curPswd: String, newPswd: String)(implicit lc: LoginContext, request: Request[AnyContent]): Boolean
}
