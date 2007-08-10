
var canBeSaved = 0; //this indicate whether the format can be saved or not 
var prevHighlight = new Date(); //this is to indicate when is the last preview highlighting done
var errorHighlight = new Date(); //this is to indicate when is the last error highlighting done


/* This function is to allow options in the select tags to move from
*  start to target field
*/
function moveLeftRight(start, target){ 
	var index = new Array();
	var chosen = new Array();
	index[0] = -1;
	indexNo = 0;
	var arSelected = new Array();
	
	// to remember all the selection of the user in the start select
	for (var i = 0; i < start.options.length; i++){	
		if (start.options[ i ].selected){
			index[indexNo] = i;
			indexNo ++;
		}
	}
	indexNo = 0;
	
	//if no field is selected, the index returned is -1. exit function
	if (index[0] == -1){
		return;
	}
	
	//all the text selected by the user from the start table
	for (var i = 0; i <index.length; i++){   
		chosen[i] = start.options[index[i]].text;
	}
	
	//initial array of the target fields
	for (var i = 0; i < target.options.length; i++){   
			arSelected.push(target.options[ i ].text);
	}
	
	//make sure available fields don't get removed, but add it to the selected
	if (target != $("Available")){ 
	
		//add to the available fields from selected fields
		for (i = 0; i < chosen.length; i++){   
			var tempOption = document.createElement('option');
			tempOption.text = chosen[i];
			tempOption.setAttribute("format","");
			try{
				target.add(tempOption, null);
			}
			catch(ex){
				target.add(tempOption);
			}
		}
	}
	
	//if it is from selected, remove them
	else{
		for( i = 0; i<index.length ; i++){
			index[i] = index[i] - i;
			start.remove(index[i]);
		}
	}
}

function saveButton(){
	$("begin").action = "save";
	save();
	var name="";
	var error = 0;
	var msg1 = "";
	var msg2 = "";
	var msg3 = "";
	
	$('nameWordsDiv').style.color = "black";
	$('selectedWordsDiv').style.color = "black";
	
	name = $("formatType").value;
	$('errorMsg').innerHTML = "&nbsp;";
	
	//name and fields cannot be empty;
	if ($("Selected").length == 0){
		msg1 = "Must select at least 1 field in Selected Fields <br/>";
		$('selectedWordsDiv').style.color = "red";
		error = 1;
	}
	if (name.blank() == true){
		msg2 = "Name cannot be empty <br/>";
		$("formatType").value = "";
		$('nameWordsDiv').style.color = "red";
		error = 1;
	}
	if (name.length > 100){
		msg2 = "Name cannot be longer than 100 characters <br/>";
		$('nameWordsDiv').style.color = "red";
		error = 1;
	}
	//see whether fields + patterns can be saved or not
	if ( canBeSaved == 0 ){
		msg3 = "invalid pattern detected. please check the pattern <br/>";
		$('selectedWordsDiv').style.color = "red";
		error = 1;
	}
	
	if (error == 1){
		$('errorMsg').innerHTML = "Error in saving: <br />" + msg2 + msg1 + msg3 +"<br/>";
		var currTime = new Date();
		//check if it's less than 2 sec = 2000 ms
		if ( currTime - errorHighlight < 2000){
			//do nothing
		}
		else{
			//do highlighting and set new date 
			new Effect.Highlight($('errorMsg'), {'duration': 2, 'startcolor': '#FFE900'});
			errorHighlight = new Date();
		}
	}
	else{
		document.begin.submit();
			
	}
}

//making sure everything that needs to be submitted are saved and ready
//save everything field and it's associated format into our array
function save(){
	
	//see prototype documentation - this basically initialises an object array
	var ourArray = $A(); 
	
	//save all options in the selected select tag into array
	for( i = 0; i < $("Selected").length; i++){
		var oneEntry = $H(); //this initialises a hash
		oneEntry.field = $("Selected").options[i].text;
		if ($($("Selected").options[i]).readAttribute('format') == null){
			oneEntry.format = "";
		}
		else{
			oneEntry.format = $($("Selected").options[i]).readAttribute('format');
		}
		ourArray.push(oneEntry);
	}
	
	//turn our array into JSON string format
	$("fieldArray").value = ourArray.toJSON();
	
	//save the delimiter. if a choice is made, use it, if not, then whatever the user puts
	if ( $("delimiterChoice").options[$("delimiterChoice").selectedIndex].text.include("Custom") == true){
		$("delimiter").value = $("delimiterMade").value;
	}
	else{
		$("delimiter").value = $("delimiterChoice").options[$("delimiterChoice").selectedIndex].value;
	}
}

function cancelButton() {
	$("begin").action = "overview";
	
	document.begin.submit();
}

function deleteButton(){
	formatId = $("availableFormat").value;
	
	//if no format, just return to overview. otherwise, delete the format
	if (formatId == -1){
		$("begin").action = "overview";
	}
	else{
		$("begin").action = "delete";
	}
	
	document.begin.submit();
}

//used to enable or disable selection arrows
function unfreeze(){ 
	var sel = "";
	var index = new Array();
	index[0] = -1;
	indexNo = 0;
	
	//used to determine how many are selected in the select tag
	for (var i = 0; i < $("Selected").options.length; i++){	
		if ($("Selected").options[ i ].selected){
			index[indexNo] = i;
			indexNo ++;
		}
	}
	
	//if nothing is selected in available, disable right arrow button.
	if ($("Available").selectedIndex >=0) {
		$("right").disabled = false;
	}else{
		$("right").disabled = true;
	}
	
	//if nothing is selected in selected tag, disable left arrow button
	if( $("Selected").selectedIndex >=0 ){
		$("left").disabled = false;
		
		//to enable or disable up and down arrow button for reordering fields
		if ( $("Selected").selectedIndex == 0 || index.last() == $("Selected").options.length - 1){
			
			//combined selection is at the top
			if ( $("Selected").selectedIndex == 0){ 
				$("up").disabled = true;
				$("down").disabled = false;
			}
			
			//combined selection is at the bottom
			if ( index.last() == $("Selected").options.length - 1) { 
				$("down").disabled = true;
				$("up").disabled = false;
			}
			
			//either all are selected or top and bottom are both selected
			if ($("Selected").selectedIndex == 0 && index.last() == $("Selected").options.length - 1 ){ 
				$("up").disabled = true;
				$("down").disabled = true;
			}
		}
		
		//anywhere else in Selected is selected
		if ( $("Selected").selectedIndex > 0 && index.last() < ($("Selected").length - 1)  ){ 
			$("up").disabled = false;
			$("down").disabled = false;
		}
		
		//only 1 selection has been made, let them view the field format text input
		//if there is reading or timestamp format associated with the field
		if (indexNo == 1){ 
			sel = $("Selected").options[$("Selected").selectedIndex].text;
			
			//a field with reading has been selected
			if (sel.include('reading') == true){ 
				$("valueDiv").style.display = "block";
				$("timestampDiv").style.display = "none";
				$("plainTextDiv").style.display = "none";
				seeFieldFormat($("valueFormat"), $("valueReccomended"));
				$("valueWords").innerHTML = "Reading Pattern for: " + sel;
			}
			
			//a field with timestamp has been selected
			if (sel.include('timestamp') == true){ 
				$("timestampDiv").style.display = "block";
				$("valueDiv").style.display = "none";
				$("plainTextDiv").style.display = "none";
				seeFieldFormat($("timestampFormat"), $("timestampReccomended"));
				$("timestampWords").innerHTML = "Timestamp Pattern for: " + sel;
			}
			
			//plain text input has been selected
			if (sel.include('Plain Text') == true){
				$("plainTextDiv").style.display = "block";
				$("valueDiv").style.display = "none";
				$("timestampDiv").style.display = "none";
				seeFieldFormat($("plainTextFormat"), "");
			}
			
			//no formatting found.
			if (sel.include('timestamp') == false && sel.include('reading') == false 
				&& sel.include('Plain Text') == false) {
				$("valueDiv").style.display = "none";
				$("plainTextDiv").style.display = "none";
				$("timestampDiv").style.display = "none";
			}
		}
		
		//more than 1 selection has been made, don't display the format editor
		else{
			$("valueDiv").style.display = "none";
			$("plainTextDiv").style.display = "none";
			$("timestampDiv").style.display = "none";
		}
		
	//nothing is selected, disable all buttons
	}else{
		$("left").disabled = true;
		$("up").disabled = true;
		$("down").disabled = true;
		$("valueDiv").style.display = "none";
		$("timestampDiv").style.display = "none";
		$("plainTextDiv").style.display = "none";
	}
}

//to disable or enable delimiter input textbox
function unfreezeDelim(){
	var dChoice = "";
	dChoice = $("delimiterChoice").options[$("delimiterChoice").selectedIndex].text;
	if ( dChoice.include("Custom") == true){
		$("delimiterMade").disabled = false;
		$("delimiterMade").value = "";
		$("delimiter").value = "";
		$("delimiterMade").focus();
	}
	else{
		$("delimiterMade").disabled = true;
		$("delimiterMade").value = $("delimiterChoice").options[$("delimiterChoice").selectedIndex].value;
	}
	updatePreviewDiv($('preview'));
}



//save the format into the correct selected fields option
function fieldFormatSaver(input){ 
	if ($("Selected").selectedIndex == -1){
		return;
	}
	else{
		$("Selected").options[$("Selected").selectedIndex].setAttribute("format",input.value);
	}
	
	//make sure to save to the array 
	save(); 
}


//to initialise the default format as well as to update whenever a user choose
//from a dropdown list
function defaultFormatInitiater( select, inputText){
	if (select.options[select.selectedIndex].text == "No Format") {
		inputText.value = "";
	}
	else if(select.options[select.selectedIndex].text.include("Custom") == true){
		inputText.value = "";
		inputText.focus();
	}
	else{
		inputText.value = select.options[select.selectedIndex].text;
	}
	//save the input text as the format
	fieldFormatSaver(inputText);

	//update the ajax request
	updatePreviewDiv($('preview'));
}

// function to display format associated with the field in input textbox
function seeFieldFormat(inputTag, selectionTag){ 
	var selFormat = "";
	var test = $("Selected").options;
	selFormat = $($("Selected").options[$("Selected").selectedIndex]).readAttribute('format');
	
	//if no format yet, give it nothing .
	if (selFormat == "" || selFormat == null){
		inputTag.value = "";
		selectionTag.selectedIndex = 0;
		return;
	}

	//display the tag if there is a format associated
	else {
	
		inputTag.value = selFormat;
		
		//assume it is custom format until proven otherwise
		selectionTag.selectedIndex = 1;
		
		//check if the format is the same as any of the selection
		//if it is the same, make the selection same like the input
		for (i = 0; i<selectionTag.length; i++){
			if (selectionTag.options[i].text == inputTag.value){
				selectionTag.selectedIndex = i;
				break;
			}
		}
	}
}

//preview in this case is the container for the ajax output. 
//the result is displayed in the parameter passed to this function
function updatePreviewDiv(theDiv){
	var stringOutput = "";
	
	//save everything first so that everything that can be submitted is up to date 
	save();
	var URL = "updatePreview";

	//the ajax request to the server
	new Ajax.Request(
		encodeURIComponent(URL), 
		{
			method: "post",
			
			//if successful, display the string to the page
			onSuccess: function(transport){
				theDiv.innerHTML = transport.responseText;
				canBeSaved = 1;
			},
			
			//any exception raised on the java side is displayed on the page
			onFailure: function(transport){
				theDiv.innerHTML = "<label style='color: red;'>" + transport.responseText + "</label>";
				canBeSaved = 0;
			},

			parameters: { 
			
				//send these to the server: delimiter, header, footer, field array, format id, name of format
				delimiter : $("delimiter").value ,
				header : $("headerField").value , 
				footer : $("footerField").value , 
				fieldArray : $("fieldArray").value, 
				
				//this is format id, -1 if new format creation
				availableFormat: $("availableFormat").value, 
				formatType: $("formatType").value
			}
		}
	);
	var currTime = new Date();
	if ( currTime - prevHighlight < 1000){
			//do nothing
		}
		else{
		
			//do highlighting and set new date 
			new Effect.Highlight(theDiv, {'duration': 1, 'startcolor': '#FFE900'});
			prevHighlight = new Date();
		}
}

//Helper function to make a html element display appear or disappear
function displayHelper(elem){
	elem.toggle();
}
