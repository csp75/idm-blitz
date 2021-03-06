/*
 *  Copyright 2009 NIIF Institute.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package edu.internet2.middleware.shibboleth.idp.slo;

import edu.internet2.middleware.shibboleth.idp.util.HttpServletHelper;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.opensaml.util.storage.StorageService;
import org.opensaml.xml.util.DatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Adam Lantos  NIIF / HUNGARNET
 */
public class SingleLogoutContextStorageHelper {

    /** Name of the key to the current single logout context: {@value} . */
    public static final String SLO_CTX_KEY_NAME = "_idp_slo_ctx_key";
    /**
     * {@link ServletContext} parameter name bearing the name of the {@link StorageService} partition into which
     * {@link SingleLogoutContext}s are stored: {@value} .
     */
    public static final String SLO_CTX_PARTITION_CTX_PARAM =
            "sloContextPartitionName";
    /** Default name for the {@link StorageService} partition which holds
     * {@link SingleLogoutContext}s: {@value} . */
    public static final String DEFAULT_SLO_CTX_PARTITION = "sloContexts";
    /** Class logger. */
    private static final Logger log =
            LoggerFactory.getLogger(SingleLogoutContextStorageHelper.class);

    /**
     * Gets the single logout context from the current request. The logout context
     * is only in this location while the request is being transferred from the
     * Single logout servlet to the SLO Profile handler.
     *
     * @param httpRequest current HTTP request
     *
     * @return the single logout context or null if no logout context is bound to the request
     */
    public static SingleLogoutContext getSingleLogoutContext(HttpServletRequest httpRequest) {
        return (SingleLogoutContext) httpRequest.getAttribute(SLO_CTX_KEY_NAME);
    }

    /**
     * Gets the {@link SingleLogoutContext} for the user issuing the HTTP request.
     *
     * @param context the Servlet context
     * @param storageService storage service to use when retrieving the login context
     * @param httpRequest current HTTP request
     *
     * @return the single logout context or null if none is available
     */
    @SuppressWarnings("unchecked")
    public static SingleLogoutContext getSingleLogoutContext(StorageService storageService, ServletContext context,
            HttpServletRequest httpRequest) {
        if (storageService == null) {
            throw new IllegalArgumentException("Storage service may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("Servlet context may not be null");
        }
        if (httpRequest == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }

        SingleLogoutContext sloContext = getSingleLogoutContext(httpRequest);
        if (sloContext == null) {
            log.debug("SingleLogoutContext not bound to HTTP request, retrieving it from storage service");
            Cookie sloContextKeyCookie =
                    HttpServletHelper.getCookie(httpRequest, SLO_CTX_KEY_NAME);
            if (sloContextKeyCookie == null) {
                log.debug("SingleLogoutContext key cookie was not present in request");
                return null;
            }

            String sloContextKey =
                    DatatypeHelper.safeTrimOrNullString(sloContextKeyCookie.getValue());
            if (sloContextKey == null) {
                log.warn("Corrupted SingleLogoutContext Key cookie, it did not contain a value");
            }
            log.debug("SingleLogoutContext key is '{}'", sloContextKey);

            String partition =
                    HttpServletHelper.getContextParam(context, SLO_CTX_PARTITION_CTX_PARAM, DEFAULT_SLO_CTX_PARTITION);
            log.debug("partition: {}", partition);
            SingleLogoutContextEntry entry =
                    (SingleLogoutContextEntry) storageService.get(partition, sloContextKey);
            if (entry != null) {
                if (entry.isExpired()) {
                    log.debug("SingleLogoutContext found but it was expired");
                } else {
                    sloContext = entry.getSingleLogoutContext();
                }
            } else {
                log.debug("No single logout context in storage service");
            }
        }

        return sloContext;
    }

    /**
     * Binds a {@link SingleLogoutContext} to the current request.
     *
     * @param sloContext login context to be bound
     * @param httpRequest current HTTP request
     */
    public static void bindSingleLogoutContext(SingleLogoutContext sloContext, HttpServletRequest httpRequest) {
        if (httpRequest == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        httpRequest.setAttribute(SLO_CTX_KEY_NAME, sloContext);
    }

    /**
     * Binds a {@link SingleLogoutContext} to the issuer of the current request.
     * The binding is done by creating a random UUID, placing that in a cookie
     * in the request, and storing the context in to the storage service under that key.
     *
     * @param sloContext the single logout context to be bound
     * @param storageService the storage service which will hold the context
     * @param context the Servlet context
     * @param httpRequest the current HTTP request
     * @param httpResponse the current HTTP response
     */
    public static void bindSingleLogoutContext(SingleLogoutContext sloContext, StorageService storageService,
            ServletContext context, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        if (storageService == null) {
            throw new IllegalArgumentException("Storage service may not be null");
        }
        if (httpRequest == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (sloContext == null) {
            return;
        }

        bindSingleLogoutContext(sloContext, httpRequest);


        String partition = HttpServletHelper.getContextParam(
                context, SLO_CTX_PARTITION_CTX_PARAM, DEFAULT_SLO_CTX_PARTITION);
        log.debug("SingleLogoutContext partition: {}", partition);

        /* BLITZ patch (deleted) : External storage service support

        String contextKey = UUID.randomUUID().toString();
        while (storageService.contains(partition, contextKey)) {
            contextKey = UUID.randomUUID().toString();
        }
        log.debug("SingleLogoutContext key: {}", contextKey);

        SingleLogoutContextEntry entry =
                new SingleLogoutContextEntry(sloContext, 1800000);
        storageService.put(partition, contextKey, entry);
        */

        log.debug("SingleLogoutContext key: {}", sloContext.getContextKey());
        Cookie contextKeyCookie = new Cookie(SLO_CTX_KEY_NAME, sloContext.getContextKey());
        contextKeyCookie.setPath("".equals(httpRequest.getContextPath()) ? "/" : httpRequest.getContextPath());

        /* BLITZ patch (added) : Security reason */
        contextKeyCookie.setSecure(true);
        contextKeyCookie.setHttpOnly(true); // httpRequest.isSecure()
        //

        httpResponse.addCookie(contextKeyCookie);
    }

    /**
     * Unbinds a {@link SingleLogoutContext} from the current request.
     * The unbinding results in the destruction of the associated context key
     * cookie and removes the context from the storage service.
     *
     * @param storageService storage service holding the context
     * @param context the Servlet context
     * @param httpRequest current HTTP request
     * @param httpResponse current HTTP response
     *
     * @return the login context that was unbound or null if there was no bound context
     */
    @SuppressWarnings("unchecked")
    public static SingleLogoutContext unbindSingleLogoutContext(StorageService storageService, ServletContext context,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        if (storageService == null || context == null || httpRequest == null ||
                httpResponse == null) {
            return null;
        }

        String sloContextKey = removeSingleLogoutContextCookie(httpRequest, httpResponse);
        if (sloContextKey == null) {
            return null;
        }

        SingleLogoutContextEntry entry =
                (SingleLogoutContextEntry) storageService.remove(HttpServletHelper.getContextParam(context,
                SLO_CTX_PARTITION_CTX_PARAM, DEFAULT_SLO_CTX_PARTITION), sloContextKey);
        if (entry != null && !entry.isExpired()) {
            return entry.getSingleLogoutContext();
        }
        return null;
    }

    /**
     * Removes cookie for SingleLogoutContext and returns the logout context key.
     * 
     * @param httpRequest
     * @param httpResponse
     */
    protected static String removeSingleLogoutContextCookie(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Cookie sloContextKeyCookie =
                HttpServletHelper.getCookie(httpRequest, SLO_CTX_KEY_NAME);
        if (sloContextKeyCookie == null) {
            return null;
        }
        String sloContextKey =
                DatatypeHelper.safeTrimOrNullString(sloContextKeyCookie.getValue());
        if (sloContextKey == null) {
            log.warn("Corrupted SingleLogoutContext Key cookie, it did not contain a value");
        }

        if (sloContextKeyCookie == null) {
            return null;
        }
        sloContextKeyCookie.setMaxAge(0);

        /* BLITZ patch (added) : Security reason */
        sloContextKeyCookie.setSecure(true);
        sloContextKeyCookie.setHttpOnly(true);
        //

        httpResponse.addCookie(sloContextKeyCookie);

        return sloContextKey;
    }
}
