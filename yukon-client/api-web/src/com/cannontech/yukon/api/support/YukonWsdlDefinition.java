package com.cannontech.yukon.api.support;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.ws.wsdl.wsdl11.ProviderBasedWsdl4jDefinition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.provider.InliningXsdSchemaTypesProvider;
import org.springframework.ws.wsdl.wsdl11.provider.SuffixBasedPortTypesProvider;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;

import com.google.common.collect.ImmutableList;

public class YukonWsdlDefinition implements Wsdl11Definition {

    private final InliningXsdSchemaTypesProvider typesProvider = new InliningXsdSchemaTypesProvider();

    private final SuffixBasedMessagesWithHeadersProvider messagesProvider = new SuffixBasedMessagesWithHeadersProvider();

    private final SuffixBasedPortTypesProvider portTypesProvider = new SuffixBasedPortTypesProvider();

    private final Soap11WithHeadersProvider soapProvider = new Soap11WithHeadersProvider();

    private final ProviderBasedWsdl4jDefinition delegate = new ProviderBasedWsdl4jDefinition();

    public YukonWsdlDefinition() {
        delegate.setTypesProvider(typesProvider);
        messagesProvider.setHeaderNames(ImmutableList.of("yukonUser", "extra"));
        delegate.setMessagesProvider(messagesProvider);
        delegate.setPortTypesProvider(portTypesProvider);
        delegate.setBindingsProvider(soapProvider);
        delegate.setServicesProvider(soapProvider);
    }

    /**
     * Sets the target namespace used for this definition.
     * <p/>
     * Defaults to the target namespace of the defined schema.
     */
    @Required
    public void setTargetNamespace(String targetNamespace) {
        delegate.setTargetNamespace(targetNamespace);
    }

    /**
     * Sets the single XSD schema to inline. Either this property, or {@link #setSchemaCollection(XsdSchemaCollection)
     * schemaCollection} must be set.
     */
    public void setSchema(final XsdSchema schema) {
        typesProvider.setSchema(schema);
    }

    /**
     * Sets the XSD schema collection to inline. Either this property, or {@link #setSchema(XsdSchema) schema} must be
     * set.
     */
    public void setSchemaCollection(XsdSchemaCollection schemaCollection) {
        typesProvider.setSchemaCollection(schemaCollection);
    }

    /** Sets the port type name used for this definition. Required. */
    @Required
    public void setPortTypeName(String portTypeName) {
        portTypesProvider.setPortTypeName(portTypeName);
    }

    /** Sets the value used for the SOAP Address location attribute value. */
    public void setLocationUri(String locationUri) {
        soapProvider.setLocationUri(locationUri);
    }

    /** Sets the service name. */
    @Required
    public void setServiceName(String serviceName) {
        soapProvider.setServiceName(serviceName);
    }

    @PostConstruct
    public void init() throws Exception {
        delegate.afterPropertiesSet();
    }

    public Source getSource() {
        return delegate.getSource();
    }
}
