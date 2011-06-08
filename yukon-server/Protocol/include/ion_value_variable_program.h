/*-----------------------------------------------------------------------------*
 *
 * File:   ion_value_variable_program.h
 *
 * Class:  CtiIONProgram
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __ION_VALUE_VARIABLE_PROGRAM_H__
#define __ION_VALUE_VARIABLE_PROGRAM_H__
#pragma warning( disable : 4786 )


#include <vector>

#include "ion_value_variable.h"

class CtiIONStatement;
class CtiIONMethod;


class CtiIONProgram : public CtiIONValueVariable
{
private:

    std::vector< CtiIONStatement * > _statements;

protected:

    unsigned char getVariableClassDescriptor( void ) const;

    unsigned int getSerializedValueLength( void ) const;
    void putSerializedValue( unsigned char *buf ) const;

public:

    CtiIONProgram( );
    CtiIONProgram( CtiIONStatement *initial );
    CtiIONProgram( unsigned char *byteStream, unsigned long streamLength );

    virtual ~CtiIONProgram( );

    CtiIONProgram &addStatement( CtiIONStatement *toAdd );
    CtiIONProgram &clearAndDestroy( void );
};


class CtiIONStatement
{
private:

    bool _valid;
    unsigned short _handle;
    CtiIONMethod *_method;

protected:

    friend class CtiIONProgram;

    CtiIONStatement( unsigned char *byteStream, unsigned long streamLength, unsigned long *bytesUsed );

    unsigned int getLength( void ) const;
    void write( unsigned char *buf ) const;

    void setValid( bool valid );

public:

    CtiIONStatement( );
    CtiIONStatement( unsigned int handle, CtiIONMethod *method );
    virtual ~CtiIONStatement( );

    bool isValid( void );

    void setHandle( unsigned long handle );
    int  getHandle( void );

    void setMethod( CtiIONMethod *method );
    CtiIONMethod getMethod( void );
};


class CtiIONMethod
{
private:

    bool            _valid;
    unsigned char   _methodNum;
    unsigned short  _extendedMethodNum;
    CtiIONValue    *_parameter;

protected:

    friend class CtiIONStatement;

    CtiIONMethod( unsigned char *byteStream, unsigned long streamLength, unsigned long *bytesUsed );

    unsigned int getLength( void ) const;
    void write( unsigned char *buf ) const;

    void setValid( bool valid );

public:

    enum IONSimpleMethods;
    enum IONExtendedMethods;

    CtiIONMethod( );
    CtiIONMethod( IONSimpleMethods method, CtiIONValue *parameter=NULL );
    CtiIONMethod( IONExtendedMethods method, CtiIONValue *parameter=NULL );
    virtual ~CtiIONMethod( );

    int isValid( void );

    enum IONSimpleMethods
    {
        ReadIONClass                  = 0x01,
        ReadIONName                   = 0x02,
        ReadIONLabel                  = 0x03,
        ReadRegisterTime              = 0x14,
        ReadRegisterValue             = 0x15,
        WriteRegisterValue            = 0x16,
        ReadBooleanRegisterOnName     = 0x1C,
        ReadBooleanRegisterOffName    = 0x1D,
        ReadBooleanRegisterOnLabel    = 0x1E,
        ReadBooleanRegisterOffLabel   = 0x1F,
        ReadBooleanRegisterStateLabel = 0x20,
        ReadArrayRegisterDepth        = 0x23,
        ReadArrayRegisterRollover     = 0x25,
        ReadLogRegisterPosition       = 0x28,
        ReadEventLogAlarms            = 0x2D,
        ReadModuleSetupCounter        = 0x50,
        ReadManagerSetupCounter       = 0x64,
        ReadValue                     = 0x95,
        WriteValue                    = 0x96,
        ExtendedMethod                = 0xFF
    };

    enum IONExtendedMethods
    {
        NoExtendedMethod             = 0x0000,
        WriteIONLabel                = 0x0080,
        ReadSecurityLevel            = 0x0081,
        ReadAllSecurityLevels        = 0x0082,
        ReadParentHandle             = 0x0083,
        ReadOwners                   = 0x0084,
        ReadISA                      = 0x0085,
        WriteBooleanRegisterOnLabel  = 0x01F4,
        WriteBooleanRegisterOffLabel = 0x01F5,
        ReadEnumerations             = 0x0208,
        ReadNumericBounds            = 0x021C,
        ReadEventLogAlarmRollover    = 0x0230,
        ReadCalendarValue            = 0x0259,
        WriteCalendarValue           = 0x025A,
        ReadCalendarProfile          = 0x025B,
        WriteCalendarProfile         = 0x025C,
        ReadProfileLabels            = 0x025D,
        WriteProfileLabels           = 0x025E,
        ReadProfileNames             = 0x025F,
        WriteActivityDepth           = 0x0260,
        ReadModuleInputHandles       = 0x03E8,
        WriteModuleInputHandles      = 0x03E9,
        ReadModuleInputClasses       = 0x03EA,
        ReadModuleOutputHandles      = 0x03EB,
        ReadModuleSetupHandles       = 0x03EC,
        ReadModuleUpdatePeriod       = 0x03ED,
        ReadModuleSecurity           = 0x03EE,
        ReadModuleInputNames         = 0x03EF,
        WriteModuleSetupCounter      = 0x03FC,
        ReadModuleState              = 0x0406,
        WriteModuleState             = 0x0407,
        CreateModule                 = 0x05DC,
        DestroyModule                = 0x05DD,
        ReadManagedClass             = 0x05DE,
        WriteManagerSetupCounter     = 0x05DF,
        ReadCollectiveState          = 0x05E1
    };
};


#endif  //  #ifndef __ION_VALUE_VARIABLE_PROGRAM_H__


