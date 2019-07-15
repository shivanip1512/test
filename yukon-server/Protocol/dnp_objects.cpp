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

#include "std_helper.h"

using namespace std;

namespace Cti {
namespace Protocols {
namespace DNP {

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


ObjectBlock::ObjectBlock( QualifierType type, int group, int variation ) :
    _group(group),
    _variation(variation),
    _qualifier(type),
    _start(0),
    _unsolicited(false)
{
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
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unsupported qualifier type "<< type);
        }
    }
}


int ObjectBlock::getGroup(void) const
{
    return _group;
}


int ObjectBlock::getVariation(void) const
{
    return _variation;
}


size_t ObjectBlock::getIndexLength() const
{
    switch( _qualifier )
    {
        case ByteIndex_ByteQty:
        case ByteIndex_ShortQty:
            return 1;
        case ShortIndex_ShortQty:
            return 2;
        default:
            return 0;
    }
}

size_t ObjectBlock::getQuantityLength() const
{
    switch( _qualifier )
    {
        case NoIndex_ByteQty:
        case ByteIndex_ByteQty:
            return 1;
        case NoIndex_ShortQty:
        case ByteIndex_ShortQty:
        case ShortIndex_ShortQty:
            return 2;
        default:
            return 0;
    }
}

size_t ObjectBlock::getStartStopLength() const
{
    switch( _qualifier )
    {
        case NoIndex_ByteStartStop:
            return 1;
        case NoIndex_ShortStartStop:
            return 2;
        default:
            return 0;
    }
}


void ObjectBlock::setUnsolicited()
{
    _unsolicited = true;
}


std::unique_ptr<ObjectBlock> ObjectBlock::makeObjectBlock( QualifierType type, int group, int variation )
{
    return std::unique_ptr<ObjectBlock>(new ObjectBlock(type, group, variation));
}


ObjectBlockPtr ObjectBlock::makeRangedBlock(ObjectPtr object, const unsigned rangeStart)
{
    auto objBlock =
            makeObjectBlock(
                    rangeStart < 256
                        ? NoIndex_ByteStartStop
                        : NoIndex_ShortStartStop,
                    object->getGroup(),
                    object->getVariation());

    objBlock->_start = rangeStart;

    objBlock->_objectList.push_back(std::move(object));

    return std::move(objBlock);
}


template<class T>
static ObjectBlockPtr ObjectBlock::makeRangedBlock(std::map<unsigned, std::unique_ptr<const T>> objects)
{
    if( objects.empty() )
    {
        return ObjectBlockPtr();
    }

    const unsigned biggestIndex = objects.rbegin()->first;  // max offset in the block

    const T &object = *(objects.begin()->second);

    auto objBlock =
            makeObjectBlock(
                biggestIndex < 256
                    ? NoIndex_ByteStartStop
                    : NoIndex_ShortStartStop,
                object.getGroup(),
                object.getVariation());

    objBlock->_start = objects.begin()->first;      // first offset

    for( auto &kv : objects )
    {
        objBlock->_objectList.emplace_back(std::move(kv.second));
        objBlock->_objectIndices.push_back(kv.first);
    }

    return std::move(objBlock);
}


template<class T>
static std::vector<ObjectBlockPtr> ObjectBlock::makeRangedBlocks(std::map<unsigned, std::unique_ptr<const T>> objects)
{
    // create a mapping of the start index to number of consecutive indexes on the input collection keys
    //  e.g  { 1, 2, 3, 6, 8, 9 } -->  { { 1, 3 }, { 6, 1 }, { 8, 2 } }

    std::map<unsigned, unsigned>    splices;

    unsigned
        stride = 0,
        workingIndex = 0;

    for ( auto & e : objects )
    {
        if ( e.first != ( workingIndex + stride ) )
        {
            workingIndex = e.first;
            stride = 0;
        }
        splices[ workingIndex ] = ++stride;
    }

    // break up the input and build the individual blocks

    std::vector<ObjectBlockPtr> blocks;

    for ( auto & [ startIndex, indexCount ] : splices )
    {
        std::map<unsigned, std::unique_ptr<const T>> subsetObjects;

        // extract each splice into the subset mapping and build the block from it

        for ( unsigned count = 0; count < indexCount; ++count )
        {
            auto node = objects.extract( startIndex + count );
            subsetObjects.insert( std::move(node) );
        }

        blocks.emplace_back( ObjectBlock::makeRangedBlock( std::move(subsetObjects) ) );
    }

    return std::move(blocks);
}


//  explicit instantiations for DNP Slave (since it is used internally in ctiprot.dll, no need to export with IM_EX_PROT)
template ObjectBlockPtr ObjectBlock::makeRangedBlock( std::map<unsigned, std::unique_ptr<const AnalogInput>> objects );
template ObjectBlockPtr ObjectBlock::makeRangedBlock( std::map<unsigned, std::unique_ptr<const AnalogOutputStatus>> objects );
template ObjectBlockPtr ObjectBlock::makeRangedBlock( std::map<unsigned, std::unique_ptr<const BinaryInput>> objects );
template ObjectBlockPtr ObjectBlock::makeRangedBlock( std::map<unsigned, std::unique_ptr<const BinaryOutput>> objects );
template ObjectBlockPtr ObjectBlock::makeRangedBlock( std::map<unsigned, std::unique_ptr<const Counter>> objects );


template std::vector<ObjectBlockPtr> ObjectBlock::makeRangedBlocks( std::map<unsigned, std::unique_ptr<const AnalogInput>> objects );
template std::vector<ObjectBlockPtr> ObjectBlock::makeRangedBlocks( std::map<unsigned, std::unique_ptr<const AnalogOutputStatus>> objects );
template std::vector<ObjectBlockPtr> ObjectBlock::makeRangedBlocks( std::map<unsigned, std::unique_ptr<const BinaryInput>> objects );
template std::vector<ObjectBlockPtr> ObjectBlock::makeRangedBlocks( std::map<unsigned, std::unique_ptr<const BinaryOutput>> objects );
template std::vector<ObjectBlockPtr> ObjectBlock::makeRangedBlocks( std::map<unsigned, std::unique_ptr<const Counter>> objects );


ObjectBlockPtr ObjectBlock::makeNoIndexNoRange( int group, int variation )
{
    return makeObjectBlock(
                    NoIndex_NoRange,
                    group,
                    variation);
}


ObjectBlockPtr ObjectBlock::makeIndexedBlock( ObjectPtr object, unsigned index )
{
    auto objBlock =
            makeObjectBlock(
                    index < 256
                        ? ByteIndex_ByteQty
                        : ShortIndex_ShortQty,
                    object->getGroup(),
                    object->getVariation());

    objBlock->_objectList.emplace_back(std::move(object));
    objBlock->_objectIndices.push_back(index);

    return std::move(objBlock);
}


ObjectBlockPtr ObjectBlock::makeLongIndexedBlock( ObjectPtr object, unsigned index )
{
    auto objBlock =
            makeObjectBlock(
                    ShortIndex_ShortQty,
                    object->getGroup(),
                    object->getVariation());

    objBlock->_objectList.emplace_back(std::move(object));
    objBlock->_objectIndices.push_back(index);

    return std::move(objBlock);
}


template <class T>
ObjectBlockPtr ObjectBlock::makeLongIndexedBlock( std::map<unsigned, std::unique_ptr<const T>> objects )
{
    if( objects.empty() )
    {
        return ObjectBlockPtr();
    }

    const T &object = *(objects.begin()->second);

    auto objBlock =
            makeObjectBlock(
                    ShortIndex_ShortQty,
                    object.getGroup(),
                    object.getVariation());

    for( auto &kv : objects )
    {
        unsigned index = kv.first;

        objBlock->_objectList.emplace_back(std::move(kv.second));
        objBlock->_objectIndices.push_back(index);
    }

    return std::move(objBlock);
}


//  explicit instantiations for DNP Slave (since it is used internally in ctiprot.dll, no need to export with IM_EX_PROT)
template ObjectBlockPtr ObjectBlock::makeLongIndexedBlock( std::map<unsigned, std::unique_ptr<const AnalogInput>> objects );
template ObjectBlockPtr ObjectBlock::makeLongIndexedBlock( std::map<unsigned, std::unique_ptr<const AnalogOutputStatus>> objects );
template ObjectBlockPtr ObjectBlock::makeLongIndexedBlock( std::map<unsigned, std::unique_ptr<const BinaryInput>> objects );
template ObjectBlockPtr ObjectBlock::makeLongIndexedBlock( std::map<unsigned, std::unique_ptr<const BinaryOutput>> objects );
template ObjectBlockPtr ObjectBlock::makeLongIndexedBlock( std::map<unsigned, std::unique_ptr<const Counter>> objects );


ObjectBlockPtr ObjectBlock::makeQuantityBlock( ObjectPtr object )
{
    auto objBlock =
            makeObjectBlock(
                    NoIndex_ByteQty,
                    object->getGroup(),
                    object->getVariation());

    objBlock->_objectList.emplace_back(std::move(object));

    return std::move(objBlock);
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
        retval.object = _objectList.at(offset).get();
    }

    if( _objectIndices.size() > offset )
    {
        retval.index = _objectIndices.at(offset);
    }

    return retval;
}


void ObjectBlock::erase( void )
{
    _objectList.clear();
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

    for( const auto &obj : _objectList )
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

        blockSize += obj->getSerializedLen();
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

    unsigned idx = 0;
    for( const auto &obj : _objectList )
    {
        //  add on the index size
        //  ACH: maybe a switch/case someday when there are more than two indexing options...
        if( _qualifier == ByteIndex_ByteQty || _qualifier == ByteIndex_ShortQty )
        {
            buf[pos++] = _objectIndices[idx] & 0xff;
        }
        else if( _qualifier == ShortIndex_ShortQty )
        {
            buf[pos++] = _objectIndices[idx] & 0xff;
            buf[pos++] = (_objectIndices[idx] >> 8) & 0xff;
        }

        //  ACH: should add support for single-bit objects

        obj->serialize(&buf[pos]);

        pos += obj->getSerializedLen();

        idx++;
    }

    return pos;
}


int ObjectBlock::restore( const unsigned char *buf, int len )
{
    int pos = 0, bitpos = 0, qty = 0;

    if( len < ObjectBlockMinSize )
    {
        return len;
    }

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
            _start    = buf[pos++];

            auto stop = buf[pos++];

            qty  = stop - _start;
            qty += 1;

            break;
        }

        case NoIndex_ShortStartStop:
        {
            _start  = buf[pos++];
            _start |= buf[pos++] << 8;

            unsigned short stop;
            stop    = buf[pos++];
            stop   |= buf[pos++] << 8;

            qty  = stop - _start;
            qty += 1;

            break;
        }

        case NoIndex_NoRange:
        {
            qty = 0;

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

        std::unique_ptr<Object> tmpObject;
        unsigned objbitlen = 0;
        unsigned objlen    = 0;

        //  special case for single-bit objects...
        if( (_group == BinaryInput::Group  && _variation == BinaryInput::BI_SingleBitPacked) ||
            (_group == BinaryOutput::Group && _variation == BinaryOutput::BO_SingleBit)      ||
            (_group == BinaryOutputControl::Group && _variation == BinaryOutputControl::BOC_PatternMask) ||
            (_group == InternalIndications::Group && _variation == InternalIndications::II_InternalIndications) )
        {
            std::tie(tmpObject, objbitlen) = restoreBitObject(buf + pos, len - pos, bitpos);
        }
        else
        {
            std::tie(tmpObject, objlen)    = restoreObject(buf + pos, len - pos);
        }

        if( tmpObject )
        {
            _objectList.push_back(std::move(tmpObject));

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

    if( bitpos > 0 )
    {
        pos++;
    }

    return pos;
}


template<class T>
std::unique_ptr<Object> make_object(unsigned char variation)
{
    return std::make_unique<T>(variation);
}


const std::map<int, std::function<unique_ptr<Object>(unsigned char)>> GroupLookup {
        { AnalogInput::Group,            &make_object<AnalogInput>             },
        { AnalogInputChange::Group,      &make_object<AnalogInputChange>       },
        { AnalogInputFrozen::Group,      &make_object<AnalogInputFrozen>       },
        { AnalogInputFrozenEvent::Group, &make_object<AnalogInputFrozenEvent>  },
        { AnalogOutputStatus::Group,     &make_object<AnalogOutputStatus>      },
        { AnalogOutput::Group,           &make_object<AnalogOutput>            },
        { BinaryInput::Group,            &make_object<BinaryInput>             },
        { BinaryInputChange::Group,      &make_object<BinaryInputChange>       },
        { BinaryOutput::Group,           &make_object<BinaryOutput>            },
        { BinaryOutputControl::Group,    &make_object<BinaryOutputControl>     },
        { Counter::Group,                &make_object<Counter>                 },
        { CounterEvent::Group,           &make_object<CounterEvent>            },
        { CounterFrozen::Group,          &make_object<CounterFrozen>           },
        { CounterFrozenEvent::Group,     &make_object<CounterFrozenEvent>      },
        { Time::Group,                   &make_object<Time>                    },
        { TimeCTO::Group,                &make_object<TimeCTO>                 },
        { TimeDelay::Group,              &make_object<TimeDelay>               }};

ObjectBlock::RestoredObject ObjectBlock::restoreObject( const unsigned char *buf, int len )
{
    RestoredObject result;

    if( auto factoryMethod = mapFind(GroupLookup, _group) )
    {
        result.first = (*factoryMethod)(_variation);

        if( result.first )
        {
            result.second = result.first->restore(buf, len);

            if( result.first->isValid() )
            {
                return result;
            }
        }
    }

    CTILOG_ERROR(dout, "unhandled object type ("<< _group <<"), variation ("<< _variation <<"), aborting processing");

    result.first.reset();
    result.second = len;

    return result;
}


const std::map<int, std::function<unique_ptr<Object>(unsigned char)>> BitGroupLookup {
        { BinaryInput::Group,            make_object<BinaryInput>         },
        { BinaryOutput::Group,           make_object<BinaryOutput>        },
        { BinaryOutputControl::Group,    make_object<BinaryOutputControl> },
        { InternalIndications::Group,    make_object<InternalIndications> }};

ObjectBlock::RestoredObject ObjectBlock::restoreBitObject( const unsigned char *buf, int len, int bitoffset )
{
    RestoredObject result;

    if( auto factoryMethod = mapFind(BitGroupLookup, _group) )
    {
        result.first = (*factoryMethod)(_variation);

        if( result.first )
        {
            result.second = result.first->restoreBits(buf, bitoffset, len);

            if( result.first->isValid() )
            {
                return result;
            }
        }
    }

    CTILOG_ERROR(dout, "unhandled object type ("<< _group <<"), variation ("<< _variation <<"), aborting processing");

    result.first.reset();
    result.second = len * 8;

    return result;
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
                        pMsg->setId(*i_itr);

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

