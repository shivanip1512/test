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
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2005/03/10 21:21:41 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dllbase.h"
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

namespace Cti       {
namespace Protocol  {
namespace DNP       {

Object::Object( int group, int variation )
{
    _group     = group;
    _variation = variation;
}

Object::~Object()
{

}

int Object::restore(const unsigned char *buf, int len )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return len;
}


int Object::restoreBits( const unsigned char *buf, int bitoffset, int len )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return (len * 8) - bitoffset;
}


bool Object::isValid( void )
{
    return _valid;
}


int Object::serialize(unsigned char *buf) const
{
    return 0;
}

int Object::getSerializedLen(void) const
{
    return 0;
}

int Object::getGroup(void) const
{
    return _group;
}


int Object::getVariation(void) const
{
    return _variation;
}


CtiPointDataMsg *Object::getPoint( const TimeCTO *cto ) const
{
    return NULL;
}



ObjectBlock::ObjectBlock()
{
    _restoring = true;
    _valid = false;
}


ObjectBlock::ObjectBlock( QualifierType type )
{
    //  this sets the object to take the group and variation from the first object added;
    //    i don't know if this is necessary or desirable, but it's currently the way most
    //    locations construct this object

    init(type, -1, -1);
}

ObjectBlock::ObjectBlock( QualifierType type, int group, int variation )
{
    init(type, group, variation);
}


void ObjectBlock::init( QualifierType type, int group, int variation )
{
    _restoring = false;
    _group     = group;
    _variation = variation;

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


ObjectBlock::~ObjectBlock()
{
    erase();
}


int ObjectBlock::getGroup(void) const
{
    return _group;
}


int ObjectBlock::getVariation(void) const
{
    return _variation;
}


bool ObjectBlock::addObject( const Object *object )
{
    bool success = false;

    if( object )
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

                if( object->getGroup()     == _group &&
                    object->getVariation() == _variation )
                {
                    _objectList.push_back(object);

                    success = true;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - attempt to insert mismatched group and variation into an object block **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << RWTime() << " **** current: (" << _group << ", " << _variation << ")  attempted: (" << object->getGroup() << ", " << object->getVariation() << ") ****" << endl;
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
                    dout << RWTime() << " **** Checkpoint - attempt to use invalid qualifier (" << _qualifier << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                break;
            }
        }
    }

    return success;
}


bool ObjectBlock::addObjectIndex( const Object *object, int index )
{
    bool success = false;

    if( object )
    {
        if( index > 0 )
        {
            //  MAGIC NUMBER WARNING:  turning 1-based offset into a 0-based offset
            index--;

            switch( _qualifier )
            {
                case ByteIndex_ByteQty:
                case ShortIndex_ShortQty:
                {
                    if( _group < 0 )
                    {
                        _group     = object->getGroup();
                        _variation = object->getVariation();
                    }

                    if( object->getGroup()     == _group &&
                        object->getVariation() == _variation )
                    {
                        _objectList.push_back(object);
                        _objectIndices.push_back(index);

                        success = true;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - attempt to insert mismatched group and variation into an object block **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << RWTime() << " **** current: (" << _group << ", " << _variation << ")  attempted: (" << object->getGroup() << ", " << object->getVariation() << ") ****" << endl;
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
                        dout << RWTime() << " **** Checkpoint - attempt to use invalid qualifier (" << _qualifier << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    break;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - invalid index (" << index << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return success;
}


/*void ObjectBlock::addRange( Object *object, int start, int stop )
{

}
*/


int ObjectBlock::size( void ) const
{
    return _objectList.size();
}

bool ObjectBlock::empty( void ) const
{
    return _objectList.empty();
}


ObjectBlock::object_descriptor ObjectBlock::operator[]( unsigned offset ) const
{
    return at(offset);
}


ObjectBlock::object_descriptor ObjectBlock::at( unsigned offset ) const
{
    object_descriptor retval = {0, -1};

    if( _objectList.size() > offset )
    {
        retval.object = _objectList.at(offset);
    }

    if( _objectIndices.size() > offset )
    {
        //  MAGIC NUMBER WARNING:  turning 0-based offset into 1-based offset
        retval.index = _objectIndices.at(offset) + 1;
    }

    return retval;
}


void ObjectBlock::erase( void )
{
    while( !_objectList.empty() )
    {
        delete _objectList.back();

        _objectList.pop_back();
    }
}


int ObjectBlock::getSerializedLen( void ) const
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


int ObjectBlock::serialize( unsigned char *buf ) const
{
    int pos, qty;

    qty = _objectList.size();

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
                buf[pos++] = qty & 0xff;
                break;
            }

        case NoIndex_ShortQty:
        case ShortIndex_ShortQty:
            {
                buf[pos++] = qty & 0xff;
                buf[pos++] = (qty >> 8) & 0xff;
                break;
            }

        case NoIndex_ByteStartStop:
            {
                int stop;

                //  start = 5, qty = 1, want stop = 5
                stop  = _start + qty - 1;

                buf[pos++] = _start & 0xff;
                buf[pos++] = (stop) & 0xff;
                break;
            }

        case NoIndex_ShortStartStop:
            {
                int stop;

                //  start = 5, qty = 1, want stop = 5
                stop  = _start + qty - 1;

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


int ObjectBlock::restore( const unsigned char *buf, int len )
{
    int pos, bitpos, qty;
    Object *tmpObj;
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
                qty  = buf[pos++];
                qty |= buf[pos++] << 8;
                break;
            }

            case NoIndex_ByteQty:
            case ByteIndex_ByteQty:
            {
                qty = buf[pos++];
                break;
            }

            case NoIndex_ByteStartStop:
            {
                _start = buf[pos++];

                //  the stop index
                tmp    = buf[pos++];

                qty   = tmp - _start;
                qty  += 1;

                break;
            }

            case NoIndex_ShortStartStop:
            {
                _start  = buf[pos++];
                _start |= buf[pos++] << 8;

                //  the stop index
                tmp     = buf[pos++];
                tmp    |= buf[pos++] << 8;

                qty   = tmp - _start;
                qty  += 1;

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

        for( int qty_restored = 0; pos < len && qty_restored < qty; qty_restored++ )
        {
            int idx = 0;

            switch( _qualifier )
            {
                case ShortIndex_ShortQty:
                {
                    idx  = buf[pos++];
                    idx |= buf[pos++] << 8;
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
                    idx = _start + qty_restored;
                    break;
                }

                case NoIndex_ByteQty:
                case NoIndex_ShortQty:
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    idx = 0 + qty_restored;

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
                _objectList.push_back(tmpObj);

                _objectIndices.push_back(idx);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - error restoring object at pos " << pos << "/" << len << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


int ObjectBlock::restoreObject( const unsigned char *buf, int len, Object *&obj )
{
    int lenUsed;

    switch( _group )
    {
        case AnalogInput::Group:
            obj = CTIDBG_new AnalogInput(_variation);
            break;

        case AnalogInputChange::Group:
            obj = CTIDBG_new AnalogInputChange(_variation);
            break;

        case AnalogInputFrozen::Group:
            obj = CTIDBG_new AnalogInputFrozen(_variation);
            break;

        case AnalogInputFrozenEvent::Group:
            obj = CTIDBG_new AnalogInputFrozenEvent(_variation);
            break;

        case AnalogOutput::Group:
            obj = CTIDBG_new AnalogOutput(_variation);
            break;

        case AnalogOutputBlock::Group:
            obj = CTIDBG_new AnalogOutputBlock(_variation);
            break;

        case BinaryInput::Group:
            obj = CTIDBG_new BinaryInput(_variation);
            break;

        case BinaryInputChange::Group:
            obj = CTIDBG_new BinaryInputChange(_variation);
            break;

        case BinaryOutput::Group:
            obj = CTIDBG_new BinaryOutput(_variation);
            break;

        case BinaryOutputControl::Group:
            obj = CTIDBG_new BinaryOutputControl(_variation);
            break;

        case Counter::Group:
            obj = CTIDBG_new Counter(_variation);
            break;

        case CounterChange::Group:
            obj = CTIDBG_new CounterChange(_variation);
            break;

        case CounterFrozen::Group:
            obj = CTIDBG_new CounterFrozen(_variation);
            break;

        case CounterFrozenEvent::Group:
            obj = CTIDBG_new CounterFrozenEvent(_variation);
            break;

        case Time::Group:
            obj = CTIDBG_new Time(_variation);
            break;

        case TimeCTO::Group:
            obj = CTIDBG_new TimeCTO(_variation);
            break;

        default:
            obj = NULL;
            break;
    }

    if( obj != NULL )
    {
        lenUsed = obj->restore(buf, len);

        if( !obj->isValid() )
        {
            delete obj;
        }
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


int ObjectBlock::restoreBitObject( const unsigned char *buf, int bitoffset, int len, Object *&obj )
{
    int bitpos;

    bitpos = bitoffset;

    switch( _group )
    {
        case BinaryInput::Group:
            obj = CTIDBG_new BinaryInput(_variation);
            break;

        case BinaryOutput::Group:
            obj = CTIDBG_new BinaryOutput(_variation);
            break;

        case BinaryOutputControl::Group:
            obj = CTIDBG_new BinaryOutputControl(_variation);
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

void ObjectBlock::getPoints( queue< CtiPointDataMsg * > &points, const TimeCTO *cto ) const
{
    CtiPointDataMsg *pMsg;

    switch( _group )
    {
        case AnalogInput::Group:
        case AnalogInputChange::Group:
        case AnalogInputFrozen::Group:
        case AnalogInputFrozenEvent::Group:
        case AnalogOutput::Group:
        case BinaryInput::Group:
        case BinaryInputChange::Group:
        case BinaryOutput::Group:
        case Counter::Group:
        case CounterChange::Group:
        case CounterFrozen::Group:
        case CounterFrozenEvent::Group:
        {
            object_vector::const_iterator o_itr;
            index_vector::const_iterator  i_itr = _objectIndices.begin();

            for( o_itr = _objectList.begin(); o_itr != _objectList.end(); o_itr++ )
            {
                //  we're passing in the CTO object here;  the points can do with it as they please
                if( *o_itr && (pMsg = (*o_itr)->getPoint(cto)) )
                {
                    if( i_itr != _objectIndices.end() )
                    {
                        //  MAGIC NUMBER WARNING:  turning 0-based offset into 1-based offset
                        pMsg->setId(*i_itr + 1);

                        //  if it is a binary output, offset by BinaryOutputStatusOffset (currently 10,000)
                        //    to make sure it doesn't collide with the other status points
                        if( (*o_itr)->getGroup() == BinaryOutput::Group )
                        {
                            pMsg->setId(pMsg->getId() + BinaryOutput::BinaryOutputStatusOffset);
                        }
                        else if( (*o_itr)->getGroup() == AnalogOutput::Group )
                        {
                            pMsg->setId(pMsg->getId() + AnalogOutput::AnalogOutputOffset);
                        }

                        i_itr++;
                    }

                    points.push(pMsg);
                }
            }
        }

        default:
        {
            break;
        }
    }
}


}
}
}

