package com.blitz.idm.idp.config

import com.blitz.idm.app.{NestedConfiguration, MainConfiguration, App}

/**
  * The the configuration singleton of IdP application.
  * Example:
  *
  * main-conf {
  *   data-dir = "/opt/data"
  * }
  *
  * idp-conf {
  *   idp-home = ${main-conf.data-dir}"/idp"
 *
 *    web {
 *      context-root = "/idp"
 *      https-port = 8080
 *      weblogin-url = "https://weblogin.org"
 *    }
 *
 *    cache {
 *      session-cache-sec = 3600
 *      login-ctx-cache-sec = 1800
 *      logout-ctx-cache-sec = 1800
 *      attribute-cache-sec = 3600
 *    }
 *
 *    status-page-allowed-ips = "127.0.0.1"
 *    load-balancing-enabled = true
 *    idtoken-lifetime-sec = 3600
 *    entity-id = "http://main.idp.org"
 *
 * }
 */

class IdpConfiguration extends MainConfiguration("idp-conf") {
  implicit val self = this

  val idpHome = getString("idp-home")

  val web = new NestedConfiguration("web")(this) {

    val ctxRoot = getString("context-root")
    val httpsPort = getInt("https-port")
    val webloginUrl = getString("weblogin-url")

  }

  val cache = new NestedConfiguration("cache")(this) {

    val sessionCache = getLong("session-cache-sec")
    val loginCtxCache = getLong("login-ctx-cache-sec")
    val logoutCtxCache = getLong("logout-ctx-cache-sec")
    val attributeCache = getLong("attribute-cache-sec")

  }

  val statusPageAllowedIps = getString("status-page-allowed-ips")
  val loadBalancingEnabled = getBoolean("load-balancing-enabled")
  val idTokenLifetime = getLong("idtoken-lifetime-sec")
  val entityId = getString("entity-id")

}

object IdpApp extends App {

  def name: String = "IdP"

  def configuration[T >: MainConfiguration]: IdpConfiguration = new IdpConfiguration

  lazy val javaProxyConf = IdpConfigurationJavaProxy

}

object IdpConfigurationJavaProxy {

  val idpHome = IdpApp.configuration.idpHome

  val web_ctxRoot = IdpApp.configuration.web.ctxRoot
  val web_httpsPort = IdpApp.configuration.web.httpsPort
  val web_webloginUrl = IdpApp.configuration.web.webloginUrl

  val cache_sessionCache = IdpApp.configuration.cache.sessionCache
  val cache_loginCtxCache = IdpApp.configuration.cache.loginCtxCache
  val cache_logoutCtxCache = IdpApp.configuration.cache.logoutCtxCache
  val cache_attributeCache = IdpApp.configuration.cache.attributeCache

  val statusPageAllowedIps = IdpApp.configuration.statusPageAllowedIps
  val loadBalancingEnabled = IdpApp.configuration.loadBalancingEnabled
  val idTokenLifetime = IdpApp.configuration.idTokenLifetime
  val entityId = IdpApp.configuration.entityId

}