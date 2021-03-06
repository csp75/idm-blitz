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

package edu.internet2.middleware.shibboleth.common.config.attribute.resolver;

import java.util.Map;

import org.opensaml.util.resource.ResourceException;
import org.springframework.context.ApplicationContext;

import edu.internet2.middleware.shibboleth.common.attribute.BaseAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolver;
import edu.internet2.middleware.shibboleth.common.config.BaseConfigTestCase;
import edu.internet2.middleware.shibboleth.common.profile.provider.BaseSAMLProfileRequestContext;

/**
 * Test configuration code for attribute resolver.
 */
public class BasicAttributeResolverTest extends BaseConfigTestCase {

    /**
     * Test Handle Request.
     * 
     * @throws ResourceException if unable to access resource
     * @throws AttributeResolutionException if unable to resolve attributes
     */
    public void testResolverInstantiation() throws ResourceException, AttributeResolutionException {
        ApplicationContext ac = createSpringContext(new String[] { DATA_PATH + "/config/base-config.xml",
                DATA_PATH + "/config/attribute/resolver/resolver-config.xml", });
        AttributeResolver resolver = (AttributeResolver) ac.getBean("resolver");

        BaseSAMLProfileRequestContext context = new BaseSAMLProfileRequestContext();
        context.setPrincipalName("ttrojan");

        Map<String, BaseAttribute> actual = resolver.resolveAttributes(context);

        assertEquals(3, actual.size());

        BaseAttribute principalName = actual.get("principalName");
        assertEquals(1, principalName.getValues().size());

        BaseAttribute affiliation = actual.get("eduPersonAffiliation");
        assertEquals(3, affiliation.getValues().size());

        BaseAttribute entitlement = actual.get("eduPersonEntitlement");
        assertEquals(1, entitlement.getValues().size());
    }
}