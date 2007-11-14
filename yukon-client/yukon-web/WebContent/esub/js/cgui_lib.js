/* 
    Copyright 2002 Christopher T Lewis 

    This file is part of the CGUI Library 

    CGUI is free software; you can redistribute it and/or 
    modify it under the terms of the GNU Lesser General Public 
    License as published by the Free Software Foundation; either 
    version 2.1 of the License, or (at your option) any later version.  

    CGUI is distributed in the hope that it will be useful, 
    but WITHOUT ANY WARRANTY; without even the implied warranty of 
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
    Lesser General Public License for more details.  

    You should have received a copy of the GNU Lesser General Public 
    License along with this library; if not, write to the Free Software 
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 

    Contact information: 

      Christopher T Lewis 
      Saskatoon Research Centre 
      107 Science Place 
      Saskatoon SK Canada S7N 0X2 
      LewisCT@agr.gc.ca 

      Nishan V. Sothilingam
      Saskatoon Research Center
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      SothilingamN@agr.gc.ca
*/ 
/**
@ --------------------------------------------------------------
@ Object: EventHandler
@ Desc:	  Creates a generic EventHandler class for all the GUI
@ elements that exist.  Various event listeners are also created
@ depending on the needs of the element
@ Example:
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: EventHandler
@ --------------------------------------------------------------
*/
function EventHandler() {
    //does nothing just creates an instance of EventHandler for use
}

/** 
@ -------------------------------------------------------------- 
@ Method: handleEvent 
@ Description: calls appropriate event 
@ Parameters: evt - incoming event 
@ Note: if ( e.type in this ) this[e.type](e);  
@                only works in IE6.0 and maybe IE5.5 
@ -------------------------------------------------------------- 
*/ 

EventHandler.prototype.handleEvent=function(evt) { 
    //if ( e.type in this ) this[e.type](e); 
    
    var type = evt.type; 

    if ( this[type] != null ) { 
        this[type](evt); 
    } else { 
       // alert("Undefined event handler for " + type); 
    } 
}; 



/**
@ --------------------------------------------------------------
@ Method: addEventListeners
@ Description: adds standard event listeners for all GUI Elements.  
@ Parameters:  obj, the SVG Element that the event listener is added to
@ --------------------------------------------------------------
*/
EventHandler.prototype.addEventListeners = function(obj) { 
         obj.addEventListener ("mousedown", this, false); 
         obj.addEventListener ("mouseup", this, false); 
         obj.addEventListener ("mouseout", this, false); 
         obj.addEventListener ("mousemove", this, false); 
	 obj.addEventListener ("mouseover", this, false);
         obj.addEventListener ("mousein", this, false);
}
/* 
    Copyright 2002 Christopher T Lewis 

    This file is part of the CGUI Library 

    CGUI is free software; you can redistribute it and/or 
    modify it under the terms of the GNU Lesser General Public 
    License as published by the Free Software Foundation; either 
    version 2.1 of the License, or (at your option) any later version.  

    CGUI is distributed in the hope that it will be useful, 
    but WITHOUT ANY WARRANTY; without even the implied warranty of 
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
    Lesser General Public License for more details.  

    You should have received a copy of the GNU Lesser General Public 
    License along with this library; if not, write to the Free Software 
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 

    Contact information: 

      Christopher T Lewis 
      Saskatoon Research Centre 
      107 Science Place 
      Saskatoon SK Canada S7N 0X2 
      LewisCT@agr.gc.ca 
*/ 


/**
@ -------------------------------------------------------------- 
@ Object: CGUI 
@ Desc: 
@ The 'root' level GUI element in the GUI project.  
@ Anything which displays in SVG inherits from here.  
@ Example: 
@ Inherits from: EventHandler
@ Notes: 
@ -------------------------------------------------------------- 
*/ 

/** 
@ -------------------------------------------------------------- 
@ Constructor: CGUI 
@ Parameter: x - x coordinate of this GUI component relative to parent 
@            y - y coordinate of this GUI component relative to parent 
@        width - width of this GUI component 
@        heigh - height of this GUI component 
@ -------------------------------------------------------------- 
*/ 
function CGUI (x, y, width, height) { 
  if (arguments.length > 0) { 
    CGUI.debug ("Creating CGUI"); 
    this.width = width; 
    this.height = height; 
    this.x = x; 
    this.y = y; 

    this.svg_reference = document.createElement ("svg"); 
    if (CGUI.debug_on) 
      this.svg_reference.setAttribute ("CGUI_TYPE", this.CGUI_TYPE); 
    this.svg_reference.setAttribute ("width", width); 
    this.svg_reference.setAttribute ("height", height); 
    this.svg_reference.setAttribute ("x", x); 
    this.svg_reference.setAttribute ("y", y); 

    this.anchor = new Anchor(this); 
    this.svg_reference.jsref = this; 
    return this; 
  } 
  return null; 
}
CGUI.prototype=new EventHandler;
CGUI.prototype.CGUI_TYPE="CGUI"; 

CGUI.init = function (skinfilename, maininitfunction, maxtimeoutcount) { 
    var svgdoc = svgDocument; 
    var root = svgDocument.getDocumentElement(); 
    
    root.addEventListener("mousemove", new MouseTracker("mousemove"),false); 
    root.addEventListener("mouseup", new MouseTracker("mouseup"), false); 
    this.root = root; 
    this.svgdoc = svgdoc; 

    CGUI.createDefs();
		
		if(skinfilename) {
      CGUI.MaxLoadTimeoutCount = maxtimeoutcount ? maxtimeoutcount : 30;
      CGUI.MainInitFunction = maininitfunction;
      CGUI.loadSkin(skinfilename);
      CGUI.skintimeout = setTimeout("CGUI.SkinLoadCheck()",100);
      } 

/* temporary.  Being used to test translation of mouse-coordinates. */
  CGUI.ym = new TextBar ("YM: ", 120, 15, "white");
           CGUI.ym.setAnchor (CGUI.root, "left", 120, "bottom", 0);
           CGUI.ym.addToParent (CGUI.root);
  CGUI.xm = new TextBar ("XM: ", 120, 15, "white");
           CGUI.xm.setAnchor (CGUI.root, "left", 0, "bottom", 0);
           CGUI.xm.addToParent (CGUI.root);

  CGUI.yma = new TextBar ("YMA: ", 120, 15, "white");
           CGUI.yma.setAnchor (CGUI.root, "left", 120, "bottom", -15);
           CGUI.yma.addToParent (CGUI.root);
  CGUI.xma = new TextBar ("XMA: ", 120, 15, "white");
           CGUI.xma.setAnchor (CGUI.root, "left", 0, "bottom", -15);
           CGUI.xma.addToParent (CGUI.root);
/* end of temporary addition. */

}

CGUI.SkinLoadCheck = function () {
  CGUI.MaxLoadTimeoutCount-=1;
  if(CGUI.skinloaded || (CGUI.MaxLoadTimeoutCount <=0)) {
     if( skintimeout ) clearTimeout( skintimeout );
     CGUI.MainInitFunction();
     }
  else return;
  }
 

CGUI.createDefs = function () { 
    var defs = svgDocument.getElementsByTagName("defs"); 
    var frag; 
    var def; 

    if ( defs.length > 0 ) { 
        def = defs.item(0); 
    } else { 
        // Don't really need to do this 
        def = svgDocument.createElement("defs"); 
        svgDocument.documentElement.appendChild(def); 
    } 

    def.appendChild( parseXML( 
'       <linearGradient id="gradientBorder" ' + 
'           x1="0%" y1="0%" x2="100%" y2="100%"\n' + 
'           spreadMethod="pad" gradientUnits="objectBoundingBox">\n' + 
'           <stop offset="0%" stop-color="rgb(200,200,200)"/>\n' + 
'           <stop offset="100%" stop-color="rgb(150,150,150)"/>\n' + 
'       </linearGradient>', svgDocument 
    )); 

    def.appendChild( parseXML( 
'       <linearGradient id="gradientAltBorder" ' + 
'           x1="0%" y1="0%" x2="100%" y2="100%"\n' + 
'           spreadMethod="pad" gradientUnits="objectBoundingBox">\n' + 
'           <stop offset="0%" stop-color="rgb(150,150,150)"/>\n' + 
'           <stop offset="100%" stop-color="rgb(75,75,75)"/>\n' + 
'       </linearGradient>', svgDocument 
    )); 

    def.appendChild( parseXML( 
'      <linearGradient id="gradientFace" ' + 
'           x1="100%" y1="100%" x2="0%" y2="0%"\n' + 
'           spreadMethod="pad" gradientUnits="objectBoundingBox">\n' + 
'           <stop offset="0%" stop-color="rgb(108,129,204)"/>\n' + 
'           <stop offset="100%" stop-color="rgb(50,50,50)"/>\n' + 
'       </linearGradient>', svgDocument 
    )); 

    def.appendChild( parseXML( 
'      <linearGradient id="gradientAltFace" ' + 
'           x1="100%" y1="100%" x2="0%" y2="0%"\n' + 
'           spreadMethod="pad" gradientUnits="objectBoundingBox">\n' + 
'           <stop offset="0%" stop-color="rgb(108,129,204)"/>\n' + 
'           <stop offset="100%" stop-color="rgb(80,80,224)"/>\n' + 
'       </linearGradient>', svgDocument 
    )); 

    def.appendChild( parseXML( 
'    <filter id="DropShadow">'+ 
'       <feGaussianBlur in="SourceAlpha" stdDeviation="1" result="MyBlur"/>'+ 
'       <feOffset in="MyBlur" dx="3" dy="3" result="FinalBlur"/>'+ 
'       <feMerge>'+ 
'           <feMergeNode in="FinalBlur"/>'+ 
'           <feMergeNode in="SourceGraphic"/>'+ 
'       </feMerge>'+ 
'    </filter>', svgDocument 
    ));
 
   def.appendChild( parseXML( 
'      <linearGradient id="gradientAltFaceDep" ' + 
'           x1="100%" y1="100%" x2="0%" y2="0%"\n' + 
'           spreadMethod="pad" gradientUnits="objectBoundingBox">\n' + 
'           <stop offset="0%" stop-color="rgb(120,079,294)"/>\n' + 
'           <stop offset="100%" stop-color="rgb(080,180,204)"/>\n' + 
'       </linearGradient>', svgDocument 
    )); 

def.appendChild( parseXML( 
'       <linearGradient id="gradientAltBorderDep" ' + 
'           x1="0%" y1="0%" x2="100%" y2="100%"\n' + 
'           spreadMethod="pad" gradientUnits="objectBoundingBox">\n' + 
'           <stop offset="0%" stop-color="rgb(150,150,150)"/>\n' + 
'           <stop offset="100%" stop-color="rgb(75,75,75)"/>\n' + 
'       </linearGradient>', svgDocument 
    )); 

} 

/** 
@ -------------------------------------------------------------- 
@ Method: debug 
@ Description: add text to debug buffer 
@ Parameter: text - the text to add 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 
CGUI.debug = function (text) { 
} 

/** 
@ ------------------------------------------------------------- 
@ Method: debug_enable 
@ Description: enable CGUI debug logging 
@ 
@ Example: 
@ ------------------------------------------------------------- 
*/ 
CGUI.debug_enable = function () { 
  CGUI.error_log = ""; 
  CGUI.debug_on = true; 
  CGUI.debug = function (text) { 
    CGUI.error_log += text + "\n"; 
    if (CGUI.error_log.length > 4000) CGUI.dump_log(); 
  } 
  CGUI.debug("Debug log:"); 
} 

CGUI.debug_disable = function () { 
  CGUI.dump_log(); 
  CGUI.error_log = ""; 
  CGUI.debug_on = false; 
  CGUI.debug = function (text) {} 
} 

/** 
@ ------------------------------------------------------------- 
@ Method: print_log 
@ Description: pring the debug log 
@ 
@ Example: 
@ Notes: Requires a perl CGI called logdebug.pl; do whatever 
@        you want in this script - I simply add the message 
@        to a log file.  
@ ------------------------------------------------------------- 
*/ 
CGUI.dump_log = function () { 
  if (CGUI.debug_on) { 
    window.postURL (postURLPath + "logdebug.pl", "text="+CGUI.error_log, null); 
    CGUI.error_log = ""; 
  } 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: setAnchor 
@ Description: Set this anchor.  
@ Parameter: target - the object to anchor to.  
@ Parameter: ax - "left" "center" "right", the horizontal anchor position 
@ Parameter: offsetx - the offset ('+':right '-':left) from the ax position 
@ Parameter: ay - "top" "center" "bottom", the vertical anchor position 
@ Parameter: offsety - the offset ('+':down '-':up) from the ay position 
@ 
@ Example: 
@ Continuing the setAnchor (frame, "left", "15", "bottom", "-15") 
@ would anchor the object(the button) 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.setAnchor = function (target, ax, offsetx, ay, offsety) { 
  if (this.anchor) { 
    this.anchor.setAnchor (target, ax, offsetx, ay, offsety); 
  } else { 
    var alert_txt = "Unable to anchor this object." + 
                    "  It seems it has no anchor property."; 
    alert (alert_txt); 
  } 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: addToParent 
@ Description: append this CGUI element to it's parent 
@ Parameter: parent - element to append to 
@                     may or may not be a CGUI element 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.addToParent = function (parent) { 
  if (parent.CGUI_TYPE) { 
    this.svg_reference = parent.svg_reference.appendChild (this.svg_reference); 
    this.parent = parent; 
  } else { 
    this.svg_reference = parent.appendChild (this.svg_reference); 
    this.parent = parent; 
  } 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: removeFromParent 
@ Description: remove this CGUI element from it's parent 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.removeFromParent = function () { 
  if (this.parent == CGUI_TYPE) { 
    this.parent.svg_reference.removeChild(this.svg_reference); 
  } else { 
    this.parent.removeChild (this.svg_reference); 
  } 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: toString 
@ Description: the default CGUI toString method 
@              you'll probably want to override this for any new 
@              CGUI elements 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.toString = function () { 
  return this.CGUI_TYPE; 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: setX 
@ Description: Set this CGUI Element's X attribute 
@ Parameter: x - the x coordinate 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.setX = function (x) { 
  this.x = x; 
  this.svg_reference.setAttribute ("x", x); 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: setY 
@ Description: Set this CGUI Elements y coordinate 
@ Parameter: y - the y coordinate for this CGUI element 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.setY = function (y) { 
  this.y = y; 
  this.svg_reference.setAttribute ("y", y); 
} 

/**
@ --------------------------------------------------------------
@ Method: setBkColor
@ Description: sets background color of content, if there is one
@ Parameter: iColor - any valid svg color format
@ Added:  Chris Peto, Moved: Nishan Sothilingam
@ --------------------------------------------------------------
*/
CGUI.prototype.setBkColor = function (iColor)
{
   this.svg_reference.getFirstChild().setAttribute("fill",iColor);
}

/**
@ --------------------------------------------------------------
@ Method: setBkOpacity
@ Description: sets background opacity of content, if there is one
@ Parameter: opac - a number range from 0.0 (tranparent) to 1(opaque)
@ Added:  Nishan Sothilingam
@ --------------------------------------------------------------
*/
CGUI.prototype.setBkOpacity = function (opac)
{
   this.svg_reference.getFirstChild().setAttribute("opacity",opac);
}




/** 
@ -------------------------------------------------------------- 
@ Method: setWidth 
@ Description: Set this CGUI elements width 
@ Parameter: width - the width for this element 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.setWidth = function (width) { 
  this.width = width; 
  this.svg_reference.setAttribute ("width", width); 
} 


/** 
@ -------------------------------------------------------------- 
@ Method: setHeight 
@ Description: Set this CGUIElement's height 
@ Parameter: height - the height for this element 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.setHeight = function (height) { 
  this.height = height; 
  this.svg_reference.setAttribute ("height", height); 
} 


/** 
@ -------------------------------------------------------------- 
@ Method: getX 
@ Description: get the x coordinate for this element 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.getX = function () { 
  return this.x; 
} 


/** 
@ -------------------------------------------------------------- 
@ Method: getY 
@ Description: Get the Y coordinate for this element 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.getY = function () { 
  return this.y; 
} 


/** 
@ -------------------------------------------------------------- 
@ Method: getWidth 
@ Description: get the width of this element 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.getWidth = function () { 
  return this.width; 
} 


/** 
@ -------------------------------------------------------------- 
@ Method: getHeight 
@ Description: get the height of this element 
@ 
@ Example: 
@ -------------------------------------------------------------- 
*/ 

CGUI.prototype.getHeight = function () { 
  return this.height; 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: getUserCoordinate 
@ Description: gets x and y in user coordinates, i.e. when Zoomin in.  
@ Parameters: node - node for transformation 
@             x    - x position from mouse 
@             y    - y position from mouse 
@ Returns:    CTM 
@ -------------------------------------------------------------- 
*/ 
CGUI.prototype.getUserCoordinate = function(node, x, y) { 
    var svgRoot    = svgDocument.documentElement; 
    var pan        = svgRoot.getCurrentTranslate(); 
    var zoom       = svgRoot.getCurrentScale(); 
    var CTM        = this.getTransformToElement(node); 
    var iCTM       = CTM.inverse(); 
    var worldPoint = svgDocument.documentElement.createSVGPoint(); 

    worldPoint.x = (x - pan.x) / zoom; 
    worldPoint.y = (y - pan.y) / zoom; 

    var pt = worldPoint.matrixTransform(iCTM); 

    return pt; 
}; 

/** 
@ -------------------------------------------------------------- 
@ Method: getScreenCoordinate 
@ Description: gets x and y in root coordinates, i.e. Tooltips.  
@ Parameters: node - node for transformation 
@             x    - x position for svg node x 
@             y    - y position for svg node y 
@ Returns:    CTM 
@ -------------------------------------------------------------- 
*/ 
CGUI.prototype.getScreenCoordinate = function(node, x, y) { 
    var svgRoot    = svgDocument.documentElement; 
    var pan        = svgRoot.getCurrentTranslate();
    var zoom       = svgRoot.getCurrentScale(); 
    var CTM        = this.getTransformToElement(node);
    var worldPoint = svgDocument.documentElement.createSVGPoint(); 
    worldPoint.x = x;
    worldPoint.y = y; 
    return worldPoint.matrixTransform(CTM); 
}; 


/** 
@ -------------------------------------------------------------- 
@ Method: getTransformToElement 
@ Description: gets CTM for node 
@ Parameters: node - node for transformation 
@ Returns:    CTM 
@ -------------------------------------------------------------- 
*/ 
CGUI.prototype.getTransformToElement = function(node) { 
    var CTM = node.getCTM(); 
    while ( (node = node.parentNode) != svgDocument ) {
        CTM = node.getCTM().multiply(CTM);
    } 

    return CTM; 
}; 

/** 
@ -------------------------------------------------------------- 
@ Method: getOffsetToElement 
@ Description: gets x and y offsets to node, i.e Frame x any y 
@ Parameters: node - node to get offsets 
@ Returns:    root based point up to node but not including 
@ -------------------------------------------------------------- 
*/ 
CGUI.prototype.getOffsetToElement = function(node, stopnode) { 
    var x = 0; 
    var y = 0;

    if(!stopnode) stopnode = svgDocument.documentElement; 
    while ( (node = node.parentNode) != stopnode ) {
        if(node.getTagName() == "svg") { 
            x += Number(node.getAttribute("x"));
            y += Number(node.getAttribute("y")); 
        }        
    } 

    var worldPoint = svgDocument.documentElement.createSVGPoint(); 
    worldPoint.x = x;
    worldPoint.y  = y; 
    return worldPoint; 
}; 

/** 
@ -------------------------------------------------------------- 
@ Method: getClientToScreen 
@ Description: Builds a root relative x and y coordinate for a svg node 
@              and handles transform and scaling
@ Parameters: node - svg node to get root offset
@             x    - x coordinate of svg node 
@             y    - y coordinate of svg node
@ Returns:    point 
@ -------------------------------------------------------------- 
*/ 
CGUI.prototype.getClientToScreen = function(node,x,y) { 
    //get offsets of all svgs 
    var pt = this.getOffsetToElement(node);
    x += pt.x; 
    y += pt.y;

    //get translation of point 
    var rootbasedPoint = this.getScreenCoordinate (node, x, y);
    return rootbasedPoint;
};

/**
@ --------------------------------------------------------------
@ Method: getPointToScreen
@ Description: Builds a parent svg relative x and y coordinate for a svg node
@              and handles transform and scaling 
@ Parameters: node - svg node to get parent svg offset 
@             x    - x coordinate of screen
@             y    - y coordinate of screen 
@ Returns:    point
@ --------------------------------------------------------------
*/
CGUI.prototype.getScreenToClient = function(node,x,y) {
        //get offsets of all svgs
        var pt = this.getOffsetToElement(node); 
        x-=pt.x;
        y-=pt.y; 
        var parentPoint = this.getUserCoordinate (node, x, y); 
        return parentPoint;
};

//skins

/**

@ --------------------------------------------------------------
@ Method: loadUI
@ Description: Async svg loader callback, then after parseXML
@              appends it to a hidden <g>.
@              This way the Designer
@              can visually design the skin. To do: check for success
@ Parameters: asyncStatus - loading status
@ --------------------------------------------------------------

*/

function loadUI (asyncStatus) {
	var skinRoot = window.parseXML(asyncStatus.content, svgDocument);
	
	var g   = svgDocument.createElement("g");
  g.setAttribute("visibility", "hidden");
	g.appendChild(skinRoot);
	CGUI.root.appendChild(g);
	CGUI.skinloaded = true;
	
	//alert(svgDocument.getElementById("tooltiprect"));
	}
	
/**

@ --------------------------------------------------------------
@ Method: loadSkin
@ Description: Loads an svg with getURL. 
@ Parameters: filename - svg file to load
@ --------------------------------------------------------------

*/

CGUI.loadSkin = function(filename) {
		
    //get file
		CGUI.skinloaded = false;
		getCtiURL(filename,loadUI);
		

};

/**

@ --------------------------------------------------------------
@ Method: copyElement
@ Description: Clones a node 
@ Parameters: id - id of node to clone
@ returns:  cloned node when found or null when not found
@ --------------------------------------------------------------

*/

CGUI.copyElement = function(id) {
		
    //get object
		var svgobj = svgDocument.getElementById(id);
		//alert(svgobj);
		if(svgobj) return svgobj.cloneNode(false);
		else       return null;
};


/** 
@ -------------------------------------------------------------- 
@ Method: getJSRef
@ Description: Climb the DOM and find the nearest ancestor
@              with a "jsref" property, then return the JS ref.
@ Parameter: target - the initial SVG node
@ Example:  Assume you have a mouseover event handler set on 
@           the svg representation of a button.  The handler
@           might call the getJSRef method to proceed up the
@           DOM until it finds the "g" element of the button
@           which has a reference to the javascript 
@           representation of the button.
@ -------------------------------------------------------------- 
*/
CGUI.getJSRef = function (target) {
  if (target == null) return;
  if (target.CGUI_TYPE) {
      return target;
  } else {
    if (target.jsref) {
      return target.jsref;
    } else if (target.parentNode) {
      return CGUI.getJSRef (target.parentNode);
    } else {
      return;
    }
  } 
}

/** 
@ -------------------------------------------------------------- 
@ Method: getLeftEdge
@ Description: Get the coordinate of the left edge of this 
@              GUI element.
@              If the CGUI element is contained in a non-CGUI
@              element, i.e. an SVGGElement, the non-CGUI 
@              element is skipped.  Maybe I should be taking
@              the x coordinate of the non-CGUI element if 
@              it has one?
@ Parameter: 
@ Example: A ContentPane is placed at position 1 relative to
@          a frame, which has an x coordinate of 10, so the 
@          left edge of the content pane is at x coordinate 11.
@          When this frame is moved, the ContentPane is still
@          at x coordinate 1 relative to the frame, but now 
@          at 11+dx with respect to the main svg.
@ -------------------------------------------------------------- 
*/
CGUI.prototype.getLeftEdge = function () {
  var leftedge = Number(this.x);
  if (this.parent) {
    if (this.parent.CGUI_TYPE) {
      leftedge += this.parent.getLeftEdge();
    } else {
      var parent = CGUI.getJSRef (this.parent);
      if (parent) 
        leftedge += parent.getLeftEdge ();
    }
  }
  return leftedge;
}

/** 
@ -------------------------------------------------------------- 
@ Method: getTopEdge
@ Description: Get the coordinate of the top edge of this 
@              GUI element.
@              If the CGUI element is contained in a non-CGUI
@              element, i.e. an SVGGElement, the non-CGUI 
@              element is skipped.  Maybe I should be taking
@              the y coordinate of the non-CGUI element if 
@              it has one?
@ Parameter: 
@ Example: A ContentPane is placed at position 1 relative to
@          a frame, which has an y coordinate of 10, so the 
@          top edge of the content pane is at y coordinate 11.
@          When this frame is moved, the ContentPane is still
@          at y coordinate 1 relative to the frame, but now 
@          at 11+dy with respect to the main svg.
@ -------------------------------------------------------------- 
*/
CGUI.prototype.getTopEdge = function () {
  var topedge = Number(this.y);
  if (this.parent) {
    if (this.parent.CGUI_TYPE) {
      topedge += this.parent.getTopEdge();
    } 
    else {
      var parent = CGUI.getJSRef (this.parent);
      if (parent) 
        topedge += parent.getTopEdge();
    }
  }
  return topedge;
}

/** 
@ -------------------------------------------------------------- 
@ Method: relativeMouseX
@ Description: Give the mouse coordinate relative to the 
@              CGUI object.
@ Parameter: 
@ Example:  If the mouse is over a frame, the top left corner 
@           of the frame will give a coordinate of 0,0 relative
@           to itself.
@ -------------------------------------------------------------- 
*/
CGUI.prototype.relativeMouseX = function () {
  var leftedge = this.getLeftEdge();
  return MouseTracker.x - leftedge;
}

/** 
@ -------------------------------------------------------------- 
@ Method: relativeMouseY
@ Description: Give the mouse coordinate relative to the 
@              CGUI object.
@ Parameter: 
@ Example:  If the mouse is over a frame, the top left corner 
@           of the frame will give a coordinate of 0,0 relative
@           to itself.
@ -------------------------------------------------------------- 
*/
CGUI.prototype.relativeMouseY = function () {
  var topedge = this.getTopEdge();
  return MouseTracker.y - topedge;
}


/* 
    Copyright 2003 Chris Peto 

    This file is part of the CGUI Library 

    CGUI is free software; you can redistribute it and/or 
    modify it under the terms of the GNU Lesser General Public 
    License as published by the Free Software Foundation; either 
    version 2.1 of the License, or (at your option) any later version.  

    CGUI is distributed in the hope that it will be useful, 
    but WITHOUT ANY WARRANTY; without even the implied warranty of 
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
    Lesser General Public License for more details.  

    You should have received a copy of the GNU Lesser General Public 
    License along with this library; if not, write to the Free Software 
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 

    Contact information: 

      Chris Peto 
      Resource Solutions 
      Hoehenweg 34 
      63303 Dreieich, Germany 
      CPeto@resource-solutions.de 
*/ 
     

/** 
@ -------------------------------------------------------------- 
@ Object: Tooltip 
@ Desc:  Shows a round rectangle with text 
@ Example: var tt = new Tooltip(button,0,15,
@                     "I'm tooltip text for 'All Alone'"); 
@          tt.addToParent(CGUI.root);
@ --------------------------------------------------------------
*/

//Tooltip timer to remove tooltip after x seconds
Tooltip.timer;
Tooltip.timer_path;

/**
@ --------------------------------------------------------------
@ Constructor: Tooltip
@ Parameter: text - text to display in Tooltip
@ --------------------------------------------------------------
*/
function Tooltip (parent,xoffset,yoffset,text, skinrectid, skintextid) {
  if (arguments.length > 0) {
    this.base = CGUI;
    this.base(xoffset,yoffset,200,20); 

    this.CGUI_TYPE = "TOOLTIP";

    this.text = text; 
    this.xoffset = xoffset;
    this.yoffset = yoffset; 

    this.svg_reference.setAttributeNS(null, "style","overflow:visible"); 
    var btn = document.createElement ("g"); 
    btn.setAttribute("visibility","hidden"); 
    this.visible = false; 
    this.group = btn; 
    var txt = null; 
    var rect = null;
    this.textyoffset = 15;
    if(skintextid) {
      txt = CGUI.copyElement(skintextid); 
    }
    if(skinrectid) { 
      rect = CGUI.copyElement(skinrectid);
    } 
    if(!txt || !rect) { 
      txt = null; rect = null; 
    } else {
      txty = txt.getAttribute("y"); 
      recty = rect.getAttribute("y");
      this.textyoffset = Number(txty) - Number(recty); 
    }
    if(!txt) {
      txt = document.createElement ("text"); 
      txt.setAttribute ("fill", "black");
      txt.setAttribute ("font-size",14); 
    }
    if(!txt.firstChild ) 
      txt.appendChild (document.createTextNode (this.text)); 
    else 
      txt.getFirstChild().setData (this.text);

    txt.setAttribute ("class", "tooltiptext");

    txt.getStyle.setProperty ("text-anchor", "middle"); 
    txt.setAttribute ("pointer-events", "none");
    var w = txt.getComputedTextLength();
    txt.setAttribute ("y", this.textyoffset);
    txt.setAttribute ("x", (w/2) + 10); 
    this.tn = txt; 
    if(!rect) { 
      rect = document.createElement ("rect");
      rect.setAttribute ("class", "tooltipface");
      rect.setAttribute ("stroke", "black"); 
      rect.setAttribute ("stroke-width", "1pt");
      rect.setAttribute ("fill", "rgb(251,253,199)"); 
      rect.setAttribute ("height", "100%");
      rect.setAttribute ("rx", "5"); 
      rect.setAttribute ("ry", "5");
    } 
    rect.setAttribute ("width", w+20); 
    this.bg = rect; 
    btn.appendChild (this.bg);
    btn.appendChild (txt); 
    this.svg_reference.appendChild (btn); 
    this.coordparent = parent; 
    //this.addToParent(parent);
    this.addEventListeners(this.coordparent.svg_reference); 
    return this;
  }
  return null;
}
Tooltip.prototype = new CGUI;
Tooltip.superclass = CGUI.prototype;

/**
@ --------------------------------------------------------------
@ Method: mouseout
@ Description: called by mousedown event, initializes slider to be moved
@              and sets position based on mouse position
@ Parameters:  e             - mouse event
@ --------------------------------------------------------------
*/
Tooltip.prototype.mouseout = function(e) {
   if(!this.visible) 
     return; this.group.setAttribute("visibility","hidden");

   this.visible = false; 

   if (Tooltip.timer) clearTimeout (Tooltip.timer);
   Tooltip.timer_path = "";
};

/**
@ --------------------------------------------------------------
@ Method: mouseover
@ Description: called by mouseover event, handles positioning of 
               tooltips
@ Parameters:  e             - mouse event
@ --------------------------------------------------------------
*/
Tooltip.prototype.mouseover = function(e) {
   if(this.visible) 
     return; 

   var w = this.tn.getComputedTextLength(); 

   //alert(txt.getAttribute("style")+", "+txt.getFirstChild().getData()+", "+w);
   //txt.setAttribute ("y", this.textyoffset); 

   this.tn.setAttribute ("x", (w/2) + 10);
   this.bg.setAttribute ("width", w+20); 
   var objx = 0; var objy = 0;
   var node = null; 
 
   //get svg node and coordparent x any y relative to svg
   if(this.coordparent.getX) { 
     objx = this.xoffset+this.coordparent.getX();
     objy = this.yoffset+this.coordparent.getY(); 
     node = this.coordparent.svg_reference;
   } else {
      objx = this.xoffset+Number(this.coordparent.getAttribute("x")); 
      objy = this.yoffset+Number(this.coordparent.getAttribute("y"));
      node = this.coordparent; 
   }
   //since tooltip should be added to root 
   //get new x and y relative to root
   var localPoint = this.getClientToScreen(node,objx,objy); 
   this.svg_reference.setAttribute("x", localPoint.x); 
   this.svg_reference.setAttribute("y", localPoint.y);
   this.group.setAttribute("visibility","visible");
   this.visible = true; 

   Tooltip.timer_path = this;
   Tooltip.timer = setTimeout (
       "Tooltip.timer_path.mouseout()",
       2000);
};

/** 
@ -------------------------------------------------------------- 
@ Method: getText 
@ Description: get the text of this Tooltip 
@ -------------------------------------------------------------- 
*/ 
Tooltip.prototype.getText = function () { 
  return this.text; 
} 


/** 
@ -------------------------------------------------------------- 
@ Method: setText 
@ Description: Set the text for this label 
@ -------------------------------------------------------------- 
*/ 
Tooltip.prototype.setText = function (text) { 
  this.text = text; 
  //if (this.svg_reference) { 
  //  this.svg_reference.getFirstChild().getFirstChild().setData (text); 
  //} 
} 

/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/
     

/**
@ --------------------------------------------------------------
@ Object: Pane
@ Desc:
@ Example:
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Pane
@ Parameter: x - x coordinate relative to parent
@            y - y coordinate relative to parent
@        width - width of Pane
@       height - height of Pane
@ --------------------------------------------------------------
*/
function Pane (x,y,width,height) { 
  if (arguments.length > 0) { 
    CGUI.debug ("creating Pane");
    this.CGUI_TYPE = "PANE";
    this.base = CGUI;
    this.base (x,y,width,height);
    this.x = x; this.y = y;
    this.width = width; this.height = height;
    this.createPane();
  }
}
Pane.prototype = new CGUI;
Pane.superclass = CGUI.prototype;


/**
@ --------------------------------------------------------------
@ Method: createPane
@ Description: create this pane
@              - pane is simply a rectangle in the SVG document
@ --------------------------------------------------------------
*/
Pane.prototype.createPane = function () {
  var r = this.svg_reference.appendChild (document.createElement ("rect"));
  r.setAttribute ("class", "paneface");
  r.setAttribute ("width", "100%");
  r.setAttribute ("height","100%"); 
  r.setAttribute ("x", 0);
  r.setAttribute ("y", 0);
  r.setAttribute ("fill", "none");
}

Pane.prototype.hide = function (hide) {
  if (hide) {
    this.svg_reference.setAttribute ("display", "none");
  } else {
    this.svg_reference.setAttribute ("display", "inline");
  }
}

Pane.prototype.hidden = function () {
  return (this.svg_reference.getAttribute ("display") == "none") ? 1 : 0;
}
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/
     

/**
@ --------------------------------------------------------------
@ Object: Label
@ Desc:
@ Example: 
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Label
@ Parameter: x - x coordinate relative to parent
@            y - y coordinate relative to parent
@        width - width of label
@       height - height of label
@         text - text to display in label
@	iColor - this is the color of the text to be displayed (added NVS)
@ --------------------------------------------------------------
*/
function Label (x, y, width, height, text,iColor) {
  if (arguments.length > 0) {
    this.base = CGUI;
    this.base(x,y,width,height);
    this.CGUI_TYPE = "LABEL";
    this.x = x;  this.y = y;
    this.height = height;  this.width = width;
    this.text = text;
    var t = document.createElement ("text");
    t.setAttribute ("class", "label");
    t.setAttribute ("y", "12");
    t.setAttribute ("fill", iColor);
    t.appendChild (document.createTextNode (text));
    t.setAttribute ("pointer-events", "none");
    this.svg_reference.appendChild (t);
    return this;
  }
  return null;
}
Label.prototype = new CGUI;
Label.superclass = CGUI.prototype;


/**
@ --------------------------------------------------------------
@ Method: getText
@ Description: get the text of this label
@ --------------------------------------------------------------
*/
Label.prototype.getText = function () {
  return this.text;
}


/**
@ --------------------------------------------------------------
@ Method: setText
@ Description: Set the text for this label
@ --------------------------------------------------------------
*/
Label.prototype.setText = function (text) {
  this.text = text;
  if (this.svg_reference) {
    this.svg_reference.getFirstChild().getFirstChild().setData (text);
  } 
}



/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/


/**
@ --------------------------------------------------------------
@ Object: ControlBox
@ Desc:
@ Example:
@ Inherits from: Nothing
@ Notes: Each Window object has an associated ControlBox
@ Originally, the control box was, indeed a box.  Now it is a
@ collection of buttons, and it might be more appropriate to have
@ rewrite it as a ControlGroup, and inherit from a generic Group
@ class.
@ This class is highly coupled to the Window class
@ --------------------------------------------------------------
*/

ControlBox.ALL_CTRLS = 0x1FF;
ControlBox.SCROLLV_CTRLS = 0x01;
ControlBox.SCROLLH_CTRLS = 0x02;
ControlBox.SCALE_CTRLSH = 0x04;
ControlBox.SCALE_CTRLSV = 0x08;

ControlBox.XCOORD_DISP = 0x10;
ControlBox.YCOORD_DISP = 0x20;

ControlBox.CROSSV = 0X40;
ControlBox.CROSSH = 0X80;
ControlBox.RESET = 0X100;


// A timer is used to control scrolling and scaling when the
// button is held down. This requires a global pointers:
   // timer fires when this amount of time passes
   ControlBox.sbTimeout;    

   // path to the object who's method is called in response to timeout
   ControlBox.sbPath;

   // number of times the event has occured
   ControlBox.sbCount;

/**
@ --------------------------------------------------------------
@ Constructor: ControlBox
@ Parameter: parent - parent GUI object that will contain this ControlBox
@            target - GUI object (Content) which this ControlBox will control
@            ctrl_opts - one of:
@              ControlBox.ALL_CTRLS   || 0xFF
@              ControlBox.SCROLLV_CTRLS || 0x1
@              ControlBox.SCROLLH_CTRLS || 0x2
@              ControlBox.SCALE_CTRLSH   || 0x4
@              ControlBox.SCALE_CTRLSV   || 0x8
@         ControlBox.YCOORD_DISP   || 0x20
@         ControlBox.XCOORD_DISP   || 0x10
@         ControlBox.CROSSV  || 0x40
@         ControlBox.CROSSH  || 0x80
@         ControlBox.RESET   || 0X100

@ --------------------------------------------------------------
*/
function ControlBox (parent, target, ctrl_opts) {
  if (parent || target || ctrl_opts) {
    CGUI.debug ("creating ControlBox");
    this.target = target;
    this.parent = parent;
    this.lock_check=0;
    this.scrl_fac=1;

    this.slid_adj_h=false;
    this.scaling_start=0;

    this.ctrl_opts = ctrl_opts;
    

    /*
    Border Reset is an option, currently not default option.
    Border is nesscessary to ensure crosshair returns to center
    mark.  Once the use mouses over the border, a reset method
    is invoked in Window.js and the crosshair is centered.
    */
    if (ctrl_opts & ControlBox.RESET){
        this.border_top=this.addBorder("bt");
        this.border_bottom=this.addBorder("bb");
        this.border_left=this.addBorder("bl");
        this.border_right=this.addBorder("br");
      }
    
    if (ctrl_opts & ControlBox.CROSSH)
    {
      this.centermark_h=this.markCenterH();
    }
    if (ctrl_opts & ControlBox.CROSSV)
    {
      this.centermark_v=this.markCenterV();
    }

    if (ctrl_opts & ControlBox.SCALE_CTRLSH) {
      this.zoomin_h = this.createButton("+");
      this.zoomout_h = this.createButton("-");
      this.lock = this.createButton("*");

    }
      
    if (ctrl_opts & ControlBox.SCALE_CTRLSV) {
      this.zoomin_v = this.createButton("i");
      this.zoomout_v = this.createButton("o");
    }


    if (ctrl_opts & ControlBox.XCOORD_DISP) {
      this.centerbox_x = this.addCenterBox("X");
    }
    if (ctrl_opts & ControlBox.YCOORD_DISP) {
      this.centerbox_y = this.addCenterBox("Y");
    }
    if (ctrl_opts & ControlBox.SCROLLV_CTRLS) {
      this.up = this.createButton("^");
      this.down = this.createButton("v");
  
    if (ctrl_opts & ControlBox.SCROLLH_CTRLS){
        var scale_width = (ctrl_opts & ControlBox.SCALE_CTRLSV) ? 90 : 60; 
      }
      else
      {
  var scale_width = (ctrl_opts & ControlBox.SCALE_CTRLSV) ? 90 : 45;
      }            
      this.vslider = new Slider(
                     0, 0, this.parent.height - scale_width, 15, 90, parent,
                     ControlBox.transVTo
                     );
      this.vslider.offset = scale_width;
      this.vslider.group = this;
      this.vslider.addToParent (this.parent);
      this.vslider.setAnchor (this.parent, "right", 0, "top", 15);
    }
    }
    if (ctrl_opts & ControlBox.SCROLLH_CTRLS) {
      this.right = this.createButton(">");
      this.left = this.createButton("<");
          if (ctrl_opts & ControlBox.SCROLLV_CTRLS){
        var scale_height = (ctrl_opts & ControlBox.SCALE_CTRLSH) ? 90 : 60; 
      }
      else
      {
  var scale_height = (ctrl_opts & ControlBox.SCALE_CTRLSH) ? 90 : 45;
      }
      this.hslider = new Slider(
                     0, 0, this.parent.width - scale_height,15, 0,parent,
                     ControlBox.transHTo
                       );

      this.hslider.offset = scale_height;
      this.hslider.group = this;
      this.hslider.addToParent (this.parent);
      this.hslider.setAnchor (this.parent, "left", 15, "bottom", 0);
    }


    this.reverse = false;
    return this;
}

ControlBox.prototype.CGUI_TYPE = "CONTROLBOX1";

ControlBox.prototype.toString = function () {
  return "ControlBox";
}


/**
@ --------------------------------------------------------------
@ Method: checkResetDisable
@ Description: used by Window to check if RESET has been enabled
@ returns 1 for false, and 0 for true.  
@ Added: Nishan Sothilingam
@ --------------------------------------------------------------
*/

ControlBox.prototype.checkResetDisable = function(){
  if (this.ctrl_opts & ControlBox.RESET) {
    return 0;
  }
  else
    return 1;
  }

/**
@ --------------------------------------------------------------
@ Method: adjustAnchor
@ Description:
@  - adjust the anchor of any controls that exist
@  - this is primarily used when resizing a window.
@ --------------------------------------------------------------
*/
ControlBox.prototype.adjustAnchor = function () {
// not actually being used anymore...
}


/**
@ --------------------------------------------------------------
@ Method: reverseControls
@ Description: swap up/down and left/right
@ --------------------------------------------------------------
*/
ControlBox.prototype.reverseControls = function (bac_width) {
  con_height=138;

  this.reverse = !this.reverse;
  
  this.reverseLeftRight();
  this.reverseUpDown();
  this.reverseUpDownSlid(bac_width,con_height);
}


/**
@ --------------------------------------------------------------
@ Method: reverseUpDownSlid
@ Description: sets the slider settings when a BAC is rotated
@ into a positive OR negative orientation. 
@ Added:  Nishan Sothilingam 
@ --------------------------------------------------------------
*/
ControlBox.prototype.reverseUpDownSlid = function (bac_width, con_height) {
   this.hslider.initMinmax (this.hslider.max, this.hslider.min);
   this.vslider.initMinmax (this.vslider.max,this.vslider.min);
   if (this.reverse==true){
      this.hslider.reversed=true; 
      this.vslider.reversed=true;
   }
  else 
  {
      this.hslider.reversed=false;
      this.vslider.reversed=false;
  }
     
}


/**
@ --------------------------------------------------------------
@ Method: reverseLeftRight
@ Description: swap left/right
@ --------------------------------------------------------------
*/
ControlBox.prototype.reverseLeftRight = function () {
  var temp = this.right.event;
  this.right.event = this.left.event;
  this.left.event = temp;
}

/**
@ --------------------------------------------------------------
@ Method: reverseUpDown
@ Description: swap up/down
@ --------------------------------------------------------------
*/
ControlBox.prototype.reverseUpDown = function () {
  var temp = this.up.event;
  this.up.event = this.down.event;
  this.down.event = temp;
}

/**
@ --------------------------------------------------------------
@ Method: addCenterBox
@ Description: Add a box to the top/center of the parent which
@              displays the center coordinate of the parents
@              content.
@
@ Parameter: vert - which vertex this box will monitor (X, Y)
@ --------------------------------------------------------------
*/
ControlBox.prototype.addCenterBox = function (vert) {
  var width = ((this.ctrl_opts & 0x30) == 0x30) ? 75 : 120;
  var offset = ((this.ctrl_opts & 0x30) == 0x30) ? 75 : 0;

  switch (vert) {
  case "X" : var b = new TextBar ("X: ", width, 15, "black");
       b.setAnchor (this.parent, "left", 0, "top", 0);
             b.addToParent (this.parent);
             break;
  case "Y" : var b = new TextBar ("Y: ", width, 15, "black");
       b.setAnchor (this.parent, "left", offset, "top", 0);
             b.addToParent (this.parent);
             break;
  }
  b.setBkColor("white");
  b.setBkOpacity(0.6);

  return b;
}

/**
@ --------------------------------------------------------------
@ Method: addBorder
@ Description: Add a border to the controls which acts as a buffer
@ zone to detect a crosshair reset.  The border covers all edges
@ of the content window and is somewhat transparent so it blends in 
@ well with the rest of the contents.  This is neccessary because
@ we need to activate the reset only when an svg object that is not
@ a window is triggered.  So we have to ignore everything in the main
@ content pane, and use a BAR type border.  So when a mouseover event
@ occurs, we know a BAR as been hit, and not an item in the window. 
@
@ Parameter: type - which type this box will be
@ Added: Nishan Sothilingam
@ --------------------------------------------------------------
*/

ControlBox.prototype.addBorder=function(type){

  switch(type){
  case "bt": var b = new Bar (this.parent.width,15);
       b.setAnchor(this.parent, "left", 0, "top", 0);
       b.addToParent(this.parent);
       break;
  case "bb": var b = new Bar (this.parent.width,15);
       b.setAnchor(this.parent, "left", 0, "bottom", 0);
       b.addToParent(this.parent);
       break;

  case "bl": var b = new Bar (15,this.parent.height);
       b.setAnchor(this.parent, "left", 0, "top", 0);
       b.addToParent(this.parent);
       break;
  case "br": var b = new Bar (15, this.parent.height);
       b.setAnchor(this.parent, "right", 0, "top", 0);
       b.addToParent(this.parent);
       break;

}
  b.setBkColor("white");
  b.setBkOpacity(0.6);
  return b;
}
/**
@ --------------------------------------------------------------
@ Method: adjustCenterMarkH
@ Description:
@   CenterMarkH refers to the bar down the middle marking the
@   horizontal center of the display.  This function adjusts
@   it in response to the window being resized.
@ --------------------------------------------------------------
*/
ControlBox.prototype.adjustCenterMarkH = function () {
  var children = this.centermark_h;
  for (var i=0; i < children.length; i++) {
    if (children[i].anchor) children[i].anchor.adjustAnchor();
    else {
      CGUI.debug ("Lost Anchor - adjustCenterMarkH - dunno why.");
    }
  }
}

/**
@ --------------------------------------------------------------
@ Method: adjustCenterMarkV
@ Description:
@   CenterMarkV refers to the bar down the middle marking the
@   verticle center of the display.  This function adjusts it
@   in response to the window being resized.
@ --------------------------------------------------------------
*/
ControlBox.prototype.adjustCenterMarkV = function () {
  var children = this.centermark_v;
  for (var i=0; i < children.length; i++) {
    if (children[i].anchor) children[i].anchor.adjustAnchor();
    else {
      CGUI.debug ("Lost Anchor- adjustCenterMarkV - dunno why.");
    }
  }
}
/**
@---------------------------------------------------------
@Method: crossMoveH
@Description:  Keeps track of the cross hairs on the horizontal
@access and adjusts it according to the mouse position
@parameter: posy - this is the y position
@parameter: posx - this is the x position 
@content-height:  A numerical value representing the window height in pixels
@Added:Nishan Sothilingam
@---------------------------------------------------------
*/
ControlBox.prototype.crossMoveH = function(posy,posx,content_height) {
  //gap represents break in crosshairs -> ie, 3 pixels
  var gap = 3;
  var children = this.centermark_h;
  if (children!=null){
          children[0].setHeight(posy-gap);
          children[0].anchor.adjustAnchorX("left", posx);
    children[1].setHeight(content_height-posy-gap);
          children[1].anchor.adjustAnchorX("left",posx);
  }
}

/**
@---------------------------------------------------------
@Method: crossMoveV
@Description:  Keeps track of the cross hairs on the vertical
@access and adjusts it according to the mouse position
@parameter: posy - this is the y position
@parameter: posx - this is the x position 
@content-width:  A numerical value representing the window width in pixels
@Added: Nishan Sothilingam
@---------------------------------------------------------
*/
ControlBox.prototype.crossMoveV = function(posy, posx,content_width) {
  var gap=3;
  var children = this.centermark_v;
  if (children!=null){
      children[1].setWidth(content_width-posx-gap);
      children[1].anchor.adjustAnchorY("top", posy);
     children[0].setWidth(posx-gap);
  children[0].anchor.adjustAnchorY("top", posy);
  }
}

/**
@----------------------------------------------------------------
@Method: cross_resetH
@Description: resets the horizontal cross hair position to the center of the
@content pane.
@parameter: content_width:  used for calculating the center for crosshair
@reset
@Added: Nishan Sothilingam
@----------------------------------------------------------------
*/
ControlBox.prototype.cross_resetH = function(content_width) {
  var gap=3;
  var children_h = this.centermark_h;
  if (children_h != null){
       children_h[1].setHeight(content_width/2-gap);
      children_h[1].setAnchor(this.parent,"center",0,"bottom",0);
      children_h[0].setHeight(content_width/2-gap);
      children_h[0].setAnchor(this.parent,"center", 0, "top",0);
  }
}




/**
@----------------------------------------------------------------
@Method: cross_resetV
@Description: resets the vertical cross hair position to the center of the
@content pane.
@parameter:  content_height, used in calculating crosshair reset
@Added: Nishan Sothilingam
@----------------------------------------------------------------
*/
ControlBox.prototype.cross_resetV = function(content_height) {
  var gap=3;
  var children_v = this.centermark_v;
  if (children_v!=null){
    children_v[1].setWidth(content_height/2-gap);
    children_v[1].setAnchor(this.parent,"right",0,"center",0);
    children_v[0].setWidth(content_height/2-gap);
    children_v[0].setAnchor(this.parent,"left", 0, "center",0);            
  }
}

/**
@ --------------------------------------------------------------
@ Method: markCenterH
@ Description:
@   CenterMarkH refers to the bar down the middle marking the
@   Horizontal center of the display.  This function creates it.
@ --------------------------------------------------------------
*/
ControlBox.prototype.markCenterH = function () {
  var arr = new Array();
  var r = document.createElement ("rect");
  r.setAttribute ("width", "0.5");
  r.setAttribute ("height", "20");
  r.setAttribute ("fill", "black");
  var wrap = new Wrapper (r);
  wrap.setAnchor (this.parent, "center", 0, "top", 0);
  wrap.addToParent(this.parent);
  arr[0] = wrap;

  r = r.cloneNode(true);
  var wrap = new Wrapper (r);
  wrap.setAnchor (this.parent, "center", 0, "bottom", 0);
  wrap.addToParent(this.parent);
  arr[1] = wrap;

  return arr;
}

/**
@ --------------------------------------------------------------
@ Method: markCenterV
@ Description:
@   CenterMarkV refers to the bar down the middle marking the
@   Verticle center of the display.  This function creates it.
@ --------------------------------------------------------------
*/
ControlBox.prototype.markCenterV = function () {
  // add in a bar and use it as a text box to display the center coordinate
  var arr = new Array();
  var r = document.createElement ("rect");
  r.setAttribute ("width", "20");
  r.setAttribute ("height", "0.5");
  r.setAttribute ("fill", "black");
  var wrap = new Wrapper (r);
  wrap.setAnchor (this.parent, "left", 0, "center", 0);
  wrap.addToParent (this.parent);
  arr[0] = wrap;

  r = r.cloneNode(true);
  wrap = new Wrapper (r);
  wrap.setAnchor (this.parent, "right", 0, "center", 0);
  wrap.addToParent (this.parent);
  arr[1] = wrap;
  return arr;
}

/**
@ --------------------------------------------------------------
@ Method: transLeft
@ Description: translate the content left
@ Parameter: evt - the event triggering this request
@ Quirk:  slider integration with dynamic scale is not correct,
@ though there are several solutions, none of the ones
@ I had could do it perfectly

@ --------------------------------------------------------------
*/
ControlBox.transLeft = function (evt, amount) {
  var target = evt.getTarget().parentNode.parentNode.jsref;
  if (target.group) {
  if (target.group.hslider) {

      amount = target.group.hslider.adjustValue ((amount) ? -amount : -1);
    }
    target.group.target.transRight (Math.abs(amount));
   
    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )

    if (evt) ControlBox.sbPath = target.group;
  
    if (ControlBox.sbPath)
      ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.transRight(null, 1)",
              (evt?500:25)
    );
  } else {if (CGUI.getJSRef(evt.getTarget()).group.lock_check==1)
{
  ControlBox.scaleDownV(evt);
  ControlBox.scaleDownH(evt);
}
else

    alert ("No Group");
  }
}

ControlBox.prototype.transLeft = function (evt, amount) {
  if (this.target) {

    if (this.hslider) {
      amount = this.hslider.adjustValue ((amount) ? amount : 1); 
      //the amount the slider adjusts must be changed depending on
      //the scale    
      this.hslider.adjustValue (5*1/this.target.trans.s_x);
    }
        this.target.transLeft (Math.abs(amount));

    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
  
    if (evt) ControlBox.sbPath = this;
  
    if (ControlBox.sbPath) {
      ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.transLeft (null, "+ 5+")",
              (evt?500:25)
       );
    }
  } else {
    alert ("No target");
  }
}


/**
@ --------------------------------------------------------------
@ Method: transRight
@ Description: translate the content right
@ Parameter: evt - the event triggering this request
@ Quirk:  slider integration with dynamic scale is not correct,
@ though there are several solutions, none of the ones
@ I had could do it perfectly
@ --------------------------------------------------------------
*/
ControlBox.transRight = function (evt, amount) {
  var target = evt.getTarget().parentNode.parentNode.jsref;
  if (target.group) {

    if (target.group.hslider) {
      amount = target.group.hslider.adjustValue ((amount) ? amount : 1);
    }

    target.group.target.transLeft (amount);

    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )

    if (evt) ControlBox.sbPath = target.group;

    if (ControlBox.sbPath) {
      ControlBox.sbTimeout = setTimeout(
            "ControlBox.sbPath.transLeft(null, 1)",
            (evt?500:25)
      );
    }
  } else {
    alert ("No Group");
  }
}

ControlBox.prototype.transRight = function (evt, amount) {
  if (this.target) {

    if (this.hslider) {
      amount = this.hslider.adjustValue ((amount) ? -amount : -1);
      //thumb adjustment depends on scale of content      
      this.hslider.adjustValue (-5*1/this.target.trans.s_x);

    }
    
    this.target.transRight (Math.abs(amount)*this.hslider.trans_hori);

    if( ControlBox.sbTimeout ) clearTimeout(ControlBox.sbTimeout )
  
    if (evt) ControlBox.sbPath = this;
  
    if (ControlBox.sbPath)
      ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.transRight(null, "+5+")",
              (evt?500:25)
    );
  } else {
    alert ("No target");
  }
}



/**
@ --------------------------------------------------------------
@ Method: setAnchor
@ Description: Set this anchor.
@ Parameter: target - the object to anchor to.
@ Parameter: ax - "left" "center" "right", the horizontal anchor position
@ Parameter: offsetx - the offset ('+':right '-':left) from the ax position
@ Parameter: ay - "top" "center" "bottom", the vertical anchor position
@ Parameter: offsety - the offset ('+':down '-':up) from the ay position
@
@ Example:
@ Continuing the setAnchor (frame, "left", "15", "bottom", "-15")
@ would anchor the object(the button)
@ Added - Chris Peto
@ --------------------------------------------------------------
*/

ControlBox.transHTo = function (newx,evt) {
  var target = evt.getTarget().parentNode.parentNode.parentNode.jsref;
  if (target.group) {
    target.group.target.transHTo (-newx);
    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
  } else {
    alert ("No Group");
  }
}

/**
@ --------------------------------------------------------------
@ Method: setAnchor
@ Description: Set this anchor.
@ Parameter: target - the object to anchor to.
@ Parameter: ax - "left" "center" "right", the horizontal anchor position
@ Parameter: offsetx - the offset ('+':right '-':left) from the ax position
@ Parameter: ay - "top" "center" "bottom", the vertical anchor position
@ Parameter: offsety - the offset ('+':down '-':up) from the ay position
@
@ Example:
@ Continuing the setAnchor (frame, "left", "15", "bottom", "-15")
@ would anchor the object(the button)
 @ added - Chris Peto
@ --------------------------------------------------------------
*/

ControlBox.transVTo = function (newy, evt) {
  var target = CGUI.getJSRef (evt.getTarget());
  if (target.group) {
    target.group.target.transVTo (-newy);
    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
  } else {
    alert ("No Group");
  }
}
/**
@ --------------------------------------------------------------
@ Method: transUp
@ Description: translate the content up
@ Parameter: evt - the event triggering this request
@ --------------------------------------------------------------
*/
ControlBox.transUp = function (evt, amount) {
  

  var target = evt.getTarget().parentNode.parentNode.jsref;
  if (target.group) {
    if (target.group.vslider) {
      amount = target.group.vslider.adjustValue (-1);
    }
    
  target.group.target.transDown (Math.abs(amount));

    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
  
    if (evt) ControlBox.sbPath = target.group;
  
    if (ControlBox.sbPath)
      ControlBox.sbTimeout = setTimeout(
                 "ControlBox.sbPath.transDown(null, 1)",
                 (evt?500:25)
      );
  } else {
    alert ("No Group");
  }
}

ControlBox.prototype.transUp = function (evt, amount) {

  
  if (this.target) {

    if (this.vslider) {
      amount = this.vslider.adjustValue ((amount) ? amount : 1);
    }


      this.target.transUp(Math.abs(amount)*this.vslider.trans_vert);



    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
  
    if (evt) ControlBox.sbPath = this;
  
    if (ControlBox.sbPath)
      ControlBox.sbTimeout = setTimeout(
                 "ControlBox.sbPath.transUp(null, 1)",
                 (evt?500:25)
      );
  } else {
    alert ("No Target");
  }
}

/**
@ --------------------------------------------------------------
@ Method: transDown
@ Description: translate the content down
@ Parameter: evt - the event triggering this request
@ --------------------------------------------------------------
*/
ControlBox.transDown = function (evt, amount) {
  var target = evt.getTarget().parentNode.parentNode.jsref;
  if (target.group) {

    if (target.group.vslider) {
      amount = target.group.vslider.adjustValue ((amount) ? amount : 1);
    }

    target.group.target.transUp (Math.abs(amount));

    if ( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
  
    if (evt) ControlBox.sbPath = target.group;
  
    if (ControlBox.sbPath)
      ControlBox.sbTimeout = setTimeout(
                 "ControlBox.sbPath.transUp(null, 1)",
                 (evt?500:25)
      );
  } else {
    alert ("No Group");
  }
 
}

ControlBox.prototype.transDown = function (evt, amount) {
  if (this.target) {
  
    if (this.vslider) {
      amount = this.vslider.adjustValue ((amount) ? -amount : -1);
    }

    this.target.transDown (Math.abs(amount)*this.vslider.trans_vert);

    if ( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
  
    if (evt) ControlBox.sbPath = this;
  
    if (ControlBox.sbPath)
      ControlBox.sbTimeout = setTimeout(
                 "ControlBox.sbPath.transDown(null, 1)",
                 (evt?500:25)
      );
  } else {
    alert ("No Group");
  }
}




/**
@ --------------------------------------------------------------
@ Method: scaleUpHori
@ Description: called when content is to be scaled
@ Parameter: evt - the event triggering this request
@ --------------------------------------------------------------
*/


ControlBox.scaleUpHori = function(evt, factor){
  if (CGUI.getJSRef(evt.getTarget()).group.lock_check==1)
  {
    ControlBox.scaleUpV(evt);
    ControlBox.scaleUpH(evt);
  }
  else
    ControlBox.scaleUpH(evt);
}

/**
@ --------------------------------------------------------------
@ Method: scaleDownHori
@ Description: called when content is to be scaled
@ Parameter: evt - the event triggering this request
@ --------------------------------------------------------------
*/

ControlBox.scaleDownHori = function(evt, factor){
  if (CGUI.getJSRef (evt.getTarget()).group.lock_check==1)
  {
    ControlBox.scaleDownV(evt);
    ControlBox.scaleDownH(evt);
  }
  else
    ControlBox.scaleDownH(evt);
}

/**
@ --------------------------------------------------------------
@ Method: scaleUpVert
@ Description: called when content is to be scaled
@ Parameter: evt - the event triggering this request
@ --------------------------------------------------------------
*/

ControlBox.scaleUpVert = function(evt, factor){
  var target = evt.getTarget().parentNode.parentNode.jsref;
  if (CGUI.getJSRef(evt.getTarget()).group.lock_check==1)
  {
    ControlBox.scaleUpV(evt);
    ControlBox.scaleUpH(evt);
  }
  else
    ControlBox.scaleUpV(evt);
}


/**
@ --------------------------------------------------------------
@ Method: scaleDownVert
@ Description: called when content is to be scaled
@ Parameter: evt - the event triggering this request
@ --------------------------------------------------------------
*/


ControlBox.scaleDownVert = function(evt, factor){
var target = evt.getTarget().parentNode.parentNode.jsref;
 
if (CGUI.getJSRef(evt.getTarget()).group.lock_check==1)
{
  ControlBox.scaleDownV(evt);
  ControlBox.scaleDownH(evt);
}
else
  ControlBox.scaleDownV(evt);
}



/**
@ --------------------------------------------------------------
@ Method: lock
@ Description: Tell the content to scale down in the vertical dir
@ Parameter: evt - event causing this scaling
@ --------------------------------------------------------------
*/

ControlBox.lock_aspect = function (evt) {
  CGUI.getJSRef (evt.getTarget()).group.lock_aspect();
}

ControlBox.prototype.lock_aspect = function(){
if (this.lock_check==1)
    this.lock_check=0;
else 
    this.lock_check=1;
}




/**
@ --------------------------------------------------------------
@ Method: scaleUpH
@ Description: Tell the content to scale in the horizontal direction
@ Parameter: evt - event causing this scaling
@ --------------------------------------------------------------
*/

ControlBox.scaleUpH = function (evt, factor) {
  var target = evt.getTarget().parentNode.parentNode.jsref;

  //this should be called only once to set up the right number for dynamic adjustment
  if (target.group.slid_adj_h == false){
      target.group.scaling_start=target.group.target.trans.s_x*(1.1*1.1);
  }
  target.group.slid_adj_h=true;
  if (target.group) {
    if (target.group.target.trans.s_x >= target.group.scaling_start){
      target.group.zoomout_h.enable();
      //if the slider is reversed we want to adjust min and max differently
      if (target.group.hslider.reversed==true){
  var new_max=Math.round(target.group.hslider.max-(1/target.group.target.trans.s_x));
  var new_min=Math.round(target.group.hslider.min+(1/target.group.target.trans.s_x));

      }
      //if slider is + orientation, set min and max appropriatly
      else{
  var new_max=Math.round(target.group.hslider.max+(1/target.group.target.trans.s_x));
  var new_min=Math.round(target.group.hslider.min-(1/target.group.target.trans.s_x));
      }
      //set the new min and max of the slider
      target.group.hslider.max = new_max; 
      target.group.hslider.min = new_min;
    }
    else{
      // do nothing
    }

    target.group.target.scaleUpH (factor);  
    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
    if (evt) {
      ControlBox.sbPath = target.group;
      ControlBox.sbCount = 0;
    } 
    else {
      ControlBox.sbCount++;
    }
  
    if (ControlBox.sbPath) {

           ControlBox.sbTimeout = setTimeout(
               "ControlBox.sbPath.scaleUpH(null, " +
                factor + ")", (evt?500:25)
      );
     }
    } 
    else {
    alert ("No Group");
  }
}

ControlBox.prototype.scaleUpH = function (evt, factor) {
  if (this.target) {

  //puts a cap on minimization if scale is below a certain number
  if (this.target.trans.s_x >= this.scaling_start){
    this.zoomout_h.enable();
    /*adjust the sliders appropriatly to orientation
    the magic number 1 controls the amount of white space
    present on dynmaic scale, increase the number for more white 
    space*/
    if (this.hslider.reversed==true){

      this.hslider.max=this.hslider.max-(1/this.target.trans.s_x);
      this.hslider.min=this.hslider.min+(1/this.target.trans.s_x);
    }
    else{
      this.hslider.max=this.hslider.max+(1/this.target.trans.s_x);
      this.hslider.min=this.hslider.min-(1/this.target.trans.s_x);
    }
  }

    factor=null;
    this.target.scaleUpH (factor);
    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )
  
    if (evt) {
      ControlBox.sbPath = this;
      ControlBox.sbCount = 0;
    } else {
      ControlBox.sbCount++;
    }
  
    if (ControlBox.sbPath) {
      factor = (ControlBox.sbCount<5)?1.1:1.2;      
    //lock aspect ratio code, if locked -> scale both ways, else do not
    if (this.lock_check==1)
    {
      ControlBox.sbTimeout = setTimeout(
               "ControlBox.sbPath.scaleUpH(null, " +
                factor + ");ControlBox.sbPath.scaleUpV(null, " + factor +")", (evt?500:25)
      );
    }
    else
    {
       ControlBox.sbTimeout = setTimeout(
               "ControlBox.sbPath.scaleUpH(null, " +
                factor + ")", (evt?500:25)
      );
    }
    }
  } 
  else {
    alert ("No Group");
  }
}

/**
@ --------------------------------------------------------------
@ Method: scaleUpV
@ Description: Tell the content to scale in the vertical direction
@ Parameter: evt - event causing this scaling
@ --------------------------------------------------------------
*/
ControlBox.scaleUpV = function (evt, factor) {
  //lots of magic numbers, must clean up


  var target = evt.getTarget().parentNode.parentNode.jsref;
  //adjust the vertical min and max up
  var new_max=Math.round(target.group.vslider.max+(1/target.group.target.trans.s_y*2));
  var new_min=Math.round(target.group.vslider.min-(1/target.group.target.trans.s_y*4));
  //start adjusting only when s_y is > 7
  // 7 appears to be a perfect number since bac heights are constant
  if (target.group.target.trans.s_y>.95)
    target.group.zoomout_v.enable();

  if (target.group.target.trans.s_y>7)
  {
    target.group.vslider.max = new_max; 
    target.group.vslider.min = new_min;
  }

  if (target.group) {
    target.group.target.scaleUpV (factor);

    if( ControlBox.sbTimeout ) clearTimeout (ControlBox.sbTimeout)

    if (evt) {
      ControlBox.sbPath = target.group;
      ControlBox.sbCount = 0;
    } else {
      ControlBox.sbCount++;
    }

    if (ControlBox.sbPath) {
      factor = (ControlBox.sbCount<5)?1.1:1.2;
                 ControlBox.sbTimeout = setTimeout(
               "ControlBox.sbPath.scaleUpV(null, " +
                factor + ")", (evt?500:25)
      );
      }
    
  } else {
    alert ("No Group");
  }
}

ControlBox.prototype.scaleUpV = function (evt, factor) {
  //adjusting min and max on vertical slider
  var new_max=Math.round(this.vslider.max+(1/this.target.trans.s_y*2));
  var new_min=Math.round(this.vslider.min-(1/this.target.trans.s_y*3));
  //enable zoomout when trans.s_y reaches this point
  if (this.target.trans.s_y>.95)
    this.zoomout_v.enable();

  if(this.target.trans.s_y>7){
    this.vslider.max = new_max; 
    this.vslider.min = new_min;
  }


  if (this.target) {
    this.target.scaleUpV (factor);

    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )

    if (evt) {
      ControlBox.sbPath = this;
      ControlBox.sbCount = 0;
    } else {
      ControlBox.sbCount++;
    }

    if (ControlBox.sbPath) {
      factor = (ControlBox.sbCount<5)?1.1:1.2;

      if (this.lock_check==1)
      {
           ControlBox.sbTimeout = setTimeout(
               "ControlBox.sbPath.scaleUpV(null, " +
                factor + ");ControlBox.sbPath.scaleUpH(null, "+factor+")", (evt?500:25)
      );
      }
      else
      {
     ControlBox.sbTimeout = setTimeout(
               "ControlBox.sbPath.scaleUpV(null, " +
                factor + ")", (evt?500:25)
      );
      }  
    }
  } 
  else {
    alert ("No target");
  }
}

/**
@ --------------------------------------------------------------
@ Method: scaleDownH
@ Description: Tell the content to scale down in the horizontal dir
@ Parameter: evt - event causing this scaling
@ --------------------------------------------------------------
*/
ControlBox.scaleDownH = function (evt, factor) {
  var target = evt.getTarget().parentNode.parentNode.jsref;

  //horizonal slider adjustment depending on orientation
  if (target.group.hslider.reversed==true){
    var new_max=Math.round(target.group.hslider.max+(1/(target.group.target.trans.s_x/1.1)));
    var new_min=Math.round(target.group.hslider.min-(1/(target.group.target.trans.s_x/1.1)));

  }
  else{
    var new_max=Math.round(target.group.hslider.max-(1/(target.group.target.trans.s_x/1.1)));
    var new_min=Math.round(target.group.hslider.min+(1/(target.group.target.trans.s_x/1.1)));
  }

  //disable zoomout on the horizontal axis when we go below
  //a certains scale
  if (target.group.target.trans.s_x<=target.group.scaling_start/(1.1*1.1))
  {
    target.group.zoomout_h.disable();
    target.group.zoomout_h.deselect();
    if (target.group.lock_check==1)
    {
      target.group.zoomout_v.disable();
      target.group.zoomout_v.deselect();
    }
  }
 
  //this should be called only once to set up the right number for dynamic adjustment to start.  Once this number is set, the horizontal scale will only start adjusting once this number has been passed.  It is unique to each BAC so it must be calculated
  if (target.group.slid_adj_h == false){
      target.group.scaling_start=target.group.target.trans.s_x*(1.1*1.1);
  }
  target.group.slid_adj_h=true;

  if (target.group.target.trans.s_x >= target.group.scaling_start*(1.1))
  {
   target.group.hslider.max=new_max;
   target.group.hslider.min=new_min;
  }
  else{
    //do nothing
  }
  if (target.group) {
    target.group.target.scaleDownH (factor);

    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )

    if (evt) {
      ControlBox.sbPath = target.group;
      ControlBox.sbCount = 0;
    } else {
      ControlBox.sbCount++;
    }

    if (ControlBox.sbPath) {
      factor = (ControlBox.sbCount<5)?1.1:1.2;

    ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.scaleDownH(null, "+
              factor +")", (evt?500:25)
      ); 
    } 
    else {
      alert ("No Group");
    }
  }
}

ControlBox.prototype.scaleDownH = function (evt, factor) {
  
  //disable zoomout when s_x reaches a certain scale
  if (this.target.trans.s_x<=this.scaling_start/(1.1*1.1))
  {
    this.zoomout_h.disable();
    this.zoomout_h.deselect();
    if (this.lock_check==1) {
      this.zoomout_v.disable();
      this.zoomout_v.deselect();
    }
  }

  if (this.hslider.reversed == true){
    var new_max=this.hslider.max+(1/(this.target.trans.s_x/1.1));
    var new_min=this.hslider.min-(1/(this.target.trans.s_x/1.1));

  }
  else{
     var new_max=this.hslider.max-(1/(this.target.trans.s_x/1.1));
     var new_min=this.hslider.min+(1/(this.target.trans.s_x/1.1));
  }

  //only start scaling when s_x is greater or equal to the set number for
  //scaling
  if (this.target.trans.s_x >= this.scaling_start*(1.1))
  {
    this.hslider.max=new_max;
    this.hslider.min=new_min;
  }
  

  if (this.target) {
    factor=null;
    this.target.scaleDownH (factor);

    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )

    if (evt) {
      ControlBox.sbPath = this;
      ControlBox.sbCount = 0;
    } else {
      ControlBox.sbCount++;
    }

    if (ControlBox.sbPath) {
      factor = (ControlBox.sbCount<5)?1.1:1.2;
    if (this.zoomout_h.enabled==true){ 
      if (this.lock_check==1)
      {
  ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.scaleDownV(null, "+
              factor +");ControlBox.sbPath.scaleDownH(null, "+factor+")", (evt?500:25)
  );
      }
      else
      {
  ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.scaleDownH(null, "+
              factor +")", (evt?500:25)
  );
      }
    }
  } 
  else {
    alert ("No target");
  }
}}
/**
@ --------------------------------------------------------------
@ Method: scaleDownV
@ Description: Tell the content to scale down in the vertical dir
@ Parameter: evt - event causing this scaling
@ --------------------------------------------------------------
*/
ControlBox.scaleDownV = function (evt, factor) {
    var target = evt.getTarget().parentNode.parentNode.jsref;

 if (target.group.target.trans.s_y <.95) {
      target.group.zoomout_v.disable();  
      target.group.zoomout_v.deselect();  
 }

    var new_max=Math.round(target.group.vslider.max-(1/(target.group.target.trans.s_y/1.1)*2));
    var new_min=Math.round(target.group.vslider.min+(1/(target.group.target.trans.s_y/1.1)*4));
    
    if (target.group.target.trans.s_y>6){

      target.group.vslider.max = new_max; 
      target.group.vslider.min = new_min;
      
    }


    if (target.group) {
      target.group.target.scaleDownV (factor);

      if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )

      if (evt) {
  ControlBox.sbPath = target.group;
        ControlBox.sbCount = 0;
      } else {
        ControlBox.sbCount++;
      }

      if (ControlBox.sbPath) {
  factor = (ControlBox.sbCount<5)?1.1:1.2;
  ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.scaleDownV(null, "+
              factor +")", (evt?500:25)
  );
      }
    } 
    else {
    alert ("No Group");
  }
}

ControlBox.prototype.scaleDownV = function (evt, factor) {
 if (this.target.trans.s_y <.95){
      this.zoomout_v.disable();
      this.zoomout_v.deselect();
  } 

  var new_max=Math.round(this.vslider.max-(1/(this.target.trans.s_y/1.1)*2));
  var new_min=Math.round(this.vslider.min+(1/(this.target.trans.s_y/1.1)*4));
 
    
  if (this.target.trans.s_y>6){
    this.vslider.max = new_max; 
    this.vslider.min = new_min;
  }

   if (this.target) {
    this.target.scaleDownV (factor);

    if( ControlBox.sbTimeout ) clearTimeout( ControlBox.sbTimeout )

    if (evt) {
      ControlBox.sbPath = this
      ControlBox.sbCount = 0;
    } else {
      ControlBox.sbCount++;
    }

    if (ControlBox.sbPath) {
      factor = (ControlBox.sbCount<5)?1.1:1.2;
    if (this.zoomout_v.enabled==true)
    {
      if (this.lock_check==1) {
  ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.scaleDownV(null, "+ factor +
              "); ControlBox.sbPath.scaleDownH(null,"+factor+")", (evt?500:25)
        );
      } else {
       ControlBox.sbTimeout = setTimeout(
              "ControlBox.sbPath.scaleDownV(null, "+
              factor +")", (evt?500:25)
      );
      }
    }
  }
  } else {
    alert ("No target");
  }
}


/**
@ --------------------------------------------------------------
@ Method: createButton
@ Description: creates the buttons available in the control box.
@ Parameter: c - the button to create
@   options are: ^ - up
@                v - down
@                < - left
@                > - right
@                + - zoomin horizontal
@                - - zoomout horizontal
@                i - zoomin verticle
@                o - zoomout verticle
@
@ Notes: Behaviour of the buttons
@   mousedown event calls
@     ^ - transUp(evt)
@     > - transRight(evt)
@     < - transLeft(evt)
@     v - transDown(evt)
@     + - scaleUpH(evt)
@     - - scaleDownH(evt)
@     i - scaleUpV(evt)
@     o - scaleDownV(evt)
@
@   the mousedown events set a timer which will call them again
@   after x seconds if the mouse is still down & in the button.
@
@   mouseOut - clears timeout - preventing additional calls
@   mouseUp - clears timeout - preventing additional calls
@ --------------------------------------------------------------
*/
ControlBox.prototype.createButton = function (c) {
  var b = null;

  switch (c) {
  case '^': b = new Button (15, 15, c, "ControlBox.transUp(evt)");
            b.setAnchor (this.parent, "right", 0, "top", 0);
            break;
  case '>': b = new Button (15, 15, c, "ControlBox.transRight(evt)");
            var h_offset = 0;
            h_offset -= (this.ctrl_opts & ControlBox.SCROLLV_CTRLS) ? 15 : 0; 
            h_offset -= (this.ctrl_opts & ControlBox.SCALE_CTRLSV) ? 30 : 0; 
            b.setAnchor (this.parent, "right", h_offset, "bottom", 0);
            break;
  case '<': b = new Button (15, 15, c, "ControlBox.transLeft(evt)");
            b.setAnchor (this.parent, "left", 0, "bottom", 0);
            break;
  case 'v': b = new Button (15, 15, c, "ControlBox.transDown(evt)");
            var v_offset = 0;
            v_offset -= (this.ctrl_opts & ControlBox.SCROLLH_CTRLS) ?
                        15 : 0;
            v_offset -= (this.ctrl_opts & ControlBox.SCALE_CTRLSV) ?
                        30 : 0;
            b.setAnchor (this.parent, "right", 0, "bottom", v_offset);
            break;
  case '+': b = new Button (15, 15, c, "ControlBox.scaleUpHori(evt)");
            b.setAnchor (this.parent, "right", -15, "bottom", 0);
            break;
  case '-': b = new Button (15, 15, c, "ControlBox.scaleDownHori(evt)");
            b.setAnchor (this.parent, "right", -30, "bottom", 0);
            break;
  case 'i': b = new Button (15, 15, '+', "ControlBox.scaleUpVert(evt)");
            b.setAnchor (this.parent, "right", 0, "bottom", -15);
            break;
  case 'o': b = new Button (15, 15, '-', "ControlBox.scaleDownVert(evt)");
            b.setAnchor (this.parent, "right", 0, "bottom", -30);
            break;
  case '*': b=new Button(15,15, 'L', "ControlBox.lock_aspect(evt)");
      b.setAnchor(this.parent, "right", 0, "bottom", 0);
      b.enableToggle();
      break;
  default : alert ("Unknown character " + c + ": Valid (^,V,>,<,+,-,i,o,*)");
            b = null;
            break;
  }

  if (b) {
     b.group = this;
     b.addToParent (this.parent);
     var o = new Object;
     o.handleEvent = ControlBox.clearTimeout;
     b.svg_reference.addEventListener("mouseup", o, false);
     b.svg_reference.addEventListener("mouseout", o, false);
  }

  return b;
}

/**
@ --------------------------------------------------------------
@ Method: clearTimeout
@ Description: clear the timer being used to allow scrolling to
@              occur while the mouse button is held down.
@
@ Parameter: evt - the event causing the timer to clear
@ --------------------------------------------------------------
*/

ControlBox.clearTimeout = function(evt) {
  if (ControlBox.sbTimeout) {
    clearTimeout ( ControlBox.sbTimeout );
    ControlBox.sbPath = "";
  }
}



/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/


/**
@ --------------------------------------------------------------
@ Object: Content
@ Desc:
@ Example:
@ Inherits from: Pane
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Content
@ Parameter: context - document context to use
@          transform - transform object associated with this content
@                  x - x coordinate for this object
@                  y - y coordinate for this object
@              width - width of this object
@             height - height of this object
@ --------------------------------------------------------------
*/
function Content (context, transform, x, y, width, height) {
  if (arguments.length > 0) {
    CGUI.debug ("creating Content");
    this.base = Pane;
    this.base (x, y, width, height);
    this.trans = transform;
    this.context = context;
    this.open ();
    this.oheight = height;
    this.owidth = width;

    // coordinate that is currently being displayed at left edge
    this.center_coord = 0; // the center of the contentPane
    this.rotated = false;  // has the content been rotated 180 deg?
    this.new_zero_x = 0;   // when the content is flipped the origin
                           // must be adjusted.
  }
}
Content.prototype = new Pane;
Content.prototype.content_superclass = Pane.prototype;
Content.prototype.CGUI_TYPE = "CONTENT";


/**
@ --------------------------------------------------------------
@ Method: open
@ Description:
@   - Adds a group <g> to the SVG.
@   - SVG displayed in this content pane will be added to this group.
@   - Makes the rect from Pane white, causing content panes to have a
@     white background.
@ --------------------------------------------------------------
*/

Content.prototype.open = function () {
  this.content = this.svg_reference.appendChild (document.createElement ("g"));
  this.svg_reference.getFirstChild().setAttribute ("class", "contentface");
}

/**
@ --------------------------------------------------------------
@ Method: setBkColor
@ Description: Set the backround color of this contentPane
@
@
@ Parameter: iColor - the svg color, can be any valid svg color format
@ Added - Chris Peto
@ --------------------------------------------------------------
*/

Content.prototype.setBkColor = function (iColor) {
  this.svg_reference.getFirstChild().setAttribute ("fill", iColor);
}


/**
@ --------------------------------------------------------------
@ Method: setWidth
@ Description: Set the width of this contentPane
@              Doing this scales the content proportionally
@
@ Parameter: width - the width
@ --------------------------------------------------------------
*/
Content.prototype.setWidth = function (width) {
  var factor = (this.width/width);
  this.trans.s_x /= factor;

  var adjust = (this.width - width) / 2;

  if (this.trans.r > 0) {
    this.trans.t_x += adjust;
  } else {
    this.trans.t_x -= adjust;
  }

  this.sup_setWidth = this.content_superclass.setWidth;
  this.sup_setWidth (width);
  CGUI.debug ("    Called content_superclass.setWidth");

  this.refresh();
}

/**
@ --------------------------------------------------------------
@ Method: setHeight
@ Description: Set the height of this contentPane
@              Doing this scales the content proportionally
@
@ Parameter: height - the height
@ --------------------------------------------------------------
*/
Content.prototype.setHeight = function (height) {
  if (height == 0) height = 1;
  var factor = (this.height/height);
  this.trans.s_y /= factor;

  var adjust = (this.height - height) / 2;
  this.trans.t_y -= adjust;
  this.sup_setHeight = this.content_superclass.setHeight;
  this.sup_setHeight (height);
  CGUI.debug ("    Called content_superclass.setHeight");

  this.refresh();
}


/**
@ --------------------------------------------------------------
@ Method: close
@ Description: close this content pane
@   - removes this pane from the panes parent
@ --------------------------------------------------------------
*/
Content.prototype.close = function () {
  CGUI.debug ("Content close");
  this.parent.svg_reference.removeChild(this.svg_reference);
}


/**
@ --------------------------------------------------------------
@ Method: addAsText
@ Description: Add SVG content to this contentPane which is
@              XML encoded.
@
@ Parameter: text - the xml encoded content to add
@
@ Example:
@   Given a varaible text with the following xml string:
@
@     text = "<rect x=10 y=10 width=10 height=10/>";
@
@   Adding it to a contentPane called contentPane1 like so:
@
@     contentpane1.addAsText (text);
@
@   Would result in a 10x10 square being added to the the
@   content pane at position x=10, y=10
@ --------------------------------------------------------------
*/
Content.prototype.addAsText = function (text) {
  CGUI.debug ("content addAsText");
  var frag = parseXML (text, this.context);
  return this.addAsSVG (frag);
}


/**
@ --------------------------------------------------------------
@ Method: addAsSVG
@ Description: Add SVG content to the content pane which is
@              already an SVG Object.
@
@ Parameter: node - the SVG node to add
@
@ Example:
@   The following pseudocode creates a rectangle, and adds it
@   to the content pane
@
@     r = document.createElement ("rect");
@       r.setAttribute ("x", 10);
@       r.setAttribute ("y", 10);
@       r.setAttribute ("width", 10);
@       r.setAttribute ("height", 10);
@     contentPane1.addAsSVG (r);
@
@  This has the same effect as adding the xml string in the
@  addAsText example above.
@ --------------------------------------------------------------
*/
Content.prototype.addAsSVG = function (node) {
  CGUI.debug ("content addAsSVG");
  var top_node = null;
  if (!node) return;
  if (node.nodeType == 11) { // nodeType 11 is a documentFragment
    top_node = node.firstChild;
    this.content.appendChild (node);
  } else {
    top_node = this.content.appendChild (node);
  }
  return top_node;
}

/**
@ --------------------------------------------------------------
@ Method: addAsObj
@ Description: Add CGUI based object to the content pane which is
@              already an SVG Object.
@
@ Parameter: obj - the CGUI based object to add
@
@ Example:
@   The following pseudocode creates a CGUI based Slider, and adds it
@   to the content pane
@
@     redslider = new Slider(
@                14, 13, 239,18, 0,null,
@                changeRedColor
@            );
@						
@			contentPane1.addAsObj (redslider);
@
@  This has the same effect as adding the xml string in the
@  addAsText example above.
@ --------------------------------------------------------------
*/
Content.prototype.addAsObj = function (obj) {
  CGUI.debug ("content addAsObj");
  var top_node = null;
  if (!obj) return;
  if (obj) { // nodeType 11 is a documentFragment
    top_node = obj.svg_reference.firstChild;
    obj.addToParent(this.content);
  }
  return top_node;
}


/**
@ --------------------------------------------------------------
@ Method: refresh
@ Description: Set the transform on the SVG object to the
@              transform contained in the jscript object.
@              This will refresh the display so it reflects
@              any changes made to the jscript.
@
@ Example:
@  Having changed the transform on this object directly in the
@  code - make sure you know what you're doing - you now want
@  to see the change in the contentPane.  Thus:
@
@    contentPane1.someHowChangeTransform (x=20);
@    contentPane1.refresh();
@
@  Will result in the content shifting 20 to the right (assuming
@  the previous value of x was 0).
@ --------------------------------------------------------------
*/
Content.prototype.refresh = function () {
  CGUI.debug("Old: " + this.content.getAttribute ("transform"));
  var new_tr = this.trans.transformString (this);
  CGUI.debug("New: " + new_tr);
  this.content.setAttribute ("transform", new_tr);
  CGUI.debug("Set.");
}

/**
@ --------------------------------------------------------------
@ Method: rotate
@ Description: rotate the content
@ Parameter: angle - how many degrees to rotate the content
@ Parameter: new_zero_x - the new zero coordinate on the x axis
@                         relative to the rotation
@
@ Example:
@   I use this method in the Bioviz genome browser to flip
@   content 180 degrees.  It's probably not generic enough
@   to be here.  Rotating something other than 180 degrees
@   with a random number for the new_zero_x has an interesting
@   effect on scaling and translating the content.
@   I would probably recommend that people not use this function
@   unless they're prepared to fix it.  :)
@ --------------------------------------------------------------
*/
Content.prototype.rotate = function (angle, new_zero_x) {
  CGUI.debug ("rotating content");
  this.new_zero_x = new_zero_x;
  if (angle) {
    this.trans.r += angle;
    if (this.trans.r >= 360) this.trans.r -= 360;
    if (this.trans.r == 0) this.rotated = false;
    else this.rotated = true;
  }

  var new_tr = this.trans.transformString (this);
  this.content.setAttribute ("transform", new_tr);

  if (this.updateCenter) this.updateCenter();
}


/**
@ --------------------------------------------------------------
@ Method: scaleUpH
@ Description: Zoom in on the content in the horizontal direction
@ Parameter: factor - by what factor should I scale the content
@
@ Example:
@ --------------------------------------------------------------
*/
Content.prototype.scaleUpH = function (factor) {
  CGUI.debug ("content scaleUpH");

  if (factor) {
    this.trans.s_x = this.trans.s_x*factor;
  } else {
    this.trans.s_x = this.trans.s_x*1.1;
  }

  var new_tr = this.trans.transformString (this);
  this.content.setAttribute ("transform", new_tr);
}

/**
@ --------------------------------------------------------------
@ Method: scaleUpV
@ Description: Zoom in on the content in the vertical direction
@ Parameter: factor - by what factor should I scale the content
@
@ Example:
@ --------------------------------------------------------------
*/
Content.prototype.scaleUpV = function (factor) {
  CGUI.debug ("content scaleUpV");
  if (factor) {
    this.trans.s_y = this.trans.s_y*factor;
  } else {
    this.trans.s_y = this.trans.s_y*1.1;
  }

  var new_tr = this.trans.transformString (this);
  this.content.setAttribute ("transform", new_tr);
}

/**
@ --------------------------------------------------------------
@ Method: scaleDownH
@ Description: zoom out in the horizontal direction
@ Parameter: factor - by what factor should I scale the content
@
@ Example:
@ --------------------------------------------------------------
*/
Content.prototype.scaleDownH = function (factor) {
  CGUI.debug ("content scaleDownH");
  if (factor) {
    this.trans.s_x = this.trans.s_x/factor;
  } else {
    this.trans.s_x = this.trans.s_x/1.1;
  }

  var new_tr = this.trans.transformString (this);
  this.content.setAttribute ("transform", new_tr);
}

/**
@ --------------------------------------------------------------
@ Method: scaleDownV
@ Description: zoom out in the vertical direction
@ Parameter: factor - by what factor should I scale the content
@
@ Example:
@ --------------------------------------------------------------
*/
Content.prototype.scaleDownV = function (factor) {
  CGUI.debug ("content scaleDownV");
  if (factor) {
    this.trans.s_y = this.trans.s_y/factor;
  } else {
    this.trans.s_y = this.trans.s_y/1.1;
  }

  var new_tr = this.trans.transformString (this);
  this.content.setAttribute ("transform", new_tr);
}

/**
@ --------------------------------------------------------------
@ Method: transRight
@ Description: translate the content to the right which gives
@              the appearance that the content pane is moving left.
@
@ Parameter: amount - the amount to move
@ --------------------------------------------------------------
*/
Content.prototype.transRight = function (amount) {
  CGUI.debug ("content transRight");
  if (amount != 0) {
    this.trans.t_x += (amount) ?  Math.ceil (amount/this.trans.s_x) : 
                                  Math.ceil (1/this.trans.s_x);

    if (this.updateCenter) this.updateCenter();

    var new_tr = this.trans.transformString (this);
    this.content.setAttribute ("transform", new_tr);
  }
}

/**
@ --------------------------------------------------------------
@ Method: transLeft
@ Description: translate the content to the left which gives
@              the appearance that the content pane is moving right.
@
@ Parameter: amount - the amount to move
@ --------------------------------------------------------------
*/
Content.prototype.transLeft = function (amount) {
  CGUI.debug ("content transleft");
  if (amount != 0) {
    this.trans.t_x -= (amount) ?  Math.ceil (amount/this.trans.s_x) : 
                                  Math.ceil(1/this.trans.s_x);

    if (this.updateCenter) this.updateCenter();
  
    var new_tr = this.trans.transformString (this);
    this.content.setAttribute ("transform", new_tr);
  }
}

/**
@ --------------------------------------------------------------
@ Method: setAnchor
@ Description: Set this anchor.
@ Parameter: target - the object to anchor to.
@ Parameter: ax - "left" "center" "right", the horizontal anchor position
@ Parameter: offsetx - the offset ('+':right '-':left) from the ax position
@ Parameter: ay - "top" "center" "bottom", the vertical anchor position
@ Parameter: offsety - the offset ('+':down '-':up) from the ay position
@
@ Example:
@ Continuing the setAnchor (frame, "left", "15", "bottom", "-15")
@ would anchor the object(the button)
@ Added - Chris Peto
@ --------------------------------------------------------------
*/

Content.prototype.transHTo = function (newx) {
  CGUI.debug ("content transHTo");
  this.trans.t_x = (newx) ?  Math.ceil (newx) : Math.ceil(1);

  var new_tr = this.trans.transformString (this);
  this.content.setAttribute ("transform", new_tr);

  if (this.updateCenter) this.updateCenter();
}


/**
@ --------------------------------------------------------------
@ Method: setAnchor
@ Description: Set this anchor.
@ Parameter: target - the object to anchor to.
@ Parameter: ax - "left" "center" "right", the horizontal anchor position
@ Parameter: offsetx - the offset ('+':right '-':left) from the ax position
@ Parameter: ay - "top" "center" "bottom", the vertical anchor position
@ Parameter: offsety - the offset ('+':down '-':up) from the ay position
@
@ Example:
@ Continuing the setAnchor (frame, "left", "15", "bottom", "-15")
@ would anchor the object(the button)
@ Added - Chris Peto
@ --------------------------------------------------------------
*/

Content.prototype.transVTo = function (newy) {
  CGUI.debug ("content transVTo");
  this.trans.t_y = (newy) ?  Math.ceil (newy) : Math.ceil (1);

  var new_tr = this.trans.transformString (this);
  this.content.setAttribute ("transform", new_tr);
  if (this.updateCenter) this.updateCenter();
}
/**
@ --------------------------------------------------------------
@ Method: transUp
@ Description: translate the content up which gives the
@              appearance that the content pane is moving down.
@
@ Parameter: amount - the amount to move
@ --------------------------------------------------------------
*/
Content.prototype.transUp = function (amount) {
  CGUI.debug ("content transUp");

  if (amount != 0) {
    this.trans.t_y -= (amount) ?
                      Math.ceil (amount/this.trans.s_y) :
                      Math.ceil (1/this.trans.s_y);

    var new_tr = this.trans.transformString (this);
    this.content.setAttribute ("transform", new_tr);
    if (this.updateCenter) this.updateCenter();
  } 
}


/**
@ --------------------------------------------------------------
@ Method: transDown
@ Description: translate the content down which gives the
@              appearance that the content pane is moving up.
@
@ Parameter: amount - the amount to move
@ --------------------------------------------------------------
*/
Content.prototype.transDown = function (amount) {
  CGUI.debug ("content transdown");

  if (amount != 0) {
    this.trans.t_y += (amount) ?
                      Math.ceil (amount/this.trans.s_y) :
                      Math.ceil (1/this.trans.s_y);

    var new_tr = this.trans.transformString (this);
    this.content.setAttribute ("transform", new_tr);
    if (this.updateCenter) this.updateCenter();
  } 
}

/**
@ --------------------------------------------------------------
@ Method: relativeContentX
@ Description: Give the mouse coordinate relative to the 
@              Content.  Note that this is distinct from the
@              relativeMouseX which doesn't take into account
@              the scale and translate applied to the Content.
@ Parameter: 
@ --------------------------------------------------------------
*/
Content.prototype.relativeContentX = function () {
  var left_edge = this.getLeftEdge();

  var xadjust = left_edge;
  xadjust += this.trans.t_x;

  var center_x = this.getWidth()/2;
  var distance_from_left = MouseTracker.x - left_edge;
  var distance_from_center_x = center_x - distance_from_left;
  var adj_dist_from_center_x = (center_x-distance_from_left)/this.trans.s_x;
  xadjust += adj_dist_from_center_x - distance_from_center_x;


  return MouseTracker.x-xadjust;
}

/**
@ --------------------------------------------------------------
@ Method: relativeContentY
@ Description: Give the mouse coordinate relative to the 
@              Content.  Note that this is distinct from the
@              relativeMouseY which doesn't take into account
@              the scale and translate applied to the Content.
@ Parameter: 
@ --------------------------------------------------------------
*/
Content.prototype.relativeContentY = function () {
  var top_edge = this.getTopEdge();

  var yadjust = top_edge;
  yadjust += this.trans.t_y;

  var center_y = this.getHeight()/2;
  var distance_from_top = MouseTracker.y - top_edge;
  var distance_from_center_y = center_y - distance_from_top;
  var adj_dist_from_center_y = (center_y-distance_from_top)/this.trans.s_y;
  yadjust += adj_dist_from_center_y - distance_from_center_y;

  return MouseTracker.y-yadjust;
  
}


/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/

/**
@ --------------------------------------------------------------
@ Object: Anchor
@ Desc:
@ The 'Anchor' class is used to anchor one object to some position
@ inside another object.  It seemed like a simple alternative to 
@ implementing a layout manager.  Though it is more limited... 
@
@ Notes: 
@ Each Class that inherits from CGUI contains an anchor object.
@
@ Example: 
@ For instance, you can add a button to a frame then anchor the 
@ button to the bottom left corner of the frame.
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Anchor
@ Parameter: parent - anchor need to know about it's parent
@ --------------------------------------------------------------
*/
function Anchor (parent) {
  if (parent) {
    CGUI.debug ("Created anchor, parent:" + parent);
    // initially this anchor is not set, so it is anchored to nothing
    this.anchorTo = '';
    this.parent = parent;
    return this;
  }
}

/**
@ --------------------------------------------------------------
@ Method: toString
@ Description: Produce a string representation of this anchor.
@              useful for debugging.
@ --------------------------------------------------------------
*/
Anchor.prototype.toString = function () {
  var str = "Anchor:\n";
      str += "anchorto: " + this.anchorTo + "\n";
      str += "parent: " + this.parent + "\n";
      str += "ax: " + this.ax + "\n";
      str += "offsetx: " + this.offsetx + "\n";
      str += "ay: " + this.ay + "\n";
      str += "offsety: " + this.offsety + " \n";
  return str;
}
            
/**
@ --------------------------------------------------------------
@ Method: isSet
@ Description: Check to see if this anchor has been set
@ --------------------------------------------------------------
*/
Anchor.prototype.isSet = function () {
  return (this.parent) ? 1 : 0;
}

/**
@ --------------------------------------------------------------
@ Method: setAnchor
@ Description: Set this anchor.
@ Parameter: target - the object to anchor to.  
@ Parameter: ax - "left" "center" "right", the horizontal anchor position
@ Parameter: offsetx - the offset ('+':right '-':left) from the ax position
@ Parameter: ay - "top" "center" "bottom", the vertical anchor position
@ Parameter: offsety - the offset ('+':down '-':up) from the ay position
@
@ Example: 
@ Continuing the setAnchor (frame, "left", "15", "bottom", "-15")
@ would anchor the object(the button)
@ --------------------------------------------------------------
*/
Anchor.prototype.setAnchor = function (target, ax, offsetx, ay, offsety) {
  CGUI.debug ("set anchor");
  this.ax = ax;
  this.offsetx = offsetx;
  this.ay = ay;
  this.offsety = offsety;
  this.anchorTo = target;
  this.adjustAnchor();
}

/**
@ --------------------------------------------------------------
@ Method: adjustAnchorX
@ Description: Adjust the X position of this anchor
@ Parameter: ax - "left" "center" "right", the horizontal anchor position
@ Parameter: offsetx - the offset ('+':right '-':left) from the ax position
@
@ Example: 
@ Continuing from setAnchor: adjustAnchor ("right", "-15")
@ would move the object to the right side, where it was on the left
@ and it would now be offset 15 to the left of the right edge.  
@ --------------------------------------------------------------
*/
Anchor.prototype.adjustAnchorX = function (ax, offsetx) {
  this.ax = ax;
  this.offsetx = offsetx;
  this.adjustAnchor();
}

/**
@ --------------------------------------------------------------
@ Method: adjustAnchorY
@ Description: adjust the Y position of this anchor
@ Parameter: ay - "top" "center" "bottom", the vertical anchor position
@ Parameter: offsety - the offset ('+':down '-':up) from the ay position
@
@ Example: 
@ Continuing from setAnchor: adjustAnchor ("top", "15")
@ would move the object to the top edge, where it was on the bottom
@ and it would now be offset 15 down from the top edge.
@ --------------------------------------------------------------
*/
Anchor.prototype.adjustAnchorY = function (ay, offsety) {
  this.ay = ay;
  this.offsety = offsety;
  this.adjustAnchor();
}

/**
@ --------------------------------------------------------------
@ Method: adjustAnchor
@ Description: adjust this anchor.
@
@ Example:
@ The adjustAnchor method goes through and resets the x and y 
@ coordinates of the anchors parent so that it is now in the right
@ position relative to the anchorTo object.
@  - so it adjusts the buttons x,y values so that the button is 
@    now in the appropriate position relative to the Frame.
@ --------------------------------------------------------------
*/
Anchor.prototype.adjustAnchor = function () {
  CGUI.debug ("adjusting anchor");
  var x_pos = 0; // the calculated x_position for the object
  var y_pos = 0; // the calculated y_position for the object
  var t_svgref;  // the target or anchorTo objects svg_reference
  var p_svgref;  // the parent objects svg_reference
  var t_width;   // the target width
  var p_width;   // the parent width
  var t_height;  // the target height
  var p_height;  // the parent height

  // put the anchor's member variables into variables as it 
  // simplifies the code a little I think...
  var ax = this.ax;
  var ay = this.ay;
  var offsetx = this.offsetx;
  var offsety = this.offsety;
  var parent = this.parent;
  var target = this.anchorTo;

  if (target.CGUI_TYPE) {
    CGUI.debug ("CGUI_TYPE: " + target.CGUI_TYPE);
    t_svgref = target.svg_reference;
  } else {
    CGUI.debug ("CGUI_TYPE: Non-CGUI_TYPE");
    t_svgref = target;
  }

  t_width = t_svgref.getAttribute("width");
  t_height = t_svgref.getAttribute("height");

  CGUI.debug ("t_widht/t_height" + t_width+"/"+t_height);

  if (t_height.substring(t_height.length-1) == "%") {
    var percent = 1;
    percent *= Number(t_height.substring(0, t_height.length-1))/100; 
    var t_parent = t_svgref.getParentNode();
    t_height = t_parent.getAttribute("height");
    while (t_height.substring(t_height.length-1) == "%") {
      percent *= Number(t_height.substring(0, t_height-1))/100;
      t_parent = t_parent.getParentNode();
      t_height = t_parent.getAttribute("height");
    }
    t_height = t_height * percent;
    CGUI.debug ("relative height: " + t_height);
  }
  if (t_width.substring(t_width.length-1) == "%" ) {
    var percent = 1;
    percent *= Number(t_width.substring(0, t_width.length-1))/100; 
    var t_parent = t_svgref.getParentNode();
    if (t_parent) {
      t_width = t_parent.getAttribute("width");
      while (t_width.substring(t_width.length-1) == "%") {
        percent *= Number(t_width.substring(0, t_width-1))/100;
        t_parent = t_parent.getParentNode();
        t_width = t_parent.getAttribute("width");
      }
      t_width = t_width * percent;
    } else {
      t_width = percent;
    }
    CGUI.debug ("relative widht: " + t_width);
  }

  if (parent.CGUI_TYPE) {
    CGUI.debug ("Parent CGUI_TYPE: " + parent.CGUI_TYPE);
    p_svgref = parent.svg_reference;
  } else {
    CGUI.debug ("Parent CGUI_TYPE: Non-CGUI_TYPE");
    p_svgref = parent;
  }

  p_width = p_svgref.getAttribute("width");
  p_height = p_svgref.getAttribute("height");
  
  CGUI.debug ("p_width/p_height: " + p_width + "/" + p_height);

  if (p_width.substring(p_width.length-1) == "%") {
    p_width = Number (t_width)*
              (Number (p_width.substring(0, p_width.length-1))/100); 
    CGUI.debug ("relative p_width: " + p_width);
  }
  if (p_height.substring(p_height.length-1) == "%") {
    p_height = Number (t_height) * 
               (Number (p_height.substring(0, p_height.length-1))/100);
    CGUI.debug ("relative p_height: " + p_height);
  }
     
  switch (ax) {
  case "left"  : x_pos = Number (0) + Number (offsetx);
                 break;
  case "center": x_pos = Number (0) + 
                         Number (t_width)/2 - 
                         Number (p_width)/2 +
                         Number (offsetx);
                 break;
  case "right" : x_pos = Number (0) + 
                         Number (t_width) -
                         Number (p_width) + 
                         Number (offsetx);
                 break;
  case "none"  : x_pos = Number (p_svgref.getAttribute (x));
                 break;
  default      : alert ("Unrecognized value for ax");
                 break;
  } 
  CGUI.debug ("x_pos: " + x_pos);
  switch (ay) {
  case "top"   : y_pos = Number (0) + Number (offsety);
                 break;
  case "center": y_pos = Number (0) + 
                         Number (t_height)/2 - 
                         Number (p_height)/2 +
                         Number (offsety);
                 break;
  case "bottom" : y_pos = Number (0) + 
                         Number (t_height) -
                         Number (p_height) + 
                         Number (offsety);
                  break;
  case "none"   : y_pos = Number (p_svgref.getAttribute ("y"));
                  break;
  default       : alert ("Unrecognized value for ay");
                  break;
  } 
  CGUI.debug ("y_pos: " + y_pos);
  if (parent.CGUI_TYPE) {
    parent.x = x_pos;
    parent.y = y_pos;
  } 

  p_svgref.setAttribute ("x", x_pos);
  p_svgref.setAttribute ("y", y_pos);
}
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/

/**
@ --------------------------------------------------------------
@ Object: Button
@ Desc:
@ Example: 
@ Inherits from: CGUI
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Button
@ Parameter: width - width of button
@            height - height of button
@            text - text to display in button
@            event - function to call on mousedown event
@ --------------------------------------------------------------
*/
function Button (width, height, text, event) {
  if (width || height || text || event) {
    CGUI.debug ("Creating Button");
    this.text = text;
    this.base = CGUI;
    this.base (0, 0, width, height);
    this.event = event;
    this.enabled = true;
    this.toggle = false;
    this.chec = 0;
    if (this.CGUI_TYPE == "BUTTON") {
        this.draw();
    }
  }
}
Button.prototype = new CGUI;
Button.prototype.CGUI_TYPE = "BUTTON";

Button.prototype.draw = function () {
    var btn = document.createElement ("g");
  
    var rect = document.createElement ("rect");
    rect.setAttribute ("class", "buttonface");
    rect.setAttribute ("stroke", "black");
    rect.setAttribute ("stroke-width", "2pt");
    rect.setAttribute ("fill", "grey");
    rect.setAttribute ("width", "100%");
    rect.setAttribute ("height", "100%");
    this.addEventListeners(rect);
    this.face = rect;

    var txt = document.createElement ("text");
    txt.appendChild (document.createTextNode (this.text));
    txt.setAttribute ("class", "buttontext");
    txt.setAttribute ("text-anchor", "middle");
    txt.setAttribute ("pointer-events", "none");
    txt.setAttribute ("fill", "white");
    txt.setAttribute ("y", 12);
    txt.setAttribute ("x", this.width/2);

    btn.appendChild (this.face);
    btn.appendChild (txt);

    this.svg_reference.appendChild (btn);
    return this;
};

/**
@ --------------------------------------------------------------
@ Method: setText
@ Description: Set the text for this Button
@ Parameter: text - the text to display on the button
@ --------------------------------------------------------------
*/

Button.prototype.setText = function (text) {
  this.text = text;
  this.svg_reference.firstChild.firstChild.nextSibling.firstChild.setData (text);
}

/**
@ --------------------------------------------------------------
@ Method: clearText
@ Description: Clear the text from a Button
@ --------------------------------------------------------------
*/

Button.prototype.clearText = function () {
  this.setText("");
}

/**
@ --------------------------------------------------------------
@ Method: hideText
@ Description: Hide the text associated with this button
@ --------------------------------------------------------------
*/

Button.prototype.hideText = function () {
  this.svg_reference.firstChild.firstChild.nextSibling.setAttribute ("visibility", "hidden");
}

/**
@ --------------------------------------------------------------
@ Method: toString
@ Description: Display the text associated with this button.  
@              If there is no text associated it returns 
@              "BUTTON"
@ --------------------------------------------------------------
*/

Button.prototype.toString = function () {
  return (this.text) ? this.text : this.CGUI_TYPE;
}

/**
@ --------------------------------------------------------------
@ Method: setEvent
@ Description: event that happens when this button is clicked
@ Parameter: event - the event
@            may be a function, an object or a string
@ --------------------------------------------------------------
*/

Button.prototype.setEvent = function (event) {
  this.event = event;
}

/**
@ --------------------------------------------------------------
@ Method: getEvent
@ Description: return event that happens when this button is clicked
@ Parameter: event - the event
@            may be a function, an object or a string
@ --------------------------------------------------------------
*/

Button.prototype.getEvent = function () {
  return this.event;
}

/**
@ --------------------------------------------------------------
@ Method: toggleState
@ Description: toggleState is called when a mousedown event
@              occurs on this a "toggle" button
@ Parameter: evt - the event
@ Note: This function stops the event propagation so that
@       nothing underneath receives it
@ --------------------------------------------------------------
*/

Button.prototype.toggleState = function (evt) {
  if (this.selected) {
    this.deselect();
  } else {
    this.select();
  }
}

/**
@ --------------------------------------------------------------
@ Method: mousedown
@ Description: mousedown function is called when a mousedown event
@              occurs on this object
@ Parameter: evt - the event
@ Note: This function stops the event propagation so that
@       nothing underneath receives it
@ --------------------------------------------------------------
*/

Button.prototype.mousedown = function (evt) {
  if (this.enabled) {
    evt.stopPropagation();
    if (this.toggle) {
      this.toggleState();
    } else {
      this.select();
    }

    if (!this.event) return;
    switch ( typeof(this.event) ) {
      case "function": this.event(evt);             break;
      case "object":   this.event.handleEvent(evt); break;
      case "string":   eval(this.event);            break;
      default: alert ("unrecognized listener type"); break;
    }; 
  } else {
    alert ("Button Disabled");
  }
}

/**
@ --------------------------------------------------------------
@ Method: enableToggle
@ Description: makes the button element toggable, much
@ like a switch
@ --------------------------------------------------------------
*/
Button.prototype.enableToggle = function()
{
    this.toggle=true;
}


/**
@ --------------------------------------------------------------
@ Method: mouseup
@ Description: mouseup function is called when a mouseup event
@              occurs on this object
@ Parameter: evt - the event
@ --------------------------------------------------------------
*/

Button.prototype.mouseup = function (evt) {
  if (this.enabled && (!this.toggle || (this.toggle && !this.selected))) {
    this.face.setAttribute ("class", "buttonfacehover");
    this.deselect();
  }
}

/**
@ --------------------------------------------------------------
@ Method: mouseout
@ Description: mouseout function is called when a mouseout event
@              occurs on this object
@ Parameter: evt - the event
@ --------------------------------------------------------------
*/

Button.prototype.mouseout = function (evt) {
  if (this.enabled && (!this.toggle || (this.toggle && !this.selected)))  {
    this.face.setAttribute ("class", "buttonface");
    this.deselect();
  }
}

/**
@ --------------------------------------------------------------
@ Method: select
@ Description: Select this button
@              Changes the appearance to the "selected" state
@ --------------------------------------------------------------
*/

Button.prototype.select = function () {
  this.selected = true;
  this.face.setAttribute ("class", "buttonfacedep");
}

/**
@ --------------------------------------------------------------
@ Method: deselect
@ Description: Deselect this button
@ --------------------------------------------------------------
*/

Button.prototype.deselect = function (evt) {
  if (this.selected) {
    this.selected = false;
  }
}

/**
@ --------------------------------------------------------------
@ Method: disable
@ Description: disable this button
@              Changes the appearance to the "disable" state
@              Removes the mousedown event
@              Sets enabled to false
@ --------------------------------------------------------------
*/

Button.prototype.disable = function () {
  if (this.enabled == true)  {
    this.enabled = false;
    this.face.setAttribute ("class", "buttonfacedisable");
  }
}

/**
@ --------------------------------------------------------------
@ Method: enable
@ Description: enable this button
@              Changes the appearance to the enabled state
@              Restores the mousedown event
@              Sets enabled to true
@ --------------------------------------------------------------
*/
Button.prototype.enable = function () {
  if (!this.enabled) {
    this.enabled = true;
    if (this.selected) {
      this.face.setAttribute ("class", "buttonfacedep");
    } else {
      this.face.setAttribute ("class", "buttonface");
    }
  }
}


/**
@ --------------------------------------------------------------
@ Method: mouseover
@ Description: mouseover function is called when a mouseover event
@              occurs on this object
@ Parameter: evt - the event 
@ --------------------------------------------------------------
*/

Button.prototype.mouseover = function (evt) {
  if (this.enabled && !this.selected) {
      this.face.setAttribute ("class", "buttonfacehover");
  }
}
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/
     

/**
@ --------------------------------------------------------------
@ Object: Window
@ Desc:
@ Example:
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Window
@ Parameter: x - x coordinate relative to the parent
@            y - y coordinate relative to the parent
@        width - width of the Window
@       height - height of the Window
@ --------------------------------------------------------------
*/
function Window (x, y, width, height, ctrl_opts) {
  if (arguments.length > 0) {
    CGUI.debug ("creating window");
    this.base = Content;
    this.base (this.context, new Transform(), x, y, width, height);

    this.reverse_orientation = false;
    
    this.controls = this.createControls (ctrl_opts);
    this.context = svgDocument;
    this.exists = true;
   
    this.addEventListeners(this.svg_reference);
  }
}
Window.prototype = new Content;
Window.prototype.window_superclass = Content.prototype;
Window.prototype.CGUI_TYPE = "WINDOW";


/**
@ --------------------------------------------------------------
@ Method: toString
@ Description: Return a string representation of this Window
@ --------------------------------------------------------------
*/
Window.prototype.toString = function () {
   return "Window-------------\n" + 
          "x: " + this.x + " " +
          "y: " + this.y + " " +
          "Width: " + this.width + " " +
          "Height: " + this.height + " " +
          "\n------------Window\n"; 
}


/**
@ --------------------------------------------------------------
@ Method: updateCenter
@ Description: update the coordinate box marking the center
@              of this window.
@ 
@ Notes: if the content has been rotated, then the coordinate needs 
@        to be calculated relative to the new center coordinate.
@ --------------------------------------------------------------
*/
Window.prototype.updateCenter = function() {

  if (this.rotated) {
    this.center_coordx = this.trans.t_x + this.new_zero_x -
                            this.width/2;
    this.center_coordy = this.trans.t_y - this.height/2;
  } else {
    this.center_coordx = -1 * this.trans.t_x + this.width/2;
    this.center_coordy = -1 * this.trans.t_y + this.height/2;
  }
  if (this.controls && this.controls.centerbox_x)
    this.controls.centerbox_x.setMessage ("X: " + this.center_coordx);

  if (this.controls && this.controls.centerbox_y)
    this.controls.centerbox_y.setMessage ("Y: " + this.center_coordy);

} 


/**
@ --------------------------------------------------------------
@ Method: createControls
@ Description: create the controls used in this window
@ Parameter: ctrl_opts - what controls are needed
@ --------------------------------------------------------------
*/
Window.prototype.createControls = function (ctrl_opts) {
  this.controls = new ControlBox (this, this, ctrl_opts);

  this.controls.top_right = document.createElement ("g");
    if (this.controls.border_right)
      this.controls.border_right.addToParent(this.controls.top_right);
    if (this.controls.up)
      this.controls.up.addToParent(this.controls.top_right);
    if (this.controls.vslider) 
      this.controls.vslider.addToParent(this.controls.top_right);
  this.svg_reference.appendChild(this.controls.top_right);

  this.controls.btm_left = document.createElement ("g");
    if (this.controls.border_bottom) 
      this.controls.border_bottom.addToParent(this.controls.btm_left);
    if (this.controls.left)
      this.controls.left.addToParent(this.controls.btm_left);
    if (this.controls.hslider) 
      this.controls.hslider.addToParent(this.controls.btm_left);
  this.svg_reference.appendChild(this.controls.btm_left);

  this.controls.btm_right = document.createElement ("g");
    if (this.controls.zoomin_h) 
      this.controls.zoomin_h.addToParent(this.controls.btm_right);
    if (this.controls.zoomout_h) 
      this.controls.zoomout_h.addToParent(this.controls.btm_right);
    if (this.controls.zoomin_v) 
      this.controls.zoomin_v.addToParent(this.controls.btm_right);
    if (this.controls.zoomout_v) 
     this.controls.zoomout_v.addToParent(this.controls.btm_right);
    if (this.controls.lock) 
      this.controls.lock.addToParent(this.controls.btm_right);
    if (this.controls.down) 
     this.controls.down.addToParent(this.controls.btm_right);
    if (this.controls.right) 
      this.controls.right.addToParent(this.controls.btm_right);
  this.svg_reference.appendChild(this.controls.btm_right);


  return this.controls;
}


/**
@ --------------------------------------------------------------
@ Method: resetOrientation
@ Description: reset the orientation of this content pane
@ --------------------------------------------------------------
*/
Window.prototype.resetOrientation = function () {
  this.reverse_orientation = false;
  if (this.controls.reverse) this.reverseControls();
  if (this.rotated) this.rotated = false;
}


/**
@ --------------------------------------------------------------
@ Method: resetContentTransform
@ Description: reset the content to it's original unscaled/
@              untranslated state.
@ --------------------------------------------------------------
*/
Window.prototype.resetContentTransform = function () {
  this.trans.reset();
}


/**
@ --------------------------------------------------------------
@ Method: resetContent
@ Description: reset the window to a nice blank content pane.
@        - remove all content
@        - reset the content transform
@        - reset the orientation
@ --------------------------------------------------------------
*/
Window.prototype.resetContent = function () {
  this.removeContent();
  this.resetContentTransform();
  this.resetOrientation();
}


/**
@ --------------------------------------------------------------
@ Method: removeContent
@ Description: remove the content from this window
@ --------------------------------------------------------------
*/
Window.prototype.removeContent = function () {
  var children = this.content.getChildNodes();
  while (children.length) {
    this.content.removeChild (children.item(0));
  }
}


/**
@ --------------------------------------------------------------
@ Method: rotateContent
@ Description: rotate the content
@ Parameter: Used to adjust the x coordinate when the content
@            has been rotated. 
@
@ Notes:
@ Ideally, this should allow you to rotate the content to 
@ whatever angle you want.  It doesn't.  At present this is
@ a Bioviz specific kludge.  You can rotate 180 degrees only.
@ Anything else results in odd - though amusing - behaviour.
@ Some day, someone should fix it and make it a generic 
@ rotation...
@ --------------------------------------------------------------
*/
Window.prototype.rotateContent = function (new_zero_x,con_height) {

  if (this.reverse_orientation) {
    
    if (!new_zero_x) new_zero_x = this.new_zero_x;
    this.reverseControls(new_zero_x,con_height);
    this.rotate(180, new_zero_x);
  } else {
    alert ("Sorry, you're only allowed to rotate BACs with a (-) orientation");
  }
}


/**
@ --------------------------------------------------------------
@ Method: reverseControls
@ Description: reverse the controls on this window
@   up <-> down, left <-> right
@ Used with the rotate 180 for bioviz
@ --------------------------------------------------------------
*/
Window.prototype.reverseControls = function (new_zero_x,con_height) {
  this.controls.reverseControls(new_zero_x,con_height);
}


/**
@ --------------------------------------------------------------
@ Method: close
@ Description: close this window
@ --------------------------------------------------------------
*/
Window.prototype.close = function () {
  CGUI.debug ("window close");
  if (!this.exists) return;

  this.exists = false;
  this.close();
  if (this.parent && this.parent.svg_reference) {
    this.parent.svg_reference.removeChild (this.svg_reference);
    this.svg_reference = null;
  }
}
/**
@ --------------------------------------------------------------
@ Method: crossMoveHoriz
@ Description: this function gets the window's left edge and
@ the content width, using these variables, calls the function
@ crossMoveH.  crossMoveH takes care of crosshair movement on
@ a horizontal axis 
@ --------------------------------------------------------------
*/
Window.prototype.crossMoveHoriz = function () {
  var left_edge = this.getLeftEdge();
  var top_edge = this.getTopEdge();
  var content_height=this.getHeight();
  this.controls.crossMoveH(MouseTracker.y-top_edge, MouseTracker.x-left_edge, content_height);

  
}

/**
@ --------------------------------------------------------------
@ Method: crossMoveVert
@ Description: this function gets the window's top edge and
@ the content height, using these variables, calls the function
@ crossMoveV.  crossMoveV takes care of crosshair movement on
@ a vertical axis 


@ Parameter: 
@ --------------------------------------------------------------
*/
Window.prototype.crossMoveVert = function () {
  var top_edge = this.getTopEdge();
  var left_edge = this.getLeftEdge();
  var content_width=this.getWidth();
  this.controls.crossMoveV(MouseTracker.y-top_edge,MouseTracker.x-left_edge, content_width);

}

/**
@ --------------------------------------------------------------
@ Method: mouseover
@ Description: called by mouseover event, handles resetting of the 
@ crosshairs.
@ Parameters:  evt - incoming event
@ --------------------------------------------------------------
*/
Window.prototype.mouseover = function(evt) {
  var x=this.controls.checkResetDisable();
  if (x==0){
    var test = CGUI.getJSRef(evt.target);
    var content_width = this.getWidth();
    var content_height = this.getHeight();

    if (test != this.toString()){
      this.controls.cross_resetH(content_height);
      this.controls.cross_resetV(content_width);
    }
  }
      
};

/**
@ --------------------------------------------------------------
@ Method: setHeight
@ Description: Set the height of this window.
@              When the height changes we need to update the 
@              windows controls etc...
@ Parameters:  height - the new height
@ --------------------------------------------------------------
*/
Window.prototype.setHeight = function (height) {
  var adjust = (this.height - height) / 2;

  this.sup_setHeight = this.window_superclass.setHeight;
  this.sup_setHeight (height);

 
  if (!this.hidden()) {
  this.controls.btm_right.setAttribute ("transform",
              "translate("+(this.width - this.owidth) + "," +
                           (this.height - this.oheight)+")");
  this.controls.btm_left.setAttribute ("transform",
              "translate(0," + (this.height - this.oheight) + ")");

  if(this.controls.vslider)
    this.controls.vslider.Resize(this.height-this.controls.vslider.offset, 15);

  if (this.controls.border_right)
    this.controls.border_right.setHeight(this.height);

  if (this.controls.border_left)
    this.controls.border_left.setHeight(this.height);

  this.controls.cross_resetH(this.height);
  }

  //adjust the vertical slider min and max appropriatly to the resize
  //this does not have to be done with the hslider
  //presumably has to do with content centering
  if (this.controls.vslider != null){
    this.controls.vslider.max = this.controls.vslider.max + adjust;
    this.controls.vslider.min = this.controls.vslider.min + adjust;
    this.controls.vslider.setValue(this.height/2);
  }
  if (this.updateCenter) this.updateCenter();
}

/**
@ --------------------------------------------------------------
@ Method: setWidth
@ Description: Set the width of this window.
@              When the width changes we need to update the 
@              windows controls etc...
@ Parameters:  width - the new width 
@ --------------------------------------------------------------
*/
Window.prototype.setWidth = function (width) {
  this.sup_setWidth = this.window_superclass.setWidth;
  this.sup_setWidth (width);

  if (!this.hidden()) {
  this.controls.btm_right.setAttribute ("transform",
              "translate("+(this.width - this.owidth)+"," +
                           (this.height - this.oheight)+")");
  this.controls.top_right.setAttribute ("transform",
              "translate("+(this.width - this.owidth) + ", 0)");

  if(this.controls.hslider)
    this.controls.hslider.Resize(this.width-this.controls.hslider.offset, 15);

  if (this.controls.border_top)
    this.controls.border_top.setWidth(this.width);

  if (this.controls.border_bottom)
    this.controls.border_bottom.setWidth(this.width);

  this.controls.cross_resetV(this.width);
  }

  if (this.updateCenter) this.updateCenter(); 
}
/* 
    Copyright 2002 Chris Peto 

    This file is part of the CGUI Library 

    CGUI is free software; you can redistribute it and/or 
    modify it under the terms of the GNU Lesser General Public 
    License as published by the Free Software Foundation; either 
    version 2.1 of the License, or (at your option) any later version.  

    CGUI is distributed in the hope that it will be useful, 
    but WITHOUT ANY WARRANTY; without even the implied warranty of 
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
    Lesser General Public License for more details.  

    You should have received a copy of the GNU Lesser General Public 
    License along with this library; if not, write to the Free Software 
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307USA 

    Contact information: 

      Chris Peto 
      Resource Solutions 
      Hoehenweg 34 
      63303 Dreieich, Germany 
      CPeto@resource-solutions.de 
*/ 

/** 
@ -------------------------------------------------------------- 
@ Object: MenuItem 
@ Desc: 
@ Example: 
@ Inherits from: CGUI 
@ Revised by: Chris Lewis
@ -------------------------------------------------------------- 
*/ 

MenuItem.MAIN         = 0; 
MenuItem.ITEM         = 1; 
MenuItem.SEPARATOR    = 2; 
/**
@ -------------------------------------------------------------- 
@ Constructor: MenuItem 
@ Parameter: width - width of button 
@            height - height of button 
@            text - text to display in button 
@            event - function to call on mousedown event 
@ -------------------------------------------------------------- 
*/
function MenuItem (type,width, height, text, event, sub_menuitem_array) { 
  if (width || height || text || event) { 
    CGUI.debug ("Creating MenuItem"); 

    this.base = Button; 
    this.base (width, height, text, event); 
    this.width = width;
    this.height = height;
    this.text = text;
    this.event = event;

    this.sub_menu = sub_menuitem_array; 
    if (this.sub_menu) this.enableToggle();
    this.type = type; 

    if (this.CGUI_TYPE == "MENUITEM") {
        this.draw();
    }
  }
};
MenuItem.prototype = new Button; 
MenuItem.prototype.CGUI_TYPE = "MENUITEM"; 

MenuItem.prototype.draw = function () {
    var btn = document.createElement ("g");
    var rect; 

    if (this.type != MenuItem.SEPARATOR) { 
        rect = document.createElement ("rect"); 
	rect.setAttribute ("class", "menuitem");
        rect.setAttribute ("fill", "grey"); 
        rect.setAttribute ("stroke", "black"); 
        rect.setAttribute ("width", "100%"); 
        rect.setAttribute ("height", "100%"); 
        this.addEventListeners(rect);
        this.face = rect; 

        btn.appendChild (this.face); 

        var txt = document.createElement ("text"); 
	txt.setAttribute ("class", "menuitemtext");
        txt.appendChild (document.createTextNode (this.text)); 
        txt.setAttribute ("pointer-events", "none"); 
        txt.setAttribute ("fill", "white"); 
        txt.setAttribute ("y", 12); 
        if(this.type == MenuItem.MAIN) { 
          txt.setAttribute ("text-anchor", "middle"); 
          txt.setAttribute ("x", this.width/2); 
        } else { 
          txt.setAttribute ("text-anchor", "start"); 
          txt.setAttribute ("x", 20); 
          } 
        btn.appendChild (txt); 
    } else { 
	btn = document.createElement ("g");  
        rect = document.createElement ("rect"); 
	rect.setAttribute ("class", "menuitem");
        rect.setAttribute ("fill", "grey"); 
        rect.setAttribute ("stroke", "black"); 
        rect.setAttribute ("width", "100%"); 
        rect.setAttribute ("height", "100%"); 
        this.face = rect; 
        var seprect = document.createElement ("rect"); 
	rect.setAttribute ("class", "menuitem");
        seprect.setAttribute ("stroke", "black"); 
        seprect.setAttribute ("stroke-width", "1pt"); 
        seprect.setAttribute ("fill", "grey"); 
        seprect.setAttribute ("y", "50%"); 
        seprect.setAttribute ("x", "2"); 
        seprect.setAttribute ("width", this.width-4); 
        seprect.setAttribute ("height", "1"); 
        btn.appendChild (this.face); 
        btn.appendChild (seprect); 
    } 

    this.svg_reference.appendChild (btn); 

    if(this.type != MenuItem.MAIN) 
        this.hideItem();
  
    return this; 
}; 

/** 
@ -------------------------------------------------------------- 
@ Method: hideItem 
@ Description: Hide the text associated with this button 
@ -------------------------------------------------------------- 
*/ 

MenuItem.prototype.hideItem = function () { 
  this.deselect();
  this.svg_reference.firstChild.setAttribute ("visibility", "hidden"); 
  this.face.setAttribute ("class", "menuitem");
} 

/** 
@ -------------------------------------------------------------- 
@ Method: showItem 
@ Description: Hide the text associated with this button 
@ -------------------------------------------------------------- 
*/

MenuItem.prototype.showItem = function () { 
  this.svg_reference.firstChild.setAttribute ("visibility", "inline"); 
  this.face.setAttribute ("class", "menuitem");
} 
 

/** 
@ -------------------------------------------------------------- 
@ Method: select 
@ Description: Select this button 
@              Changes the appearance to the "selected" state 
@ -------------------------------------------------------------- 
*/ 

MenuItem.prototype.select = function () { 
  this.face.setAttribute ("class", "menuitemdep"); 
  this.selected = true; 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: deselect 
@ Description: Deselect this button 
@              Changes the appearance to the "deselect" state 
@ -------------------------------------------------------------- 
*/ 

MenuItem.prototype.deselect = function () { 
  this.face.setAttribute ("class", "menuitem"); 
  this.selected = false; 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: mouseover
@ Description: Extend buttons mouseover event 
@              Now the mouseover will expand existing submenu's 
@ -------------------------------------------------------------- 
*/ 

MenuItem.prototype.mouseover = function (evt) {
  this.supmouseover = Button.prototype.mouseover;
  this.supmouseover(evt);
  if (this.parent.menu_vis && this.sub_menu) { 
    this.select();
    this.showSubTree(evt);
  }
}

/** 
@ -------------------------------------------------------------- 
@ Method: mouseup
@ Description: Extend buttons mouseup event
@              Now the mouseup fires eventmup if it is defined
@ -------------------------------------------------------------- 
*/ 

MenuItem.prototype.mouseup = function (evt) {
  this.supmouseup = Button.prototype.mouseup;
  this.supmouseup(evt);

  if (this.eventmup) {

    switch ( typeof(this.eventmup) ) {
      case "function": this.eventmup(evt);             break;
      case "object":   this.eventmup.handleEvent(evt); break;
      case "string":   eval(this.eventmup);            break;
      default: alert ("unrecognized listener type"); break;
    };
    this.parent.hideAll(true);
  }
} 

/** 
@ -------------------------------------------------------------- 
@ Method: showSubTree
@ Description: Method is called on mouseover and is mousedown 
@              handler.
@              Method opens and closes menu's as appropriate.
@    Behaves as follows:
@      mousedown unselected menuitem
@       - close other menu's if open
@       - select item if not selected
@         - open submenu if present
@       - deselect item if selected 
@         - close submenu if present
@         - fire mouseup event if present
@ -------------------------------------------------------------- 
*/
MenuItem.prototype.showSubTree = function(evt) { 
  if(this.parent.menu_vis == true && this.type == MenuItem.MAIN) { 
    this.parent.hideAll();
    if (!this.selected) return;
  } 
  this.parent.menu_vis = true; 
  this.hideSiblingTree(this,this.my_sibling_arr); 
  if(this.sub_menu) { 
    if (this.selected) {
      for (var i = 0; i < this.sub_menu.length; i++) 
      { 
	this.sub_menu[i].showItem(); 
      } 
    } else {
      for (var i = 0; i < this.sub_menu.length; i++) 
      { 
	this.sub_menu[i].hideItem(); 
      }
    }
  } 
} 

/** 
@ -------------------------------------------------------------- 
@ Method: hideTree
@ Description: Hide menu structure from this point down.
@ -------------------------------------------------------------- 
*/

MenuItem.prototype.hideTree = function(menu) 
{ 
  if(menu) { 
    for (var i = 0; i < menu.length; i++) 
    { 
       menu[i].hideItem(); 
       if(menu[i].sub_menu) this.hideTree(menu[i].sub_menu); 
    } 
  } 

} 

/** 
@ -------------------------------------------------------------- 
@ Method: hideSiblingTree
@ Description: Hide all other menus which are siblings of this
@              menu.
@ -------------------------------------------------------------- 
*/

MenuItem.prototype.hideSiblingTree = function(me,menu) 
{ 
  if(menu) { 
    for (var i = 0; i < menu.length; i++) 
    { 
       if(menu[i] == me) continue; 
       if(menu[i].sub_menu) {
         menu[i].deselect();
         this.hideTree(menu[i].sub_menu); 
       }
    } 
  } 

} 
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/
    
/**
@ --------------------------------------------------------------
@ Object: Bar
@ Desc:  a textBar
@ Example: possibly used as a title bar or a status bar
@ Inherits from: CGUI
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Bar
@ Parameter: text - text to display on the bar if any
@            width - width of bar
@            height - height of bar
@ --------------------------------------------------------------
*/
function Bar (width, height) {
  if (width || height) {
    CGUI.debug ("Creating Bar");
    this.base = CGUI;
    this.base (0, 0, width, height);

    var r = document.createElement ("rect");
      r.setAttribute ("class", "barface");
      r.setAttribute ("width", "100%");
      r.setAttribute ("height", "100%");
      r.setAttribute ("stroke", "black");
      r.setAttribute ("fill", "grey"); 
      r.setAttribute ("stroke-width", '1');
      r.setAttribute ("opacity", '1');
  
    this.svg_reference.appendChild (r);

    return this;
  }
}
Bar.prototype = new CGUI;
Bar.prototype.CGUI_TYPE = "BAR";


/*
    Copyright 2002 Chris Peto

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307USA

    Contact information:

      Chris Peto
      Resource Solutions
      Hoehenweg 34
      63303 Dreieich, Germany
      CPeto@resource-solutions.de
*/
/**
@ --------------------------------------------------------------
@ Object: MenuBar
@ Desc:  a rollup/down menu
@ Inherits from: CGUI
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: MenuBar
@ Parameter: text - text to display on the bar if any
@            width - width of bar
@            height - height of bar
@ --------------------------------------------------------------
*/
function MenuBar (menuitem_array, width, height) {
  if (width || height) {
    CGUI.debug ("Creating MenuBar");
    this.base = Bar;
    this.base (width, height);
    this.menuitem_array = menuitem_array;

    this.svg_reference.setAttributeNS(null, "style","overflow:visible");

    this.menu_vis = false;

    return this;
  }
}

MenuBar.prototype = new Bar;
MenuBar.prototype.CGUI_TYPE = "MENUBAR";
MenuBar.prototype.buildSubMenus = function(parent, xoff,yoff,sub_menu) {

 var button_offset = yoff;
 for (var i = 0; i < sub_menu.length; i++) {
   sub_menu[i].addToParent (parent);
   sub_menu[i].eventmup = sub_menu[i].event;
   sub_menu[i].event=null;
   sub_menu[i].my_sibling_arr = sub_menu;
   //svgDocument.appendChild(sub_menu[i].svg_reference);
   sub_menu[i].setAnchor (parent, "left", xoff, "top", button_offset);
   if(sub_menu[i].sub_menu) {
     sub_menu[i].event    = sub_menu[i].showSubTree;
     sub_menu[i].eventmov = sub_menu[i].showSubTree;
     this.buildSubMenus(parent, xoff+sub_menu[i].getWidth(),button_offset,sub_menu[i].sub_menu);
     };
   button_offset += (Number (sub_menu[i].getHeight()));
  }
}

MenuBar.prototype.buildMenus = function(parent) {

 var button_xoffset = 0;
 var button_yoffset = 16;
 for (var i = 0; i < this.menuitem_array.length; i++) {
   this.menuitem_array[i].addToParent (parent);
   this.menuitem_array[i].setAnchor (parent, "left", button_xoffset, "top", 0);
   this.menuitem_array[i].my_sibling_arr = this.menuitem_array;
   if(this.menuitem_array[i].sub_menu) {
     this.menuitem_array[i].event    = this.menuitem_array[i].showSubTree;
     this.buildSubMenus(parent, button_xoffset,button_yoffset,this.menuitem_array[i].sub_menu);
     };
   button_xoffset += (Number (this.menuitem_array[i].getWidth()));
   //button_yoffset += (Number (this.menuitem_array[i].getHeight()));
  }
}

MenuBar.prototype.hideAll = function(deselect) {

 if(this.menu_vis == false) return;
 this.menu_vis = false;
 for (var i = 0; i < this.menuitem_array.length; i++) {
   if (deselect) this.menuitem_array[i].deselect();
   if(this.menuitem_array[i].sub_menu) {
     this.menuitem_array[i].hideTree(this.menuitem_array[i].sub_menu);
     };
  }
}


/**
@ --------------------------------------------------------------
@ Object: MouseTracker
@ Desc:  Mousetracker is responsible for tracking current and
@        previous mouse coordinates, so that they can be used
@        for moving and resizing windows...
@        As well as for tracking which window is being moved
@        or resized.
@ Example:
@ --------------------------------------------------------------
*/
function MouseTracker (type) {
  CGUI.debug ("Created mousetracker");
  MouseTracker.dragging = null;
  MouseTracker.resizing = null;
  MouseTracker.slider = null;
  if (type == "mousemove") this.handleEvent = MouseTracker.mouseMove;
  else if (type == "mouseup") this.handleEvent = MouseTracker.mouseUp;
  }


/**
@ --------------------------------------------------------------
@ Method: mouseMove
@ Description: tracks mouse movement
@              - if a frame is being dragged or resized, it
@                tells the frame how much to move or resize
@ Parameter: evt - event triggering action
@ --------------------------------------------------------------
*/
MouseTracker.mouseMove = function (evt) {
  //var targett,targetr;

  MouseTracker.x = (Number(evt.clientX) - Number(CGUI.root.currentTranslate.x))/
                      Number(CGUI.root.currentScale);
  MouseTracker.y = (Number(evt.clientY)-Number(CGUI.root.currentTranslate.y))/
                      Number(CGUI.root.currentScale);

  var dx = (Number (evt.clientX) - Number (MouseTracker.last_x)) /
                             Number (CGUI.root.currentScale);
  var dy = (Number (evt.clientY) - Number (MouseTracker.last_y)) /
                             Number (CGUI.root.currentScale);
 
/* temporary addition while working on the relative mouse coordinates */
  CGUI.xm.setMessage("XM: " + MouseTracker.x);
  CGUI.ym.setMessage("YM: " + MouseTracker.y);
  var target = CGUI.getJSRef(evt.target);

  if (target) {
    var x = (target.relativeContentX) ? 
             target.relativeContentX() : 
             target.relativeMouseX();
    if (target.controls != null){ 
      if  (target.controls.centerbox_x != null){
         /*must round the var x due to rescaling and resizing giving too
           many decimal places*/ 
         target.controls.centerbox_x.setMessage("X: "+ Math.round(x));
      }
      target.crossMoveVert();
    }
                     
    var y = (target.relativeContentY) ? 
             target.relativeContentY() : 
             target.relativeMouseY();
    if (target.controls != null){
      if (target.controls.centerbox_y != null){
        target.controls.centerbox_y.setMessage("Y: "+ Math.round(y));
      }
      target.crossMoveHoriz();
    }
  }

  CGUI.xma.setMessage("XMA: " + x);
  CGUI.yma.setMessage("YMA: " + y);
/* end of temporary addition */

 //target = MouseTracker.dragging
  if (MouseTracker.dragging) {
    MouseTracker.dragging.move (dx, dy);
    
  } else if (MouseTracker.resizing) {
    MouseTracker.resizing.setSize (
                 Number (MouseTracker.resizing.width) + Number(dx), 
                 Number (MouseTracker.resizing.height) + Number(dy)); 
}  
  // store new x/y coords for next calc of dx/dy
  MouseTracker.last_x = Number (evt.clientX);
  MouseTracker.last_y = Number (evt.clientY);
}


/**
@ --------------------------------------------------------------
@ Method: mouseUp
@ Description: - User is no longer dragging/resizing a frame
@              - Restore the frame to it's usual state and
@                reset variables related to mouse movement
@
@ Parameter: evt - event triggering the action
@ --------------------------------------------------------------
*/

MouseTracker.mouseUp = function (evt) {
  if (MouseTracker.slider) {
    MouseTracker.slider.active = false;
    MouseTracker.slider = null;
  } else if (MouseTracker.dragging) {
    MouseTracker.dragging.svg_reference.setAttribute ("opacity", 1);
    MouseTracker.dragging.showPanes();
    MouseTracker.dragging = null;
    MouseTracker.last_x = '';
    MouseTracker.last_y = '';
  } else if (MouseTracker.resizing) {
    MouseTracker.resizing.svg_reference.setAttribute ("opacity", 1);
    MouseTracker.resizing.showPanes();

    MouseTracker.resizing.content.setHeight (
       MouseTracker.resizing.height - 
       MouseTracker.resizing.top_offset -
       MouseTracker.resizing.bottom_offset - 2);

    MouseTracker.resizing.content.setWidth (
       MouseTracker.resizing.width-2);

    MouseTracker.resizing = null;
    MouseTracker.last_x = '';
    MouseTracker.last_y = '';
  }
}
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/


/**
@ --------------------------------------------------------------
@ Object: Frame
@ Desc:
@ Example:
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Frame
@ Parameter: x - x coordinate of this Frame relative to parent
@            y - y coordinate of this Frame relateive to parent
@        width - width of this Frame
@       height - height of this Frame
@        title - title of this Frame
@      b_close - boolean - is this Frame closable?
@     arr_btns - array of buttons to display in the button bar
@    ctrl_opts - control_options for this frame - see ControlBox
@      b_satus - boolean - does this frame have a status bar
@    b_content - boolean - does this frame have Content object
@ menu_item_array - array of MenuItems
@ --------------------------------------------------------------
*/
function Frame (x, y, width, height, title,
                b_close, arr_btns, ctrl_opts, b_status, b_content, b_resize, menu_item_array)
{
  if (arguments.length > 0) {

      CGUI.debug ("Creating Frame \n " + title + " " + x + " " + y + " ");
    this.base = CGUI;
    this.base (x,y,width,height);

    this.origx = x;
    this.origy = y;
    this.origw = width;
    this.origh = height;

    this.title = title;

    this.b_close = b_close
    this.arr_btns = arr_btns;
    this.arr_menuitems = menu_item_array;
    this.b_status = b_status;
    this.ctrl_opts = ctrl_opts;
    this.exists = true;
    this.panesHidden = false;
    this.panes = new Array();

    this.open();

    this.top_offset = (this.title?15:0) + (this.arr_menuitems?15:0);
    this.bottom_offset = ((this.b_status)?15:0)+
                         ((this.arr_btns)?15:0);
    var border = this.top_offset + this.bottom_offset;

    if (b_content) {
      this.addContentPane(
           new Window (0, this.top_offset,
                       this.width-2, this.height - border - 2,
                       ctrl_opts
           )
      );
    }

    if (this.arr_menuitems) {
      this.mb = new MenuBar (this.arr_menuitems, "100%", 15);
      this.mb.setAnchor (this, "left", 0, "top", this.title?15:0);
      this.mb.addToParent (this);
      this.mb.buildMenus(this.mb);
      }
    if (b_resize) {
      this.resize_btn_g = document.createElement ("g");
      this.resize_btn = new Button (10, 10, ".",
                   "Frame.doResize(evt, this, 'onmousedown')");
      this.resize_btn.addToParent (this.resize_btn_g);
      this.svg_reference.appendChild (this.resize_btn_g);
      this.resize_btn.setAnchor (this, "right", 0, "bottom", 0);
    }

  }
}
Frame.prototype = new CGUI;
Frame.superclass = CGUI.prototype;
Frame.prototype.CGUI_TYPE = "FRAME";


/**
@ --------------------------------------------------------------
@ Method: hidePanes
@ Description: hide the panes present in this frame
@
@ Notes: this function is used when minimizing a frame rather
@        than resizing the content panes.
@ --------------------------------------------------------------
*/
Frame.prototype.hidePanes = function () {
  if (!this.panesHidden) {
    this.panesHidden = true;
    for (var i = 0; i < this.panes.length; i++)
      this.panes[i].hide(1);
  }
}


/**
@ --------------------------------------------------------------
@ Method: showPanes
@ Description: show the panes present in this pane
@
@ Notes: this function is used when restoring the frame to it's
@        normal size.
@ --------------------------------------------------------------
*/
Frame.prototype.showPanes = function () {
  if (this.panesHidden && !this.rolledUp) {
    this.panesHidden = false;
    for (var i = 0; i < this.panes.length; i++)
      this.panes[i].hide (0);
  }
}


/**
@ --------------------------------------------------------------
@ Method: addToParentContained
@ Description:
@   Add this frame to another SVG object, but have it be
@   contained by a seperate object.  This allows you to
@   pop open a window, add it to the main display, but
@   have it close when the window from which it opened
@   closes.
@
@ Parameter: parent - the object this frame should be added to
@ Parameter: container - the object which contains this frame
@
@ Example:
@   Assume you have a window open containing a diagram and you
@   open another window which describes the diagram.
@
@   The second window could open inside the first (a), or
@   outside the first (b), but in both cases you want it to close
@   when the first window closes.
@
@     +==================+
@     |   +===+          |   +===+
@     |   | a |          |   | b |
@     |   +---+          |   +---+
@     +------------------+
@
@   In case a, the window is added to the first window.  Then
@   when the window closes, a is closed with it.  However,
@   in case b, if the second window is simply added to the
@   SVG document, then it will stay open when the first is
@   closed.  For it to close with the first window, the first
@   window must be set as it's container.
@ --------------------------------------------------------------
*/
Frame.prototype.addToParentContained = function (parent, container) {
  if (container && container.CGUI_TYPE == "FRAME") {
    if (!container.frames) container.frames = new Array ();
    container.frames[container.frames.length] = this;
    this.contained = container;
  } else if (parent.CGUI_TYPE && parent.CGUI_TYPE == "FRAME") {
    parent.frames[parent.frames.length] = this;
    this.contained = parent;
  }
  this.addToParent (parent);
}


/**
@ --------------------------------------------------------------
@ Method: getFrame
@ Description: Given some target, this method will travel up
@              through the DOM until it finds the frame
@              containing the target.
@
@ Parameter: target - the object contained in the frame.
@
@ Example:
@   Having clicked the close button on a frame, you need to
@   travel up the DOM until you find the frame to close it
@   - of course it would also be possible to store a reference
@   to the frame in the button.  It's just an example...
@ --------------------------------------------------------------
*/
Frame.getFrame = function (target) {
  if (target == null) return;
  if (target.CGUI_TYPE) {
    if (target.CGUI_TYPE.match (".*FRAME")) {
      return target;
    } else if (target.parent) {
      return Frame.getFrame (target.parent);
    } else {
      return;
    }
  } else {
    if (target.jsref) {
      return Frame.getFrame (target.jsref);
    } else if (target.parentNode) {
      return Frame.getFrame (target.parentNode);
    } else {
      return;
    }
  }
}


/**
@ --------------------------------------------------------------
@ Method: addPane
@ Description: add and pane to this frame.
@ Parameter: pane - the pane to add
@ Parameter: ax - "left" "center" "right", the horizontal anchor position
@ Parameter: offsetx - the offset ('+':right '-':left) from the ax position
@ Parameter: ay - "top" "center" "bottom", the vertical anchor position
@ Parameter: offsety - the offset ('+':down '-':up) from the ay position
@
@ Description:
@   This method adds a pane to the frame.  Started to use this to
@   allow multi-pane frames, but that's still buggy.
@   Currently their can only be one contentpane in a frame, but
@   I think the heirarchy should be modified to allow multiple
@   panes of any type. This will complicate window resizing, but is
@   I think, a necessary part of the GUI.
@ --------------------------------------------------------------
*/
Frame.prototype.addPane = function (pane, ax, offsetx, ay, offsety) {
  pane.addToParent (this);
  pane.setAnchor (this, ax, offsetx, ay, offsety + this.top_offset);
  this.panes[this.panes.length] = pane;
  return this.panes[this.panes.length-1];
}


/**
@ --------------------------------------------------------------
@ Method: addContentPane
@ Description: add a content pane to this frame
@ Parameter: contentpane - the contentpane to add
@ Notes: At present their can only be one contentpane in a frame.
@ --------------------------------------------------------------
*/
Frame.prototype.addContentPane = function (contentpane) {
  this.content = this.addPane (contentpane, "left", 1, "top", 1);
}

/**
@ --------------------------------------------------------------
@ Method: setBkColor
@ Description: sets background color of content, if there is one
@ Parameter: iColor - any valid svg color format
@ Added - Chris Peto
@ --------------------------------------------------------------
*/
Frame.prototype.setBkColor = function (iColor)
{
  if(this.content) this.content.setBkColor(iColor);
};

/**
@ --------------------------------------------------------------
@ Method: addContentAsText
@ Description: passes the xml string to the contentpane's addAsText
@              method, and attaches eventhandlers to show the id
@              of any SVG element the mouse passes over.
@
@ Parameter: text - the XML encoded SVG string to display as content.
@ --------------------------------------------------------------
*/
Frame.prototype.addContentAsText = function (text) {
  CGUI.debug ("frame addContentAsText");
  var top = this.content.addAsText (text);
  if (top) {
    top.setAttribute( "onmouseover", "Frame.showId(evt)");
    top.setAttribute( "onmousemove", "Frame.showId(evt)");
    top.setAttribute( "onmouseout", "Frame.clearId(evt)");
    top.setAttribute( "onmousedown", "Frame.clearId(evt)");
  }
  return top;
}


/**
@ --------------------------------------------------------------
@ Method: addContentAsText
@ Description: passes the svg node to the contentpane's addAsSVG
@              method, and attaches eventhandlers to show the id
@              of any SVG element the mouse passes over.
@
@ Parameter: text - the XML encoded SVG string to display as content.
@ --------------------------------------------------------------
*/
Frame.prototype.addContentAsSVG = function (node) {
  CGUI.debug ("window addContentAsSBG");
  var top = this.content.addAsSVG (node);
  if (top) {
    top.setAttribute( "onmouseover", "Frame.showId(evt)");
    top.setAttribute( "onmouseout", "Frame.clearId(evt)");
   top.setAttribute( "onmousedown", "Frame.clearId(evt)");

  }
  return top;
}

/**
@ --------------------------------------------------------------
@ Method: addContentAsObject
@ Description: passes the javascript object (obj) to the contentpane's addAsObj
@              method, and attaches eventhandlers to show the id
@              of any SVG element the mouse passes over.
@
@ Parameter: obj - CGUI based object, i.e. Slider
@ --------------------------------------------------------------
*/
Frame.prototype.addContentAsObject = function (obj) {
  CGUI.debug ("window addContentAsObj");
  var top = this.content.addAsObj (obj);
  if (top) {
    top.setAttribute( "onmouseover", "Frame.showId(evt)");
    top.setAttribute( "onmouseout", "Frame.clearId(evt)");
    top.setAttribute( "onmousedown", "Frame.clearId(evt)");
  }
  return top;
}


/**
@ --------------------------------------------------------------
@ Method: removeFrame
@ Description: removes a frame contained by this frame.
@              see discussion in addToParentContained.
@
@ Parameter: frame - the frame to remove
@ --------------------------------------------------------------
*/
Frame.prototype.removeFrame = function (frame) {
  for (var i = 0; i < this.frames.length; i++) {
    if (this.frames[i] == frame) {
      this.frames.splice (i, 1);
      return;
    }
  }
}


/**
@ --------------------------------------------------------------
@ Method: close
@ Description: - close this frame
@              - requires removing it from the parent.
@              - once closed the frame is no longer in the DOM
@ --------------------------------------------------------------
*/
Frame.prototype.close = function () {
  CGUI.debug ("Frame close");
  //if (this.content) this.content.close;
  this.exists = false;
  if (this.parent && this.parent.svg_reference) {
    this.parent.svg_reference.removeChild (this.svg_reference);
  } else {
    CGUI.root.removeChild (this.svg_reference);
  }

  this.svg_reference = null;

  if (this.contained) {
    this.contained.removeFrame (this);
  }

  if (this.frames) {
    for (var i = 0; i < this.frames.length; ) {
      this.frames[i].close();
    }
  }
}


/**
@ --------------------------------------------------------------
@ Method: toString
@ Description: produces a string representation of this frame
@ --------------------------------------------------------------
*/
Frame.prototype.toString = function () {
  return "FRAME----------------" +
         "\nx: " + this.x +
         "\ny: " + this.y +
         "\nwidth: " + this.width +
         "\nheight: " + this.height +
         "\ntitle: " + this.title +
         "\nclose: " + this.b_close +
         "\nwindow: " + this.window +
         "\nanchor: " + this.anchor +
         "\narr_buttons: " + this.arr_btns +
         "\nb_status: " + this.b_status +
         "\nb_controls: " + this.b_controls +
         "\nsvg_reference: " + this.svg_reference.getChildNodes().length +
         "\n-------------------FRAME\n";

}


/**
@ --------------------------------------------------------------
@ Method: refresh
@ Description: refresh this frame by setting the svg attributes
@              to the values in the javascript.  Primarily used
@              when resizing the frame.
@ --------------------------------------------------------------
*/
Frame.prototype.refresh = function () {
  CGUI.debug ("refreshing display");
  this.svg_reference.setAttribute ("x", this.x);
  this.svg_reference.setAttribute ("y", this.y);
  this.svg_reference.setAttribute ("width", this.width);
  this.svg_reference.setAttribute ("height", this.height);
}


/**
@ --------------------------------------------------------------
@ Method: setSize
@ Description: Set the size of the frame
@ Parameter: width - new width
@ Parameter: height - new height
@
@ Notes: adjusts the size of the contentpane, which will scale
@        the content to reflect the new size.
@        adjusts the buttonbar, titlebar, and statusbar if present.
@        doesn't adjust any additional panes... (see comment on
@        problems with addPane)
@Quirks:  I believe that a problem with frame resizing and slider 
@         thumb dissapearence is here.  Everytime the window is resized
@	  in negative orientation the thumb disapears (vertical axis)
@ --------------------------------------------------------------
*/
Frame.prototype.setSize = function (width, height) {
  CGUI.debug ("Setting Size");
  this.width = width?width:this.width;
  this.height = height?height:this.height;
  CGUI.debug ("  Frame: " + this.width + " : " + this.height);
  this.svg_reference.setAttribute ("width", this.width);
  this.svg_reference.setAttribute ("height", this.height);

  if (this.content) {
    CGUI.debug ("  Content: " + (this.width - 2) +
                " : " + (this.height - this.top_offset - this.bottom_offset));
    this.content.setWidth (this.width-2);
    CGUI.debug ("  Set width");
    this.content.setHeight (this.height - this.top_offset - 
                            this.bottom_offset -2);
  // Not sure why Nishan's doing this...
  if (this.content.controls.vslider) this.content.controls.vslider.setValue(0);

    CGUI.debug ("  Set height");
  } else {
    CGUI.debug ("  Frame has no content");
  }
  if (this.tb) {
    this.tb_btn_grp.setAttribute ("transform", "translate(" + 
                                     (this.width-this.origw) + "," + "0)"); 
   
  }
  if (this.resize_btn) this.resize_btn_g.setAttribute ("transform",
                       "translate("+(this.width-this.origw) + "," + 
                                     (this.height-this.origh) + ")"); 

  if (this.bb) {
    this.bbg.setAttribute ("transform", "translate(0, " + 
                                     (this.height-this.origh) + ")");
    this.bb.buttons.setAttribute ("transform", "translate(" + 
                                   (this.width-this.origw) + "," + "0)"); 
  }
  if (this.mb) {
    // no need to adjust the menubar, though it might be necessary to
    // adjust buttons which are on the "right" at some point...
  }
  if (this.status_bar)
    this.status_bar_g.setAttribute ("transform", "translate(0, " + 
                                     (this.height-this.origh) + ")");
}


/**
@ --------------------------------------------------------------
@ Method: setTitle
@ Description: Set the text in the title bar of this frame
@ Parameter: text - the text to use in the title
@ --------------------------------------------------------------
*/
Frame.prototype.setTitle = function (text) {
  this.tb.setMessage(text);
}


/**
@ --------------------------------------------------------------
@ Method: getTitle
@ Description: get the text used in the title of this frame
@ --------------------------------------------------------------
*/
Frame.prototype.getTitle = function () {
  return this.tb.getMessage();
}


/**
@ --------------------------------------------------------------
@ Method: setStatus
@ Description: Set the message in the status bar
@ --------------------------------------------------------------
*/
Frame.prototype.setStatus = function (text) {
  CGUI.debug ("Frame setStatus:" + text);
  if (this.status_bar) this.status_bar.setMessage(text);
}


/**
@ --------------------------------------------------------------
@ Method: rollup
@ Description: - roll up, or minimize this frame
@              - leaves the titlebar, statusbar and buttonbars
@                visible if they exist.
@ --------------------------------------------------------------
*/
Frame.prototype.rollup = function () {
  this.preRollHeight = this.height;
  var new_height = 15;  // must have a title bar if it us able to roll up
  if (this.bb) new_height += 15;  // add the height of the button bar
  if (this.status_bar) new_height += 15;  // add the height of the status bar
  if (this.mb)  new_height += 15;  // add height of menu bar
  CGUI.debug ("Set size to: " + new_height);
  this.rolledUp = true;
  this.hidePanes();
  this.setSize(this.width, new_height);
}


/**
@ --------------------------------------------------------------
@ Method: foreground
@
@ Description: Brings the frame to the foreground
@ --------------------------------------------------------------
*/
Frame.prototype.foreground = function () {
// Bring the frame to the foreground
  if (this.parent == null) {
    var temp = this.svg_reference;
    CGUI.root.removeChild (this.svg_reference);
    this.svg_reference = CGUI.root.appendChild (temp);
  }
 else {
    var temp = this.svg_reference;
    this.parent.removeChild (this.svg_reference);
    this.svg_reference = this.parent.appendChild (temp);
  }
}


/**
@ --------------------------------------------------------------
@ Method: background
@
@ Description: Puts the frame in the background
@
@ Note: I'm not sure why the frame furthest in the background
@ needs to be inserted before the third array element (nextSibling x2)
@ but if it's inserted any further towards the beginning of the array,
@ the frame just disappears.
@
@ Function added 20020624 by cram
@ --------------------------------------------------------------
*/
Frame.prototype.background = function () {
// Put the frame in the background
  if(this.parent == null) {
    var temp = this.svg_reference;
    CGUI.root.removeChild(this.svg_reference);
    this.svg_reference = CGUI.root.insertBefore(temp,
        this.parent.firstChild.nextSibling.nextSibling);
  }
  else {
    var temp = this.svg_reference;
    this.parent.removeChild(this.svg_reference);
    this.svg_reference = this.parent.insertBefore(temp,
        this.parent.firstChild.nextSibling.nextSibling);
  }
}


/**
@ --------------------------------------------------------------
@ Method: rolldown
@ Description: unroll, or restore the frame to it's previous size
@ --------------------------------------------------------------
*/
Frame.prototype.rolldown = function () {
  var new_height = this.preRollHeight;
  this.rolledUp = false;
  CGUI.debug ("Set size to: " + new_height);
  this.showPanes();
  this.setSize(this.width, new_height);
}


/**
@ --------------------------------------------------------------
@ Method: doDrag
@ Description: prepare to drag this frame around the screen
@ Parameter: evt - the event triggering this action
@ Parameter: type - the event type; do this only in response to a
@                   mousedown event
@ --------------------------------------------------------------
*/
Frame.doDrag = function (evt, type) {
  var t = evt.getTarget();
  // get the frame this titlebar is on
  var frame = Frame.getFrame (t);
  Frame.foreground(frame);

  if (!frame || !frame.svg_reference) return;
  switch (type) {
  case 'onmousedown' :
    frame.svg_reference.setAttribute ("opacity", 0.6);
    frame.hidePanes();
    MouseTracker.dragging = frame;
    // store x/y coords for calcualting dx/dy in mouseMove
    MouseTracker.last_x = evt.clientX;
    MouseTracker.last_y = evt.clientY;

    break;
  }
}




/**
@ --------------------------------------------------------------
@ Method: doResize
@ Description: prepare to resize this frame
@ Parameter: evt - the event triggering this action
@ Parameter: type - the event type; do this only in response to a
@                   mousedown event
@ --------------------------------------------------------------
*/
Frame.doResize = function (evt, target, type) {
  var frame = Frame.getFrame (target);
  Frame.foreground(frame);
  
  if (!frame || !frame.svg_reference) return;
  switch (type) {
  case 'onmousedown' :
    frame.svg_reference.setAttribute ("opacity", 0.8);
    frame.hidePanes();
    MouseTracker.resizing = frame;
    // store x/y coords for calcualting dx/dy in mouseMove
    MouseTracker.last_x = evt.clientX;
    MouseTracker.last_y = evt.clientY;

    break;
  }
}


/**
@ --------------------------------------------------------------
@ Method: makeTitleBar
@ Description: create the title bar for this frame.
@ --------------------------------------------------------------
*/
Frame.prototype.makeTitleBar = function () {
  this.tb = new TextBar (this.title, "100%", "15", "white");
  this.tb.addToParent(this);
  this.tb.setAnchor (this, "left", 0, "top", 0);

  this.tb.svg_reference.setAttribute (
                      "onmousedown", "Frame.doDrag(evt, 'onmousedown')"
  );
 

  this.tb_btn_grp = document.createElement ("g");

  if (this.b_close) {
    CGUI.debug ("Created close btn");
    // put a close button overtop of the right end of the title bar
    this.close_btn = new Button (15, 15, "X", "Frame.closeFrame(this)");
    this.close_btn.addToParent (this.tb);
    this.close_btn.setAnchor (this.tb, "right", 0, "top", 0);
    this.tb_btn_grp.appendChild (this.close_btn.svg_reference);
  }

    this.rollup_btn = new Button (15, 15, "-", "Frame.rollup(this)");
    this.rollup_btn.addToParent (this.tb);
    this.rollup_btn.setAnchor (this.tb, "right", -15, "top", 0);
    this.tb_btn_grp.appendChild (this.rollup_btn.svg_reference);

    this.back_btn = new Button (15, 15, "b", "Frame.background(this)");
    this.back_btn.addToParent (this.tb);
    this.back_btn.setAnchor (this.tb, "right", -30, "top", 0);
    this.tb_btn_grp.appendChild (this.back_btn.svg_reference);
    this.tb.svg_reference.appendChild (this.tb_btn_grp);

}


/**
@ --------------------------------------------------------------
@ Method: move
@ Description: move the frame by dx and dy
@ Parameter: dx - the amount to move the frame in the x direction
@ Parameter: dy - the amount to move the frame in the y direction
@ --------------------------------------------------------------
*/
Frame.prototype.move = function (dx, dy) {
    var x = Number (this.getX());
    x += Number(dx);
    this.setX (x);

    var y = Number (this.getY());
    y += Number (dy);
    this.setY (y);
}

/**
@ --------------------------------------------------------------
@ Method: open
@ Description: Opens the frame
@
@  This involves:
@   - creating a background (rect)
@   - creating the titlebar (if desired)
@   - creating the buttonbar (if desired)
@   - creating the statusbar (if desired)
@   - and drawing the frame on the screen
@ --------------------------------------------------------------
*/
Frame.prototype.open = function () {
  CGUI.debug ("Opening Frame");


  this.bg = document.createElement ("rect");
  this.bg.setAttribute ("class", "framebackground");
  this.bg.setAttribute ("x", 0);
  this.bg.setAttribute ("y", 0);
  this.bg.setAttribute ("stroke", "black");
  this.bg.setAttribute ("stroke-width", "2pt");
  this.bg.setAttribute ("fill", "grey");
  this.bg.setAttribute ("width", "100%");
  this.bg.setAttribute ("height", "100%");
  this.svg_reference.appendChild (this.bg);


  if (this.title) this.makeTitleBar();

  if (this.arr_btns) {
    this.bbg = document.createElement ("g");
    this.bb = new Bar ("100%", 15);
    this.bb.addToParent (this.bbg);
    this.svg_reference.appendChild(this.bbg);
    this.bb.setAnchor (this, "left", 0, "bottom", this.b_status?-15:0);
    var button_offset = 0;
    this.bb.buttons = document.createElement ("g");
    for (var i = 0; i < this.arr_btns.length; i++) {
      this.arr_btns[i].addToParent (this.bb.buttons);
      this.arr_btns[i].setAnchor (this, "right", button_offset, "top", 0);
      button_offset -= (Number (this.arr_btns[i].getWidth()));
    }
    this.bb.svg_reference.appendChild(this.bb.buttons);
  }

  if (this.b_status) {
    this.status_bar_g = document.createElement ("g");
    this.status_bar = new TextBar ("", "100%", 15, "white");
    this.status_bar.addToParent (this.status_bar_g);
    this.status_bar.setAnchor (this, "left", 0, "bottom", 0);
    this.svg_reference.appendChild (this.status_bar_g);
  }
  this.refresh();
}


/**
@ --------------------------------------------------------------
@ Method: closeFrame
@ Description: close target frame
@
@ Parameters: target - the frame to close
@ --------------------------------------------------------------
*/
Frame.closeFrame = function (target) {
  target.parent.parent.close();
}


/**
@ --------------------------------------------------------------
@ Method: rollup
@ Description: rollup target frame
@ --------------------------------------------------------------
*/
Frame.rollup = function (target) {

  var frame = Frame.getFrame(target);
  Frame.foreground(frame);

  frame.rollup();
  target.setEvent ("Frame.rolldown(this)");
}

/**
@ --------------------------------------------------------------
@ method: foreground
@ description: foreground target frame
@ --------------------------------------------------------------
*/
Frame.foreground = function (target) {
  Frame.getFrame(target).foreground();
}

/**
@ --------------------------------------------------------------
@ method: background
@ description: background target frame
@ --------------------------------------------------------------
*/
Frame.background = function (target) {
	Frame.getFrame(target).background();
}

/**
@ --------------------------------------------------------------
@ method: resize
@ description: resize target frame
@ --------------------------------------------------------------
*/
Frame.resize = function (target) {
   
    Frame.getFrame(target).resize();
}

/**
@ --------------------------------------------------------------
@ Method: rolldown
@ Description: rolldown target frame
@ --------------------------------------------------------------
*/
Frame.rolldown = function (target) {
  var frame = Frame.getFrame(target);

  Frame.getFrame(target).rolldown();
  Frame.foreground(frame);

  target.setEvent ("Frame.rollup(this)");
}

/**
@ --------------------------------------------------------------
@ Method: showId
@ Description: get the id of the target (or first ancestor with an id)
@              stick it in the status bar
@ --------------------------------------------------------------
*/
Frame.showId = function (evt) {
  var target = evt.getTarget();
  var frame = Frame.getFrame (target);
  if (frame) {
    frame.setStatus (Frame.getId (target));
  }
}

/**
@ --------------------------------------------------------------
@ Method: getId
@ Description: try to recursively get the id of a target element
@              not certain this belongs here...
@ --------------------------------------------------------------
*/
Frame.getId = function (target) {
  var id;
  if (target.nodeType == 9) return;

  id = target.getAttribute("id");
  if (id) {
    return id;
  } else if (target.parentNode) {
    return Frame.getId (target.parentNode);
  } else {
    return;
  }
}

/**
@ --------------------------------------------------------------
@ Method: clearId
@ Description: clear the status bar
@ --------------------------------------------------------------
*/
Frame.clearId = function (evt) {
  var target = evt.getTarget();
  var frame = Frame.getFrame (target);

  if (frame) {
    frame.setStatus ("");
  }
}
/**
@ --------------------------------------------------------------
@ Method: hideMenu
@ Description: hides the menu, used when user does not click on
@              menu an menubar must be hidden from an external source
@ Note: will also be called by Frame when user clicks an other part (to do)
@ Added - Chris Peto
@ --------------------------------------------------------------
*/
Frame.prototype.hideMenu = function()
{
   if(this.mb) this.mb.hideAll();
}
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/
     

/**
@ --------------------------------------------------------------
@ Object: Transform
@ Desc:
@ Example:
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Transform
@ Parameter: scale_x - initial scale in the x direction
@            scale_y - initial scale in the y direction
@        translate_x - initial translate in the x direction
@        translate_y - initial translate in the y direction
@ --------------------------------------------------------------
*/
function Transform (scale_x, scale_y, translate_x, translate_y) {
  this.s_x = (scale_x) ? scale_x : 1;
  this.s_y = (scale_y) ? scale_y : 1;
  this.t_x = (translate_x) ? translate_x : 0;
  this.t_y = (translate_y) ? translate_y : 0;
  this.r = 0;
}


/**
@ --------------------------------------------------------------
@ Method: reset
@ Description: reset to the initial scale
@ --------------------------------------------------------------
*/
Transform.prototype.reset = function () {
  this.s_x = 1;
  this.s_y = 1;
  this.t_x = 0;
  this.t_y = 0;
  this.r = 0;
}


/**
@ --------------------------------------------------------------
@ Method: transformString
@ Description: create the string passed to the transfom function
@ Parameter: the content pane being transformed
@ --------------------------------------------------------------
*/
Transform.prototype.transformString = function (content) {
  return "translate("+(content.width/2)+","+(content.height/2)+"), "+
               "scale("+this.s_x+","+this.s_y+"), "+
               "translate("+(-1*content.width/2+this.t_x)+","+
                          (-1*content.height/2+this.t_y)+")" + ","+
               "rotate("+this.r+"," + 
                       (-1*this.t_x + content.width/2) + ","+ 
                       (-1*this.t_y + content.height/2) + ")";
}


/**
@ --------------------------------------------------------------
@ Method: toString
@ Description: Return a string representation of this object
@ --------------------------------------------------------------
*/
Transform.prototype.toString = function () {
  var ret = "Translate Object:\n";
  ret += "Scale_x: " + this.s_x;
  ret += "\nScale_y: "+this.s_y;
  ret += "\nTrans_x: "+this.t_x;
  ret += "\nTrans_y: "+this.t_y;
  ret += "\nRotate: "+this.r;
  return ret;
}
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/
    
/**
@ --------------------------------------------------------------
@ Object: TextBar
@ Desc:  a text bar
@ Example: possibly used as a title bar or a status bar
@ Inherits from: Bar
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: TextBar
@ Parameter: text - text to display on the bar if any
@            width - width of bar
@            height - height of bar
@ --------------------------------------------------------------
*/
function TextBar (text, width, height, textColor) {
  if (text || width || height) {
    CGUI.debug ("Creating TextBar");
    this.base = Bar;
    this.base (width, height);
    if (textColor == null){
      textColor = "white";
    }
    this.text = new Label(0,0,"100%",15,text,textColor);

    this.text.addToParent(this);
    this.text.setAnchor (this, "left", 5, "top", 0);

    return this;
  }
}
TextBar.prototype = new Bar;
TextBar.prototype.CGUI_TYPE = "TEXT_BAR";

/**
@ --------------------------------------------------------------
@ Method: getMessage
@ Description: Get the message associated with this bar.
@   For a reason that I no longer remember, I opted to refer to 
@   the text of a bar as it's message...  perhaps getText
@   is already a function in SVG?  I don't remember right now.
@ --------------------------------------------------------------
*/

TextBar.prototype.getMessage = function () {
  CGUI.debug ("TextBar getMessage");
  if (this.text) {
    return this.text.getText();
  }
}

/**
@ --------------------------------------------------------------
@ Method: setMessage
@ Description: Set the message of this bar
@ 
@ --------------------------------------------------------------
*/

TextBar.prototype.setMessage = function( text ) {
  CGUI.debug ("TextBar setMessage");
  if (this.text) {
    this.text.setText (text);
  }
}

/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/

/**
@ --------------------------------------------------------------
@ Object: ButtonGroup
@ Desc:
@ Example: 
@ Inherits from: N/A - Might be a good idea to create a generic 
@                      Group class and have it inherit from there
@                      Such a generic Group class might be useful
@
@ Notes: 
@ Each ButtonPane has a ButtonGroup associated with it
@ --------------------------------------------------------------
*/

ButtonGroup.NONE = 0;
ButtonGroup.RADIO = 1;
ButtonGroup.CHECKBOX = 2;

/**
@ --------------------------------------------------------------
@ Constructor: ButtonGroup
@ Parameter: type - one of:
@             ButtonGroup.NONE || 0
@             ButtonGroup.RADIO || 1
@             ButtonGroup.CHECKBOX || 2
@ Parameter: fire_event - Fire the event associated with the button
@ --------------------------------------------------------------
*/
function ButtonGroup (type, fire_event) {
  if (type != null) {
    this.init (type, fire_event);
  }
}

/**
@ --------------------------------------------------------------
@ Method: init
@ Description: initialize this object
@ Parameter: type - type of button group
@ Parameter: fire_event - whether or not to fire the buttons
@                         event when this button is selected
@ --------------------------------------------------------------
*/

ButtonGroup.prototype.init = function (type, fire_event) {
  this.elements = new Array();
  this.fire_event = fire_event;
  this.type = type;
  if (type == ButtonGroup.RADIO) {
    this.mousedown = this.radioButton;
  } else if (type == ButtonGroup.CHECKBOX) {
    this.mousedown = this.checkBox;
  } else if (type == ButtonGroup.NONE) {
    // change nothing.  Allow the buttons to work like normal
  } else {
    alert ("Unknown Type: " + type);
  }
}

/**
@ --------------------------------------------------------------
@ Method: toString
@ Description: Displays this ButtonGroup as a string
@              Lists the elements of the button group
@ --------------------------------------------------------------
*/

ButtonGroup.prototype.toString = function () {
  return "ButtonGroup: Elements - " + this.elements.length;
}

/**
@ --------------------------------------------------------------
@ Method: addElement
@ Description: Add an element to the button group
@ Parameter: button - the button to add to the group
@ Notes: Should probably ensure that it is a Button which has
@        been added
@ --------------------------------------------------------------
*/

ButtonGroup.prototype.addElement = function (button) {
  this.elements[this.elements.length] = button;
  if (this.type != ButtonGroup.NONE) {
    button.enableToggle();
    button.oldmousedown = button.mousedown;
    button.mousedown = this.mousedown;
  }
    button.group = this;
}  

/**
@ --------------------------------------------------------------
@ Method: select
@ Description: Select this button
@              Changes the appearance to the "selected" state
@ --------------------------------------------------------------
*/

ButtonGroup.prototype.select = function () {
   
  this.face.setAttribute ("class", "buttonfacedep");
  this.selected = true;
}



/**
@ --------------------------------------------------------------
@ Method: deselect
@ Description: Deselect this button
@              Changes the appearance to the "deselect" state
@ --------------------------------------------------------------
*/

ButtonGroup.prototype.deselect = function () {
  this.face.setAttribute ("class", "buttonface");
  this.selected = false;
}


/**
@ --------------------------------------------------------------
@ Method: radioButton
@ Description: This function replaces the mousedown function of
@              all buttons controlled by a ButtonGroup of type
@              RADIOBUTTON
@ Parameter: evt - the event (mousedown)
@ --------------------------------------------------------------
*/

ButtonGroup.prototype.radioButton = function (evt) {
  if (this.enabled) {
    group = CGUI.getJSRef(evt.getTarget()).group;
    if (group.selected) {
      group.selected.toggleState();
      group.selected.face.setAttribute ("class", "buttonface");
    }
    if (group.fire_event)  {
      if (this.oldmousedown) this.oldmousedown(evt);
    }
    group.selected = this;
    this.toggleState();
  } else {
    alert ("Button Disabled");
  }
}

/**
@ --------------------------------------------------------------
@ Method: checkBox
@ Description: This function replaces the mousedown function of 
@              all buttons controlled by a ButtonGroup of type
@              CHECKBOX
@ Parameter: evt - the event (mousedown)
@ --------------------------------------------------------------
*/

ButtonGroup.prototype.checkBox = function (evt) {
  if (this.enabled) {
    this.toggleState();
    if (!this.selected) this.face.setAttribute ("class", "buttonfacehover");
  } else {
    alert ("Button Disabled");
  }
}

/**
@ --------------------------------------------------------------
@ Method: close
@ Description: close this ButtonGroup
@     - removes all the buttons in the group from their parent
@     - removes all the buttons from this ButtonGroup
@
@ Example: 
@ --------------------------------------------------------------
*/

ButtonGroup.prototype.close = function () {
  for (var i = 0; i < this.elements.length; ) {
    this.elements[i].removeFromParent();
    this.elements[i].splice (0, 1);
  }
}

/**
@ --------------------------------------------------------------
@ Method: index
@ Description: return the index of desired item
@              return -1 if not found
@ Parameter: item - the desired item
@
@ Example: 
@ --------------------------------------------------------------
*/
ButtonGroup.prototype.index = function (item) {
  for (var i = 0; i < this.elements.length; i++) {
    if (this.elements[i] == item) {
      return i;
    }
  }
  return -1;
}
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/
     

/**
@ --------------------------------------------------------------
@ Object: ButtonPane
@ Desc:
@ Example: 
@ Inheirits from: Pane
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: ButtonPane
@ Parameter: x - x coord for top left corner relative to parent
@            y - y coord for top left corner relative to parent
@            width - width of this pane
@            height - height of this pane
@            ctlr_grp - control group associated with this pane
@             - Each ButtonPane has an associated button control group
@ --------------------------------------------------------------
*/
function ButtonPane (x,y,width,height,ctrl_grp) {
  if (arguments.length > 0) {
    this.base = Pane;
    this.base (x,y,width, height);
    this.CGUI_TYPE = "BUTTON_PANE";
    this.ctrl_grp = ctrl_grp;
  }
}
ButtonPane.prototype = new Pane;
ButtonPane.superclass = Pane.prototype;

/**
@ --------------------------------------------------------------
@ Method: addElements
@ Description: 
@   - add elements to this buttonPane
@   - Currently a buttonPane is allowed to have buttons and 
@     labels added to it.
@   - button is set to 15x15 and the button's label is put
@     beside it.
@   - labels are added on a line of their own
@
@ Parameter: arr_elms - an array of elements to add
@
@ Example: 
@ An array like: [label1, button, button, label2, button]
@ would produce a buttonPane like:
@
@           --------------------
@           | Label1           |
@           |  @ button        |
@           |  @ button        |
@           | Label2           |
@           |  @ button        |
@           --------------------
@
@ --------------------------------------------------------------
*/

ButtonPane.prototype.addElements = function (arr_elms) {
  for (var i = 0; i < arr_elms.length; i++) { 
    var elm = arr_elms[i];
    switch (elm.CGUI_TYPE) {
    case "BUTTON" : 
      var text = elm.toString();
      if (!text) text = "Unnamed button " + i;
      elm.hideText();
      elm.setWidth (15); elm.setHeight(15);
      elm.addToParent (this);
      elm.setAnchor (this, "left", 10, "top", (i>0)?20*(i)+5:5);
      this.ctrl_grp.addElement (elm);

      var lbl = new Label (0,0, this.width - 35, 15, text, "white");
      lbl.addToParent (this);
      lbl.setAnchor (this, "right", -5, "top", (i>0)?20*(i)+5:5);
      break;
    case "LABEL" : 
      elm.addToParent (this);
      elm.setAnchor (this, "left", 5, "top", (i>0)?20*(i)+5:5);
      break;
         break;
    default : 
         alert ("Unable to deal with: " + elm.CGUI_TYPE);
         break;
    }
  }
  arr_elms = null;
}

/**
@ --------------------------------------------------------------
@ Method: getSelected
@ Description: Return the selected element
@
@ Notes: Doesn't seem like this would work well with a checkbox grp
@        Need to double check how checkbox group stores selected items...
@
@ Example: 
@ --------------------------------------------------------------
*/

ButtonPane.prototype.getSelected = function () {
// function needs to get selected, or call selected function...  ?
// for now, lets just get the selected item
  return this.ctrl_grp.selected ? this.ctrl_grp.selected : "";
}

/**
@ --------------------------------------------------------------
@ Method: getSelectedIndex
@ Description: Return the index of the selected item.
@
@ Example: 
@ --------------------------------------------------------------
*/

ButtonPane.prototype.getSelectedIndex = function () {
  alert ("here");
  return this.ctrl_grp.index(this.ctrl_group.selected);
}
/*
    Copyright 2002 Christopher T Lewis

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Contact information:

      Christopher T Lewis
      Saskatoon Research Centre
      107 Science Place
      Saskatoon SK Canada S7N 0X2
      LewisCT@agr.gc.ca
*/
     
/**
@ --------------------------------------------------------------
@ Object: Wrapper
@ Desc: an object to wrap SVG and allow the use of an anchor
@ Example: Used in the frame to anchor the crosshairs
@ Inherits from: CGUI
@ Notes: Necessary as Netscape was loosing the anchor when 
@        it was attached directly to the SVG
@ --------------------------------------------------------------
*/

/**
@ --------------------------------------------------------------
@ Constructor: Wrapper
@ Parameter: svg - the SVG to contain in this wrapper
@ --------------------------------------------------------------
*/
function Wrapper (svg) {
  if (svg) {
    CGUI.debug ("Creating Wrapper for " + svg);
    this.CGUI_TYPE = "WRAPPER";
    this.base = CGUI;
    this.base (0, 0, 0, 0);

    this.svg_reference = svg;

    return this;
  }
}
Wrapper.prototype = new CGUI;
/*
    Copyright 2002 Chris Peto

    This file is part of the CGUI Library

    CGUI is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    CGUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307USA

    Contact information:

      Chris Peto
      Resource Solutions
      Hoehenweg 34
      63303 Dreieich, Germany
      CPeto@resource-solutions.de
*/

/**
@ --------------------------------------------------------------
@ Object: Slider
@ Desc:   General slider, must be used in Frame
@ Example: var redslider = new Slider(
@                14, 13, 239,18, 0,ColorBar_frame,
@                changeRedColor
@                );
@          redslider.addToParent (my_frame);
@          redslider.setValue(128);
@          redslider.setMinmax(0, 255);
@          redslider.setBkColor("rgb(255, 0, 0)");
@
@ Inherits from: CGUI
@ --------------------------------------------------------------
*/

/*****
*
*        Slider.js
*
*
*        Original code based on Kevin Lindsey's slider example which is based on
*        Dr. Stefan Goessner's slider:
*        http://www.mecxpert.de/svg/slider.html
*
*****/

var slidercount = 0;


/*****
*
*   setup inheritance
*
*****/
Slider.prototype             = new CGUI ;
Slider.prototype.constructor = Slider;
Slider.superclass            = CGUI.prototype;


/*****
*
*   Class variables
*
*****/
Slider.VERSION = 3.0;
Slider.prototype.bodyText =
'<g class="slider" fill="none">' +
'    <rect class="background" x="0" y="0" width="{$width+18}" height="{$nheight+2}" fill="rgb(128,128,128)"/>' +   //background
'    <rect class="middle" x="1" y="1" width="{$width+16}" height="{$nheight}" fill="rgb(230,230,230)"/>' +   //middle
'    <polyline class="lefttri" points="1.5,{$nheight}, 1.5,1.5 {$width+17},1.5" stroke="rgb(100,100,100)"/>' +  //pseudo gradient to bar?
'    <polyline class="righttri" points="1,{$nheight+0.5} {$width+16.5},{$nheight+0.5}, {$width+16.5},1" stroke="white"/>' + //pseudo grandient to bar?
'    <rect class="cover" id ="{$bgid}" x="0" y="0" width="{$width+18}" height="{$nheight+2}" opacity="0.2" fill="rgb(128,128,250)"/>' + //color, same as background
'</g>';
Slider.prototype.thumbText =
'<g class="sliderthumb" fill="none">' +
'    <rect class="thumb" x="3" y="3" width="12" height="{$sheight}" fill="rgb(192,192,192)"/>' +
'    <polyline class="border" points="3,{$sheight+2.5} 14.5,{$sheight+2.5} 14.5,3"   stroke="black"/>' +
'    <polyline class="lefttri" points="4,{$sheight+1.5} 13.5,{$sheight+1.5} 13.5,4"   stroke="rgb(128,128,128)"/>' +
'    <polyline class="righttri" points="4.5,{$sheight+1} 4.5,4.5 13,4.5" stroke="white"/>' +
'</g>';


/**
@ --------------------------------------------------------------
@ Constructor: Slider
@ Parameter: x - x coordinate of this Slider relative to parent
@            y - y coordinate of this Slider relateive to parent
@        width - width of this Slider
@       height - height of this Slider
@    direction - rotation of slider
@      parfram - parent frame or parent
@        event - event to be called when slider changes position
@     bodyText - user defined svg body of slider
@    thumbText - user defined svg thumb
@ --------------------------------------------------------------
*/
function Slider(x, y, width, height, direction, parframe, event, bodyText, 
                thumbText) {
    if ( arguments.length > 0 ) {
        this.init(x, y, width, height, direction, parframe, event, bodyText, 
                  thumbText);
        return this;
    }
    return null;
};


/**
@ --------------------------------------------------------------
@ Method: init
@ Description: sets base class and build svg
@ Parameter: x - x coordinate of this Slider relative to parent
@            y - y coordinate of this Slider relateive to parent
@        width - width of this Slider
@       height - height of this Slider
@    direction - rotation of slider
@      parfram - parent frame or parent
@        event - event to be called when slider changes position
@     bodyText - user defined svg body of slider
@    thumbText - user defined svg thumb
@    Note: Could actually be done in constructor
@ --------------------------------------------------------------
*/
Slider.prototype.init = function(x, y, width, height, direction, parframe, 
                                 event, bodyText, thumbText) {

    this.CGUI_TYPE = "SLIDER";
    CGUI.debug ("Creating Slider");

    this.base = CGUI;
    this.svg_size = width;
    if (width > height) {
      this.svg_size = height;
    }
    this.base (x, y, this.svg_size, this.svg_size);
    this.width = width;
    this.height= height;
    this.nheight = height-2;
    this.sheight = height-6;

    this.nodes = new Object();

    // init properties
    //this.size      = size;
    this.direction = direction;
    this.event  = event;
    this.min       = 0;
    this.max       = width;
    this.value     = 0;
    this.active    = false;
    this.bgid = slidercount++;

    this.parframe=parframe;

    if ( bodyText  != null ) this.bodyText  = bodyText;
    if ( thumbText != null ) this.thumbText = thumbText;

    this.nodes.parent = this.svg_reference;

    this.buildSVG();
    this.addEventListeners(this.nodes.root);
    this.reversed=false;
    this.trans_hori=1;
    this.trans_vert=1;
    this.minmax_dynam_adjust=0;
};

/**
@ --------------------------------------------------------------
@ Method: Resize
@ Description: Resizes slider by rebuilding svg
@ Parameter: cx - new slide width
@            cy - new slide height
@ --------------------------------------------------------------
*/

Slider.prototype.Resize = function(cx,cy) {
  this.width = cx;
  this.height= cy;
  this.nheight = cy-2;
  this.sheight = cy-6;
  this.svg_reference.removeChild(this.svg_reference.getFirstChild());
  this.buildSVG();
  this.addEventListeners(this.nodes.root);
  this.setValue(this.value);
  this.adjustValue(this.value);
}


/**
@ --------------------------------------------------------------
@ Method: buildSVG
@ Description: build slider's svg component
@ --------------------------------------------------------------
*/
Slider.prototype.buildSVG = function() {
    
    var translate = "";
    if (this.direction > 0) {
       translate = "translate(" + this.svg_size + "," + 0 + ")";
    } else {
       translate = "translate(" + 0 + "," + 0 + ")";
    }
     
    var rotate    = "rotate(" + this.direction + ")";
    var transform = translate + " " + rotate;
    var slider    = svgDocument.createElementNS(null, "g");

    slider.appendChild( this.textToSVG(this.bodyText) );
    slider.appendChild( this.textToSVG(this.thumbText) );
    slider.setAttributeNS(null, "transform", transform);
    this.svg_reference.setAttributeNS(null, "style","overflow:visible");

    this.nodes.thumb = slider.lastChild;
    this.nodes.root  = slider;
    this.nodes.parent.appendChild(slider);
};

/**
@ --------------------------------------------------------------
@ Method: textToSVG
@ Description: replaces variables in svg text
@             this.width = 8;
@             replaces {$width+8} with 8+8 and does and eval
@             an puts 16 in the text place where {$width+8}
@ Parameters: text - text to be parsed
@ Returns:    svg objects with all variables replaced
@ Note: Cannot handle negative numbers, i.e. {$width-8}
@ --------------------------------------------------------------
*/
Slider.prototype.textToSVG = function(text) {
    var self = this;
    var svg  = text.replace(
        /\$(\{[a-zA-Z][-a-zA-Z]*\}|[a-zA-Z][-a-zA-Z]*)/g,
        function(property) {
            var name = property.replace(/[\$\{\}]/g, "");

            return self[name];
        }
    ).replace(
        /\{[^\}]+\}/g,
        function(functionText) {
            return eval( functionText.substr(1, functionText.length - 2) );
        }
    );

    return parseXML(svg, svgDocument);
};




/**
@ --------------------------------------------------------------
@ Method: setBkColor
@ Description: sets background color of slide
@              must have {$bgid} in bodyText
@ Parameters: Color - color for background, any valid svg color format
@ --------------------------------------------------------------
*/
Slider.prototype.setBkColor = function(Color) {

  if(!svgDocument.getElementById(this.bgid)) return;
  var sbg  = svgDocument.getElementById(this.bgid);
  sbg.setAttribute("fill",Color);
  this.Color = Color;
};

/*****        set methods        *****/

/**
@ --------------------------------------------------------------
@ Method: setMin
@ Description: Sets the minimal value for the slider
@ Parameters:  min - value to be set
@ --------------------------------------------------------------
*/
Slider.prototype.setMin = function(min,max) {
      if (this.reversed==false){
      max=this.max;
	
	if (this.min > this.max){
	//do nothing
	}
	else{
          this.min = min;
      	}

      }
      else{
//	DO REVERSED STUFF HERE
      }


        if (this.min < this.max) {
                if (this.value < min) this.value = min;
        } else {
               if (this.value < max) this.value = max;
        }
        this.setValue(this.value, false);
};



/**
@ --------------------------------------------------------------
@ Method: setMax
@ Description: Sets the maximum value for the slider
@ Parameters:  max - value to be set
@ --------------------------------------------------------------
*/
Slider.prototype.setMax = function(max) {
      if (this.reversed == false){
        if (this.max < this.min){
	  var min_temp = this.min;
	  var max_temp = this.max;
	  this.max = min_temp;
	  this.min = max_temp;
	}
	else{
	  this.max = max;
	}
      }
      else
      {
	 this.min = max;	  
      }

        if (this.min < this.max) {
          if (this.value > max) this.value = max;
        } 
	else  {
          if (this.value > min) this.value = min;
        }
        this.setValue(this.value, false);
};

/**
@ --------------------------------------------------------------
@ Method: adjustMax
@ Description: Sets the range for the slider
@ Parameters:  min - minimum value to be set
@              max - maximum value to be set
@ Note: To reverse the sliders min and max then have max smaller than min
@ --------------------------------------------------------------
*/
Slider.prototype.adjustMax = function(max) {
 if(this.reversed == false){
        this.max = this.max+max;
	this.min = this.min-max;
  }
  else{
	this.max = this.max-max;
	this.min = this.min+max;    
  }
      if (this.reversed == false){
	if (this.min>0)
	{
	  this.min = -10;
	}
	if (this.max<0)
	{
	  this.max = 10;
	}
}
        this.setValue(this.value, false);
};


/**
@ --------------------------------------------------------------
@ Method: adjustMin
@ Description: Sets the range for the slider
@ Parameters:  min - minimum value to be set
@              max - maximum value to be set
@ Note: To reverse the sliders min and max then have max smaller than min
@ --------------------------------------------------------------
*/

Slider.prototype.adjustMin = function(min) {
 if(this.reversed == false){
        this.min = this.min+min;
	this.max = this.max-min;
  }
  else{
	this.min = this.min-min;
	this.max = this.max-min;
  }
        
  if(this.reversed == false)
  {
	if (this.min>-10)
	  this.min=-10;
	if (this.max<10)
	  this.max=10;
  }
        if (this.min < this.max) {
                if (this.value < min) this.value = min;
        } else {
                if (this.value < max) this.value = max;
        }
	this.value=this.height/2;
        this.setValue(this.value, false);


};


/**
@ --------------------------------------------------------------
@ Method: initMinmax
@ Description: Initialize the range for the slider
@ Parameters:  min - minimum value to be set
@              max - maximum value to be set
@ Note: To reverse the sliders min and max then have max smaller than min
@       Use initMinMax to prevent the callback function associated 
@       with the slider from being called.
@ --------------------------------------------------------------
*/
Slider.prototype.initMinmax = function(min, max) {
        this.min = min;
        this.max = max;
        if (this.min < this.max) {
                if (this.value < min) this.value = min;
                if (this.value > max) this.value = max;
        } else {
                if (this.value < max) this.value = max;
                if (this.value > min) this.value = min;
        }
        this.setValue(this.value, false);
};


/**
@ --------------------------------------------------------------
@ Method: setValue
@ Description: Sets the position and current value of the slider
@ Parameters:  value - actual user value
@              call_callback - boolean to call Slider event
@ --------------------------------------------------------------
*/
Slider.prototype.setValue = function(value, call_callback) {
        var range    = this.max - this.min;
        var position = ( value - this.min ) / range * this.width;
        this.value = value;
        this.nodes.thumb.setAttributeNS(null, "transform", 
                            "translate(" +  + ", 0)");
	this.setPosition(position);
        if (call_callback && this.event) this.event(value);
};




/**
@ --------------------------------------------------------------
@ Method: adjustValue
@ Description: Sets the range for the slider
@ Parameters:  min - minimum value to be set
@              max - maximum value to be set
@ Note: To reverse the sliders min and max then have max smaller than min
@ --------------------------------------------------------------
*/


Slider.prototype.adjustValue = function (adj) {
   var new_value = this.value + adj;
   var old_value = this.value;
   if (this.reversed==false){
      if (this.min > new_value) 
         new_value = this.min;
      else if (this.max < new_value) 
         new_value = this.max;
     
    }
    else
    {
      if(this.max>new_value)
        new_value=this.max;
       else if (this.min<new_value)
        new_value=this.min;
    
    }
   this.setValue (new_value, false);
   return new_value-old_value;
};

/**
@ --------------------------------------------------------------
@ Method: setPosition
@ Description: Sets the position of the slider thumb
@ Parameters:  value         - actual user value
@              call_callback - boolean to call Slider event
@              e             - mouse event
@ --------------------------------------------------------------
*/
Slider.prototype.setPosition = function(position, call_callback,e) {
        var range = this.max - this.min;
	if( position > this.width)
	{
	    position=this.width/2;
	}
        var value = (position / this.width) * range + this.min;
        this.nodes.thumb.setAttributeNS(null, "transform", 
                                 "translate(" + position + ", 0)");
        this.value = value;
        if (call_callback && this.event) this.event(value,e);
};

/**
@ --------------------------------------------------------------
@ Method: findPosition
@ Description: Finds the position of mouse event relative to Slider
@ Parameters:  e - mouse event
@ --------------------------------------------------------------
*/
Slider.prototype.findPosition = function(e) {

    var x = this.relativeMouseX();
    var y = this.relativeMouseY();
    
    var position = (this.direction == 90) ? y : x;
    position  -= this.height/2;

    if (position < 0) {
        this.setPosition(0, true, e);
    } else if (position > this.width) {
        this.setPosition(this.width, true, e);
    } else {
        this.setPosition(position, true, e);
    }
};


/*****        Event Handlers        *****/

/**
@ --------------------------------------------------------------
@ Method: mousedown
@ Description: called by mousedown event, initializes slider to be moved
@              and sets position based on mouse position
@ Parameters:  e             - mouse event
@ --------------------------------------------------------------
*/
Slider.prototype.mousedown = function(e) {
	MouseTracker.slider = this;
        this.active = true;
        this.findPosition(e);
};


/**
@ --------------------------------------------------------------
@ Method: mouseup
@ Description: called by mouseup event
@ Parameters:  e             - mouse event
@ --------------------------------------------------------------
*/
Slider.prototype.mouseup = function(e) {
	MouseTracker.slider = null;
        this.active = false;
};

/**
@ --------------------------------------------------------------
@ Method: mousemove
@ Description: called by mousemove event, handles sliding of Slider
@ Parameters:  e             - mouse event
@ --------------------------------------------------------------
*/
Slider.prototype.mousemove = function(e) {
        if (this.active) this.findPosition(e);
};


