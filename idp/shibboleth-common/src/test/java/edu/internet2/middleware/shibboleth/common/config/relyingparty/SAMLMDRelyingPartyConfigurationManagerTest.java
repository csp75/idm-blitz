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

package edu.internet2.middleware.shibboleth.common.config.relyingparty;

import org.springframework.context.ApplicationContext;

import edu.internet2.middleware.shibboleth.common.config.BaseConfigTestCase;
import edu.internet2.middleware.shibboleth.common.relyingparty.provider.SAMLMDRelyingPartyConfigurationManager;

/**
 * Unit test of {@link SAMLMDRelyingPartyConfigurationManager}.
 */
public class SAMLMDRelyingPartyConfigurationManagerTest extends BaseConfigTestCase {

    /** Test loading the relying party configuration manager. */
    public void testManager() throws Exception {
        String[] configs = { DATA_PATH + "/config/base-config.xml",
                DATA_PATH + "/config/relyingparty/service-config.xml", };

        ApplicationContext appContext = createSpringContext(configs);
        SAMLMDRelyingPartyConfigurationManager rpConfigMgr = (SAMLMDRelyingPartyConfigurationManager) appContext
                .getBean("relyingPartyManager");

        assertNotNull(rpConfigMgr);
        assertNotNull(rpConfigMgr.getAnonymousRelyingConfiguration());
        assertNotNull(rpConfigMgr.getDefaultRelyingPartyConfiguration());
        assertNotNull(rpConfigMgr.getRelyingPartyConfiguration("urn:mace:incommon"));
        assertNotNull(rpConfigMgr.getMetadataProvider());
    }
}