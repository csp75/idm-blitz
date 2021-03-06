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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.internet2.middleware.shibboleth.common.attribute.BaseAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.provider.BasicAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.ShibbolethResolutionContext;

/** An attribute definition that splits the source attribute's values by means of a regex. */
public class RegexSplitAttributeDefinition extends BaseAttributeDefinition {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(RegexSplitAttributeDefinition.class);

    /** Regular expression used to split values. */
    private Pattern regex;
    
    /**
     * Constructor.
     * 
     * @param regularExpression expression used to split attribute values
     * @param caseSensitive whether the regular expression is case sensitive
     */
    public RegexSplitAttributeDefinition(String regularExpression, boolean caseSensitive) {
        if(!caseSensitive){
            regex = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);
        }else{
            regex = Pattern.compile(regularExpression);
        }
    }

    /** {@inheritDoc} */
    protected BaseAttribute<?> doResolve(ShibbolethResolutionContext resolutionContext)
            throws AttributeResolutionException {
        BasicAttribute<Object> attribute = new BasicAttribute<Object>();
        attribute.setId(getId());

        Collection<?> values = getValuesFromAllDependencies(resolutionContext);
        if (values == null || values.isEmpty()) {
            return attribute;
        }

        Matcher matcher;
        for (Object value : values) {
            if (value instanceof String) {
                matcher = regex.matcher((String) value);
                if(matcher.matches()){
                    attribute.getValues().add(matcher.group(1));
                } else {
                    log.debug("Value {} did not result in any values when split by regular expression {}", value, regex
                            .toString());
                }
            } else {
                log.debug("Ignoring non-string attribute value");
            }
        }

        return attribute;
    }

    /** {@inheritDoc} */
    public void validate() throws AttributeResolutionException {
        if(getSourceAttributeID() == null){
            throw new AttributeResolutionException("Source attribute ID is required but none was given.");
        }
    }
}