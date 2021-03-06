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

package edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.attributeDefinition;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.internet2.middleware.shibboleth.common.attribute.BaseAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.encoding.AttributeEncoder;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.ShibbolethResolutionContext;

/**
 * Wrapper for an {@link AttributeDefinition} within a {@link ShibbolethResolutionContext}. This wrapper ensures that
 * the definition is resolved only once per context.
 */
public class ContextualAttributeDefinition implements AttributeDefinition {

    /** Wrapped attribute definition. */
    private AttributeDefinition definition;

    /** Cached result of resolving the attribute definition. */
    private BaseAttribute attribute;

    /**
     * Constructor.
     * 
     * @param newDefinition attribute definition to wrap
     */
    public ContextualAttributeDefinition(AttributeDefinition newDefinition) {
        definition = newDefinition;
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        return definition.equals(obj);
    }

    /** {@inheritDoc} */
    public List<AttributeEncoder> getAttributeEncoders() {
        return definition.getAttributeEncoders();
    }

    /** {@inheritDoc} */
    public List<String> getDependencyIds() {
        return definition.getDependencyIds();
    }

    /** {@inheritDoc} */
    public Map<Locale, String> getDisplayDescriptions() {
        return definition.getDisplayDescriptions();
    }

    /** {@inheritDoc} */
    public Map<Locale, String> getDisplayNames() {
        return definition.getDisplayNames();
    }

    /** {@inheritDoc} */
    public String getId() {
        return definition.getId();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return definition.hashCode();
    }

    /** {@inheritDoc} */
    public boolean isDependencyOnly() {
        return definition.isDependencyOnly();
    }

    /** {@inheritDoc} */
    public BaseAttribute resolve(ShibbolethResolutionContext resolutionContext) throws AttributeResolutionException {
        if (attribute == null) {
            attribute = definition.resolve(resolutionContext);
        }

        return attribute;
    }

    /** {@inheritDoc} */
    public void validate() throws AttributeResolutionException {
        definition.validate();
    }
}