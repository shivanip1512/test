/*-----------------------------------------------------------------------------*
*
* File:   disable_entry.cpp
*
* Class:  CtiDisabledEntry
* Date:   1/6/2006
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/disable_entry.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2006/02/10 17:15:11 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"
#include "disable_entry.h"

CtiDisabledEntry::CtiDisabledEntry(long paoID, bool isDisabledFlag, CtiString &type, CtiString &name) : _isDisabled(false) ,_minObjectDisableCount(0)
{
    _id = paoID;
    _disabledFlag = isDisabledFlag;
    _type = type;
    _name = name;
}

CtiDisabledEntry::~CtiDisabledEntry()
{}

bool CtiDisabledEntry::isDisabled()
{
    return _isDisabled;
}

// Will return true if adding/removing this id resulted in a change in isDisabled
bool CtiDisabledEntry::addBlockingID(long paoID)
{
    bool retVal = false;
    _blockingIDList.insert(paoID);

    if( !_isDisabled )//this might disable this device
    {
        if( _minObjectDisableCount > 1 )
        {
            if( _blockingIDList.size() >= _minObjectDisableCount )
            {
                _isDisabled = true;
                retVal = true;
            }
        }
        else
        {
            _isDisabled = true;
            retVal = true;
        }
    }

    return retVal;
}

bool CtiDisabledEntry::addAffectedID(long paoID)
{
    DisableListPair result;
    result = _affectedIDList.insert(paoID);
    return result.second;
}

bool CtiDisabledEntry::addControllingID(long paoID)
{
    DisableListPair result;
    result = _controllingIDList.insert(paoID);
    return result.second;
}

// Will return true if adding/removing this id resulted in a change in isDisabled
bool CtiDisabledEntry::removeBlockingID(long paoID)
{
    bool retVal = false;
    _blockingIDList.erase(paoID);

    if( _isDisabled )//this might disable this device
    {
        int idCount = _blockingIDList.size();

        if( idCount >= 1 )
        {
            if( idCount < _minObjectDisableCount && !_disabledFlag )
            {
                _isDisabled = false;
                retVal = true;
            }
        }
        else
        {
            _isDisabled = false;
            retVal = true;
        }
    }

    return retVal;
}

bool CtiDisabledEntry::removeAffectedID(long paoID)
{
    bool retVal = false;

    if( _affectedIDList.erase(paoID) )
    {
        retVal = true;
    }
    return retVal;
}

bool CtiDisabledEntry::removeControllingID(long paoID)
{
    bool retVal = false;

    if( _controllingIDList.erase(paoID) )
    {
        retVal = true;
    }
    return retVal;
}

bool CtiDisabledEntry::checkDisableFlag()
{
    bool retVal = false;

    if( !_isDisabled && _disabledFlag )//this will disable this device
    {
        _isDisabled = true;
        _blockingIDList.insert(_id);//insert self!
        retVal = true;
    }

    return retVal;
}

void CtiDisabledEntry::incrementMacroCount()
{
    _minObjectDisableCount++;
}

long CtiDisabledEntry::getID()
{
    return _id;
}

DisableList *CtiDisabledEntry::getAffectedIDList()
{
    return &_affectedIDList;
}

DisableList *CtiDisabledEntry::getBlockingIDList()
{
    return &_blockingIDList;
}

DisableList *CtiDisabledEntry::getControllingIDList()
{
    return &_controllingIDList;
}
