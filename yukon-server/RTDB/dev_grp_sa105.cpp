
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sa105
*
* Date:   3/25/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/05 19:50:26 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "cmdparse.h"
#include "dev_grp_sa105.h"
#include "expresscom.h"
#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "numstr.h"
#include "utility.h"
    
//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105::CtiDeviceGroupSA105()
{

}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105::CtiDeviceGroupSA105(const CtiDeviceGroupSA105& aRef)
{
   *this = aRef;
}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105::~CtiDeviceGroupSA105()
{
}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105& CtiDeviceGroupSA105::operator=(const CtiDeviceGroupSA105& aRef)
{
   if( this != &aRef )
   {
	   Inherited::operator=( aRef );

	   {
		   CtiLockGuard<CtiLogger> doubt_guard(dout);
		   dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
	   }
   }

   return *this;
}

//====================================================================================================================
//====================================================================================================================

CtiTableSA205105Group CtiDeviceGroupSA105::getLoadGroup( void ) const
{
   return( _loadGroup );
}

//====================================================================================================================
//====================================================================================================================

CtiTableSA205105Group& CtiDeviceGroupSA105::getLoadGroup( void )
{
   return( _loadGroup );
}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105& CtiDeviceGroupSA105::setLoadGroup(const CtiTableSA205105Group& aRef)
{
   _loadGroup = aRef;
   return *this;
}

//====================================================================================================================
//====================================================================================================================

LONG CtiDeviceGroupSA105::getRouteID( void )
{
    return( _loadGroup.getRouteId() );
}

//====================================================================================================================
//====================================================================================================================

RWCString CtiDeviceGroupSA105::getDescription(const CtiCommandParser & parse) const
{
	RWCString tmpStr;

	tmpStr = "Group: " + getName();

	return tmpStr;
}

//====================================================================================================================
//====================================================================================================================

void CtiDeviceGroupSA105::getSQL( RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector )
{
}

//====================================================================================================================
//====================================================================================================================

void CtiDeviceGroupSA105::DecodeDatabaseReader( RWDBReader &rdr )
{
}

//====================================================================================================================
//====================================================================================================================

INT CtiDeviceGroupSA105::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
	INT   nRet = NoError;

	return nRet;
}

//====================================================================================================================
//====================================================================================================================

RWCString CtiDeviceGroupSA105::getPutConfigAssignment( UINT level )
{
	RWCString assign = RWCString("sa105 assign");/* +
							 " U" + CtiNumStr(_loadGroup.getUtility()) +
							 " G" + CtiNumStr(_loadGroup.getGroup()) +
							 " D" + CtiNumStr(_loadGroup.getDivision()) +
							 " S" + CtiNumStr(_loadGroup.getSubstation()) +
							 " F" + CtiNumStr(_loadGroup.getRateFamily()) +
							 " M" + CtiNumStr(_loadGroup.getRateMember());
																  */
	return  assign;
}

