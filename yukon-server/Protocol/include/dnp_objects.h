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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/06/11 21:14:04 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


/*
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

THIS MUST BE OBJECTS SOON, I HATE THIS

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
*/


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
    unsigned char group;
    unsigned char variation;
    unsigned char qual_code : 4;
    unsigned char qual_idx  : 3;
    unsigned char qual_x    : 1;
    _idx_qty idx_qty;
};

struct dnp_control_relay_output_block
{
    struct _control_code
    {
        unsigned char code       : 4;
        unsigned char queue      : 1;
        unsigned char clear      : 1;
        unsigned char trip_close : 2;
    } control_code;

    unsigned char count;
    unsigned long on_time;
    unsigned long off_time;
    unsigned char status;
};

#pragma pack( pop )

#endif // #ifndef __DNP_OBJECTS_H__
