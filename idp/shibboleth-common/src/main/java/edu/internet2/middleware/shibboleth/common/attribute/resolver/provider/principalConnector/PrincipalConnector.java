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

package edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.principalConnector;

import java.util.Set;

import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.ResolutionPlugIn;

/**
 * Principal connectors are responsible for taking subject's SAML name identifier and converting it into a string based
 * principal (user) name.
 * 
 * This plugin and all its dependencies are guarenteed to run prior to the attribute definitions meant to provide
 * attributes about the user, such that those definitions and data connectors will have access to a principal name.
 */
public interface PrincipalConnector extends ResolutionPlugIn<String> {

    /**
     * Get NamID format.
     * 
     * @return the NameID format
     */
    public String getFormat();

    /**
     * Get relying parties this connector is valid for.
     * 
     * @return set of relying parties
     */
    public Set<String> getRelyingParties();
}