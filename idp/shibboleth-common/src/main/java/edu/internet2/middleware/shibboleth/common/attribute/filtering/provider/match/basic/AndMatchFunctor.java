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

package edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.match.basic;

import java.util.List;

import edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.FilterProcessingException;
import edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.MatchFunctor;
import edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.ShibbolethFilteringContext;

/**
 * A {@link MatchFunctor} that logical ANDs the results of contained functors.
 */
public class AndMatchFunctor extends AbstractMatchFunctor {

    /** Contained functors. */
    private List<MatchFunctor> targetRules;

    /**
     * Constructor.
     *
     * @param rules rules to AND together
     */
    public AndMatchFunctor(List<MatchFunctor> rules){
        targetRules = rules;
    }
    
    /**
     * Gets the functors whose results will be ANDed.
     * 
     * @return functors whose results will be ANDed
     */
    public List<MatchFunctor> getTargetRules() {
        return targetRules;
    }

    /** {@inheritDoc} */
    protected boolean doEvaluatePolicyRequirement(ShibbolethFilteringContext filterContext)
            throws FilterProcessingException {

        if (targetRules == null  ||
            targetRules.isEmpty()) {
            //
            // we should treat the null case the same as the empty list. 
            // Based on a "default deny" we make AND(null) false, (just like
            // if (null))
            //
            return false;
        }

        for (MatchFunctor child : targetRules) {
            if (!child.evaluatePolicyRequirement(filterContext)) {
                return false;
            }
        }

        return true;
    }

    /** {@inheritDoc} */
    protected boolean doEvaluateValue(ShibbolethFilteringContext filterContext, String attributeId,
            Object attributeValue) throws FilterProcessingException {
        if (targetRules == null ||
            targetRules.isEmpty()) {
            //
            // Treat the null case the same as the empty list. 
            //
            return false;
        }

        for (MatchFunctor child : targetRules) {
            if (!child.evaluatePermitValue(filterContext, attributeId, attributeValue)) {
                return false;
            }
        }

        return true;
    }
}