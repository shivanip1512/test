/*
 * Created on Jul 15, 2003
 */
package com.cannontech.esub.util;

/**
 * A bunch of javascript functions are stuff in here.
 * @author aaron
  */
class JS {
	static final String JS =
	"var selectedRect = null;\n" +
	"function underLine(elem) {\n" +
	"	//Using non-standard Adobe SVG extension\n" +
	"	var elemStyle = elem.getStyle();\n" +
	"	elemStyle.setProperty('text-decoration','underline');\n" +
	"} //end underLine\n" +
	"function noUnderLine(elem) {\n" +
	"	//Using non-standard Adobe SVG extension\n" +	
	"	var elemStyle = elem.getStyle();\n" +
	"	elemStyle.setProperty('text-decoration','none');\n" +
	"} //end noUnderLine\n" +
	"function addBorder(elem) {\n" +
	"	var x = parseInt(elem.getAttribute('x'));\n" +
	"	var y = parseInt(elem.getAttribute('y'));\n" +
	"	var w = parseInt(elem.getAttribute('width'));\n" +
	"	var h = parseInt(elem.getAttribute('height'));\n" +
	" elem.getOwnerDocument().createElement('rect');\n" +
	"	selectedRect = elem.getOwnerDocument().createElement('rect');\n" +
	"	selectedRect.setAttribute('x', x-2);\n" +
	"	selectedRect.setAttribute('y', y-2);\n" +
	"	selectedRect.setAttribute('width', w+4);\n" +
	"	selectedRect.setAttribute('height', h+4);\n" +
	
	"	var rStyle = selectedRect.getStyle();\n" +
	"	rStyle.setProperty('fill','none');\n" +
	"	rStyle.setProperty('stroke','#CCCCCC');\n" +
	"	elem.getOwnerDocument().documentElement.appendChild(selectedRect);\n" +
	"} //end addBorder\n" +
	"function noBorder(elem) {\n" +
	"   if(selectedRect == null)\n" +
	"       return;\n" +
	"	var b = findChild(selectedRect);\n" +
	"	if(b != null) {\n" +
	"		elem.getOwnerDocument().documentElement.removeChild(b);\n" +
	"	}	\n" +
	"} //end noBorder\n" +
	"function findChild(node) {\n" +
	"	var kids = node.getOwnerDocument().getDocumentElement().getChildNodes();\n" +
	"	for(var i = 0; i < kids.getLength(); i++) {\n" +
	"		if(node == kids.item(i)) {\n" +
	"			return kids.item(i);\n" +
	"		}\n" +
	"	}\n" +
	"	return null;\n" +
	"}\n" +
	"function followLink(url) {\n" +	
	"	location = url;\n" + 
	"}\n";

}
