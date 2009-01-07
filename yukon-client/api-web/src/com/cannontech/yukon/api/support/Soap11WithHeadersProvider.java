package com.cannontech.yukon.api.support;

import java.util.Iterator;

import javax.wsdl.BindingInput;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ElementExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.xml.namespace.QName;

import org.springframework.ws.wsdl.wsdl11.provider.Soap11Provider;

public class Soap11WithHeadersProvider extends Soap11Provider {
	
	private String headerSuffix = "Header";

	@Override
	protected void populateBindingInput(Definition definition,
			BindingInput bindingInput, Input input) throws WSDLException {
		super.populateBindingInput(definition, bindingInput, input);
        addHeaderDeclarationsToBindingElement(definition, BindingInput.class, bindingInput);
	}

	@Override
	protected void populateBindingOutput(Definition definition,
			BindingOutput bindingOutput, Output output) throws WSDLException {
		super.populateBindingOutput(definition, bindingOutput, output);
		addHeaderDeclarationsToBindingElement(definition, BindingOutput.class, bindingOutput);
	}

	private <T extends ElementExtensible> void addHeaderDeclarationsToBindingElement(Definition definition, Class<T> clazz, T bindingElement)
			throws WSDLException {
		for (Iterator<?> messageIter = definition.getMessages().values().iterator(); messageIter.hasNext();) {
            Message message = (Message) messageIter.next();
            String localPart = message.getQName().getLocalPart();
            boolean isHeader = localPart.endsWith(headerSuffix);
            if (isHeader) {
            	for (Iterator<?> partIter = message.getParts().values().iterator(); partIter.hasNext();) {
            		Part part = (Part) partIter.next();
            		SOAPHeader soapHeader = (SOAPHeader) createSoapExtension(definition, clazz, "header");
            		soapHeader.setUse("literal");
            		soapHeader.setPart(part.getName());
            		soapHeader.setMessage(message.getQName());
            		bindingElement.addExtensibilityElement(soapHeader);
            	}
            }
        }
	}
	
    /**
     * Creates a SOAP extensibility element.
     *
     * @param definition the WSDL4J <code>Definition</code>
     * @param parentType a class object indicating where in the WSDL definition this extension will exist
     * @param localName  the local name of the extensibility element
     * @return the extensibility element
     * @throws WSDLException in case of errors
     * @see ExtensionRegistry#createExtension(Class, QName)
     */
    private ExtensibilityElement createSoapExtension(Definition definition, Class<?> parentType, String localName)
            throws WSDLException {
        ExtensibilityElement extensibilityElement = definition.getExtensionRegistry()
				        .createExtension(parentType, new QName(SOAP_11_NAMESPACE_URI, localName));
		return extensibilityElement;
    }
    
    public void setHeaderSuffix(String headerSuffix) {
		this.headerSuffix = headerSuffix;
	}

}
