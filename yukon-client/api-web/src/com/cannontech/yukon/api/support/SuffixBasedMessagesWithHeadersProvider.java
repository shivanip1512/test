/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cannontech.yukon.api.support;


import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.springframework.util.Assert;
import org.springframework.ws.wsdl.wsdl11.provider.DefaultMessagesProvider;
import org.springframework.ws.wsdl.wsdl11.provider.MessagesProvider;
import org.w3c.dom.Element;

import com.google.common.collect.ImmutableList;

/**
 * Implementation of the {@link MessagesProvider} interface that is based on suffixes.
 * 
 * Modified by Cannon to support header elements.
 *
 * @author Arjen Poutsma
 * @since 1.5.1
 */
public class SuffixBasedMessagesWithHeadersProvider extends DefaultMessagesProvider {

    /** The default suffix used to detect request elements in the schema. */
    public static final String DEFAULT_REQUEST_SUFFIX = "Request";

    /** The default suffix used to detect response elements in the schema. */
    public static final String DEFAULT_RESPONSE_SUFFIX = "Response";

    /** The default suffix used to detect fault elements in the schema. */
    public static final String DEFAULT_FAULT_SUFFIX = "Fault";
    
    public static final String DEFAULT_HEADER_SUFFIX = "Header";

    private String requestSuffix = DEFAULT_REQUEST_SUFFIX;

    private String responseSuffix = DEFAULT_RESPONSE_SUFFIX;

    private String faultSuffix = DEFAULT_FAULT_SUFFIX;
    
    private String headerSuffix = DEFAULT_HEADER_SUFFIX;
    
    private List<String> headerNames = ImmutableList.of();

    /**
     * Returns the suffix used to detect request elements in the schema.
     *
     * @see #DEFAULT_REQUEST_SUFFIX
     */
    public String getRequestSuffix() {
        return requestSuffix;
    }

    /**
     * Sets the suffix used to detect request elements in the schema.
     *
     * @see #DEFAULT_REQUEST_SUFFIX
     */
    public void setRequestSuffix(String requestSuffix) {
        Assert.hasText(requestSuffix, "'requestSuffix' must not be empty");
        this.requestSuffix = requestSuffix;
    }

    /**
     * Returns the suffix used to detect response elements in the schema.
     *
     * @see #DEFAULT_RESPONSE_SUFFIX
     */
    public String getResponseSuffix() {
        return responseSuffix;
    }

    /**
     * Sets the suffix used to detect response elements in the schema.
     *
     * @see #DEFAULT_RESPONSE_SUFFIX
     */
    public void setResponseSuffix(String responseSuffix) {
        Assert.hasText(responseSuffix, "'responseSuffix' must not be empty");
        this.responseSuffix = responseSuffix;
    }

    /**
     * Returns the suffix used to detect fault elements in the schema.
     *
     * @see #DEFAULT_FAULT_SUFFIX
     */
    public String getFaultSuffix() {
        return faultSuffix;
    }

    /**
     * Sets the suffix used to detect fault elements in the schema.
     *
     * @see #DEFAULT_FAULT_SUFFIX
     */
    public void setFaultSuffix(String faultSuffix) {
        Assert.hasText(faultSuffix, "'faultSuffix' must not be empty");
        this.faultSuffix = faultSuffix;
    }
    
    public void setHeaderNames(List<String> headerNames) {
		this.headerNames = headerNames;
	}

    final protected boolean isMessageElement(Element element) {
		if (super.isMessageElement(element)) {
			return isMessageElementToBeCreated(element);
		} else {
			return false;
		}
	}

	protected boolean isMessageElementToBeCreated(Element element) {
		String elementName = getElementName(element);
		Assert.hasText(elementName, "Element has no name");
		return elementName.endsWith(getRequestSuffix()) || elementName.endsWith(getResponseSuffix()) ||
		        elementName.endsWith(getFaultSuffix()) || isHeaderElement(elementName);
	}

	private boolean isHeaderElement(String elementName) {
		return headerNames.contains(elementName);
	}
	
    /**
     * Called after the {@link Message} has been created.
     * <p/>
     * Default implementation sets the name of the message to the element name.
     *
     * @param definition  the WSDL4J <code>Definition</code>
     * @param message     the WSDL4J <code>Message</code>
     * @param elementName the element name
     * @throws WSDLException in case of errors
     */
    protected void populateMessage(Definition definition, Message message, QName elementName) throws WSDLException {
    	if (isHeaderElement(elementName.getLocalPart())) {
    		QName messageName = new QName(definition.getTargetNamespace(), elementName.getLocalPart() + headerSuffix);
    		message.setQName(messageName);
    	} else {
    		super.populateMessage(definition, message, elementName);
    	}
    }

}
