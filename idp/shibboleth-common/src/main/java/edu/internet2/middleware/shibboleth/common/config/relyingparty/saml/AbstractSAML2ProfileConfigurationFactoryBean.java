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

package edu.internet2.middleware.shibboleth.common.config.relyingparty.saml;

import java.util.List;

import edu.internet2.middleware.shibboleth.common.attribute.provider.SAML2AttributeAuthority;
import edu.internet2.middleware.shibboleth.common.relyingparty.provider.CryptoOperationRequirementLevel;
import edu.internet2.middleware.shibboleth.common.relyingparty.provider.saml2.AbstractSAML2ProfileConfiguration;

/**
 * Base Spring factory bean for SAML 2 profile configurations.
 */
public abstract class AbstractSAML2ProfileConfigurationFactoryBean extends AbstractSAMLProfileConfigurationFactoryBean {

    /** Attribute authority for the profile configuration. */
    private SAML2AttributeAuthority attributeAuthority;
    
    /** Whether to encrypt NameIDs. */
    private CryptoOperationRequirementLevel encryptNameIds;

    /** Whether to encryptAssertions. */
    private CryptoOperationRequirementLevel encryptAssertions;

    /** Maximum number of times an assertion may be proxied. */
    private int assertionProxyCount;
    
    /** Audiences for proxied assertions. */
    private List<String> proxyAudiences;

    /**
     * Gets the attribute authority for the profile configuration.
     * 
     * @return attribute authority for the profile configuration
     */
    public SAML2AttributeAuthority getAttributeAuthority(){
        return attributeAuthority;
    }
    
    /**
     * Sets the attribute authority for the profile configuration.
     * 
     * @param authority attribute authority for the profile configuration
     */
    public void setAttributeAuthority(SAML2AttributeAuthority authority){
        attributeAuthority = authority;
    }
    
    /**
     * Gets the maximum number of times an assertion may be proxied.
     * 
     * @return maximum number of times an assertion may be proxied
     */
    public int getAssertionProxyCount() {
        return assertionProxyCount;
    }

    /**
     * Sets the maximum number of times an assertion may be proxied.
     * 
     * @param count maximum number of times an assertion may be proxied
     */
    public void setAssertionProxyCount(int count) {
        assertionProxyCount = count;
    }

    /**
     * Gets whether to encryption assertions.
     * 
     * @return whether to encryption assertions
     */
    public CryptoOperationRequirementLevel isEncryptAssertions() {
        return encryptAssertions;
    }

    /**
     * Sets whether to encryption assertions.
     * 
     * @param encrypt whether to encryption assertions
     */
    public void setEncryptAssertions(CryptoOperationRequirementLevel encrypt) {
        encryptAssertions = encrypt;
    }

    /**
     * Gets whether to encrypt NameIDs.
     * 
     * @return whether to encrypt NameIDs
     */
    public CryptoOperationRequirementLevel isEncryptNameIds() {
        return encryptNameIds;
    }

    /**
     * Sets whether to encrypt NameIDs.
     * 
     * @param encrypt whether to encrypt NameIDs
     */
    public void setEncryptNameIds(CryptoOperationRequirementLevel encrypt) {
        encryptNameIds = encrypt;
    }
    
    /**
     * Gets the audiences for proxied assertions.
     * 
     * @return audiences for proxied assertions
     */
    public List<String> getProxyAudiences(){
        return proxyAudiences;
    }
    
    /**
     * Sets the audiences for proxied assertions.
     * 
     * @param audiences audiences for proxied assertions
     */
    public void setProxyAudiences(List<String> audiences){
        proxyAudiences = audiences;
    }
    
    /**
     * Populates the given profile configuration with standard information.
     * 
     * @param configuration configuration to populate
     */
    protected void populateBean(AbstractSAML2ProfileConfiguration configuration){
        super.populateBean(configuration);
        
        configuration.setAttributeAuthority(getAttributeAuthority());
        configuration.setEncryptAssertion(isEncryptAssertions());
        configuration.setEncryptNameID(isEncryptNameIds());
        configuration.setProxyCount(getAssertionProxyCount());
        
        if(getProxyAudiences() != null){
            configuration.getProxyAudiences().addAll(getProxyAudiences());
        }
    }
}