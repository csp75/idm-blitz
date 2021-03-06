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

package edu.internet2.middleware.shibboleth.common.relyingparty.provider.saml1;

import edu.internet2.middleware.shibboleth.common.attribute.provider.SAML1AttributeAuthority;
import edu.internet2.middleware.shibboleth.common.relyingparty.provider.AbstractSAMLProfileConfiguration;

/**
 * SAML 1 communication profile configuration settings.
 */
public abstract class AbstractSAML1ProfileConfiguration extends AbstractSAMLProfileConfiguration {

    /** Attribute authority to use. */
    private SAML1AttributeAuthority attributeAuthority;
    
    /**
     * Gets the Attribute authority to use.
     * 
     * @return Attribute authority to use
     */
    public SAML1AttributeAuthority getAttributeAuthority(){
        return attributeAuthority;
    }
    
    /**
     * Sets the Attribute authority to use.
     * 
     * @param authority Attribute authority to use
     */
    public void setAttributeAuthority(SAML1AttributeAuthority authority){
        attributeAuthority = authority;
    }
}