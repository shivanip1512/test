#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dnp_objects
*
* Date:   5/21/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/07/25 20:53:19 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"

#include "dnp_objects.h"

#include "dnp_object_analoginput.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_class.h"
#include "dnp_object_counter.h"
#include "dnp_object_time.h"

using namespace std;

CtiDNPObject::CtiDNPObject( int group, int variation )
{
    _group     = group;
    _variation = variation;
}

CtiDNPObject::~CtiDNPObject()
{

}

int CtiDNPObject::restore( unsigned char *buf, int len )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int CtiDNPObject::restoreBits( unsigned char *buf, int bitoffset, int len )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return (len * 8) - bitoffset;
}


int CtiDNPObject::serialize(unsigned char *buf)
{
    return 0;
}

int CtiDNPObject::getSerializedLen(void)
{
    return 0;
}

int CtiDNPObject::getGroup(void)
{
    return _group;
}


int CtiDNPObject::getVariation(void)
{
    return _variation;
}


CtiPointDataMsg *CtiDNPObject::getPoint( void )
{
    return NULL;
}



CtiDNPObjectBlock::CtiDNPObjectBlock()
{
    _restoring = true;
    _valid = false;
}


CtiDNPObjectBlock::CtiDNPObjectBlock( enum QualifierType type )
{
    _restoring = false;
    _group     = -1;
    _variation = -1;

    switch( type )
    {
        case NoIndex_ByteQty:
        case ByteIndex_ByteQty:
        case ShortIndex_ShortQty:
        case NoIndex_NoRange:
        case NoIndex_ShortQty:
        {
            _qualifier = type;
            _valid     = true;

            break;
        }

        case NoIndex_ByteStartStop:
        case NoIndex_ShortStartStop:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported qualifier type " << type << endl;
            }

            _qualifier = type;
            _valid     = false;

            break;
        }
    }
}


CtiDNPObjectBlock::~CtiDNPObjectBlock()
{
    if( !_objectList.empty() )
    {
        eraseObjectList();
    }
}


void CtiDNPObjectBlock::addObject( CtiDNPObject *object )
{
    switch( _qualifier )
    {
        case NoIndex_ByteQty:
        case NoIndex_NoRange:
        case NoIndex_ShortQty:
        {
            if( _group < 0 )
            {
                _group     = object->getGroup();
                _variation = object->getVariation();
            }
            else if( object->getGroup() == _group )
            {
                _objectList.push_back(object);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            break;
        }

        case ByteIndex_ByteQty:
        case ShortIndex_ShortQty:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }

    _qty = _objectList.size();
}


void CtiDNPObjectBlock::addObjectIndex( CtiDNPObject *object, int index )
{
    switch( _qualifier )
    {
        case ByteIndex_ByteQty:
        case ShortIndex_ShortQty:
        {
            if( _group < 0 )
            {
                _group     = object->getGroup();
                _variation = object->getVariation();

                _objectList.push_back(object);
                _objectIndices.push_back(index);
            }
            else if( object->getGroup() == _group )
            {
                _objectList.push_back(object);
                _objectIndices.push_back(index);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            break;
        }

        case NoIndex_ByteQty:
        case NoIndex_NoRange:
        case NoIndex_ShortQty:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }

    _qty = _objectList.size();
}

/*void CtiDNPObjectBlock::addRange( CtiDNPObject *object, int start, int stop )
{

}

CtiDNPObject *CtiDNPObjectBlock::getObject( int index )
{

}*/

void CtiDNPObjectBlock::eraseObjectList( void )
{
    while( !_objectList.empty() )
    {
        if( _objectList.back() != NULL )
        {
            delete _objectList.back();
        }

        _objectList.pop_back();
    }
}


int CtiDNPObjectBlock::getSerializedLen( void ) const
{
    int blockSize;

    blockSize = ObjectBlockMinSize;

    //  add on the range size
    switch( _qualifier )
    {
        case ByteIndex_ByteQty:
        case NoIndex_ByteQty:
            {
                blockSize += 1;
                break;
            }

        case NoIndex_ShortQty:
        case ShortIndex_ShortQty:
        case NoIndex_ByteStartStop:
            {
                blockSize += 2;
                break;
            }

        case NoIndex_ShortStartStop:
            {
                blockSize += 4;
            }

        case NoIndex_NoRange:
            {
                break;
            }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }

    for( int i = 0; i < _objectList.size(); i++ )
    {
        //  add on the index size
        if( _qualifier == ByteIndex_ByteQty )
        {
            blockSize += 1;
        }
        else if( _qualifier == ShortIndex_ShortQty )
        {
            blockSize += 2;
        }

        blockSize += _objectList[i]->getSerializedLen();
    }

    return blockSize;
}


int CtiDNPObjectBlock::serialize( unsigned char *buf ) const
{
    int pos, tmpObjLen;

    pos = 0;

    buf[pos++] = _group;
    buf[pos++] = _variation;
    buf[pos++] = _qualifier;

    //  add on the range size
    switch( _qualifier )
    {
        case ByteIndex_ByteQty:
        case NoIndex_ByteQty:
            {
                buf[pos++] = _qty & 0xff;
                break;
            }

        case NoIndex_ShortQty:
        case ShortIndex_ShortQty:
            {
                buf[pos++] = _qty & 0xff;
                buf[pos++] = (_qty >> 8) & 0xff;
                break;
            }

        case NoIndex_ByteStartStop:
            {
                int stop;

                //  start = 5, qty = 1, want stop = 5
                stop  = _start + _qty - 1;

                buf[pos++] = _start & 0xff;
                buf[pos++] = (stop) & 0xff;
                break;
            }

        case NoIndex_ShortStartStop:
            {
                int stop;

                //  start = 5, qty = 1, want stop = 5
                stop  = _start + _qty - 1;

                buf[pos++] = _start & 0xff;
                buf[pos++] = (_start >> 8) & 0xff;
                buf[pos++] = (stop) & 0xff;
                buf[pos++] = (stop >> 8) & 0xff;
                break;
            }

        case NoIndex_NoRange:
            {
                break;
            }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }

    for( int i = 0; i < _objectList.size(); i++ )
    {
        //  add on the index size
        //  ACH: maybe a switch/case someday when there are more than two indexing options...
        if( _qualifier == ByteIndex_ByteQty )
        {
            buf[pos++] = _objectIndices[i] & 0xff;
        }
        else if( _qualifier == ShortIndex_ShortQty )
        {
            buf[pos++] = _objectIndices[i] & 0xff;
            buf[pos++] = (_objectIndices[i] >> 8) & 0xff;
        }

        _objectList[i]->serialize(&buf[pos]);

        pos += _objectList[i]->getSerializedLen();
    }

    return pos;
}


int CtiDNPObjectBlock::restore( unsigned char *buf, int len )
{
    int pos, bitpos, qtyRestored;
    CtiDNPObject *tmpObj;
    unsigned short tmp;

    pos    = 0;
    bitpos = 0;

    if( len > ObjectBlockMinSize )
    {
        _group     = buf[pos++];
        _variation = buf[pos++];
        _qualifier = buf[pos++];

        switch( _qualifier )
        {
            case ShortIndex_ShortQty:
            case NoIndex_ShortQty:
            {
                _qty  = buf[pos++];
                _qty |= buf[pos++] << 8;
                break;
            }

            case NoIndex_ByteQty:
            case ByteIndex_ByteQty:
            {
                _qty = buf[pos++];
                break;
            }

            case NoIndex_ByteStartStop:
            {
                _start = buf[pos++];

                //  the stop index
                tmp    = buf[pos++];

                _qty   = tmp - _start;
                _qty  += 1;

                break;
            }

            case NoIndex_ShortStartStop:
            {
                _start  = buf[pos++];
                _start |= buf[pos++] << 8;

                //  the stop index
                tmp     = buf[pos++];
                tmp    |= buf[pos++] << 8;

                _qty   = tmp - _start;
                _qty  += 1;

                break;
            }

            case NoIndex_NoRange:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Invalid qualifier block type " << buf[len] << " for response **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                pos = len;
                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Unknown qualifier block type " << buf[len] << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                pos = len;
                break;
            }
        }

        qtyRestored = 0;

        while( pos < len && qtyRestored < _qty )
        {
            int idx = 0;

            switch( _qualifier )
            {
                case ShortIndex_ShortQty:
                {
                    idx  = buf[pos++];
                    idx |= buf[pos++] >> 8;
                    break;
                }

                case ByteIndex_ByteQty:
                {
                    idx  = buf[pos++];
                    break;
                }

                case NoIndex_ByteStartStop:
                case NoIndex_ShortStartStop:
                {
                    idx = _start + qtyRestored;
                    break;
                }

                default:
                {
                    idx = -1;
                }
            }


            //  special case for single-bit objects...
            if( (_group ==  1 && _variation == 1) ||  //  single-bit binary input
                (_group == 10 && _variation == 1) ||  //  single-bit binary output
                (_group == 12 && _variation == 3) )   //  single-bit pattern mask
            {
                bitpos += restoreBitObject(buf + pos, bitpos, len - pos, tmpObj);

                while( bitpos >= 8 )
                {
                    bitpos -= 8;
                    pos++;
                }
            }
            else
            {
                pos += restoreObject(buf + pos, len - pos, tmpObj);
            }

            if( tmpObj != NULL )
            {
                qtyRestored++;

                _objectList.push_back(tmpObj);

                if( idx >= 0 )
                {
                    _objectIndices.push_back(idx);
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                pos = len;
            }
        }
    }
    else
    {
        pos = len;
    }

    if( bitpos > 0 )
        pos++;

    return pos;
}


bool CtiDNPObjectBlock::hasPoints( void )
{
    bool hasPoints = false;

    switch( _group )
    {
        case CtiDNPAnalogInput::Group:
        case CtiDNPAnalogInputChange::Group:
        case CtiDNPAnalogInputFrozen::Group:
        case CtiDNPAnalogInputFrozenEvent::Group:
        case CtiDNPAnalogOutput::Group:
        case CtiDNPBinaryInput::Group:
        case CtiDNPBinaryInputChange::Group:
        case CtiDNPBinaryOutput::Group:
        case CtiDNPCounter::Group:
        case CtiDNPCounterChange::Group:
        case CtiDNPCounterFrozen::Group:
        case CtiDNPCounterFrozenEvent::Group:
        {
            if( !_objectList.empty() )
            {
                hasPoints = true;
            }
        }

        default:
        {
            break;
        }
    }

    return hasPoints;
}


void CtiDNPObjectBlock::getPoints( RWTPtrSlist< CtiPointDataMsg > &pointList )
{
    CtiDNPObject *tmpObj;
    CtiPointDataMsg *pMsg;

    switch( _group )
    {
        case CtiDNPAnalogInput::Group:
        case CtiDNPAnalogInputChange::Group:
        case CtiDNPAnalogInputFrozen::Group:
        case CtiDNPAnalogInputFrozenEvent::Group:
        case CtiDNPAnalogOutput::Group:
        case CtiDNPBinaryInput::Group:
        case CtiDNPBinaryInputChange::Group:
        case CtiDNPBinaryOutput::Group:
        case CtiDNPCounter::Group:
        case CtiDNPCounterChange::Group:
        case CtiDNPCounterFrozen::Group:
        case CtiDNPCounterFrozenEvent::Group:
        {
            for( int i = 0; i < _objectList.size(); i++ )
            {
                tmpObj = _objectList[i];

                pMsg = tmpObj->getPoint();

                if( pMsg != NULL )
                {
                    if( _objectIndices.size() < i )
                    {
                        pMsg->setId(_objectIndices[i]);
                    }

                    pointList.append(pMsg);
                }
            }
        }

        default:
        {
            break;
        }
    }
}

int CtiDNPObjectBlock::restoreObject( unsigned char *buf, int len, CtiDNPObject *&obj )
{
    int lenUsed;

    switch( _group )
    {
        case CtiDNPAnalogInput::Group:
            obj = new CtiDNPAnalogInput(_variation);
            break;

        case CtiDNPAnalogInputChange::Group:
            obj = new CtiDNPAnalogInputChange(_variation);
            break;

        case CtiDNPAnalogInputFrozen::Group:
            obj = new CtiDNPAnalogInputFrozen(_variation);
            break;

        case CtiDNPAnalogInputFrozenEvent::Group:
            obj = new CtiDNPAnalogInputFrozenEvent(_variation);
            break;

        case CtiDNPAnalogOutput::Group:
            obj = new CtiDNPAnalogOutput(_variation);
            break;

        case CtiDNPAnalogOutputBlock::Group:
            obj = new CtiDNPAnalogOutputBlock(_variation);
            break;

        case CtiDNPBinaryInput::Group:
            obj = new CtiDNPBinaryInput(_variation);
            break;

        case CtiDNPBinaryInputChange::Group:
            obj = new CtiDNPBinaryInputChange(_variation);
            break;

        case CtiDNPBinaryOutput::Group:
            obj = new CtiDNPBinaryOutput(_variation);
            break;

        case CtiDNPBinaryOutputControl::Group:
            obj = new CtiDNPBinaryOutputControl(_variation);
            break;

        case CtiDNPCounter::Group:
            obj = new CtiDNPCounter(_variation);
            break;

        case CtiDNPCounterChange::Group:
            obj = new CtiDNPCounterChange(_variation);
            break;

        case CtiDNPCounterFrozen::Group:
            obj = new CtiDNPCounterFrozen(_variation);
            break;

        case CtiDNPCounterFrozenEvent::Group:
            obj = new CtiDNPCounterFrozenEvent(_variation);
            break;

        case CtiDNPTime::Group:
            obj = new CtiDNPTime(_variation);
            break;

        default:
            obj = NULL;
            break;
    }

    if( obj != NULL )
    {
        lenUsed = obj->restore(buf, len);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        lenUsed = len;
    }

    return lenUsed;
}


int CtiDNPObjectBlock::restoreBitObject( unsigned char *buf, int bitoffset, int len, CtiDNPObject *&obj )
{
    int bitpos;

    bitpos = bitoffset;

    switch( _group )
    {
        case CtiDNPBinaryInput::Group:
            obj = new CtiDNPBinaryInput(_variation);
            break;

        case CtiDNPBinaryOutput::Group:
            obj = new CtiDNPBinaryOutput(_variation);
            break;

        case CtiDNPBinaryOutputControl::Group:
            obj = new CtiDNPBinaryOutputControl(_variation);
            break;

        default:
            obj = NULL;
            break;
    }

    if( obj != NULL )
    {
        bitpos += obj->restoreBits(buf, bitpos, len);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        bitpos = len * 8;
    }

    return bitpos - bitoffset;
}

