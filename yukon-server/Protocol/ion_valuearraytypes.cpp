/*-----------------------------------------------------------------------------*
 *
 * File:   ion_valuearraytypes.cpp
 *
 * Class:  CtiION*Array
 * Date:   09/12/2001
 *
 * Author: Matthew Fisher
 *
 *         Array value classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include "ctidbgmem.h" // defines CTIDBG_new
#include "ion_valuearraytypes.h"
#include "ion_valuestructtypes.h"

#include "logger.h"
#include "guard.h"


CtiIONCharArray::CtiIONCharArray( ) :
    CtiIONArray(IONCharArray)
{
}

CtiIONCharArray::CtiIONCharArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed ) :
    CtiIONArray(IONCharArray)
{
    CtiIONValue *tmpValue;
    unsigned long pos = 0;

    setValid( true );

    //  ACH:  maybe check to see that (itemCount * itemLength) < len
    for( unsigned long i = 0; i < itemCount && isValid(); i++ )
    {
        tmpValue = CTIDBG_new CtiIONChar((buf + pos), itemLength);

        if( tmpValue != NULL && tmpValue->isValid() )
        {
            appendArrayElement(tmpValue);
            pos += itemLength;
        }
        else
        {
            setValid(false);
            pos = len;
        }
    }

    *bytesUsed = pos;
}


CtiIONChar *CtiIONCharArray::operator[]( unsigned long index )
{
    return (CtiIONChar *)getArrayElement( index );
}



CtiIONBooleanArray::CtiIONBooleanArray( ) :
    CtiIONArray(IONBooleanArray)
{

}


CtiIONBooleanArray::CtiIONBooleanArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed ) :
    CtiIONArray(IONBooleanArray)
{
    CtiIONValue *tmpValue;
    unsigned long pos = 0;

    setValid( true );

    for( unsigned long i = 0; i < itemCount && isValid(); i++ )
    {
        tmpValue = CTIDBG_new CtiIONBoolean((buf + pos), itemLength);

        if( tmpValue != NULL && tmpValue->isValid() )
        {
            appendArrayElement(tmpValue);
            pos += itemLength;
        }
        else
        {
            setValid(false);
        }
    }

    *bytesUsed = pos;
}


CtiIONUnsignedInt *CtiIONUnsignedIntArray::operator[]( unsigned long index )
{
    return (CtiIONUnsignedInt *)getArrayElement( index );
}



CtiIONFloatArray::CtiIONFloatArray( ) :
    CtiIONArray(IONFloatArray)
{

}


CtiIONFloatArray::CtiIONFloatArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed ) :
    CtiIONArray(IONFloatArray)
{
    CtiIONValue *tmpValue;
    unsigned long pos = 0;

    setValid( TRUE );

    for( unsigned long i = 0; i < itemCount && isValid( ); i++ )
    {
        tmpValue = CTIDBG_new CtiIONFloat((buf + pos), itemLength);

        if( tmpValue != NULL && tmpValue->isValid() )
        {
            appendArrayElement(tmpValue);
            pos += itemLength;
        }
        else
        {
            setValid(false);
            pos = len;
        }
    }

    *bytesUsed = pos;
}


CtiIONFloat *CtiIONFloatArray::operator[]( unsigned long index )
{
    return (CtiIONFloat *)getArrayElement( index );
}



CtiIONSignedIntArray::CtiIONSignedIntArray( ) :
    CtiIONArray(IONSignedIntArray)
{

}


CtiIONSignedIntArray::CtiIONSignedIntArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed ) :
    CtiIONArray(IONSignedIntArray)
{
    CtiIONValue *tmpValue;
    unsigned long pos = 0;

    setValid(true);

    for( unsigned long i = 0; i < itemCount && isValid(); i++ )
    {
        tmpValue = CTIDBG_new CtiIONSignedInt((buf + pos), itemLength);
        if( tmpValue != NULL && tmpValue->isValid() )
        {
            appendArrayElement( tmpValue );
            pos += itemLength;
        }
        else
        {
            setValid(false);
            pos = len;
        }
    }

    *bytesUsed = pos;
}



CtiIONUnsignedIntArray::CtiIONUnsignedIntArray( ) :
    CtiIONArray(IONUnsignedIntArray)
{

}


CtiIONUnsignedIntArray::CtiIONUnsignedIntArray( unsigned char *buf, unsigned long len, unsigned long itemCount, unsigned long itemLength, unsigned long *bytesUsed ) :
    CtiIONArray(IONUnsignedIntArray)
{
    CtiIONValue *tmpValue;
    unsigned long pos = 0;

    setValid(true);

    for( unsigned long i = 0; i < itemCount && isValid(); i++ )
    {
        tmpValue = CTIDBG_new CtiIONUnsignedInt((buf + pos), itemLength);
        if( tmpValue != NULL && tmpValue->isValid() )
        {
            appendArrayElement(tmpValue);
            pos += itemLength;
        }
        else
        {
            setValid(false);
            pos = len;
        }
    }

    *bytesUsed = pos;
}



void CtiIONStruct::init( vector< CtiIONValue * > &structValues )
{
    if( _numElements > 0 )
    {
        if( structValues.size() != _numElements )
        {
            setValid(false);
        }
    }

    for( int i = 0; i < structValues.size() && isValid(); i++ )
    {
        if( structValues[i]->isValid( ) )
        {
            appendArrayElement(structValues[i]);
        }
        else
        {
            setValid(false);
        }
    }
}


unsigned char CtiIONStruct::getStructKey( void )
{
    unsigned char key;

    key  = 0x80;
    key |= getStructType();

    return key;
}


CtiIONValue *CtiIONStruct::operator[]( Elements index )
{
    CtiIONValue *retVal;

    retVal = getArrayElement(index);

    return retVal;
}


CtiIONArray *CtiIONStruct::restoreObject( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos, itemLength;

    CtiIONValue *newValue;
    CtiIONArray *newStruct;

    vector< CtiIONValue * > structValues;

    pos = 0;

    while( (pos < len) && buf[pos] != 0xF3 )  // end of struct
    {
        newValue = CtiIONValue::restoreObject((buf + pos), (len - pos), &itemLength);

        if( newValue != NULL && newValue->isValid() )
        {
            structValues.push_back(newValue);
            pos += itemLength;
        }
        else
        {
            //  on error, we assume the rest of the stream is corrupt
            pos = len;
        }
    }

    if( (buf[pos] == 0xF3) && (pos < len) )
    {
        pos++;
    }

    //  ACH: perhaps i shouldn't try to populate a structure on error... ?
    switch( classDescriptor )
    {
        case IONLogRecord:      newStruct = CTIDBG_new CtiIONLogRecord( structValues );    break;
        case IONAlarm:          newStruct = CTIDBG_new CtiIONAlarm( structValues );        break;
        case IONEvent:          newStruct = CTIDBG_new CtiIONEvent( structValues );        break;
        case IONRange:          newStruct = CTIDBG_new CtiIONRange( structValues );        break;
        case IONList:           newStruct = CTIDBG_new CtiIONList( structValues );         break;
        case IONException:      newStruct = CTIDBG_new CtiIONException( structValues );    break;
        case IONWaveform:       newStruct = CTIDBG_new CtiIONWaveform( structValues );     break;
        case IONDate:           newStruct = CTIDBG_new CtiIONDate( structValues );         break;
        case IONCalendar:       newStruct = CTIDBG_new CtiIONCalendar( structValues );     break;
        case IONProfile:        newStruct = CTIDBG_new CtiIONProfile( structValues );      break;
        case IONStringArray:    newStruct = CTIDBG_new CtiIONStringArray( structValues );  break;

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            newStruct = NULL;
            pos = len;
        }
    }

    *bytesUsed = pos;

    return newStruct;
}



unsigned char CtiIONStructArray::getStructArrayKey( void )
{
    unsigned char key;

    key  = 0x90;
    key |= getStructArrayType();

    return key;
}


CtiIONArray *CtiIONStructArray::restoreObject( unsigned char classDescriptor, unsigned char *buf, unsigned long len, unsigned long *bytesUsed )
{
    unsigned long pos, itemLength;

    CtiIONValue *newValue;
    CtiIONArray *newStructArray;

    vector< CtiIONValue * > structValues;

    pos = 0;

    while( (buf[pos] != 0xF9) && (pos < len) )
    {
        newValue = CtiIONValue::restoreObject((buf + pos), (len - pos), &itemLength);

        if( newValue != NULL && newValue->isValid() && (((CtiIONStruct *)newValue)->getStructType( ) == classDescriptor) )
        {
            structValues.push_back(newValue);
            pos += itemLength;
        }
        else
        {
            pos = len;
        }
    }

    if( buf[pos] == 0xF9 )
    {
        pos++;

        //  ACH:  define all struct array types

        switch( classDescriptor )
        {   /*
            case IONLogArray:           newStructArray = CTIDBG_new CtiIONLogArray( structValues );         break;
            case IONAlarmArray:         newStructArray = CTIDBG_new CtiIONAlarmArray( structValues );       break;
            case IONStringArrayArray:   newStructArray = CTIDBG_new CtiIONStringArrayArray( structValues ); break;
            case IONMultiArray:         newStructArray = CTIDBG_new CtiIONMultiArray( structValues );       break;
            */
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                newStructArray = NULL;
            }
        }
    }

    *bytesUsed = pos;

    return newStructArray;
}




