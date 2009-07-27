function setupRadioControl(id, radioName) {

	var radioControl = $('radioControl_' + id);
	var contentContainer = $('contentContainer_' + id);
	var contentDivs = $A(contentContainer.childElements());
	
	// need to determine which content radio is selected before main build loop
	var contentIdx = 0;
	var initalContentIdx = 0;
	contentDivs.each(function(el) {
		var initiallySelectedAttr = el.readAttribute('initiallySelected');
		if (initiallySelectedAttr == 'true') {
			initalContentIdx = contentIdx;
		}
		contentIdx++;
	});
	
	// main build loop
	var contentIdx = 0;
	contentDivs.each(function(el) {

		var selectorName = el.readAttribute('selectorName');
		var selectorTitle = el.readAttribute('selectorTitle');
		var selectorValue = el.readAttribute('selectorValue');
		var initiallySelected = contentIdx == initalContentIdx;
			
		// create elements
		var labelTag = document.createElement('label');
		labelTag.setAttribute('title', selectorTitle);
		
		var onclick = "makeContentVisible('" + id + "', " + contentIdx + ");";
		var radio = createRadioElement(radioName, initiallySelected, onclick);
		radio.setAttribute('value', selectorValue);

		var nameSpan = document.createElement('span');
		nameSpan.innerHTML = '&nbsp;' + selectorName + '&nbsp;&nbsp;';

		// add elements to radio control div
		labelTag.appendChild(radio);
		labelTag.appendChild(nameSpan);
		radioControl.appendChild(labelTag);

		// inc contentIdx
		if (initiallySelected) {
			initalContentIdx = contentIdx;
		}
		
		contentIdx++;
	});

	// show initial content
	makeContentVisible(id, initalContentIdx);
}

function makeContentVisible(id, contentIdx) {

	var contentContainer = $('contentContainer_' + id);
	var contentDivs = $A(contentContainer.childElements());
	
	var currentContentIdx = 0;
	contentDivs.each(function(el) {

		if (currentContentIdx == contentIdx) {
			el.show();
		} else {
			el.hide()
		}
		
		currentContentIdx++;
	})
}

// why!?
// http://cf-bill.blogspot.com/2006/03/another-ie-gotcha-dynamiclly-created.html
// http://stackoverflow.com/questions/118693/how-do-you-dynamically-create-a-radio-button-in-javascript-that-works-in-all-brow/119079#119079
// http://www.quirksmode.org/dom/innerhtml.html
function createRadioElement(name, checked, onclick) {
	
    var radioHtml = '<input type="radio" name="' + name + '"';
    if (checked) {
        radioHtml += ' checked="checked"';
    }
    if (onclick != undefined) {
    	radioHtml += ' onclick="' + onclick + '"';
    }
    radioHtml += '/>';

    var radioFragment = document.createElement('div');
    radioFragment.innerHTML = radioHtml;

    return radioFragment.firstChild;
}
