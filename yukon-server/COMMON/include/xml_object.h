#ifndef __XML_OBJECT_H__
#define __XML_OBJECT_H__

#include <string>
#include <list>
#include <utility>
#include <ostream>
#include <boost/shared_ptr.hpp>

#include "dlldefs.h"

using std::string;
using std::list;
using std::ostream;
using std::pair;

class XmlObject;

/**
 * This is simple class to build a XML formatted structure and
 * drop it into an ostream 
 * 
 */
class IM_EX_CTIBASE XmlObject
{
	public:

		/* Typedef's */
		typedef boost::shared_ptr<XmlObject> XmlObjectSPtr;
		typedef pair<string,string> Parameter;
		typedef list < Parameter > ParamList;
		typedef list < XmlObjectSPtr > NodeList;

		/**
		 * Constructor. Tag Name is defaulted to xmlTag with no
		 * parameters and no child nodes.
		 */
		XmlObject();

		/**
		 * Name is what the xml tag name will be called. 
		 * Example <xmltag NAME="IAMATAG"> 
		 * Where name is 'xmltag' 
		 * 
		 * @param name 
		 */
		void setTagName(const string& name);
		const string& getTagName();

		/**
		 * Insert a parameter argument to be added to the xml tag.
		 * Example: <xmltag NAME="IAMATAG"> 
		 * Where key is 'NAME' and value is 'IAMATAG' 
		 * 
		 * @param key 
		 * @param value 
		 */
		void insertParameter(const string& key, const string& value);

		/**
		 * Insert a node. This will be another XmlObject that has been 
		 * completed. It will be stored to be used with the << operator
		 * 
		 * @param node 
		 */
		void insertXmlNode(XmlObjectSPtr node);

		/**
 		 * Output to a stream. Use string stream for unit tests. 
		 * Intended to be used with a file stream. This function will 
		 * call into each child node passing in ostream. 
		 * 
		 * @param os 
		 * 
		 * @return ostream& 
		 */
		ostream& outputXml( ostream& os );

	private:
		string tagName;
		ParamList tagParams;
		NodeList nodes;
};

#endif