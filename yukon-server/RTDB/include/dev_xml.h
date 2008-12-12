#ifndef __DEV_XML_H__
#define __DEV_XML_H__

#include "dev_remote.h"
#include "prot_xml.h"

class IM_EX_DEVDB CtiDeviceXml : public CtiDeviceRemote
{
	public:

		typedef CtiDeviceRemote Inherited;

		CtiDeviceXml();
		~CtiDeviceXml();

		virtual int recvCommRequest(OUTMESS *OutMessage);

		virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
		virtual void CtiDeviceXml::DecodeDatabaseReader(RWDBReader &rdr);
	protected:
		virtual Cti::Protocol::Interface *getProtocol();

	private:
		Cti::Protocol::XmlProtocol * xmlProtocol;

};

typedef shared_ptr<CtiDeviceXml> CtiDeviceXmlSPtr;

#endif

