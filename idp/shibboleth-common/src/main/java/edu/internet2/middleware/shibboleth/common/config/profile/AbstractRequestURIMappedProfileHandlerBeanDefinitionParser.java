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

package edu.internet2.middleware.shibboleth.common.config.profile;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Base class for request URI mapped profile handler configuration parsers.
 */
public abstract class AbstractRequestURIMappedProfileHandlerBeanDefinitionParser extends
        AbstractSingleBeanDefinitionParser {

    /** Class loggger. */
    private static Logger log = LoggerFactory
            .getLogger(AbstractRequestURIMappedProfileHandlerBeanDefinitionParser.class);

    /** {@inheritDoc} */
    protected void doParse(Element config, BeanDefinitionBuilder builder) {
        log.info("Parsing configuration for profile handler: {}", XMLHelper.getXSIType(config).getLocalPart());

        builder.addPropertyValue("requestPaths", getRequestPaths(config));
    }

    /**
     * Gets the list of request paths the profile handler handles.
     * 
     * @param config profile handler configuration element
     * 
     * @return list of request paths the profile handler handles
     */
    protected List<String> getRequestPaths(Element config) {
        ArrayList<String> requestPaths = new ArrayList<String>();
        List<Element> requestPathElems = XMLHelper.getChildElementsByTagName(config, "RequestPath");
        if (requestPathElems != null) {
            for (Element requestPathElem : requestPathElems) {
                requestPaths.add(DatatypeHelper.safeTrimOrNullString(requestPathElem.getTextContent()));
            }
        }

        return requestPaths;
    }

    /** {@inheritDoc} */
    protected boolean shouldGenerateId() {
        return true;
    }
}