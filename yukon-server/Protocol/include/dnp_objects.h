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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:11:26 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma pack( push, 1 )

struct dnp_point_descriptor
{
    unsigned char group;
    unsigned char variation;
    unsigned char qual_code : 4;
    unsigned char qual_idx  : 3;
    unsigned char qual_x    : 1;
    union
    {
        struct
        {
            unsigned char start;
            unsigned char stop;
        } ind_1oct;
        struct
        {
            unsigned short start;
            unsigned short stop;
        } ind_2oct;
/*
        struct
        {
            unsigned long start;
            unsigned long stop;
        } ind_4oct;
 */
        struct
        {
            unsigned char num;
        } qty_1oct;
        struct
        {
            unsigned short num;
        } qty_2oct;
/*
        struct
        {
            unsigned short num;
        } qty_4oct;
 */
    };
};

#pragma pack( pop )

#endif // #ifndef __DNP_OBJECTS_H__
