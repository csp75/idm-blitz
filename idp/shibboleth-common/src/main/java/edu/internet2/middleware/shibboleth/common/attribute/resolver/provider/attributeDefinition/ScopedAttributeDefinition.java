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

import java.util.Collection;

import org.opensaml.xml.util.DatatypeHelper;

import edu.internet2.middleware.shibboleth.common.attribute.BaseAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.provider.BasicAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.provider.ScopedAttributeValue;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.ShibbolethResolutionContext;

/**
 * An attribute definition that creates {@link ScopedAttributeValue}s by taking a source attribute value and applying a
 * static scope to each.
 */
public class ScopedAttributeDefinition extends BaseAttributeDefinition {

    /** Scope value. */
    private String scope;

    /**
     * Constructor.
     * 
     * @param newScope scope of the attribute
     */
    public ScopedAttributeDefinition(String newScope) {
        this.scope = newScope;
    }

    /** {@inheritDoc} */
    public BaseAttribute<ScopedAttributeValue> doResolve(ShibbolethResolutionContext resolutionContext)
            throws AttributeResolutionException {
        BasicAttribute<ScopedAttributeValue> attribute = new BasicAttribute<ScopedAttributeValue>();
        attribute.setId(getId());

        Collection<?> values = getValuesFromAllDependencies(resolutionContext);
        if (values != null && !values.isEmpty()) {
            for (Object value : values) {
                if (value != null) {
                    String strValue = DatatypeHelper.safeTrimOrNullString(value.toString());
                    if (strValue != null) {
                        attribute.getValues().add(new ScopedAttributeValue(strValue.toString(), scope));
                    }
                }
            }
        }

        return attribute;
    }

    /**
     * Get scope value.
     * 
     * @return Returns the scope.
     */
    public String getScope() {
        return scope;
    }

    /** {@inheritDoc} */
    public void validate() throws AttributeResolutionException {
        // do nothing
    }
}