/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsGetLMControlHistoryDescriptor.java,v 1.74 2004/05/18 17:48:48 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.FieldValidator;
import org.exolab.castor.xml.TypeValidator;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.exolab.castor.xml.handlers.*;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;
import org.exolab.castor.xml.validators.*;

/**
 * 
 * 
 * @version $Revision: 1.74 $ $Date: 2004/05/18 17:48:48 $
**/
public class StarsGetLMControlHistoryDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String nsPrefix;

    private java.lang.String nsURI;

    private java.lang.String xmlName;

    private org.exolab.castor.xml.XMLFieldDescriptor identity;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetLMControlHistoryDescriptor() {
        super();
        xmlName = "stars-GetLMControlHistory";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _groupID
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_groupID", "GroupID", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetLMControlHistory target = (StarsGetLMControlHistory) object;
                if(!target.hasGroupID())
                    return null;
                return new Integer(target.getGroupID());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetLMControlHistory target = (StarsGetLMControlHistory) object;
                    // ignore null values for non optional primitives
                    if (value == null) return;
                    
                    target.setGroupID( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _groupID
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _period
        desc = new XMLFieldDescriptorImpl(com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod.class, "_period", "Period", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetLMControlHistory target = (StarsGetLMControlHistory) object;
                return target.getPeriod();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetLMControlHistory target = (StarsGetLMControlHistory) object;
                    target.setPeriod( (com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler( new EnumFieldHandler(com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod.class, handler));
        desc.setImmutable(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _period
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _getSummary
        desc = new XMLFieldDescriptorImpl(java.lang.Boolean.TYPE, "_getSummary", "GetSummary", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetLMControlHistory target = (StarsGetLMControlHistory) object;
                if(!target.hasGetSummary())
                    return null;
                return new Boolean(target.getGetSummary());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetLMControlHistory target = (StarsGetLMControlHistory) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteGetSummary();
                        return;
                    }
                    target.setGetSummary( ((Boolean)value).booleanValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _getSummary
        fieldValidator = new FieldValidator();
        { //-- local scope
            BooleanValidator bv = new BooleanValidator();
            fieldValidator.setValidator(bv);
        }
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsGetLMControlHistoryDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public org.exolab.castor.mapping.AccessMode getAccessMode()
    {
        return null;
    } //-- org.exolab.castor.mapping.AccessMode getAccessMode() 

    /**
    **/
    public org.exolab.castor.mapping.ClassDescriptor getExtends()
    {
        return null;
    } //-- org.exolab.castor.mapping.ClassDescriptor getExtends() 

    /**
    **/
    public org.exolab.castor.mapping.FieldDescriptor getIdentity()
    {
        return identity;
    } //-- org.exolab.castor.mapping.FieldDescriptor getIdentity() 

    /**
    **/
    public java.lang.Class getJavaClass()
    {
        return com.cannontech.stars.xml.serialize.StarsGetLMControlHistory.class;
    } //-- java.lang.Class getJavaClass() 

    /**
    **/
    public java.lang.String getNameSpacePrefix()
    {
        return nsPrefix;
    } //-- java.lang.String getNameSpacePrefix() 

    /**
    **/
    public java.lang.String getNameSpaceURI()
    {
        return nsURI;
    } //-- java.lang.String getNameSpaceURI() 

    /**
    **/
    public org.exolab.castor.xml.TypeValidator getValidator()
    {
        return this;
    } //-- org.exolab.castor.xml.TypeValidator getValidator() 

    /**
    **/
    public java.lang.String getXMLName()
    {
        return xmlName;
    } //-- java.lang.String getXMLName() 

}
