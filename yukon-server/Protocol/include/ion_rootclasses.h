#pragma warning( disable : 4786 )

#ifndef __ION_ROOTCLASSES_H__
#define __ION_ROOTCLASSES_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_rootclasses.h
 *
 * Class:  CtiIONSerializable, CtiIONValue, CtiIONClass, CtiIONDataStream
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 *         Root classes for the ION serial protocol
 *
 *         ion_rootclasses.h                ----+
 *         |                                    |
 *         +---ion_basictypes.h             ----+
 *         |   |                                |
 *         |   +---ion_arraytypes.h         ----+-- ion_moduletypes.h
 *         |       |                            |
 *         |       +---ion_structtypes.h    ----+   (utilizes, doesn't directly inherit)
 *         |
 *         +---ion_net_*.h
 *
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <vector>
using namespace std;

#ifndef FALSE
    #define FALSE 0
#endif  //  #ifndef FALSE
#ifndef TRUE
    #define TRUE 1
#endif  //  #ifndef TRUE

#include "dlldefs.h"


class IM_EX_PROT CtiIONSerializable
{
public:
    CtiIONSerializable( )  { };
    ~CtiIONSerializable( )  { };

    virtual void putSerialized( unsigned char *buf )  { };
    virtual unsigned int getSerializedLength( void )  { return 0; };
    unsigned char *getSerialized( void );
};



class IM_EX_PROT CtiIONValue : protected CtiIONSerializable
{
public:

    ~CtiIONValue( ) { };

    enum IONValueTypes;

    IONValueTypes getType( void )   { return _valueType; };
    int           isNumeric( void );
    int           isValid( void )   { return _valid; };

    enum IONValueTypes
    {
        IONChar,
        IONFloat,
        IONSignedInt,
        IONTime,
        IONUnsignedInt,
        IONNumeric,
        IONBoolean,
        IONArray,
        IONProgram,
        IONInvalid
    };

protected:

    CtiIONValue( IONValueTypes valueType ) :
        _valueType(valueType)
        { };
    IONValueTypes _valueType;
    void setValid( int valid ) { _valid = valid; };

    friend class CtiIONDataStream;  //  ideally, only DataStream would be friend to IONValue, and only for the sake of
    friend class CtiIONStruct;      //    restoreObject.  however, Struct and Method do need to pull in subsequent values,
    friend class CtiIONMethod;      //    so they are given access as well.
    friend class CtiIONArray;
    friend class CtiIONStructArray;

    void putSerialized( unsigned char *buf );
    unsigned int getSerializedLength( void );

    virtual void putSerializedHeader( unsigned char *buf );
    virtual unsigned int getSerializedHeaderLength( void );

    virtual void putSerializedValue( unsigned char *buf ) = 0;
    virtual unsigned int getSerializedValueLength( void ) = 0;

    virtual unsigned char *putClassSize( unsigned char key, unsigned char *buf );

    static CtiIONValue *restoreObject( unsigned char *&byteStream, unsigned long &streamLength );

private:
    int _valid;
};



class IM_EX_PROT CtiIONDataStream : public CtiIONSerializable
{
public:
    CtiIONDataStream( unsigned char *byteStream, unsigned long streamLength );
    CtiIONDataStream( )  { };
    ~CtiIONDataStream( );

    CtiIONValue *getItem( int index );
    int getItemCount( void );
    CtiIONDataStream &appendItem( CtiIONValue *toInsert );

    virtual void putSerialized( unsigned char *buf );
    virtual unsigned int getSerializedLength( void );

private:
    void parseByteStream( unsigned char *byteStream, unsigned long streamLength );

    vector<CtiIONValue *> _streamValues;

};



class IM_EX_PROT CtiIONMethod
{
public:
    CtiIONMethod( )  { };

    enum IONSimpleMethods;
    enum IONExtendedMethods;

    CtiIONMethod( IONSimpleMethods method, CtiIONValue *parameter=NULL );
    CtiIONMethod( IONExtendedMethods method, CtiIONValue *parameter=NULL );
    CtiIONMethod( unsigned char *byteStream, unsigned long streamLength );
    ~CtiIONMethod( )  { };

    int isValid( void )  { return _valid; };

    unsigned int getSerializedValueLength( void );
    void putSerializedValue( unsigned char *buf );

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

private:

    unsigned char   _valid;
    unsigned char   _methodNum;
    unsigned short  _extendedMethod;
    unsigned char   _valueIncluded;
    CtiIONValue    *_parameter;

};



class IM_EX_PROT CtiIONStatement
{
public:

    CtiIONStatement( ) { };
    CtiIONStatement( unsigned int handle, CtiIONMethod *method ) :
        _handle(handle),
        _method(method)
        { };
    ~CtiIONStatement( );

    void setHandle( unsigned long handle );
    int getHandle( void );

    void setMethod( CtiIONMethod *method );
    CtiIONMethod getMethod( void );

    unsigned int getSerializedValueLength( void );
    void putSerializedValue( unsigned char *buf );

private:

    unsigned int _handle;
    CtiIONMethod *_method;
};


#endif  //  #ifndef __ION_ROOTCLASSES_H__

