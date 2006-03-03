/*
 * Created on Jul 15, 2003
 */
package com.cannontech.esub.util;

/**
 * JS can be used to accumulate Javascript that will go on to an esub display.
 * Used during svg generation.
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
	"}\n" +
	"function _onload(evt) {\n" +
	"   CGUI.init();" +
	"   buildGUI();" +
	"   refresh(evt);" +
	"}\n";
	
	private static final String CGUIHeader = 	
	"function buildGUI() {\n";
	private static final String CGUIFooter = 
	"}\n";

	// Increment this each time a button is created to give it a unique variable name
	private int buttonNum;
	
	// The javascript for CGUI widgets accumulates here
	private StringBuffer guiBuf;

	public JS() {
		reset();
	}
	
	public String getScript() {
		StringBuffer scriptBuf = new StringBuffer(JS.length()+CGUIHeader.length()+guiBuf.length()+CGUIFooter.length());
		scriptBuf.append(JS).append(CGUIHeader).append(guiBuf).append(CGUIFooter);
		return scriptBuf.toString();
	}
	
	public void createButton(int x, int y, 
							int width, int height, 
							boolean toggleType, boolean disabled, boolean selected,
							String parentElement, String text, String action) {
		String buttonNumStr = Integer.toString(buttonNum);
		String buttonVar = "button" + buttonNumStr;
		String buttonHandler = "buttonHandler" + buttonNumStr;
				
		// Create a click handler
		guiBuf	.append("var ")
				.append(buttonHandler)
				.append(" = new Object();\n")
				.append(buttonHandler)
				.append(".handleEvent = function(evt) { ")
				.append(action)
				.append(" }\n");
		
		// Create the button
		guiBuf	.append("var ")
				.append(buttonVar)
				.append(" = new Button(")
				.append(Integer.toString(width))
				.append(',')
				.append(Integer.toString(height))
				.append(",'")
				.append(text)
				.append("',")
				.append(buttonHandler)
				.append(");\n");
		
		// Set its location
		guiBuf	.append(buttonVar)
				.append(".setAnchor(")
				.append(parentElement)
				.append(",'left',")
				.append(Integer.toString(x))
				.append(",'top',")
				.append(Integer.toString(y))
				.append(");\n");
		
		if(toggleType) {
			guiBuf	.append(buttonVar)
					.append(".enableToggle(\"true\");\n");
		}
		
		if(disabled) {
			guiBuf	.append(buttonVar)
					.append(".disable()\n");
		}
		
		if(selected) {
			guiBuf	.append(buttonVar)
					.append(".select()\n");
		}
		// Add it to its parent
		guiBuf	.append(buttonVar)
				.append(".addToParent(")
				.append(parentElement)
				.append(");\n");
		
		buttonNum++;
	}
	
	public void reset() {
		buttonNum = 1;
		guiBuf = new StringBuffer();
	}
}
