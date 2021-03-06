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

package edu.internet2.middleware.shibboleth.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A base class for {@link NamespaceHandler} implementations.
 * 
 * This code is heavily based on Spring's <code>NamespaceHandlerSupport</code>. The largest difference is that bean
 * definition parsers may be registered against either an elements name or schema type. During parser lookup the schema
 * type is preferred.
 */
public abstract class BaseSpringNamespaceHandler implements NamespaceHandler {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BaseSpringNamespaceHandler.class);

    /**
     * Stores the {@link BeanDefinitionParser} implementations keyed by the local name of the {@link org.w3c.dom.Element Elements}
     * they handle.
     */
    private Map<QName, BeanDefinitionParser> parsers = new HashMap<QName, BeanDefinitionParser>();

    /**
     * Stores the {@link BeanDefinitionDecorator} implementations keyed by the local name of the
     * {@link org.w3c.dom.Element Elements} they handle.
     */
    private Map<QName, BeanDefinitionDecorator> decorators = new HashMap<QName, BeanDefinitionDecorator>();

    /**
     * Stores the {@link BeanDefinitionParser} implementations keyed by the local name of the {@link org.w3c.dom.Attr Attrs} they
     * handle.
     */
    private Map<QName, BeanDefinitionDecorator> attributeDecorators = new HashMap<QName, BeanDefinitionDecorator>();

    /**
     * Decorates the supplied {@link org.w3c.dom.Node} by delegating to the {@link BeanDefinitionDecorator} that is registered to
     * handle that {@link org.w3c.dom.Node}.
     * 
     * @param node the node decorating a the given bean definition
     * @param definition the bean being decorated
     * @param parserContext the current parser context
     * 
     * @return the deocrated bean definition
     */
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
        return findDecoratorForNode(node).decorate(node, definition, parserContext);
    }

    /**
     * Parses the supplied {@link org.w3c.dom.Element} by delegating to the {@link BeanDefinitionParser} that is registered for that
     * {@link org.w3c.dom.Element}.
     * 
     * @param element the element to be parsed into a bean definition
     * @param parserContext the context within which the bean definition is created
     * 
     * @return the bean definition created from the given element
     */
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return findParserForElement(element).parse(element, parserContext);
    }

    /**
     * Locates the {@link BeanDefinitionParser} from the register implementations using the local name of the supplied
     * {@link org.w3c.dom.Element}.
     * 
     * @param element the element to locate the bean definition parser for
     * 
     * @return the parser for the given bean element
     */
    protected BeanDefinitionParser findParserForElement(Element element) {
        QName parserId;
        BeanDefinitionParser parser = null;

        parserId = XMLHelper.getXSIType(element);
        if (parserId != null) {
            log.trace("Attempting to find parser for element of type: {}", parserId);
            parser = parsers.get(parserId);
        }

        if (parser == null) {
            parserId = XMLHelper.getNodeQName(element);
            log.trace("Attempting to find parser with element name: {}", parserId);
            parser = parsers.get(parserId);
        }

        if (parser == null) {
            log.error("Cannot locate BeanDefinitionParser for element: " + parserId);
            throw new IllegalArgumentException("Cannot locate BeanDefinitionParser for element: " + parserId);
        }

        return parser;
    }

    /**
     * Locates the {@link BeanDefinitionParser} from the register implementations using the local name of the supplied
     * {@link org.w3c.dom.Node}. Supports both {@link org.w3c.dom.Element Elements} and {@link org.w3c.dom.Attr Attrs}.
     * 
     * @param node the node to locate the decorator for
     * 
     * @return the decorator for the given node
     */
    protected BeanDefinitionDecorator findDecoratorForNode(Node node) {
        BeanDefinitionDecorator decorator = null;

        if (node instanceof Element) {
            decorator = decorators.get(XMLHelper.getXSIType((Element) node));
            if (decorator == null) {
                decorator = decorators.get(XMLHelper.getNodeQName(node));
            }
        } else if (node instanceof Attr) {
            decorator = attributeDecorators.get(node.getLocalName());
        } else {
            throw new IllegalArgumentException("Cannot decorate based on Nodes of type [" + node.getClass().getName()
                    + "]");
        }

        if (decorator == null) {
            throw new IllegalArgumentException("Cannot locate BeanDefinitionDecorator for " + " ["
                    + node.getLocalName() + "]");
        }

        return decorator;
    }

    /**
     * Subclasses can call this to register the supplied {@link BeanDefinitionParser} to handle the specified element.
     * The element name is the local (non-namespace qualified) name.
     * 
     * @param elementNameOrType the element name or schema type the parser is for
     * @param parser the parser to register
     */
    protected void registerBeanDefinitionParser(QName elementNameOrType, BeanDefinitionParser parser) {
        parsers.put(elementNameOrType, parser);
    }

    /**
     * Subclasses can call this to register the supplied {@link BeanDefinitionDecorator} to handle the specified
     * element. The element name is the local (non-namespace qualified) name.
     * 
     * @param elementNameOrType the element name or schema type the parser is for
     * @param decorator the decorator to register
     */
    protected void registerBeanDefinitionDecorator(QName elementNameOrType, BeanDefinitionDecorator decorator) {
        decorators.put(elementNameOrType, decorator);
    }

    /**
     * Subclasses can call this to register the supplied {@link BeanDefinitionDecorator} to handle the specified
     * attribute. The attribute name is the local (non-namespace qualified) name.
     * 
     * @param attributeName the name of the attribute to register the decorator for
     * @param decorator the decorator to register
     */
    protected void registerBeanDefinitionDecoratorForAttribute(QName attributeName, BeanDefinitionDecorator decorator) {
        attributeDecorators.put(attributeName, decorator);
    }
}