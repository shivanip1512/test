
// -------------------------------------------
//Makes a XML request
//
// -------------------------------------------
//pool of XMLHttp request objects, increaes this size if many request are happening
// Contains objects with the following attributes:
//  int id; boolean free; XMLHttpRequest req;
var xmlHttp_Pool = new Array(5);
var xmlHttp_msgId = 0;
var xmlHttp_isIE = false;

// -------------------------------------------
//Ensure our call to new XMLHttpRequest() returns the
// browser specific XMLHttpRequest object
// -------------------------------------------
if( typeof XMLHttpRequest != "object" )
{
	function XMLHttpRequest()
	{ 
		xmlHttp_isIE = true;
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
}

// -------------------------------------------
//Performs a GET request to the given URL and
// calls the onreadystatechange() method upon return
// -------------------------------------------
function loadXMLDoc(url, stateFunc)
{
	//used for synchronization to allow for many XMLHTTP calls
	// to be used and for each call to use it's correct callback method
	var pooledReq = getFreeXmlHttpObj();
	if( pooledReq == null )
		return;

	if( stateFunc == null ) {
		stateFunc = 'processReqChangeXML';
	}

    pooledReq.req = new XMLHttpRequest();
	pooledReq.req.onreadystatechange = eval(stateFunc);
    pooledReq.req.open("GET", url, true);
    pooledReq.req.send(null);

    return pooledReq.id;
}

// -------------------------------------------
//Gets the first free XMLHttp request object from the pool
// and marks it USED
// -------------------------------------------
function getFreeXmlHttpObj()
{
	for( var i = 0; i < xmlHttp_Pool.length; i++ )
	{
		//alert('i='+i+', '+xmlHttp_Pool[i]);
		if( xmlHttp_Pool[i] == null )
		{
			r = new Object();
			r.id = i;
			r.free = false;
			r.req = null;
			xmlHttp_Pool[i] = r;
			return r;
		}
		else if( xmlHttp_Pool[i].free )
		{
			xmlHttp_Pool[i].free = false;
			return xmlHttp_Pool[i];
		}

	}
	
	alert('Unable to find a free XMLHTTP object');
	return null;
}

// -------------------------------------------
//Gives back the XMLHttp object with the given reqID
// to the pool, thus marking it free
// -------------------------------------------
function freeReq( reqID )
{
	xmlHttp_Pool[reqID].free = true;
}

// -------------------------------------------
//Gets the XMLHttp object from the pool with the
// given reqID
// -------------------------------------------
function getReq( reqID )
{
	if( xmlHttp_Pool[reqID] == null || xmlHttp_Pool[reqID].free )
	{
		//alert('The XMLHttp object with an id = ' +
		//	reqID + ' has not been initialized');
	}
	else
		return xmlHttp_Pool[reqID];

}

// -------------------------------------------
//Callback function for the xml HTTP request
// to return XML
// -------------------------------------------
function processReqChangeXML()
{
    var xmlHTTPreq = getReq(xmlHttp_msgId);

    // only if req shows "complete"
    if( xmlHTTPreq != null
    	&& getReq(xmlHttp_msgId).req != null
    	&& getReq(xmlHttp_msgId).req.readyState == 4 )
    {    
    	var req = getReq(xmlHttp_msgId).req;

        // only if "OK"
        if( req.status == 200) 
        {
			// ...processing statements go here...
      		response = req.responseXML.documentElement;
      		method =
      			response.getElementsByTagName('method')[0].firstChild.data;

	      	result =
	      		response.getElementsByTagName('result');

			eval(method + '(result)');

			//always do this
			freeReq( xmlHttp_msgId );
        }
        else
        {
            //alert("There was a problem retrieving the XML data:\n" + req.statusText);
        }
    }

}

// -------------------------------------------
//retrieve text of an XML document element, including
// elements using namespaces
// -------------------------------------------
function getElementTextNS(parentElem, index, local, prefix)
{
    var result = "";
    if (prefix && xmlHttp_isIE) {
        // IE/Windows way of handling namespaces
        result = parentElem.getElementsByTagName(prefix + ":" + local)[index];
    } else {
        // the namespace versions of this method 
        // (getElementsByTagNameNS()) operate
        // differently in Safari and Mozilla, but both
        // return value with just local name, provided 
        // there aren't conflicts with non-namespace element
        // names
        result = parentElem.getElementsByTagName(local)[index];
    }

    if (result) {
        // get text, accounting for possible
        // whitespace (carriage return) text nodes 
        if (result.childNodes.length > 1) {
            return result.childNodes[1].nodeValue;
        } else {
            return result.firstChild.nodeValue;    		
        }
    } else {
        return "n/a";
    }
}

// -------------------------------------------
//retrieve text of an XML document attribute, including
// attributes using namespaces
// example:  getAttributeTextNS(result[i], 0, 'state', 'color');
// -------------------------------------------
function getAttributeTextNS(parentElem, index, local, attrName, prefix)
{
    var result = "";
    if (prefix && xmlHttp_isIE) {
        // IE/Windows way of handling namespaces
        result = parentElem.getElementsByTagName(prefix + ":" + local)[index];
    } else {
        // the namespace versions of this method 
        // (getElementsByTagNameNS()) operate
        // differently in Safari and Mozilla, but both
        // return value with just local name, provided 
        // there aren't conflicts with non-namespace element
        // names
        result = parentElem.getElementsByTagName(local)[index];
    }

    if (result) {
    	return result.getAttribute(attrName);
    } else {
        return "n/a";
    }
}
