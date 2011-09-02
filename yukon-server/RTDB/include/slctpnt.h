#pragma once

#include "dlldefs.h"
#include "pt_base.h"

IM_EX_PNTDB BOOL isPoint(CtiPoint*,void*);
IM_EX_PNTDB BOOL isPointOnDeviceID(CtiPoint*,void*);
IM_EX_PNTDB BOOL isStatusPoint(CtiPoint*,void*);
IM_EX_PNTDB BOOL isAnalogPoint(CtiPoint*,void*);
IM_EX_PNTDB BOOL isAccumPoint(CtiPoint*,void*);
IM_EX_PNTDB BOOL isCalcPoint(CtiPoint*,void*);

IM_EX_PNTDB BOOL isPointEqual(CtiPoint* pSp, void *arg);
