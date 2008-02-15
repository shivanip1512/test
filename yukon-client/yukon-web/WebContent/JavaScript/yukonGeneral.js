
function yukonGeneral_moveOptionPositionInSelect(selectElement, direction) {
	
	//this contains all the selected indexes
	var index = new Array(); 
	index[0] = -1;
	//the array index to remember all the selection of the user :)
	indexNo = 0; 
	var selectList = selectElement;
	
	//loop to remember all the selection in the select element
	for (var i = 0; i < selectElement.options.length; i++){	
		if (selectElement.options[ i ].selected){
			index[indexNo] = i;
			indexNo ++;
		}
	}
	
	//if no options are selected, quit
	var routeIndex = selectElement.selectedIndex; 
    if (index[0] == -1) { //none selected
        return false;
    }
    
    //if selection is at the top, can't move up anymore
    if ( index[0] + direction == -1 && direction == -1){ 
    	return false;
    }
    
    //if selection is at the bottom and can't move down anymore
    if ( index.last() == selectElement.options.length - 1 && direction == 1){ 
    	return false;
    }
    
    //all the options of the select
    var options = selectElement.options;
    
    //clone the select element - for ie fix 
	var copy = $A(options).clone(); 

	//empty the whole select so that ie doesn't complain unable to insert
    while (selectElement.options.length > 0) { 
		selectElement.options[0] = null;
	}
	
	//the logic is going down = going up but reversed...
	
	//going down
	if (direction == 1){ 
		j = index.length;
		for (i = 0; i< index.length; i++){
			
			//we do it reverse of the going up
			var routeIndex = index[j - 1]; 
			var routeIndex2 = routeIndex + direction;
			
			var temp1 = copy[routeIndex]; 
			var temp2 = copy[routeIndex2];
			
			//swap the elements
			copy[routeIndex] = temp2;
			copy[routeIndex2] = temp1;
			
			//the reverse index is decremented
			j --; 
		}
	}
	//going up
	else{
		for (i = 0; i < index.length; i++){ 
		
			//simple swapping
			var routeIndex = index[i];
			var routeIndex2 = routeIndex + direction;
			
			var temp1 = copy[routeIndex];
			var temp2 = copy[routeIndex2];
				
			copy[routeIndex] = temp2;
			copy[routeIndex2] = temp1;
		}
	}
	
	//copy the array back to the select element
	for (var x = 0; x < copy.length; x++) { 
		selectElement.options[x] = copy[x];
	}
	
	//highlight all the previously selected elements in their new position
	for (j = 0; j < index.length; j++){ 
		selectList.options[index[j] + direction].selected = true;
	}
	
    return true;
}

function yukonGeneral_addOtpionToTopOfSelect(selectObj,optValue,optText) {
    
    // new option
    var newOpt = document.createElement("option");

    // get first option group - there will always be at least one [prototype function]
    var firstOptGroup = selectObj.immediateDescendants()[0];
    
    // get first option in first group
    var firstGroupOptions = firstOptGroup.immediateDescendants();
    
    // either stick new opt in before the opt that is currently first in the first option group [DOM function] 
    // or, if there are no current opts, just append it to that first group
    if(firstGroupOptions.length >0) {
    
        topOption = firstGroupOptions[0];
    
        // prevent duplicates from getting added to top of dropdown for each run of callback
        if(topOption.text != optText) {
            firstOptGroup.insertBefore(newOpt,topOption);
        }
    }
    else {
        firstOptGroup.appendChild(newOpt);
    }
    
    // why set the option value and text now instead of when we made it? IE..
    newOpt.value = optValue;
    newOpt.text = optText;

}

