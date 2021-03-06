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

package edu.internet2.middleware.shibboleth.common.attribute.encoding.provider;

import java.util.List;

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.saml1.core.Attribute;
import org.opensaml.saml1.core.AttributeValue;
import org.opensaml.xml.XMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.internet2.middleware.shibboleth.common.attribute.BaseAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.encoding.AttributeEncodingException;
import edu.internet2.middleware.shibboleth.common.attribute.encoding.SAML1AttributeEncoder;

/**
 * Implementation of SAML 1.X scoped attribute encoder.
 */
public class SAML1ScopedStringAttributeEncoder extends
        AbstractScopedAttributeEncoder<org.opensaml.saml1.core.Attribute> implements SAML1AttributeEncoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SAML1ScopedStringAttributeEncoder.class);

    /** Attribute factory. */
    private final SAMLObjectBuilder<Attribute> attributeBuilder;

    /** Namespace of attribute. */
    private String namespace;

    /** Constructor. */
    public SAML1ScopedStringAttributeEncoder() {
        super();
        attributeBuilder = (SAMLObjectBuilder<Attribute>) Configuration.getBuilderFactory().getBuilder(
                Attribute.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    public String getNamespace() {
        return namespace;
    }

    /** {@inheritDoc} */
    public void setNamespace(String newNamespace) {
        namespace = newNamespace;
    }

    /** {@inheritDoc} */
    public Attribute encode(BaseAttribute attribute) throws AttributeEncodingException {
        Attribute samlAttribute = attributeBuilder.buildObject();
        samlAttribute.setAttributeName(getAttributeName());
        samlAttribute.setAttributeNamespace(getNamespace());
        samlAttribute.getAttributeValues()
                .addAll(encodeAttributeValues(AttributeValue.DEFAULT_ELEMENT_NAME, attribute));

        List<XMLObject> attributeValues = samlAttribute.getAttributeValues();
        if (attributeValues == null || attributeValues.isEmpty()) {
            log.debug("Unable to encode {} attribute.  It does not contain any values", attribute.getId());
            return null;
        }

        return samlAttribute;
    }

}