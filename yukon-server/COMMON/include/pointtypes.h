/*-----------------------------------------------------------------------------*
*
* File:   pointtypes
*
* Class:
* Date:   4/17/2000
*
* Author: Corey G. Plender
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __POINTTYPES_H__
#define __POINTTYPES_H__
#pragma warning( disable : 4786)


// #define SYS_DID_SYSTEM        0    // A catchall for any op which needs an ID
#define SYS_PID_SYSTEM                    0        // Catch all
#define SYS_PID_PORTER                    -1       //
#define SYS_PID_SCANNER                   -2       //
#define SYS_PID_DISPATCH                  -3       //
#define SYS_PID_MACS                      -4       //
#define SYS_PID_CAPCONTROL                -5       //
#define SYS_PID_LOADMANAGEMENT            -10      // A load management point

/*-----------------------------------------------------------------------------*
 * Point Stuff
 *
 *-----------------------------------------------------------------------------*/

typedef enum
{
   StatusPointType,
   AnalogPointType,
   PulseAccumulatorPointType,
   DemandAccumulatorPointType,
   CalculatedPointType,
   StatusOutputPointType,
   AnalogOutputPointType,
   SystemPointType,
   CalculatedStatusPointType,

   InvalidPointType               // Place Holder - allows point type looping.

}  CtiPointType_t;

typedef enum
{
   NoneControlType = 0,
   NormalControlType,
   LatchControlType,
   PseudoControlType,
   SBOLatchControlType,
   SBOPulseControlType,

   InvalidControlType              // Place Holder - allows point type looping.

}  CtiControlType_t;

// Analog point defines
#define MAX_POINTLIMITS          2     // Allow up to two point limits currently

typedef enum
{
   NoFilter = 0,
   LastValueFilter,
   DefaultValueFilter,

   InvalidFilter              // Place Holder - allows point type looping.

}  CtiFilter_t;

typedef enum
{
   ArchiveTypeNone = 0,
   ArchiveTypeOnChange,
   ArchiveTypeOnTimer,
   ArchiveTypeOnUpdate,
   ArchiveTypeOnTimerAndUpdated,

} CtiArchiveType_t;

// Point Value Calculation Types
typedef enum
{
   CalcTypeNormal = 0,                     // Your basic multiplier dataoffset combo.
   CalcTypeVoltsFromV2H

} PointCalc_t;



#endif // #ifndef __POINTTYPES_H__
