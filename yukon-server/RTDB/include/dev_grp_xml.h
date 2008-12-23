#ifndef __DEV_GROUP_XML_H__
#define __DEV_GROUP_XML_H__

#include "dev_grp_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupXml : public CtiDeviceGroupExpresscom
{
	public:

		typedef CtiDeviceGroupExpresscom Inherited;
		typedef CtiDeviceGroupBase Inherited2;

		CtiDeviceGroupXml();
		~CtiDeviceGroupXml();
		CtiDeviceGroupXml& operator=(const CtiDeviceGroupXml& aRef);

		std::vector< std::vector<string> > getParameters();
		void setParameters( std::vector< std::vector<string> > parameters );

		virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;

		virtual void getPropertiesSql(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
		virtual void decodePropertiesSql(RWDBReader &rdr);

		virtual void clearProperties();
	private:
		std::vector< std::vector<string> > _parameters;
};

typedef shared_ptr<CtiDeviceGroupXml> CtiDeviceGroupXmlSPtr;

#endif

