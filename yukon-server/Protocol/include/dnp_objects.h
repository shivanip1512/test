#pragma warning( disable : 4786)

#ifndef __DNP_OBJECTS_H__
#define __DNP_OBJECTS_H__

/*-----------------------------------------------------------------------------*
*
* File:   dnp_objects
*
* Class:
* Date:   5/21/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/06/20 21:00:38 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


/*
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

THIS MUST BE OBJECTS SOON, I HATE THIS

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
*/

class CtiDNPObjectBlock
{
    enum QualifierType;

    CtiDNPObjectBlock( enum QualifierType type );
    ~CtiDNPObjectBlock();

    enum QualifierType
    {
        NoIndex_ByteStartStop  = 0x00,
        NoIndex_ShortStartStop = 0x01,
        NoIndex_NoRange        = 0x06,
        NoIndex_ByteQty        = 0x07,
        NoIndex_ShortQty       = 0x08,
        ByteIndex_ByteQty      = 0x17,
        ShortIndex_ShortQty    = 0x28
    };

    void setStart( unsigned short );
    void setStop( unsigned short );

/*    void addObject( CtiDNPObject *object );
    void addObject( CtiDNPObject *object, unsigned short index );

    CtiDNPObject *getObject( int index );*/

    int  getLength( void );
    void serialize( char *buf );

    void restore( char *buf, int len );
};





//  this is only for a single point - right now we don't send larger blocks of descriptors/data,
//    but if there's ever a call for a range
#define DNP_MAX_POINT_DATA_SIZE 11

#pragma pack( push, 1 )

union _idx_qty
{
    struct _ind_1oct
    {
        unsigned char start;
        unsigned char stop;
        unsigned char data[DNP_MAX_POINT_DATA_SIZE];
    } ind_1oct;

    struct _ind_2oct
    {
        unsigned short start;
        unsigned short stop;
        unsigned char data[DNP_MAX_POINT_DATA_SIZE];
    } ind_2oct;
/*
    struct
    {
        unsigned long start;
        unsigned long stop;
    } ind_4oct;
*/
    struct _qty_1oct
    {
        unsigned char num;
        unsigned char data[DNP_MAX_POINT_DATA_SIZE];
    } qty_1oct;

    struct _qty_2oct
    {
        unsigned short num;
        unsigned char data[DNP_MAX_POINT_DATA_SIZE];
    } qty_2oct;
/*
    struct
    {
        unsigned short num;
    } qty_4oct;
*/
};

struct dnp_point_descriptor
{
    //  1 byte
    unsigned char group;
    //  2 bytes
    unsigned char variation;
    //  3 bytes
    unsigned char qual_code : 4;
    unsigned char qual_idx  : 3;
    unsigned char qual_x    : 1;
    //  varies (_qty_1_oct is one oct)
    _idx_qty idx_qty;
};

struct dnp_control_relay_output_block
{
    //  1 byte
    struct _control_code
    {
        unsigned char code       : 4;
        unsigned char queue      : 1;
        unsigned char clear      : 1;
        unsigned char trip_close : 2;
    } control_code;

    //  2 bytes
    unsigned char count;
    //  6 bytes
    unsigned long on_time;
    //  10 bytes
    unsigned long off_time;
    //  11 bytes
    unsigned char status;
};

struct dnp_analog_output_block_32_bit
{
    long value;
    char status;
};


#pragma pack( pop )

#endif // #ifndef __DNP_OBJECTS_H__
