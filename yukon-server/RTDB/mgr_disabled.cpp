/*-----------------------------------------------------------------------------*
*
* File:   mgr_disabled.cpp
*
* Class:  CtiDisabledManager
* Date:   1/6/2006
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_disabled.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2006/02/10 17:15:11 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )
#include "yukon.h"

#include <rw/db/db.h>

#include "mgr_disabled.h"
#include "disable_entry.h"

#include "dbaccess.h"

CtiDisabledManager::CtiDisabledManager() {}

CtiDisabledManager::~CtiDisabledManager()
{
}

void CtiDisabledManager::RefreshList(CtiDBChangeMsg *pChg)
{
    ptr_type pTempObject;
    bool rowFound = false;
    long changePoint = 0; 
    int changeType = -1;   

    if( pChg )
    {
        changePoint = pChg->getId();            
        changeType = pChg->getTypeOfChange();  
    }
    
    try
    {
        if( changePoint != 0 )
        {
            pTempObject = _smartMap.find(changePoint);

            if( pTempObject )
            {
                DisableListIterator  iter;
                ptr_type    pRelatedObject;

                //First we remove it from everyone elses affected list!
                DisableList *idList = pTempObject->getControllingIDList();
                for( iter = idList->begin(); iter != idList->end(); iter++ )
                {
                    pRelatedObject = _smartMap.find( *iter );
                    if( pRelatedObject )
                    {
                         pRelatedObject->removeAffectedID(changePoint);
                    }
                }

                //Tell everyone not to block from me!
                removeDisabled( pTempObject ); 

                //Remove me from everyones controlling list
                idList = pTempObject->getAffectedIDList();
                for( iter = idList->begin(); iter != idList->end(); iter++ )
                {
                    pRelatedObject = _smartMap.find( *iter );
                    if( pRelatedObject )
                    {
                         pRelatedObject->removeControllingID(changePoint);
                    }
                }

                _smartMap.remove(changePoint);
                if( _portMap.find(changePoint) )
                {
                    _portMap.remove(changePoint);
                }
                else if( _deviceMap.find(changePoint) )
                {
                    _deviceMap.remove(changePoint);
                }
                else if( _routeMap.find(changePoint) )
                {
                    _routeMap.remove(changePoint);
                }
                else if(_transmitterMap.find(changePoint) )
                {
                    _transmitterMap.remove(changePoint);
                }
            }
        }
        else
        {
            //I guess we are going to clear everything and start over (ouch)
            _smartMap.removeAll(NULL, 0);
            _portMap.removeAll(NULL, 0);
            _deviceMap.removeAll(NULL, 0);
            _routeMap.removeAll(NULL, 0);
            _transmitterMap.removeAll(NULL, 0);
        }

        if( changeType != ChangeTypeDelete )
        {
            //This is the initial pao table load.
            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
    
                RWDBTable   keyTable;
    
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Loading full PAO table in Disabled manager" << endl;
                }
                keyTable = db.table("YukonPAObject");
    
                selector <<
                keyTable["paobjectid"] <<
                keyTable["paoclass"] <<
                keyTable["paoname"] <<
                keyTable["type"] <<
                keyTable["disableflag"];
    
                selector.from(keyTable);
                if(changePoint != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( changePoint ) && selector.where() );
    
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                loadPaoObjects(rdr);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done loading PAO table for Disabled manger" << endl;
                }
            }
        
            //Find all ports and set their dependency information
            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
    
                RWDBTable   keyTable;
    
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Updating port connected devices" << endl;
                }
                keyTable = db.table("devicedirectcommsettings");
    
                selector <<
                keyTable["portid"] <<
                keyTable["deviceid"];
                if(changePoint != 0) selector.where( keyTable["deviceid"] == RWDBExpr( changePoint ) || keyTable["portid"] == RWDBExpr( changePoint ) && selector.where() );

                selector.from(keyTable);
    
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                appendDependencies(rdr);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done loading port connected device information" << endl;
                }
            }
    
            //Find all transmitters connected to routes and set their dependencies
            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
    
                RWDBTable   keyTable;
    
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Updating repeater connected routes" << endl;
                }
                keyTable = db.table("route");
    
                selector <<
                keyTable["deviceid"] <<
                keyTable["routeid"];
                if(changePoint != 0) selector.where( keyTable["deviceid"] == RWDBExpr( changePoint ) || keyTable["routeid"] == RWDBExpr( changePoint ) && selector.where() );
    
                selector.from(keyTable);
    
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                appendDependencies(rdr);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done loading repeater route information" << endl;
                }
            }
    
            //Find all repeater routes and set their dependencies
            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
    
                RWDBTable   keyTable;
    
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Updating repeater connected routes" << endl;
                }
                keyTable = db.table("repeaterroute");
    
                selector <<
                keyTable["deviceid"] <<
                keyTable["routeid"];
                if(changePoint != 0) selector.where( keyTable["deviceid"] == RWDBExpr( changePoint ) || keyTable["routeid"] == RWDBExpr( changePoint ) && selector.where() );

                selector.from(keyTable);
    
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                appendDependencies(rdr);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done loading repeater route information" << endl;
                }
            }
    
            //Find all standard routes and update their dependencies
            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
    
                RWDBTable   keyTable;
    
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Updating transmitter connected routes" << endl;
                }
                keyTable = db.table("deviceroutes");
    
                selector <<
                keyTable["routeid"] <<//the routes disable the devices in this case
                keyTable["deviceid"];
                if(changePoint != 0) selector.where( keyTable["deviceid"] == RWDBExpr( changePoint ) || keyTable["routeid"] == RWDBExpr( changePoint ) && selector.where() );

                selector.from(keyTable);
    
                //Select all device to route connections, then we need to look through our own database to determine
                //which transmitters are disabled.
    
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                appendDependencies(rdr);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done loading transmitter route information" << endl;
                }
            }
    
            //Find all macro routes and update their dependencies!
            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();
    
                RWDBTable   keyTable, table;
    
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Updating macro routes" << endl;
                }
                keyTable = db.table("macroroute");
    
                selector <<
                keyTable["singlerouteid"] <<//The singles need to inform the macro they are disabled
                keyTable["routeid"];
                if(changePoint != 0) selector.where( keyTable["singlerouteid"] == RWDBExpr( changePoint ) || keyTable["routeid"] == RWDBExpr( changePoint ) && selector.where() );
        
                selector.from(keyTable);
    
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                appendMacroDependencies(rdr);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done loading macro route information" << endl;
                }
            }
    
            //At this point, the dependencies are loaded, here is what happens now, if were loading all objects:
            /*
            *   The ports are checked, and block any transmitters connected to them.
            *   The transmitters are checked and block any routes connected to them.
            *   The routes are checked, and block any devices connected to them.
            *   These devices can include repeaters, which in turn block other routes,
            *   which in turn block other devices and so on.
            *   The devices themselves when they change from enabled to disabled tell
            *   the routes, devices, whatever beneath them that they are blocking.
            *
            *   The devices are split like this as it ensures the fastest time for
            *   marking all of the PAObjects
            */

            if( changePoint != 0 )
            {
                //IF we have a specific object, check its controlling points and its disable flag.
                pTempObject = _smartMap.find(changePoint);

                if( pTempObject )
                {
                    DisableListIterator  iter;
                    ptr_type pRelatedObject;
                    bool disabled = false;
    
                    //First we remove it from everyone elses affected list!
                    DisableList *idList = pTempObject->getControllingIDList();
                    for( iter = idList->begin(); iter != idList->end(); iter++ )
                    {
                        pRelatedObject = _smartMap.find( *iter );
                        if( pRelatedObject )
                        {
                             if( pRelatedObject->isDisabled() )
                             {
                                 pTempObject->addBlockingID(pRelatedObject->getID());
                                 disabled = true;
                             }
                        }
                    }

                    if( disabled || pTempObject->checkDisableFlag() )
                    {
                        addDisabled( pTempObject );
                    }
                }
            }
            else
            {
                markDisabledObjects();
            }
    
            if(_smartMap.getErrorCode() != RWDBStatus::ok)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " database had a return code of " << _smartMap.getErrorCode() << endl;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

// To use this the rdr must have 1 colums from a table, and they must be
// in the order [paobject],[dependency]
void CtiDisabledManager::appendDependencies(RWDBReader &rdr)
{
    long paobject, dependency;
    ptr_type paobjectPtr;
    while( _smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok && rdr() )
    {
        
        rdr[0] >> paobject;            // get the object
        rdr[1] >> dependency;          // get the dependency

        paobjectPtr = _smartMap.find(paobject);
        if(paobjectPtr)
        {
            paobjectPtr->addAffectedID(dependency);
        }

        paobjectPtr = _smartMap.find(dependency);
        if(paobjectPtr)
        {
            paobjectPtr->addControllingID(paobject);
        }
    }
}

void CtiDisabledManager::appendMacroDependencies(RWDBReader &rdr)
{
    long paobject, dependency;
    ptr_type paobjectPtr;
    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        
        rdr[0] >> paobject;            // get the object
        rdr[1] >> dependency;          // get the dependency

        paobjectPtr = _smartMap.find(paobject);
        if(paobjectPtr)
        {
            paobjectPtr->addAffectedID(dependency);
        }

        paobjectPtr = _smartMap.find(dependency);
        if(paobjectPtr)
        {
            paobjectPtr->addControllingID(paobject);
            paobjectPtr->incrementMacroCount();
        }
        
    }
}

void CtiDisabledManager::loadPaoObjects(RWDBReader &rdr)
{
    long objectID;
    CtiString objectName, flag, objectClass, objectType;
    CtiDisabledEntry *objectPtr;
    insert_pair insertResult;
    bool boolFlag;

    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["paobjectid"] >> objectID;            // get the object
        rdr["paoname"] >> objectName;          // get the dependency
        rdr["disableflag"] >> flag;
        rdr["paoclass"] >> objectClass;
        rdr["type"] >> objectType;

        objectClass.toLower();
        objectType.toLower();

        boolFlag = ( flag == "y" || flag == "Y" );

        objectPtr = CTIDBG_new CtiDisabledEntry(objectID, boolFlag, objectClass, objectName);

        insertResult = _smartMap.insert(objectID, objectPtr);

        if( insertResult.second )//successful insert
        {
            if( objectClass == "port" )
            {
                _portMap.insert(objectID, insertResult.first->second);
            }
            else if( objectType == "repeater" )
            {
                _deviceMap.insert(objectID, insertResult.first->second);
            }
            else if( objectClass == "route" )
            {
                _routeMap.insert(objectID, insertResult.first->second);
            }
            else if(objectClass == "transmitter" )
            {
                _transmitterMap.insert(objectID, insertResult.first->second);
            }
            else
            {
                _deviceMap.insert(objectID, insertResult.first->second);
            }
        }
        else
        {
            //failed insert, must depete newly created entry!
            delete objectPtr;
            objectPtr = NULL;
        }
    }
}

//looks at every device, and marks it as disabled or not
void CtiDisabledManager::markDisabledObjects()
{
    CtiDisabledEntrySPtr objectPtr;
    spiterator mapIterator;

    for( mapIterator = _portMap.getMap().begin(); mapIterator != _portMap.getMap().end(); mapIterator++ )
    {
        if( mapIterator->second->checkDisableFlag() )
        {
            //The flag was changed, we need to update everyone in the affected id list
            addDisabled( mapIterator->second );
        }
    }

    for( mapIterator = _transmitterMap.getMap().begin(); mapIterator != _transmitterMap.getMap().end(); mapIterator++ )
    {
        if( mapIterator->second->checkDisableFlag() )
        {
            //The flag was changed, we need to update everyone in the affected id list
            addDisabled( mapIterator->second );
        }
    }

    for( mapIterator = _routeMap.getMap().begin(); mapIterator != _routeMap.getMap().end(); mapIterator++ )
    {
        if( mapIterator->second->checkDisableFlag() )
        {
            //The flag was changed, we need to update everyone in the affected id list
            addDisabled( mapIterator->second );
        }
    }

    for( mapIterator = _deviceMap.getMap().begin(); mapIterator != _deviceMap.getMap().end(); mapIterator++ )
    {
        if( mapIterator->second->checkDisableFlag() )
        {
            //The flag was changed, we need to update everyone in the affected id list
            addDisabled( mapIterator->second );
        }
    }

}

void CtiDisabledManager::addDisabled(CtiDisabledEntrySPtr object)
{
    DisableList *idList;
    DisableListIterator iter;
    CtiDisabledEntrySPtr updateObject;

    idList = object->getAffectedIDList();

    for( iter = idList->begin(); iter != idList->end(); iter++ )
    {
        updateObject = _smartMap.find( *iter );
        if( updateObject && updateObject->addBlockingID(object->getID()) )
        {
            addDisabled(updateObject);
        }
    }
}

void CtiDisabledManager::removeDisabled(CtiDisabledEntrySPtr object)
{
    DisableList *idList;
    DisableListIterator iter;
    CtiDisabledEntrySPtr updateObject;

    idList = object->getAffectedIDList();

    for( iter = idList->begin(); iter != idList->end(); iter++ )
    {
        updateObject = _smartMap.find( *iter );
        if( updateObject && updateObject->removeBlockingID(object->getID()) )
        {
            removeDisabled(updateObject);
        }
    }
}

//Returns true if disabled
bool CtiDisabledManager::isObjectDisabled(long paoID)
{
    ptr_type paobjectPtr;
    bool retVal = false;

    paobjectPtr = _smartMap.find(paoID);
    if(paobjectPtr)
    {
        retVal = paobjectPtr->isDisabled();
    }
    return retVal;
}
