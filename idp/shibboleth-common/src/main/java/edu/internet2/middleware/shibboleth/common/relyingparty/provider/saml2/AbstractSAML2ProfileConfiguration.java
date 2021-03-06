/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.shibboleth.common.relyingparty.provider.saml2;

import java.util.Collection;

import org.opensaml.xml.util.LazySet;

import edu.internet2.middleware.shibboleth.common.attribute.provider.SAML2AttributeAuthority;
import edu.internet2.middleware.shibboleth.common.relyingparty.provider.AbstractSAMLProfileConfiguration;
import edu.internet2.middleware.shibboleth.common.relyingparty.provider.CryptoOperationRequirementLevel;

/**
 * SAML 2 communication profile configuration settings.
 */
public abstract class AbstractSAML2ProfileConfiguration extends AbstractSAMLProfileConfiguration {

    /** Attribute authority to use. */
    private SAML2AttributeAuthority attributeAuthority;

    /** Whether to encrypt NameIDs. */
    private CryptoOperationRequirementLevel encryptNameID;

    /** Whether to encrypt Assertions. */
    private CryptoOperationRequirementLevel encryptAssertion;

    /** Maximum proxy count for an assertion. */
    private int proxyCount;

    /** Audiences for the proxy. */
    private Collection<String> proxyAudiences;

    /** Constructor. */
    protected AbstractSAML2ProfileConfiguration() {
        proxyAudiences = new LazySet<String>();
    }

    /**
     * Gets the Attribute authority to use.
     * 
     * @return Attribute authority to use
     */
    public SAML2AttributeAuthority getAttributeAuthority() {
        return attributeAuthority;
    }

    /**
     * Sets the Attribute authority to use.
     * 
     * @param authority Attribute authority to use
     */
    public void setAttributeAuthority(SAML2AttributeAuthority authority) {
        attributeAuthority = authority;
    }

    /**
     * Gets whether NameIDs should be encrypted.
     * 
     * @return whether NameIDs should be encrypted
     */
    public CryptoOperationRequirementLevel getEncryptNameID() {
        return encryptNameID;
    }

    /**
     * Sets whether NameIDs should be encrypted.
     * 
     * @param encrypt whether NameIDs should be encrypted
     */
    public void setEncryptNameID(CryptoOperationRequirementLevel encrypt) {
        encryptNameID = encrypt;
    }

    /**
     * Gets whether assertions should be encrypted.
     * 
     * @return whether assertions should be encrypted
     */
    public CryptoOperationRequirementLevel getEncryptAssertion() {
        return encryptAssertion;
    }

    /**
     * Sets whether assertions should be encrypted.
     * 
     * @param encrypt whether assertions should be encrypted
     */
    public void setEncryptAssertion(CryptoOperationRequirementLevel encrypt) {
        encryptAssertion = encrypt;
    }

    /**
     * Gets the maximum number of times an assertion may be proxied.
     * 
     * @return maximum number of times an assertion may be proxied
     */
    public int getProxyCount() {
        return proxyCount;
    }

    /**
     * Gets the maximum number of times an assertion may be proxied.
     * 
     * @param count maximum number of times an assertion may be proxied
     */
    public void setProxyCount(int count) {
        proxyCount = count;
    }

    /**
     * Gets the audiences for a proxied assertion.
     * 
     * @return audiences for a proxied assertion
     */
    public Collection<String> getProxyAudiences() {
        return proxyAudiences;
    }
}