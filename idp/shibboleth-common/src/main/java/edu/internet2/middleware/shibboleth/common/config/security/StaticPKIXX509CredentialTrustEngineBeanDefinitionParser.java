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

import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import edu.internet2.middleware.shibboleth.common.config.SpringConfigurationUtils;

/** Spring bean definition parser for {urn:mace:shibboleth:2.0:security}StaticPKIXX509Credential elements. */
public class StaticPKIXX509CredentialTrustEngineBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    /** Schema type. */
    public static final QName SCHEMA_TYPE = new QName(SecurityNamespaceHandler.NAMESPACE, "StaticPKIXX509Credential");

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(StaticPKIXX509CredentialTrustEngineBeanDefinitionParser.class);

    /** {@inheritDoc} */
    protected Class getBeanClass(Element element) {
        return StaticPKIXX509CredentialTrustEngineFactoryBean.class;
    }

    /** {@inheritDoc} */
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        log.info("Parsing configuration for {} trust engine with id: {}", XMLHelper.getXSIType(element).getLocalPart(),
                element.getAttributeNS(null, "id"));

        List<Element> childElems = XMLHelper.getChildElementsByTagNameNS(element, SecurityNamespaceHandler.NAMESPACE,
                "ValidationInfo");
        builder.addPropertyValue("PKIXInfo", SpringConfigurationUtils.parseInnerCustomElements(childElems, parserContext));

        childElems = XMLHelper.getChildElementsByTagNameNS(element, SecurityNamespaceHandler.NAMESPACE, "TrustedName");
        HashSet<String> trustedNames = new HashSet<String>(childElems.size());
        for (Element nameElem : childElems) {
            trustedNames.add(DatatypeHelper.safeTrimOrNullString(nameElem.getTextContent()));
        }
        builder.addPropertyValue("trustedNames", trustedNames);
        
        childElems = XMLHelper.getChildElementsByTagNameNS(element, SecurityNamespaceHandler.NAMESPACE, "ValidationOptions");
        if (childElems.size() > 0) {
            builder.addPropertyValue("PKIXValidationOptions", SpringConfigurationUtils.parseInnerCustomElement(
                    (Element) childElems.get(0), parserContext));
        }
    }

    /** {@inheritDoc} */
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return DatatypeHelper.safeTrim(element.getAttributeNS(null, "id"));
    }
}