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
* REVISION     :  $Revision: 1.27 $
* DATE         :  $Date: 2008/02/15 21:07:24 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "dllbase.h"
#include "logger.h"

#include "dnp_objects.h"

#include "dnp_object_analoginput.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_internalindications.h"
#include "dnp_object_class.h"
#include "dnp_object_counter.h"
#include "dnp_object_time.h"

using namespace std;

namespace Cti       {
namespace Protocol  {
namespace DNP       {

Object::Object( int group, int variation ) :
    _group(group),
    _variation(variation),
    _valid(false)
{
}

Object::~Object()
{

}

int Object::restore(const unsigned char *buf, int len )
{
    CTILOG_ERROR(dout, "function unimplemented");

    return len;
}


int Object::restoreBits( const unsigned char *buf, int bitoffset, int len )
{
    // FIXME is this unimplemented ?

    CTILOG_ERROR(dout, "function unimplemented");

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
    CTILOG_ERROR(dout, "function unimplemented");

    return NULL;
}



ObjectBlock::ObjectBlock() :
    _group(0),
    _variation(0),
    _qualifier(0),
    _start(0),
    _unsolicited(false)
{
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
    _group     = group;
    _variation = variation;
    _unsolicited = false;

    switch( type )
    {
        case NoIndex_ByteQty:
        case ByteIndex_ByteQty:
        case ByteIndex_ShortQty:
        case ShortIndex_ShortQty:
        case NoIndex_NoRange:
        case NoIndex_ShortQty:
        case NoIndex_ByteStartStop:
        case NoIndex_ShortStartStop:
        {
            _qualifier = type;

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unsupported qualifier type "<< type);

            _qualifier = type;
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


void ObjectBlock::setUnsolicited()
{
    _unsolicited = true;
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
                    CTILOG_ERROR(dout,
                            endl <<" attempt to insert mismatched group and variation into an object block"<<
                            endl <<" current: ("<< _group <<", "<< _variation <<")  attempted: ("<< object->getGroup() <<", "<< object->getVariation() <<")"
                            );
                }

                break;
            }

            case ByteIndex_ByteQty:
            case ByteIndex_ShortQty:
            case ShortIndex_ShortQty:
            default:
            {
                CTILOG_ERROR(dout, "attempt to use invalid qualifier ("<< _qualifier <<")");
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
                case ByteIndex_ShortQty:
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
                        CTILOG_ERROR(dout,
                                endl <<" attempt to insert mismatched group and variation into an object block"<<
                                endl <<" current: ("<< _group <<", "<< _variation <<")  attempted: ("<< object->getGroup() <<", "<< object->getVariation() <<")"
                                );
                    }

                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "attempt to use invalid qualifier ("<< _qualifier <<")");
                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, "invalid index ("<< index <<")" );
        }
    }

    return success;
}


bool ObjectBlock::addObjectRange( Object *objectArray, const unsigned start, const unsigned stop )
{
    bool success = false;

    switch( _qualifier )
    {
        case NoIndex_ByteStartStop:
        case NoIndex_ShortStartStop:
        {
            if( objectArray )
            {
                _start = start;

                for( unsigned index = start; index <= stop; ++index )
                {
                    Object *object = objectArray + index - start;

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
                        CTILOG_ERROR(dout,
                                endl <<"attempt to insert mismatched group and variation into an object block"<<
                                endl <<"current: ("<< _group <<", "<< _variation <<")  attempted: ("<< object->getGroup() <<", "<< object->getVariation() <<")"
                                );

                        success = false;

                        // FIXME: is there a break from for loop missing here?
                    }
                }
            }
            else
            {
                CTILOG_ERROR(dout, "null object array ("<< _qualifier <<")");
            }

            break;
        }
        default:
        {
            CTILOG_ERROR(dout, "attempt to use invalid qualifier ("<< _qualifier <<")");
        }
    }

    return success;
}


unsigned ObjectBlock::size( void ) const
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


unsigned ObjectBlock::getSerializedLen( void ) const
{
    unsigned blockSize;

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
        case ByteIndex_ShortQty:
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
            CTILOG_ERROR(dout, "attempt to use invalid qualifier ("<< _qualifier <<")");
        }
    }

    for( unsigned i = 0; i < _objectList.size(); i++ )
    {
        //  add on the index size
        if( _qualifier == ByteIndex_ByteQty || _qualifier == ByteIndex_ShortQty )
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


unsigned ObjectBlock::serialize( unsigned char *buf ) const
{
    unsigned pos, qty;

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
        case ByteIndex_ShortQty:
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
            CTILOG_ERROR(dout, "attempt to use invalid qualifier ("<< _qualifier <<")");
        }
    }

    for( unsigned i = 0; i < _objectList.size(); i++ )
    {
        //  add on the index size
        //  ACH: maybe a switch/case someday when there are more than two indexing options...
        if( _qualifier == ByteIndex_ByteQty || _qualifier == ByteIndex_ShortQty )
        {
            buf[pos++] = _objectIndices[i] & 0xff;
        }
        else if( _qualifier == ShortIndex_ShortQty )
        {
            buf[pos++] = _objectIndices[i] & 0xff;
            buf[pos++] = (_objectIndices[i] >> 8) & 0xff;
        }

        //  ACH: should add support for single-bit objects

        _objectList[i]->serialize(&buf[pos]);

        pos += _objectList[i]->getSerializedLen();
    }

    return pos;
}


int ObjectBlock::restore( const unsigned char *buf, int len )
{
    int pos, bitpos, objlen, objbitlen, qty = 0;
    Object *tmpObj;
    unsigned short tmp;

    pos     = bitpos    = 0;
    objlen  = objbitlen = 0;

    if( len > ObjectBlockMinSize )
    {
        _group     = buf[pos++];
        _variation = buf[pos++];
        _qualifier = buf[pos++];

        switch( _qualifier )
        {
            case ShortIndex_ShortQty:
            case ByteIndex_ShortQty:
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
                CTILOG_ERROR(dout, "Invalid qualifier block type NoIndex_NoRange for response");

                pos = len;
                break;
            }

            default:
            {
                CTILOG_ERROR(dout, "Unknown qualifier block type "<< _qualifier);

                pos = len;
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
                case ByteIndex_ShortQty:
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
                    if( isDebugLudicrous() )
                    {
                        CTILOG_DEBUG(dout, "NoIndex_ByteQty or NoIndex_ShortQty");
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
            if( (_group == BinaryInput::Group  && _variation == BinaryInput::BI_SingleBitPacked) ||
                (_group == BinaryOutput::Group && _variation == BinaryOutput::BO_SingleBit)      ||
                (_group == BinaryOutputControl::Group && _variation == BinaryOutputControl::BOC_PatternMask) ||
                (_group == InternalIndications::Group && _variation == InternalIndications::II_InternalIndications) )
            {
                objbitlen = restoreBitObject(buf + pos, bitpos, len - pos, tmpObj);
            }
            else
            {
                objlen = restoreObject(buf + pos, len - pos, tmpObj);
            }

            if( tmpObj != NULL )
            {
                _objectList.push_back(tmpObj);

                _objectIndices.push_back(idx);

                bitpos += objbitlen;

                while( bitpos >= 8 )
                {
                    bitpos -= 8;
                    pos++;
                }

                pos += objlen;
            }
            else
            {
                CTILOG_ERROR(dout, "error restoring object at pos = "<< pos <<", len = "<< len);

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
    int lenUsed = 0;

    switch( _group )
    {
        case AnalogInput::Group:            obj = CTIDBG_new AnalogInput(_variation);               break;
        case AnalogInputChange::Group:      obj = CTIDBG_new AnalogInputChange(_variation);         break;
        case AnalogInputFrozen::Group:      obj = CTIDBG_new AnalogInputFrozen(_variation);         break;
        case AnalogInputFrozenEvent::Group: obj = CTIDBG_new AnalogInputFrozenEvent(_variation);    break;
        case AnalogOutputStatus::Group:           obj = CTIDBG_new AnalogOutputStatus(_variation);              break;
        case AnalogOutput::Group:      obj = CTIDBG_new AnalogOutput(_variation);         break;
        case BinaryInput::Group:            obj = CTIDBG_new BinaryInput(_variation);               break;
        case BinaryInputChange::Group:      obj = CTIDBG_new BinaryInputChange(_variation);         break;
        case BinaryOutput::Group:           obj = CTIDBG_new BinaryOutput(_variation);              break;
        case BinaryOutputControl::Group:    obj = CTIDBG_new BinaryOutputControl(_variation);       break;
        case Counter::Group:                obj = CTIDBG_new Counter(_variation);                   break;
        case CounterEvent::Group:           obj = CTIDBG_new CounterEvent(_variation);              break;
        case CounterFrozen::Group:          obj = CTIDBG_new CounterFrozen(_variation);             break;
        case CounterFrozenEvent::Group:     obj = CTIDBG_new CounterFrozenEvent(_variation);        break;
        case Time::Group:                   obj = CTIDBG_new Time(_variation);                      break;
        case TimeCTO::Group:                obj = CTIDBG_new TimeCTO(_variation);                   break;

        default:
            obj = NULL;
            break;
    }

    if( obj )
    {
        lenUsed = obj->restore(buf, len);

        if( !obj->isValid() )
        {
            delete obj;
            obj = 0;
        }
    }

    if( !obj )
    {
        CTILOG_ERROR(dout, "unhandled object type ("<< _group <<"), variation ("<< _variation <<"), aborting processing");

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

        case InternalIndications::Group:
            obj = CTIDBG_new InternalIndications(_variation);
            break;

        default:
            obj = NULL;
            break;
    }

    if( obj )
    {
        bitpos += obj->restoreBits(buf, bitpos, len);
    }
    else
    {
        CTILOG_ERROR(dout, "unhandled object type ("<< _group <<"), variation ("<< _variation <<"), aborting processing");

        bitpos = len * 8;
    }

    return bitpos - bitoffset;
}

void ObjectBlock::getPoints( Interface::pointlist_t &points, const TimeCTO *cto, const Time *arrival ) const
{
    CtiPointDataMsg *pMsg;

    switch( _group )
    {
        case AnalogInput::Group:
        case AnalogInputChange::Group:
        case AnalogInputFrozen::Group:
        case AnalogInputFrozenEvent::Group:
        case AnalogOutputStatus::Group:
        case BinaryInput::Group:
        case BinaryInputChange::Group:
        case BinaryOutput::Group:
        case Counter::Group:
        case CounterEvent::Group:
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
                        else if( (*o_itr)->getGroup() == AnalogOutputStatus::Group )
                        {
                            pMsg->setId(pMsg->getId() + AnalogOutputStatus::AnalogOutputOffset);
                        }
                        else if( (*o_itr)->getGroup() == CounterFrozen::Group )
                        {
                            pMsg->setId(pMsg->getId() + CounterFrozen::CounterFrozenOffset);
                        }

                        i_itr++;
                    }

                    //  if we have a time of arrival AND the time wasn't set by an event point,
                    //    set it to the time the DNP RTU claims to have sent it
                    if( arrival && !(pMsg->getTags() & TAG_POINT_DATA_TIMESTAMP_VALID) )
                    {
                        pMsg->setTime(CtiTime(arrival->getSeconds()));
                        pMsg->setTags(TAG_POINT_DATA_TIMESTAMP_VALID);
                    }

                    if( _unsolicited )
                    {
                        pMsg->setTags(TAG_POINT_DATA_UNSOLICITED);
                    }

                    points.push_back(pMsg);
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

