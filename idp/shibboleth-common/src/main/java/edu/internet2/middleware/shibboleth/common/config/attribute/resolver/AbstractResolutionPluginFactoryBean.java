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

package edu.internet2.middleware.shibboleth.common.config.attribute.resolver;

import java.util.Set;

import org.opensaml.xml.util.DatatypeHelper;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Base class for resolver resolution plugin factories.
 */
public abstract class AbstractResolutionPluginFactoryBean extends AbstractFactoryBean {

    /** ID of resolution plug-ins this plugin depends on. */
    private Set<String> dependencyIds;

    /** Unique ID of the plugin. */
    private String pluginId;

    /**
     * Sets the ID of resolution plug-ins this plugin depends on.
     * 
     * @param ids ID of attribute definitions this plugin depends on
     */
    public void setDependencyIds(Set<String> ids) {
        dependencyIds = ids;
    }

    /**
     * Gets the ID of resolution plug-ins this plugin depends on.
     * 
     * @return ID of data connectors this plugin depends on
     */
    public Set<String> getDependencyIds() {
        return dependencyIds;
    }

    /**
     * Gets the unique ID of this plugin.
     * 
     * @return unique ID of this plugin
     */
    public String getPluginId() {
        return pluginId;
    }

    /**
     * Sets the unique ID of this plugin.
     * 
     * @param id unique ID of this plugin
     */
    public void setPluginId(String id) {
        pluginId = DatatypeHelper.safeTrimOrNullString(id);
    }
}