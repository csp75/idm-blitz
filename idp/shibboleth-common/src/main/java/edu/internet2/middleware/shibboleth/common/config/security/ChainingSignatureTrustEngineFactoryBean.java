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

package edu.internet2.middleware.shibboleth.common.config.security;

import java.util.List;

import org.opensaml.xml.security.trust.ChainingTrustEngine;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.signature.impl.ChainingSignatureTrustEngine;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Spring factory bean used to created {@link ChainingSignatureTrustEngine}s.
 */
public class ChainingSignatureTrustEngineFactoryBean extends AbstractFactoryBean {

    /** List of chain members. */
    private List<SignatureTrustEngine> chain;

    /**
     * Gets the chain member list.
     * 
     * @return chain member list
     */
    public List<SignatureTrustEngine> getChain() {
        return chain;
    }

    /**
     * Sets the chain member list.
     * 
     * @param newChain the new chain member list
     */
    public void setChain(List<SignatureTrustEngine> newChain) {
        chain = newChain;
    }

    /** {@inheritDoc} */
    public Class getObjectType() {
        return ChainingTrustEngine.class;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    protected Object createInstance() throws Exception {
        ChainingSignatureTrustEngine engine = new ChainingSignatureTrustEngine();
        if (chain != null) {
            engine.getChain().addAll(chain);
        }
        return engine;
    }
}