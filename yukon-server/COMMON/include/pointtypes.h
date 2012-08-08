#pragma once

// #define SYS_DID_SYSTEM        0    // A catchall for any op which needs an ID
#define SYS_PID_SYSTEM                    0        // Catch all
#define SYS_PID_PORTER                    -1       //
#define SYS_PID_SCANNER                   -2       //
#define SYS_PID_DISPATCH                  -3       //
#define SYS_PID_MACS                      -4       //
#define SYS_PID_CAPCONTROL                -5       //
#define SYS_PID_LOADMANAGEMENT            -10      // A load management point


enum CtiPointType_t
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

    // Placeholder - allows point type looping.
    InvalidPointType
};


enum CtiControlType_t
{
    ControlType_Normal,
    ControlType_Latch,
    ControlType_Pseudo,
    ControlType_SBOLatch,
    ControlType_SBOPulse,

    ControlType_Invalid
};

typedef enum
{
   ArchiveTypeNone = 0,
   ArchiveTypeOnChange,
   ArchiveTypeOnTimer,
   ArchiveTypeOnUpdate,
   ArchiveTypeOnTimerAndUpdated,
   ArchiveTypeOnTimerOrUpdated

} CtiArchiveType_t;

// Point Value Calculation Types
typedef enum
{
   CalcTypeNormal = 0,                     // Your basic multiplier dataoffset combo.
   CalcTypeVoltsFromV2H

} PointCalc_t;
