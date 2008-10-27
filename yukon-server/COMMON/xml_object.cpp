#include "yukon.h"

#include "xml_object.h"

XmlObject::XmlObject()
{
	tagName = "xmlTag";
}

void XmlObject::setTagName (const string& name)
{
	tagName = name;
}

const string& XmlObject::getTagName()
{
	return tagName;
}

void XmlObject::insertParameter(const string& key, const string& value)
{
	Parameter param(key,value);
	tagParams.push_back(param);
}

void XmlObject::insertXmlNode(XmlObjectSPtr node)
{
	nodes.push_back(node);
}

ostream& XmlObject::outputXml( ostream& os )
{
	//Opening Tag
	os << "<" << tagName;
	for (ParamList::iterator p_itr = tagParams.begin(); p_itr != tagParams.end(); p_itr++)
	{
		os << " " << p_itr->first << "=\"" << p_itr->second << "\"";
	}
	os << ">\n";

	// Child Nodes:

	for (NodeList::iterator n_itr = nodes.begin(); n_itr != nodes.end(); n_itr++)
	{
		(*n_itr)->outputXml(os);
	}

	//Close this tag
	os << "</" << tagName << ">\n";

	return os;
}
