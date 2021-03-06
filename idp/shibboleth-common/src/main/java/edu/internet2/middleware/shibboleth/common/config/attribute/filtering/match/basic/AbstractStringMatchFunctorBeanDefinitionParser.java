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

package edu.internet2.middleware.shibboleth.common.config.attribute.filtering.match.basic;

import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

import edu.internet2.middleware.shibboleth.common.config.attribute.filtering.BaseFilterBeanDefinitionParser;

/**
 * Base class for string matching functors.
 */
public abstract class AbstractStringMatchFunctorBeanDefinitionParser extends BaseFilterBeanDefinitionParser {

    /** {@inheritDoc} */
    protected void doParse(Element configElement, BeanDefinitionBuilder builder) {
        super.doParse(configElement, builder);

        builder.addPropertyValue("matchString", DatatypeHelper.safeTrimOrNullString(configElement.getAttributeNS(null,
                "value")));

        boolean ignoreCase = false;
        if (configElement.hasAttributeNS(null, "ignoreCase")) {
            ignoreCase = XMLHelper.getAttributeValueAsBoolean(configElement.getAttributeNodeNS(null, "ignoreCase"));
        }
        builder.addPropertyValue("caseSensitive", !ignoreCase);
    }
}