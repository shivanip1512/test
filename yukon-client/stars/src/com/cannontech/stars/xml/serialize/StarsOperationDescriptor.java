/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsOperationDescriptor.java,v 1.16 2002/11/20 22:57:17 zyao Exp $
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
 * @version $Revision: 1.16 $ $Date: 2002/11/20 22:57:17 $
**/
public class StarsOperationDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsOperationDescriptor() {
        super();
        xmlName = "stars-Operation";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _starsLogin
        desc = new XMLFieldDescriptorImpl(StarsLogin.class, "_starsLogin", "stars-Login", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsLogin();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsLogin( (StarsLogin) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsLogin();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsLogin
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsLogoff
        desc = new XMLFieldDescriptorImpl(StarsLogoff.class, "_starsLogoff", "stars-Logoff", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsLogoff();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsLogoff( (StarsLogoff) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsLogoff();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsLogoff
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsSuccess
        desc = new XMLFieldDescriptorImpl(StarsSuccess.class, "_starsSuccess", "stars-Success", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsSuccess();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsSuccess( (StarsSuccess) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsSuccess();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsSuccess
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsFailure
        desc = new XMLFieldDescriptorImpl(StarsFailure.class, "_starsFailure", "stars-Failure", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsFailure();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsFailure( (StarsFailure) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsFailure();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsFailure
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsNewCustomerAccount
        desc = new XMLFieldDescriptorImpl(StarsNewCustomerAccount.class, "_starsNewCustomerAccount", "stars-NewCustomerAccount", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsNewCustomerAccount();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsNewCustomerAccount( (StarsNewCustomerAccount) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsNewCustomerAccount();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsNewCustomerAccount
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsNewCustomerAccountResponse
        desc = new XMLFieldDescriptorImpl(StarsNewCustomerAccountResponse.class, "_starsNewCustomerAccountResponse", "stars-NewCustomerAccountResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsNewCustomerAccountResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsNewCustomerAccountResponse( (StarsNewCustomerAccountResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsNewCustomerAccountResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsNewCustomerAccountResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsProgramSignUp
        desc = new XMLFieldDescriptorImpl(StarsProgramSignUp.class, "_starsProgramSignUp", "stars-ProgramSignUp", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsProgramSignUp();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsProgramSignUp( (StarsProgramSignUp) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsProgramSignUp();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsProgramSignUp
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsSearchCustomerAccount
        desc = new XMLFieldDescriptorImpl(StarsSearchCustomerAccount.class, "_starsSearchCustomerAccount", "stars-SearchCustomerAccount", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsSearchCustomerAccount();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsSearchCustomerAccount( (StarsSearchCustomerAccount) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsSearchCustomerAccount();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsSearchCustomerAccount
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsSearchCustomerAccountResponse
        desc = new XMLFieldDescriptorImpl(StarsSearchCustomerAccountResponse.class, "_starsSearchCustomerAccountResponse", "stars-SearchCustomerAccountResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsSearchCustomerAccountResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsSearchCustomerAccountResponse( (StarsSearchCustomerAccountResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsSearchCustomerAccountResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsSearchCustomerAccountResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetAllCustomerAccounts
        desc = new XMLFieldDescriptorImpl(StarsGetAllCustomerAccounts.class, "_starsGetAllCustomerAccounts", "stars-GetAllCustomerAccounts", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetAllCustomerAccounts();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetAllCustomerAccounts( (StarsGetAllCustomerAccounts) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetAllCustomerAccounts();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetAllCustomerAccounts
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetAllCustomerAccountsResponse
        desc = new XMLFieldDescriptorImpl(StarsGetAllCustomerAccountsResponse.class, "_starsGetAllCustomerAccountsResponse", "stars-GetAllCustomerAccountsResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetAllCustomerAccountsResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetAllCustomerAccountsResponse( (StarsGetAllCustomerAccountsResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetAllCustomerAccountsResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetAllCustomerAccountsResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetCustomerAccount
        desc = new XMLFieldDescriptorImpl(StarsGetCustomerAccount.class, "_starsGetCustomerAccount", "stars-GetCustomerAccount", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetCustomerAccount();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetCustomerAccount( (StarsGetCustomerAccount) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetCustomerAccount();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetCustomerAccount
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetCustomerAccountResponse
        desc = new XMLFieldDescriptorImpl(StarsGetCustomerAccountResponse.class, "_starsGetCustomerAccountResponse", "stars-GetCustomerAccountResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetCustomerAccountResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetCustomerAccountResponse( (StarsGetCustomerAccountResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetCustomerAccountResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetCustomerAccountResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsUpdateCustomerAccount
        desc = new XMLFieldDescriptorImpl(StarsUpdateCustomerAccount.class, "_starsUpdateCustomerAccount", "stars-UpdateCustomerAccount", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsUpdateCustomerAccount();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsUpdateCustomerAccount( (StarsUpdateCustomerAccount) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsUpdateCustomerAccount();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsUpdateCustomerAccount
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsUpdateCustomerAccountResponse
        desc = new XMLFieldDescriptorImpl(StarsUpdateCustomerAccountResponse.class, "_starsUpdateCustomerAccountResponse", "stars-UpdateCustomerAccountResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsUpdateCustomerAccountResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsUpdateCustomerAccountResponse( (StarsUpdateCustomerAccountResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsUpdateCustomerAccountResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsUpdateCustomerAccountResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsUpdateLMPrograms
        desc = new XMLFieldDescriptorImpl(StarsUpdateLMPrograms.class, "_starsUpdateLMPrograms", "stars-UpdateLMPrograms", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsUpdateLMPrograms();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsUpdateLMPrograms( (StarsUpdateLMPrograms) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsUpdateLMPrograms();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsUpdateLMPrograms
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsProgramOptOut
        desc = new XMLFieldDescriptorImpl(StarsProgramOptOut.class, "_starsProgramOptOut", "stars-ProgramOptOut", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsProgramOptOut();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsProgramOptOut( (StarsProgramOptOut) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsProgramOptOut();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsProgramOptOut
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsProgramOptOutResponse
        desc = new XMLFieldDescriptorImpl(StarsProgramOptOutResponse.class, "_starsProgramOptOutResponse", "stars-ProgramOptOutResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsProgramOptOutResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsProgramOptOutResponse( (StarsProgramOptOutResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsProgramOptOutResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsProgramOptOutResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsProgramReenable
        desc = new XMLFieldDescriptorImpl(StarsProgramReenable.class, "_starsProgramReenable", "stars-ProgramReenable", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsProgramReenable();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsProgramReenable( (StarsProgramReenable) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsProgramReenable();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsProgramReenable
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsProgramReenableResponse
        desc = new XMLFieldDescriptorImpl(StarsProgramReenableResponse.class, "_starsProgramReenableResponse", "stars-ProgramReenableResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsProgramReenableResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsProgramReenableResponse( (StarsProgramReenableResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsProgramReenableResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsProgramReenableResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsYukonSwitchCommand
        desc = new XMLFieldDescriptorImpl(StarsYukonSwitchCommand.class, "_starsYukonSwitchCommand", "stars-YukonSwitchCommand", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsYukonSwitchCommand();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsYukonSwitchCommand( (StarsYukonSwitchCommand) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsYukonSwitchCommand();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsYukonSwitchCommand
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsYukonSwitchCommandResponse
        desc = new XMLFieldDescriptorImpl(StarsYukonSwitchCommandResponse.class, "_starsYukonSwitchCommandResponse", "stars-YukonSwitchCommandResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsYukonSwitchCommandResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsYukonSwitchCommandResponse( (StarsYukonSwitchCommandResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsYukonSwitchCommandResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsYukonSwitchCommandResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetLMControlHistory
        desc = new XMLFieldDescriptorImpl(StarsGetLMControlHistory.class, "_starsGetLMControlHistory", "stars-GetLMControlHistory", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetLMControlHistory();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetLMControlHistory( (StarsGetLMControlHistory) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetLMControlHistory();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetLMControlHistory
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetLMControlHistoryResponse
        desc = new XMLFieldDescriptorImpl(StarsGetLMControlHistoryResponse.class, "_starsGetLMControlHistoryResponse", "stars-GetLMControlHistoryResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetLMControlHistoryResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetLMControlHistoryResponse( (StarsGetLMControlHistoryResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetLMControlHistoryResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetLMControlHistoryResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCreateCallReport
        desc = new XMLFieldDescriptorImpl(StarsCreateCallReport.class, "_starsCreateCallReport", "stars-CreateCallReport", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsCreateCallReport();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsCreateCallReport( (StarsCreateCallReport) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCreateCallReport();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCreateCallReport
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCreateCallReportResponse
        desc = new XMLFieldDescriptorImpl(StarsCreateCallReportResponse.class, "_starsCreateCallReportResponse", "stars-CreateCallReportResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsCreateCallReportResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsCreateCallReportResponse( (StarsCreateCallReportResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCreateCallReportResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCreateCallReportResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetCallReportHistory
        desc = new XMLFieldDescriptorImpl(StarsGetCallReportHistory.class, "_starsGetCallReportHistory", "stars-GetCallReportHistory", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetCallReportHistory();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetCallReportHistory( (StarsGetCallReportHistory) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetCallReportHistory();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetCallReportHistory
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetCallReportHistoryResponse
        desc = new XMLFieldDescriptorImpl(StarsGetCallReportHistoryResponse.class, "_starsGetCallReportHistoryResponse", "stars-GetCallReportHistoryResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetCallReportHistoryResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetCallReportHistoryResponse( (StarsGetCallReportHistoryResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetCallReportHistoryResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetCallReportHistoryResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCreateServiceRequest
        desc = new XMLFieldDescriptorImpl(StarsCreateServiceRequest.class, "_starsCreateServiceRequest", "stars-CreateServiceRequest", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsCreateServiceRequest();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsCreateServiceRequest( (StarsCreateServiceRequest) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCreateServiceRequest();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCreateServiceRequest
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCreateServiceRequestResponse
        desc = new XMLFieldDescriptorImpl(StarsCreateServiceRequestResponse.class, "_starsCreateServiceRequestResponse", "stars-CreateServiceRequestResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsCreateServiceRequestResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsCreateServiceRequestResponse( (StarsCreateServiceRequestResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCreateServiceRequestResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCreateServiceRequestResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetServiceRequestHistory
        desc = new XMLFieldDescriptorImpl(StarsGetServiceRequestHistory.class, "_starsGetServiceRequestHistory", "stars-GetServiceRequestHistory", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetServiceRequestHistory();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetServiceRequestHistory( (StarsGetServiceRequestHistory) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetServiceRequestHistory();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetServiceRequestHistory
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetServiceRequestHistoryResponse
        desc = new XMLFieldDescriptorImpl(StarsGetServiceRequestHistoryResponse.class, "_starsGetServiceRequestHistoryResponse", "stars-GetServiceRequestHistoryResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetServiceRequestHistoryResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetServiceRequestHistoryResponse( (StarsGetServiceRequestHistoryResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetServiceRequestHistoryResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetServiceRequestHistoryResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetEnrollmentPrograms
        desc = new XMLFieldDescriptorImpl(StarsGetEnrollmentPrograms.class, "_starsGetEnrollmentPrograms", "stars-GetEnrollmentPrograms", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetEnrollmentPrograms();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetEnrollmentPrograms( (StarsGetEnrollmentPrograms) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetEnrollmentPrograms();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetEnrollmentPrograms
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetEnrollmentProgramsResponse
        desc = new XMLFieldDescriptorImpl(StarsGetEnrollmentProgramsResponse.class, "_starsGetEnrollmentProgramsResponse", "stars-GetEnrollmentProgramsResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetEnrollmentProgramsResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetEnrollmentProgramsResponse( (StarsGetEnrollmentProgramsResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetEnrollmentProgramsResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetEnrollmentProgramsResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetCustSelectionLists
        desc = new XMLFieldDescriptorImpl(StarsGetCustSelectionLists.class, "_starsGetCustSelectionLists", "stars-GetCustSelectionLists", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetCustSelectionLists();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetCustSelectionLists( (StarsGetCustSelectionLists) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetCustSelectionLists();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetCustSelectionLists
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsGetCustSelectionListsResponse
        desc = new XMLFieldDescriptorImpl(StarsGetCustSelectionListsResponse.class, "_starsGetCustSelectionListsResponse", "stars-GetCustSelectionListsResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsGetCustSelectionListsResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsGetCustSelectionListsResponse( (StarsGetCustSelectionListsResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsGetCustSelectionListsResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsGetCustSelectionListsResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCreateAppliance
        desc = new XMLFieldDescriptorImpl(StarsCreateAppliance.class, "_starsCreateAppliance", "stars-CreateAppliance", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsCreateAppliance();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsCreateAppliance( (StarsCreateAppliance) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCreateAppliance();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCreateAppliance
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCreateApplianceResponse
        desc = new XMLFieldDescriptorImpl(StarsCreateApplianceResponse.class, "_starsCreateApplianceResponse", "stars-CreateApplianceResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsCreateApplianceResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsCreateApplianceResponse( (StarsCreateApplianceResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCreateApplianceResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCreateApplianceResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCreateLMHardware
        desc = new XMLFieldDescriptorImpl(StarsCreateLMHardware.class, "_starsCreateLMHardware", "stars-CreateLMHardware", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsCreateLMHardware();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsCreateLMHardware( (StarsCreateLMHardware) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCreateLMHardware();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCreateLMHardware
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCreateLMHardwareResponse
        desc = new XMLFieldDescriptorImpl(StarsCreateLMHardwareResponse.class, "_starsCreateLMHardwareResponse", "stars-CreateLMHardwareResponse", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsCreateLMHardwareResponse();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsCreateLMHardwareResponse( (StarsCreateLMHardwareResponse) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCreateLMHardwareResponse();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCreateLMHardwareResponse
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsDefaultThermostatSettings
        desc = new XMLFieldDescriptorImpl(StarsDefaultThermostatSettings.class, "_starsDefaultThermostatSettings", "stars-DefaultThermostatSettings", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsOperation target = (StarsOperation) object;
                return target.getStarsDefaultThermostatSettings();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsOperation target = (StarsOperation) object;
                    target.setStarsDefaultThermostatSettings( (StarsDefaultThermostatSettings) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsDefaultThermostatSettings();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsDefaultThermostatSettings
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsOperationDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsOperation.class;
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
