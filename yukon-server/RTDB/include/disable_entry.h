/*-----------------------------------------------------------------------------*
*
* File:   disable_entry.h
*
* Class:  CtiDisabledEntry
* Date:   1/6/2006
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/disable_entry.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/09/15 17:59:18 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DISABLE_ENTRY_H__
#define __DISABLE_ENTRY_H__

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;
#include <set>

#include "ctistring.h"

typedef std::set<long>           DisableList;
typedef std::set<long>::_Pairib  DisableListPair;
typedef std::set<long>::iterator DisableListIterator;

class CtiDisabledEntry
{
protected:

private:

    long        _id;                    //This objects id
    CtiString   _name;                  //Name given to this device.
    CtiString   _type;                  //Type helps with identification and may be useful for reporting
    bool        _isDisabled;            //Once initialization is complete, this tells us if this pao is disabled
    bool        _disabledFlag;          //This is the flag in the yukonpaobject table, used on initialization and with macros
    int         _minObjectDisableCount; //The number of disabled objects necessary before this is disabled (macro routes)
    DisableList _controllingIDList;     //The devices that can block me
    DisableList _blockingIDList;        //The objects that are currently blocking this device
    DisableList _affectedIDList;        //These are the objects this device able to block

public:
    CtiDisabledEntry(long paoID, bool isDisabledFlag, CtiString &type, CtiString &name);

    ~CtiDisabledEntry();

    bool isDisabled();

    // These will return true if adding/removing this id resulted in a change in isDisabled
    bool addBlockingID(long paoID);
    bool addAffectedID(long paoID);
    bool addControllingID(long paoID);
    bool removeBlockingID(long paoID);
    bool removeAffectedID(long paoID);
    bool removeControllingID(long paoID);
    bool checkDisableFlag();

    void    incrementMacroCount();
    long    getID();

    DisableList *getAffectedIDList();
    DisableList *getBlockingIDList();
    DisableList *getControllingIDList();
};

typedef shared_ptr< CtiDisabledEntry > CtiDisabledEntrySPtr;


#endif // #ifndef __DISABLE_ENTRY_H__
