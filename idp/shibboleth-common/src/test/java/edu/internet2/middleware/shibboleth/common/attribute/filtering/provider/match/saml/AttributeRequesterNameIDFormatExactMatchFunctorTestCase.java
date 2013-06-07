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

package edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.match.saml;

import edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.match.BaseTestCaseMetadata;

/** {@link AttributeRequesterNameIDFormatExactMatchFunctor} unit test. */
public class AttributeRequesterNameIDFormatExactMatchFunctorTestCase extends BaseTestCaseMetadata {

    protected void setUp() throws Exception {
        metadataFile = MD_PATH + "/shibboleth.net-metadata.xml";
        issuerEntityId = "https://issues.shibboleth.net/shibboleth";
        requesterEntityId = "https://idp.shibboleth.net/idp/shibboleth";

        super.setUp();
        
        requestContext.setPeerEntityRoleMetadata(requestContext.getPeerEntityMetadata().getRoleDescriptors().get(0));
    }

    public void testEvaluatePolicyRequirement() throws Exception {
        AttributeRequesterNameIDFormatExactMatchFunctor functor = new AttributeRequesterNameIDFormatExactMatchFunctor();
        functor.setNameIdFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
        assertTrue(functor.evaluatePolicyRequirement(filterContext));

        functor = new AttributeRequesterNameIDFormatExactMatchFunctor();
        functor.setNameIdFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        assertFalse(functor.evaluatePolicyRequirement(filterContext));
    }

    public void testEvaluatePermitValue() throws Exception {
        AttributeRequesterNameIDFormatExactMatchFunctor functor = new AttributeRequesterNameIDFormatExactMatchFunctor();
        functor.setNameIdFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
        assertTrue(functor.evaluatePermitValue(filterContext, null, null));

        functor = new AttributeRequesterNameIDFormatExactMatchFunctor();
        functor.setNameIdFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        assertFalse(functor.evaluatePermitValue(filterContext, null, null));
    }

    public void testEvaluateDenyRule() throws Exception {
        AttributeRequesterNameIDFormatExactMatchFunctor functor = new AttributeRequesterNameIDFormatExactMatchFunctor();
        functor.setNameIdFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
        assertTrue(functor.evaluateDenyRule(filterContext, null, null));

        functor = new AttributeRequesterNameIDFormatExactMatchFunctor();
        functor.setNameIdFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        assertFalse(functor.evaluateDenyRule(filterContext, null, null));
    }
}