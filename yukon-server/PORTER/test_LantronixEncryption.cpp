/*---------------------------------------------------------------------------
        Filename:  test_LantronixEncryption.cpp

        Programmer:  Thain Spar

        Initial Date:  10/10/2008

        COPYRIGHT:  Copyright (C) Cannon Technologies 2007
---------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test Lantronix Encryption"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>

#include "yukon.h"
#include "LantronixEncryptionImpl.h"

using boost::unit_test_framework::test_suite;
using namespace std;

/*
	virtual void decode(char *buf, long bufLen);
	virtual void encode(char *buf, long bufLen);
	void setKey(string key);
*/

BOOST_AUTO_UNIT_TEST(test_decode_pre_encoded)
{
	const int eSize = 66;

	/*IV is the first 16 bytes. 2 for length. the remaining are the data*/
  	unsigned char cEncoded[eSize] = {1,2,3,4,5,6,7,8,9,9,9,9,9,9,9,9,
										 0,35,
										  51,112,170,0,216,
										  103,107,85,85,132,
										  36,142,132,128,36,
										  43,26,215,194,189,
										  16,195,44,150,110,
										  230,71,108,39,50,
										  212,227,231,35,234,
										  180,243,180,128,242,
										  113,152,160,155,47,
										  252,163,145};
	unsigned char key[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	string sKey((char*)key,16);

	string plaintext = "Some things are better left unread.";
	LantronixEncryptionImpl* filter = new LantronixEncryptionImpl();
	filter->setKey(sKey);

	unsigned char* plainText = NULL;
	/* decode will change the passed array, and return the new size. */
	int newSize = filter->decode(cEncoded, eSize, plainText);

	string decodedPlaintext((char*)(plainText),newSize);

	delete [] plainText; 

    BOOST_CHECK_EQUAL(plaintext.compare(decodedPlaintext), 0);
}

BOOST_AUTO_UNIT_TEST(test_encode_pre_encoded)
{
	/*IV is the first 16 bytes. 2 for length. the remaining are the data*/
	const int eSize = 66;
	unsigned char cEncoded[eSize] = {1,2,3,4,5,6,7,8,9,9,9,9,9,9,9,9,
										 0,35,
										  51,112,170,0,216,
										  103,107,85,85,132,
										  36,142,132,128,36,
										  43,26,215,194,189,
										  16,195,44,150,110,
										  230,71,108,39,50,
										  212,227,231,35,234,
										  180,243,180,128,242,
										  113,152,160,155,47,
										  252,163,145};
	unsigned char key[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	string sKey((char*)key,16);

	string plaintext = "Some things are better left unread.";

	LantronixEncryptionImpl* filter = new LantronixEncryptionImpl();
	filter->setKey(sKey);
	filter->setIV(cEncoded);

	unsigned char* cText = NULL;

	int cTextSize = filter->encode((unsigned char*)plaintext.c_str(),plaintext.size(),cText);

	BOOST_CHECK_EQUAL(eSize, cTextSize);

	for (int i = 0; i < cTextSize; i++)
	{
		BOOST_CHECK_EQUAL(cText[i], cEncoded[i]);
	}

	delete [] cText;
}


