
var canBeSaved = 0; //this indicate whether the format can be saved or not 
var prevHighlight = new Date(); //this is to indicate when is the last preview highlighting done
var errorHighlight = new Date(); //this is to indicate when is the last error highlighting done

// Method to add fields to the selected fields select input
function addToSelected(){
	
	var fields = $F('availableFields');
	var selectedFields = $('selectedFields');
	
	for(var i = 0;i < fields.length; i++){
		var tempOption = document.createElement('option');
		tempOption.text = fields[i];
		tempOption.setAttribute("format","");
		tempOption.setAttribute("maxLength","0");
		
		// IE hack
		try {
			selectedFields.add(tempOption, null);
		}
		catch(ex){
			selectedFields.add(tempOption);
		}
	}
	
	availableFieldsChanged();
}

// Method to remove fields from the selected fields select input
function removeFromSelected(){
	
	var selectedFields = $('selectedFields');
	
	for(var i = selectedFields.options.length - 1;i>=0; i--){
		if(selectedFields.options[i].selected){
			selectedFields.remove(i);
		}
	}
	
	selectedFieldsChanged();
}

function availableFieldsChanged() {

	var availableFields = $('availableFields');

	// Enable/Disable right arrow
	var selectedAvailableFields = $F(availableFields);
	if(selectedAvailableFields.length > 0) {
		$('rightArrowButton').disabled = false;
	} else {
		$('rightArrowButton').disabled = true;
	}
	
	updatePreview();
} 

function selectedFieldsChanged(){

	var selectedFields = $('selectedFields');

	// Enable/Disable left arrow
	var selectedSelectedFields = $F(selectedFields);
	if(selectedSelectedFields.length > 0) {
		$('leftArrowButton').disabled = false;
		
		// Enable/Disable up arrow
		if(selectedFields.selectedIndex == 0) {
			$('upArrowButton').disabled = true;
		} else {
			$('upArrowButton').disabled = false;
		}

		// Enable/Disable down arrow
		if(selectedFields[selectedFields.options.length - 1].selected) {
			$('downArrowButton').disabled = true;
		} else {
			$('downArrowButton').disabled = false;
		}

		// Show format ui if applicable
		if(selectedSelectedFields.length == 1) {
			
			var option = selectedFields.options[selectedFields.selectedIndex];
			
			var valueDiv = $("valueFormatDiv");
			var timestampDiv = $("timestampFormatDiv");
			var plainTextDiv = $("plainTextDiv");
			
			if(option.text.include('reading')) {
				valueDiv.style.display = "block";
				
				var format = getAttributeValue(option, 'format');
				var maxLength = getAttributeValue(option, 'maxLength');
				$('valueFormat').value = format;
				$('maxLength').value = maxLength;

				selectFormatOption($('valueFormatSelect'), format)
			} else {
				valueDiv.style.display = "none";
			}

			if(option.text.include('timestamp')) {
				timestampDiv.style.display = "block";
				var format = getAttributeValue(option, 'format');
				$('timestampFormat').value = format;
				selectFormatOption($('timestampFormatSelect'), format)
			} else {
				timestampDiv.style.display = "none";
			}

			if(option.text == 'Plain Text') {
				plainTextDiv.style.display = "block";
				var format = getAttributeValue(option, 'format');
				$('plainTextFormat').value = format;
			} else {
				plainTextDiv.style.display = "none";
			}
		}
		
	} else {
		$('leftArrowButton').disabled = true;
	}

	updatePreview();
}

function saveButton(){
	$("begin").action = "save";
	save();
	var errorMsg = "";
	
	var name = $("formatName").value;
	$('errorMsg').innerHTML = "&nbsp;";
	
	//name and fields cannot be empty;
	if ($("selectedFields").length == 0){
		errorMsg += "Must select at least 1 field in Selected Fields <br/>";
	}
	if (name.blank()){
		errorMsg += "Name cannot be empty <br/>";
		$("formatName").value = "";
	}
	if (name.length > 100){
		errorMsg += "Name cannot be longer than 100 characters <br/>";
	}
	//see whether fields + patterns can be saved or not
	if ( canBeSaved == 0 ){
		errorMsg += "invalid pattern detected. please check the pattern <br/>";
	}
	
	if (errorMsg.length > 0){
		$('errorMsg').innerHTML = "Error saving format: <br />" + errorMsg + "<br/>";
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

function save(){
	
	var fieldArray = $A(); 
	var selectedFields = $("selectedFields");
	//save all options in the selected select tag into array
	for( i = 0; i < selectedFields.length; i++){
		
		var currentField = $H();
		var currentOption = $(selectedFields.options[i]);
		
		currentField.field = currentOption.text;
		
		currentField.format = getAttributeValue(currentOption, 'format');
		currentField.maxLength = getAttributeValue(currentOption, 'maxLength');
		
		fieldArray.push(currentField);
	}
	
	// Set value of fieldArray to be submitted with form on save
	$("fieldArray").value = fieldArray.toJSON();

}

function getAttributeValue(option, attribute){
	
	var value = option.readAttribute(attribute);
	
	if(value == null){
		return "";
	}
	
	return value;
	
}

function cancelButton() {
	window.location = "/spring/dynamicBilling/overview";
}

function deleteButton(){
	formatId = $("formatId").value;
	
	//if no format, just return to overview. otherwise, delete the format
	if (formatId == -1){
		$("begin").action = "overview";
	}
	else{
		$("begin").action = "delete";
	}
	
	document.begin.submit();
}

//to disable or enable delimiter input textbox
function updateDelimiter(){

	var selection = $F("delimiterChoice");
	var delimiter = $("delimiter");
	
	if (selection == 'Custom'){
		delimiter.readOnly = false;
		delimiter.value = "";
		delimiter.focus();
	} else {
		delimiter.readOnly = 'readOnly';
		delimiter.value = $F("delimiterChoice");
	}
	updatePreview();
}

function updateFormat(input, attribute, method){ 

	var selectedFields = $("selectedFields");
	var index = selectedFields.selectedIndex;

    if (index != -1 && method != 'noUpdate'){

        if (method == 'maxLength') {
            selectedFields.options[index].setAttribute('maxLength', $(attribute).value);
        } else {
            var patternIndex = $(input).selectedIndex;
            if (patternIndex != null){
                var inputValue = $(input).options[patternIndex].innerText;

                if(method == 'select') {
                    switch(inputValue) {
                        case "No Format":
                            $(attribute).value = "";
                            break;
                        case "Custom":
                            break;
                        default:
                            $(attribute).value = inputValue;
                            break;
                    }
            
                    selectFormatOption($(input), $(attribute).value);
                }
                if (method == 'text') {
                    selectFormatOption($(input), $(attribute).value);
                }
            }
            selectedFields.options[index].setAttribute('format', $(attribute).value);
        }
    }
	//make sure to save to the array 
	save(); 
	
	updatePreview();
}

function selectFormatOption(select, value) { 
	
	if(value == '') {
		value = 'No Format';
	}
	for (var i = 0; i < select.options.length; i++){
		if (select.options[i].text == value){
			select.selectedIndex = i;
			return;
		}
	}
	select.selectedIndex = 1;
}

function updatePreview(){
	
	var theDiv = $('preview');

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
				formatId: $("formatId").value, 
				formatName: $("formatName").value
			}
		}
	);
	var currTime = new Date();
	if ( currTime - prevHighlight < 1000){
		//do nothing
	} else {
		//do highlighting and set new date 
		new Effect.Highlight(theDiv, {'duration': 1, 'startcolor': '#FFE900'});
		prevHighlight = new Date();
	}
}

//Helper function to make a html element display appear or disappear
function displayHelper(elem){
	elem.toggle();
}
