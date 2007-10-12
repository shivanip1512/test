/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id: CommandsType.java,v 1.3 2007/10/12 14:44:59 stacey Exp $
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.ArrayList;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class CommandsType.
 * 
 * @version $Revision: 1.3 $ $Date: 2007/10/12 14:44:59 $
 */
public class CommandsType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _commandList
     */
    private java.util.ArrayList _commandList;


      //----------------/
     //- Constructors -/
    //----------------/

    public CommandsType() 
     {
        super();
        _commandList = new ArrayList();
    } //-- com.cannontech.common.device.definition.model.castor.CommandsType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addCommand
     * 
     * 
     * 
     * @param vCommand
     */
    public void addCommand(com.cannontech.common.device.definition.model.castor.Command vCommand)
        throws java.lang.IndexOutOfBoundsException
    {
        _commandList.add(vCommand);
    } //-- void addCommand(com.cannontech.common.device.definition.model.castor.Command) 

    /**
     * Method addCommand
     * 
     * 
     * 
     * @param index
     * @param vCommand
     */
    public void addCommand(int index, com.cannontech.common.device.definition.model.castor.Command vCommand)
        throws java.lang.IndexOutOfBoundsException
    {
        _commandList.add(index, vCommand);
    } //-- void addCommand(int, com.cannontech.common.device.definition.model.castor.Command) 

    /**
     * Method clearCommand
     * 
     */
    public void clearCommand()
    {
        _commandList.clear();
    } //-- void clearCommand() 

    /**
     * Method enumerateCommand
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateCommand()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_commandList.iterator());
    } //-- java.util.Enumeration enumerateCommand() 

    /**
     * Method getCommand
     * 
     * 
     * 
     * @param index
     * @return Command
     */
    public com.cannontech.common.device.definition.model.castor.Command getCommand(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _commandList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.cannontech.common.device.definition.model.castor.Command) _commandList.get(index);
    } //-- com.cannontech.common.device.definition.model.castor.Command getCommand(int) 

    /**
     * Method getCommand
     * 
     * 
     * 
     * @return Command
     */
    public com.cannontech.common.device.definition.model.castor.Command[] getCommand()
    {
        int size = _commandList.size();
        com.cannontech.common.device.definition.model.castor.Command[] mArray = new com.cannontech.common.device.definition.model.castor.Command[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.cannontech.common.device.definition.model.castor.Command) _commandList.get(index);
        }
        return mArray;
    } //-- com.cannontech.common.device.definition.model.castor.Command[] getCommand() 

    /**
     * Method getCommandCount
     * 
     * 
     * 
     * @return int
     */
    public int getCommandCount()
    {
        return _commandList.size();
    } //-- int getCommandCount() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeCommand
     * 
     * 
     * 
     * @param vCommand
     * @return boolean
     */
    public boolean removeCommand(com.cannontech.common.device.definition.model.castor.Command vCommand)
    {
        boolean removed = _commandList.remove(vCommand);
        return removed;
    } //-- boolean removeCommand(com.cannontech.common.device.definition.model.castor.Command) 

    /**
     * Method setCommand
     * 
     * 
     * 
     * @param index
     * @param vCommand
     */
    public void setCommand(int index, com.cannontech.common.device.definition.model.castor.Command vCommand)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _commandList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _commandList.set(index, vCommand);
    } //-- void setCommand(int, com.cannontech.common.device.definition.model.castor.Command) 

    /**
     * Method setCommand
     * 
     * 
     * 
     * @param commandArray
     */
    public void setCommand(com.cannontech.common.device.definition.model.castor.Command[] commandArray)
    {
        //-- copy array
        _commandList.clear();
        for (int i = 0; i < commandArray.length; i++) {
            _commandList.add(commandArray[i]);
        }
    } //-- void setCommand(com.cannontech.common.device.definition.model.castor.Command) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.common.device.definition.model.castor.CommandsType) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.CommandsType.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
