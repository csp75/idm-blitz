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

package edu.internet2.middleware.shibboleth.common.config.metadata;

import javax.xml.namespace.QName;

import org.opensaml.saml2.metadata.provider.RequiredValidUntilFilter;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import edu.internet2.middleware.shibboleth.common.config.SpringConfigurationUtils;

/** Bean definition parser for {@link RequiredValidUntilFilter} beans. */
public class RequiredValidUntilFilterBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    /** Schema type name. */
    public static final QName TYPE_NAME = new QName(MetadataNamespaceHandler.NAMESPACE, "RequiredValidUntil");

    /** {@inheritDoc} */
    protected Class getBeanClass(Element element) {
        return RequiredValidUntilFilter.class;
    }

    /** {@inheritDoc} */
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        if (element.hasAttributeNS(null, "maxValidityInterval")) {
            long interval = SpringConfigurationUtils.parseDurationToMillis(
                    "'maxValidityInterval' on metadata filter of type " + XMLHelper.getXSIType(element), element
                            .getAttributeNS(null, "maxValidityInterval"), 1000) / 1000;
            builder.addConstructorArgValue(interval);
        }
    }

    /** {@inheritDoc} */
    protected boolean shouldGenerateId() {
        return true;
    }
}