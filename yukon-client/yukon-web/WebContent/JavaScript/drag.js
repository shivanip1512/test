
/*beginDrag is called on a mousedown event in order to be able to drag an element with constraints specified:
The parameters specify the contraints and directions and function to be called.
ct:constraint on top
cb:constraint on bottom
cr:constraint on right
cl:contraint on left
f:function to be called each time mouse is moved
direction: specify 'horizontal' or 'vertical' depending on the direction 
*/

function beginDrag(event,ct, cb, cr, cl, f, direction,id) {

  var elementToDrag;
  var x, y;
  var deltaX, deltaY;
  
 if(id && id != '') {

elementToDrag = document.getElementById(id);
	
	}
	else {

 if (browser.isMicrosoft)
      elementToDrag = window.event.srcElement;
    if (browser.isNetscape)
      elementToDrag = event.target;
}
  
 if (browser.isMicrosoft) {
    x = window.event.clientX + document.documentElement.scrollLeft
      + document.body.scrollLeft;
    y = window.event.clientY + document.documentElement.scrollTop
      + document.body.scrollTop;
  }
  if (browser.isNetscape) {
    x = event.clientX + window.scrollX;
    y = event.clientY + window.scrollY;
  }

deltaX  = x - parseInt(elementToDrag.style.left, 10);
  deltaY  = y - parseInt(elementToDrag.style.top,  10);

  if (isNaN(deltaX)) deltaX = 0;
  if (isNaN(deltaY)) deltaY  = 0;


elementToDrag.style.zIndex = ++elementToDrag.style.zIndex;

if (browser.isMicrosoft) {
    document.attachEvent("onmousemove", moveHandler);
    document.attachEvent("onmouseup",   dragStop);
    window.event.cancelBubble = true;
    window.event.returnValue = false;
  }
  if (browser.isNetscape) {
    document.addEventListener("mousemove", moveHandler,   true);
    document.addEventListener("mouseup",   dragStop, true);
    event.preventDefault();
    event.stopPropagation();
  }


function moveHandler(event) {

var x2, y2;

if (browser.isMicrosoft) {
    x2 = window.event.clientX + document.documentElement.scrollLeft
      + document.body.scrollLeft;
    y2 = window.event.clientY + document.documentElement.scrollTop
      + document.body.scrollTop;
  }
  if (browser.isNetscape) {
    x2 = event.clientX + window.scrollX;
    y2 = event.clientY + window.scrollY;
  }

 if (direction == 'vertical') { 

  elementToDrag.style.top  = (y2 - deltaY) + "px";
  if ((y2-deltaY) < ct)
  {
  	elementToDrag.style.top = ct + "px";
  }
  else if ((y2-deltaY) > cb) 
  {
    elementToDrag.style.top = cb + "px";
  }
 }
else if (direction == 'horizontal') {
     
      elementToDrag.style.left  = (x2 - deltaX) + "px";
        if ((x2-deltaX) < cl)
        {
  	   elementToDrag.style.left = cl + "px";
        }
        else if ((x2-deltaX) > cr) 
        {
           elementToDrag.style.left = cr + "px";
        }
		
		
		
}
	
 eval(f);
  
if (browser.isMicrosoft) {
    window.event.cancelBubble = true;
    window.event.returnValue = false;
  }
  if (browser.isNetscape)
    event.preventDefault();
}


function dragStop(event) {

if (browser.isMicrosoft) {
    document.detachEvent("onmousemove", moveHandler);
    document.detachEvent("onmouseup",   dragStop);
  }
  if (browser.isNetscape) {
    document.removeEventListener("mousemove", moveHandler,   true);
    document.removeEventListener("mouseup",   dragStop, true);
  }
}
}



