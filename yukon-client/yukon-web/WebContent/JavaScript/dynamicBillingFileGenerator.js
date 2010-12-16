var patternCanBeSaved = false; //this indicate whether the pattern of the format can be saved or not 
var formatInfoCanBeSaved = false; //this indicate whether the format informoration can be saved such as format name and delimiter.
var prevHighlight = new Date(); //this is to indicate when is the last preview highlighting done
var errorHighlight = new Date(); //this is to indicate when is the last error highlighting done

// Method to add fields to the selected fields select input
function addToSelected(roundingMode){
	
	var field = $F('availableFields');
	var selectedFields = $('selectedFields');
    
    var newOpt = document.createElement('option');
    newOpt.setAttribute('value', field);
    
    newOpt.setAttribute("format","");
    if(field.include('timestamp')) {
        newOpt.setAttribute("format","MM/dd/yyyy");
    } else if (field.include('reading')) {
        if (field.include('Demand')) {
            newOpt.setAttribute("format","##0.00");
        } else if (field.include('Consumption')) {
            newOpt.setAttribute("format","#####");
        }
    }
    newOpt.setAttribute("readingType", "ELECTRIC");
    newOpt.setAttribute("roundingMode", roundingMode);
	newOpt.setAttribute("maxLength","0");
    newOpt.setAttribute("padChar"," ");
    newOpt.setAttribute("padSide","none");
    newOpt.setAttribute("selected","selected");
    newOpt.appendChild(document.createTextNode(field));
    selectedFields.appendChild(newOpt);
    
    $('upArrowButton').disabled = false;
    
    // move down to newly added item, just makes IE sad though =~(
    selectedFields.selectedIndex = selectedFields.options.length - 1;
    
	selectedFieldsChanged();
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

function selectedFieldsChanged(){

	var selectedFields = $('selectedFields');

	// Enable/Disable left arrow
	var selectedSelectedFields = $F(selectedFields);
    
    
    
	if(selectedSelectedFields.length > 0) {
		$('removeButton').disabled = false;
		
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
        
		// Show format ui
		var option = selectedFields.options[selectedFields.selectedIndex];
		
        // Checks to see if the format is reading
		var valueDiv = $("valueFormatDiv");
		if(option.text.include('reading')) {
			valueDiv.style.display = "block";
			updateReadingFormatFields(option);
		} else {
			valueDiv.style.display = "none";
		}

        // Checks to see if the format is timestamp
		var timestampDiv = $("timestampFormatDiv");
        if(option.text.include('timestamp')) {
			timestampDiv.style.display = "block";
            updateTimestampFormatFields(timestampDiv, option);
		} else {
			timestampDiv.style.display = "none";
		}

        // Checks to see if the format is plain text
		var plainTextDiv = $("plainTextDiv");
        if(option.text == 'Plain Text') {
			plainTextDiv.style.display = "block";
            updatePlainTextFormatFields(plainTextDiv, option);
		} else {
			plainTextDiv.style.display = "none";
		}

        // Checks to see if it is non of the above cases
        var genericFormatDiv = $("genericFormatDiv");
        if(option.text.include('reading') || 
           option.text.include('timestamp') ||
           option.text == 'Plain Text'){
           
            genericFormatDiv.style.display = "none";
        } else {
			genericFormatDiv.style.display = "block";
            updateGenericFormatFields(genericFormatDiv, option);
        }
		
	} else {
		$('removeButton').disabled = true;
	}

	updatePreview();
}


function addFieldButton() {

    $('addFieldsDropDown').toggle();

    if ($('addFieldsDropDown').visible()) {
    
        $('addButton').disabled = true;
    
    } else {
    
        $('addButton').disabled = false;
        
    }
    
}

function saveButton(){
    $("begin").action = "save";
	save();
	var errorMsg = "";
	
	var name = $("formatName").value;
    $('errorMsg').innerHTML = "&nbsp;";
	
	//name and fields cannot be empty;
	if ($("selectedFields").length == 0){
		errorMsg += "Selected Fields must contain at least 1 available field<br/>";
	}
	if (name.blank()){
		errorMsg += "Name cannot be empty <br/>";
		$("formatName").value = "";
	}
	if (name.length > 100){
		errorMsg += "Name cannot be longer than 100 characters <br/>";
	}
	//see whether fields + patterns can be saved or not
	if ( !patternCanBeSaved){
		errorMsg += "invalid pattern detected. please check the pattern <br/>";
	}
	//see whether format information can be saved or not
    if ( !formatInfoCanBeSaved){
        errorMsg += "invalid format information detected. please check the format information <br/>";
    }
	if (errorMsg.length > 0){
		$('errorMsg').innerHTML = "Error saving format: <br />" + errorMsg + "<br/>";
		var currTime = new Date();
		//check if it's less than 2 sec = 2000 ms
		if ( currTime - errorHighlight >= 2000){
			//do highlighting and set new date 
			flashYellow($('errorMsg'), 2);
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
	    var currentOption = $(selectedFields.options[i]);
	    fieldArray.push($H({
		    'field': currentOption.text,
		    'format': getAttributeValue(currentOption, 'format'),
		    'maxLength': getAttributeValue(currentOption, 'maxLength'),
		    'padChar': getAttributeValue(currentOption, 'padChar'),
		    'padSide': getAttributeValue(currentOption, 'padSide'),
		    'readingType': getAttributeValue(currentOption, 'readingType'),
		    'roundingMode': getAttributeValue(currentOption, 'roundingMode')
		}));
	}
	
	// Set value of fieldArray to be submitted with form on save
	$("fieldArray").value = Object.toJSON(fieldArray);

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

function updateFormat(headerText, method){ 

	var selectedFields = $("selectedFields");
	var index = selectedFields.selectedIndex;
    
    if (index != -1 && method != 'noUpdate'){
        var option = selectedFields.options[index];

        switch (method){
            case "maxLength":
                var attributeValue = $(headerText+'MaxLength').value;
                option.setAttribute('maxLength', attributeValue);
                break;
            case "padChar":
                var attributeValue = $(headerText+'PadChar').value;
                selectPadCharSelectOption($(headerText+'PadCharSelect'), attributeValue);
                option.setAttribute('padChar', attributeValue);
                break;
            case "padSide":
                var attributeValue = $(headerText+'PadSide').value;
                option.setAttribute('padSide', attributeValue);
                updatePadSideSelect(selectedFields, attributeValue);
                checkPaddingSide(headerText, attributeValue);
                break;
            case "padCharSelect":
                var attributeValue = getAttributeValue(option, 'padChar');
                var selectDiv = $(headerText+'PadCharSelect');
                var patternIndex = selectDiv.selectedIndex;
                if (patternIndex != null) {
                    var inputValue = selectDiv.options[patternIndex].value;
                    switch(inputValue) {
                        case "Space":
                            attributeValue = " ";
                            break;
                        case "Zero":
                            attributeValue = "0";
                            break;
                        case "Custom":
                            attributeValue = "";
                            $(headerText+'PadChar').focus();
                            break;
                        default:
                            break;
                    }
                }
                option.setAttribute('padChar', attributeValue);
                $(headerText+'PadChar').value = attributeValue
                break;
            case "readingType":
                var attributeValue = $(headerText+'ReadingType').value;
                option.setAttribute('readingType', attributeValue);
                break;
            case "roundingMode":
                var attributeValue = $(headerText+'RoundingMode').value;
                option.setAttribute('roundingMode', attributeValue);
                break;
            case "formatWithSelect":
                var attributeValue = getAttributeValue(option, 'format');
                
                var selectDiv = $(headerText+'FormatSelect');
                var patternIndex = selectDiv.selectedIndex;
                if (patternIndex != null){
                    var inputValue = selectDiv.options[patternIndex].value;
                    switch(inputValue) {
                        case "No Format":
                            attributeValue = "";
                            break;
                        case "Custom":
                            $(headerText+'Format').focus();
                            break;
                        default:
                            attributeValue = inputValue;
                            break;
                    }
                }
                selectedFields.options[index].setAttribute('format', attributeValue);
                $(headerText+'Format').value = attributeValue;
                break;
            case "formatWithSelectText":
                var attributeValue = $(headerText+'Format').value;
                selectedFields.options[index].setAttribute('format', attributeValue);
                selectFormatOption($(headerText+'FormatSelect'), attributeValue);
                break;
            case "formatWithoutSelect":
                var attributeValue = $(headerText+'Format').value;
                selectedFields.options[index].setAttribute('format', attributeValue);
                break;
            default:
                break;
        }
    }
	//make sure to save to the array 
	save(); 
	
	updatePreview();
}

function checkPaddingSide(headerText, padSideValue){
    if (padSideValue == "none") {
        $(headerText+'PadChar').disabled = true;
        $(headerText+'PadCharSelect').disabled = true;
    } else {
        $(headerText+'PadChar').disabled = false;
        $(headerText+'PadCharSelect').disabled = false;
    }
}

function selectPadCharSelectOption(select, value) { 
    if (value == '0') {
        value = 'Zero';
    }
    if (value == ' ') {
        value = 'Space';
    }
    for (var i = 0; i < select.options.length; i++){
        if (select.options[i].text == value){
            select.selectedIndex = i;
            return;
        }
    }
    select.selectedIndex = 2;
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

function updateFormatName(){
    var theDiv = $('preview');
    
    var stringOutput = "";
    
    //save everything first so that everything that can be submitted is up to date 
    save();
    var URL = "updateFormatName";
    
    //the ajax request to the server
    new Ajax.Request(
        encodeURIComponent(URL), 
        {
            method: "post",
            
            //if successful, display the string to the page
            onSuccess: function(transport){
                var errorText = transport.responseText;
                if(errorText.length > 0) {
                    $('errorMsg').innerHTML = errorText;
                    formatInfoCanBeSaved = false;
                } else {
                    $('errorMsg').innerHTML = "&nbsp;";
                    formatInfoCanBeSaved = true;
                }
            },
            
            //any exception raised on the java side is displayed on the page
            onFailure: function(transport){
                theDiv.innerHTML = "<label style='color: red;'>" + transport.responseText + "</label>";
                patternCanBeSaved = false;
            },

            parameters: { 
                //this is format id, -1 if new format creation
                formatId: $("formatId").value, 
                formatName: $("formatName").value
            }
        }
    );
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
                patternCanBeSaved = true;
			},
			
			//any exception raised on the java side is displayed on the page
			onFailure: function(transport){
				theDiv.innerHTML = "<label style='color: red;'>" + transport.responseText + "</label>";
				patternCanBeSaved = false;
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
		flashYellow(theDiv);
		prevHighlight = new Date();
	}
}

//Helper function to make a html element display appear or disappear
function displayHelper(elem){
	elem.toggle();
}

//Helps with setting the padding side
function updatePadSideSelect(padSideSelect, padSide){
    for ( var i = 0; i < padSideSelect.options.length; i++) {
        if (padSideSelect.options[i].value == padSide){
            padSideSelect.options.selectedIndex = i;
        }
    }
}

// gets all the initial values for the current selected field
function updateReadingFormatFields(option){

    // gets the initial readingType value
    var readingTypeValue = getAttributeValue(option, 'readingType');
    $('readingReadingType').value = readingTypeValue;

    // gets the initial roundingMode value
    var roundingModeValue = getAttributeValue(option, 'roundingMode');
    $('readingRoundingMode').value = roundingModeValue;
    
    // gets the initial format value
    var format = getAttributeValue(option, 'format');
    $('readingFormat').value = format;
    selectFormatOption($('readingFormatSelect'), format);
    
    // get the initial maxLength value
    var maxLength = getAttributeValue(option, 'maxLength');
    $('readingMaxLength').value = maxLength;

    // get the initial padChar value
    var padChar = getAttributeValue(option, 'padChar');
    $('readingPadChar').value = padChar;
    selectPadCharSelectOption($('readingPadCharSelect'), padChar);

    // get the initial padChar value
    var padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect($('readingPadSide'), padSide);
    if (padSide == "none") {
        $('readingPadChar').disabled = true;
        $('readingPadCharSelect').disabled = true;
    } else {
        $('readingPadChar').disabled = false;
        $('readingPadCharSelect').disabled = false;
    }
}

// gets all the initial values for the current selected field
function updateTimestampFormatFields(timestampDiv, option){

    // gets the initial readingType value
    var readingTypeValue = getAttributeValue(option, 'readingType');
    $('timestampReadingType').value = readingTypeValue;

    // gets the initial format value
    var format = getAttributeValue(option, 'format');
    $('timestampFormat').value = format;
    selectFormatOption($('timestampFormatSelect'), format);
    
    // get the initial maxLength value
    var maxLength = getAttributeValue(option, 'maxLength');
    $('timestampMaxLength').value = maxLength;

    // get the initial padChar value
    var padChar = getAttributeValue(option, 'padChar');
    $('timestampPadChar').value = padChar;
    selectPadCharSelectOption($('timestampPadCharSelect'), padChar);

    // get the initial padChar value
    var padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect($('timestampPadSide'), padSide);
    if (padSide == "none") {
        $('timestampPadChar').disabled = true;
        $('timestampPadCharSelect').disabled = true;
    } else {
        $('timestampPadChar').disabled = false;
        $('timestampPadCharSelect').disabled = false;
    }
}

// gets all the initial values for the current selected field
function updatePlainTextFormatFields(plainTextDiv, option){

    // gets the initial format value
    var format = getAttributeValue(option, 'format');
    $('plainFormat').value = format;
    
}

// gets all the initial values for the current selected field
function updateGenericFormatFields(genericFormatDiv, option){

    // get the initial maxLength value
    var maxLength = getAttributeValue(option, 'maxLength');
    $('genericMaxLength').value = maxLength;

    // get the initial padChar value
    var padChar = getAttributeValue(option, 'padChar');
    $('genericPadChar').value = padChar;
    selectPadCharSelectOption($('genericPadCharSelect'), padChar);

    // get the initial padChar value
    var padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect($('genericPadSide'), padSide);
    if (padSide == "none") {
        $('genericPadChar').disabled = true;
        $('genericPadCharSelect').disabled = true;
    } else {
        $('genericPadChar').disabled = false;
        $('genericPadCharSelect').disabled = false;
    }
}
function toggleHelperPopup(id) {
    $(id).toggle();
}
