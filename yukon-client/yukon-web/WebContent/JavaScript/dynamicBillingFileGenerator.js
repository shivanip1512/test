var patternCanBeSaved = false; //this indicate whether the pattern of the format can be saved or not 
var formatInfoCanBeSaved = false; //this indicate whether the format informoration can be saved such as format name and delimiter.
var prevHighlight = new Date(); //this is to indicate when is the last preview highlighting done
var errorHighlight = new Date(); //this is to indicate when is the last error highlighting done

// Method to add fields to the selected fields select input
function addToSelected(roundingMode){
    
    var field = jQuery('#availableFields').val();
    var selectedFields = jQuery('#selectedFields');
    
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

    if (field.include('reading') ||
            field.include('timestamp')) {
        newOpt.setAttribute("readingType", "ELECTRIC");
    } else {
        newOpt.setAttribute("readingType", "DEVICE_DATA");
    }
    
    newOpt.setAttribute("readingChannel", "ONE");
    newOpt.setAttribute("roundingMode", roundingMode);
    newOpt.setAttribute("maxLength","0");
    newOpt.setAttribute("padChar"," ");
    newOpt.setAttribute("padSide","none");
    newOpt.setAttribute("selected","selected");
    newOpt.appendChild(document.createTextNode(field));
    selectedFields.append(newOpt);
    
    jQuery('#upArrowButton').removeAttr('disabled');
    
    // move down to newly added item, just makes IE sad though =~(
    var options = selectedFields.children("option");
    selectedFields.selectedIndex = options.length - 1;
    
    selectedFieldsChanged();
}

// Method to remove fields from the selected fields select input
function removeFromSelected(){
    
    var selectedFields = jQuery('#selectedFields');
    var options = selectedFields.children("option");
    
    for(var i = options.length - 1;i>=0; i--){
        if(options[i].selected){
            selectedFields.remove(i);
        }
    }
    
    selectedFieldsChanged();
}

function selectedFieldsChanged(){

    var selectedFields = jQuery('#selectedFields');

    // Enable/Disable left arrow
    var selectedSelectedFields = (selectedFields).val();
    
    if(selectedSelectedFields && selectedSelectedFields.length > 0) {
        jQuery('#removeButton').removeAttr('disabled');
        
        // Enable/Disable up arrow
        if(selectedFields.selectedIndex == 0) {
            jQuery('#upArrowButton').attr('disabled', 'disabled');
        } else {
            jQuery('#upArrowButton').removeAttr('disabled');
        }

        var options = selectedFields.find("option");
        
        // Enable/Disable down arrow
        if(options[options.length - 1].selected) {
            jQuery('#downArrowButton').attr('disabled', 'disabled');
        } else {
            jQuery('#downArrowButton').removeAttr('disabled');
        }
        
        // Show format ui
        var option = selectedFields.children("option:selected");
        
        // Checks to see if the format is reading
        var valueDiv = jQuery("#valueFormatDiv");
        if(option.text().include('reading')) {
            valueDiv.css('display',"block");
            updateReadingFormatFields(option);
        } else {
            valueDiv.css('display',"none");
        }

        // Checks to see if the format is timestamp
        var timestampDiv = jQuery("#timestampFormatDiv");
        if(option.text().include('timestamp')) {
            timestampDiv.css('display',"block");
            updateTimestampFormatFields(timestampDiv, option);
        } else {
            timestampDiv.css('display',"none");
        }

        // Checks to see if the format is plain text
        var plainTextDiv = jQuery("#plainTextDiv");
        if(option.text() == 'Plain Text') {
            plainTextDiv.css('display',"block");
            updatePlainTextFormatFields(plainTextDiv, option);
        } else {
            plainTextDiv.css('display',"none");
        }

        // Checks to see if it is non of the above cases
        var genericFormatDiv = jQuery("#genericFormatDiv");
        if(option.text().include('reading') || 
           option.text().include('timestamp') ||
           option.text() == 'Plain Text'){
           
            genericFormatDiv.css('display',"none");
        } else {
            genericFormatDiv.css('display',"block");
            updateGenericFormatFields(genericFormatDiv, option);
        }
        
    } else {
        jQuery('#removeButton').attr('disabled', 'disabled');
    }

    updatePreview();
}


function addFieldButton() {

    var sel = jQuery('#addFieldsDropDown');
    sel.toggle();

    if (sel.find(":visible").length > 0) {
    
        jQuery('#addButton').attr('disabled', 'disabled');
    
    } else {
    
        jQuery('#addButton').removeAttr('disabled');
        
    }
    
}

function saveButton(){
    var form = jQuery("#billingFormatForm");
    form.attr("action", "save");
    save();
    var errorMsg = [];
    
    var name = jQuery("#formatName").val();
    var err = jQuery('#errorMsg');
    err.html("&nbsp;");
    
    //name and fields cannot be empty;
    if (jQuery(selectedFields).val().length == 0){
        errorMsg.push(BILLING_ERRORS['fieldsNotEmpty']);
    }
    if (name.blank()){
        errorMsg.push(BILLING_ERRORS['nameNotEmpty']);
        jQuery("#formatName").val("");
    }
    if (name.length > 100){
        errorMsg.push(BILLING_ERRORS['nameMaxLength']);
    }
    //see whether fields + patterns can be saved or not
    if ( !patternCanBeSaved){
        errorMsg.push(BILLING_ERRORS['invalidPattern']);
        // TODO FIXME: This needs to highlight appropriate field(s)
    }
    //see whether format information can be saved or not
    if ( !formatInfoCanBeSaved){
        errorMsg.push(BILLING_ERRORS['invalidFormat']);
        // TODO FIXME: This needs to highlight appropriate field(s)
    }
    if (errorMsg.length > 0){
        err.html(BILLING_ERRORS['errorSaving'] +"<br />" + errorMsg.join("<br/>") + "<br/>");
        var currTime = new Date();
        //check if it's less than 2 sec = 2000 ms
        if ( currTime - errorHighlight >= 2000){
            //do highlighting and set new date 
            flashYellow(err, 2); // TODO FIXME
            errorHighlight = new Date();
        }
    }
    else{
//        document.billingFormatForm.submit();
//        MeteringBilling.do_save_format();
        form.submit();
    }
    return false;
}

function save() {
    var selectedFieldsEl = document.getElementById('selectedFields'),
        jsonifiedFieldArr,
        fieldArr = jQuery.map(selectedFieldsEl.options, function(currentOption, indexorkey) {
            return {
                'field': currentOption.text,
                'format': jQuery(currentOption).attr('format'),
                'maxLength': jQuery(currentOption).attr('maxLength'),
                'padChar': jQuery(currentOption).attr('padChar'),
                'padSide': jQuery(currentOption).attr('padSide'),
                'readingType': jQuery(currentOption).attr('readingType'),
                'readingChannel': jQuery(currentOption).attr('readingChannel'),
                'roundingMode': jQuery(currentOption).attr('roundingMode')
            };
        });
    jsonifiedFieldArr = JSON.stringify(fieldArr);
    // Set value of fieldArray to be submitted with form on save
    jQuery('#fieldArray').val(jsonifiedFieldArr);
}

function getAttributeValue(option, attribute){
    
    var value = option.attr(attribute);
    if(value == null){
        return "";
    }
    
    return value;
    
}

//to disable or enable delimiter input textbox
function updateDelimiter(){

    var selection = jQuery("#delimiterChoice").val();
    var delimiter = jQuery("#delimiter");
    
    if (selection == 'Custom'){
        delimiter.readOnly = false;
        delimiter.val("");
        delimiter.focus();
    } else {
        delimiter.readOnly = 'readOnly';
        delimiter.val(selection);
    }
    updatePreview();
}

function updateFormat(headerText, method){ 

//    var selectedFields = jQuery("#selectedFields");
//    var options = selectedFields.children("option");
    var selectedFields = document.getElementById('selectedFields');
    var options = selectedFields.options;
    var index = selectedFields.selectedIndex;
    
    if (index != -1 && method != 'noUpdate'){
        var option = options[index];

        switch (method){
            case "maxLength":
                var attributeValue = jQuery("#"+ headerText+'MaxLength').val();
                option.setAttribute('maxLength', attributeValue);
                break;
            case "padChar":
                var attributeValue = jQuery("#"+ headerText+'PadChar').val();
                selectPadCharSelectOption(jQuery("#"+ headerText+'PadCharSelect'), attributeValue);
                option.setAttribute('padChar', attributeValue);
                break;
            case "padSide":
                var attributeValue = jQuery("#"+ headerText+'PadSide').val();
                option.setAttribute('padSide', attributeValue);
                updatePadSideSelect(selectedFields, attributeValue);
                checkPaddingSide(headerText, attributeValue);
                break;
            case "padCharSelect":
                var attributeValue = getAttributeValue(option, 'padChar');
                var selectDiv = jQuery("#"+ headerText+'PadCharSelect');
                var patternSelected = selectDiv.find("option:selected");
                if (patternSelected.length > 0) {
                    var inputValue = patternSelected.val();
                    switch(inputValue) {
                        case "Space":
                            attributeValue = " ";
                            break;
                        case "Zero":
                            attributeValue = "0";
                            break;
                        case "Custom":
                            attributeValue = "";
                            jQuery("#"+ headerText+'PadChar').focus();
                            break;
                        default:
                            break;
                    }
                }
                option.setAttribute('padChar', attributeValue);
                jQuery("#"+ headerText+'PadChar').val(attributeValue);
                break;
            case "readingType":
                var attributeValue = jQuery("#"+ headerText+'ReadingType').val();
                option.setAttribute('readingType', attributeValue);
                break;
            case "readingChannel":
                var attributeValue = jQuery("#"+ headerText+'ReadingChannel').val();
                option.setAttribute('readingChannel', attributeValue);
                break;
            case "roundingMode":
                var attributeValue = jQuery("#"+ headerText+'RoundingMode').val();
                option.setAttribute('roundingMode', attributeValue);
                break;
            case "formatWithSelect":
                var attributeValue = getAttributeValue(option, 'format');
                
                var selectDiv = jQuery("#"+ headerText+'FormatSelect');
                var patternSelected = selectDiv.find("option:selected");
                if (patternSelected.length > 0){
                    var inputValue = patternSelected.val();
                    switch(inputValue) {
                        case "No Format":
                            attributeValue = "";
                            break;
                        case "Custom":
                            jQuery("#"+ headerText+'Format').focus();
                            break;
                        default:
                            attributeValue = inputValue;
                            break;
                    }
                }
                options[index].setAttribute('format', attributeValue);
                jQuery("#"+ headerText+'Format').val(attributeValue);
                break;
            case "formatWithSelectText":
                var attributeValue = jQuery("#"+ headerText+'Format').val();
                options[index].setAttribute('format', attributeValue);
                selectFormatOption(jQuery("#"+ headerText+'FormatSelect'), attributeValue);
                break;
            case "formatWithoutSelect":
                var attributeValue = jQuery("#"+ headerText+'Format').val();
                options[index].setAttribute('format', attributeValue);
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
        jQuery("#"+ headerText+'PadChar').attr('disabled','disabled');
        jQuery("#"+ headerText+'PadCharSelect').attr('disabled','disabled');
    } else {
        jQuery("#"+ headerText+'PadChar').removeAttr('disabled');
        jQuery("#"+ headerText+'PadCharSelect').removeAttr('disabled');
    }
}

function selectPadCharSelectOption(jQuerySelect, value) { 
    if (value == '0') {
        value = 'Zero';
    }
    if (value == ' ') {
        value = 'Space';
    }
    var options = jQuerySelect.children("option");
    for (var i = 0; i < options.length; i++){
        if (options[i].text == value){
            jQuerySelect.selectedIndex = i;
            return;
        }
    }
    jQuerySelect.selectedIndex = 2;
}

// TODO FIXME VERIFY THIS WORKS
function selectFormatOption(jQuerySelect, value) { 
    
    if(value == '') {
        value = 'No Format';
    }
    var options = jQuerySelect.children("option");
    for (var i = 0; i < options.length; i++){
        if (options[i].text == value){
            jQuerySelect.selectedIndex = i;
            return;
        }
    }
    jQuerySelect.selectedIndex = 2;
}

function updateFormatName(){
    var theDiv = jQuery('#preview');
    
    //save everything first so that everything that can be submitted is up to date 
    save();
    var URL = "/dynamicBilling/updateFormatName";

    //the ajax request to the server
    new Ajax.Request(
        URL, //encodeURIComponent(URL), 
        {
            method: "post",
            
            //if successful, display the string to the page
            onSuccess: function(transport){
                var errorText = transport.responseText;
                if(errorText.length > 0) {
                    jQuery('#errorMsg').html(errorText);
                    formatInfoCanBeSaved = false;
                } else {
                    jQuery('#errorMsg').html("&nbsp;");
                    formatInfoCanBeSaved = true;
                }
            },
            
            //any exception raised on the java side is displayed on the page
            onFailure: function(transport){
                theDiv.html("<label style='color: red;'>" + transport.responseText + "</label>");
                patternCanBeSaved = false;
            },

            parameters: { 
                //this is format id, -1 if new format creation
                formatId: jQuery("#formatId").val(), 
                formatName: jQuery("#formatName").val()
            }
        }
    );
}

function updatePreview(){
    
    var theDiv = jQuery('#preview');

    //save everything first so that everything that can be submitted is up to date 
    save();
    var URL = "/dynamicBilling/updatePreview";

    var currTime = new Date();
    if ( currTime - prevHighlight < 1000){
        //do nothing
    } else {
        //do highlighting and set new date 
        flashYellow(theDiv);
        prevHighlight = new Date();
    }
//    if (jQuery("formatName").val() == undefined) {
//        return;
//    }
    //the ajax request to the server
    new Ajax.Request(
        URL, //encodeURIComponent(URL), 
        {
            method: "post",
            
            //if successful, display the string to the page
            onSuccess: function(transport){
                theDiv.html(transport.responseText);
                patternCanBeSaved = true;
            },
            
            //any exception raised on the java side is displayed on the page
            onFailure: function(transport){
                theDiv.html("<label style='color: red;'>" + transport.responseText + "</label>");
                patternCanBeSaved = false;
            },

            parameters: { 
            
                //send these to the server: delimiter, header, footer, field array, format id, name of format
                delimiter : jQuery("#delimiter").val() ,
                header : jQuery("#headerField").val() , 
                footer : jQuery("#footerField").val() , 
                fieldArray : jQuery("#fieldArray").val(), 
                
                //this is format id, -1 if new format creation
                formatId: jQuery("#formatId").val(), 
                formatName: jQuery("#formatName").val()
            }
        }
    );
}

//Helper function to make a html element display appear or disappear
function displayHelper(elem){
    elem.toggle();
}

//Helps with setting the padding side
function updatePadSideSelect(padSideSelect, padSide){
    var options = padSideSelect.children("option");
    for ( var i = 0; i < options.length; i++) {
        if (options[i].value == padSide){
            padSideSelect.selectedIndex = i;
        }
    }
}

// gets all the initial values for the current selected field
function updateReadingFormatFields(option){

    // gets the initial readingType value
    var readingTypeValue = getAttributeValue(option, 'readingType');
    jQuery('#readingReadingType').val(readingTypeValue);

    // gets the initial readingChannel value
    var readingChannelValue = getAttributeValue(option, 'readingChannel');
    jQuery('#readingReadingChannel').val(readingChannelValue);

    // gets the initial roundingMode value
    var roundingModeValue = getAttributeValue(option, 'roundingMode');
    jQuery('#readingRoundingMode').val(roundingModeValue);
    
    // gets the initial format value
    var format = getAttributeValue(option, 'format');
    jQuery('#readingFormat').val(format);
    selectFormatOption(jQuery('#readingFormatSelect'), format);
    
    // get the initial maxLength value
    var maxLength = getAttributeValue(option, 'maxLength');
    jQuery('#readingMaxLength').val(maxLength);

    // get the initial padChar value
    var padChar = getAttributeValue(option, 'padChar');
    jQuery('#readingPadChar').val(padChar);
    selectPadCharSelectOption(jQuery('#readingPadCharSelect'), padChar);

    // get the initial padChar value
    var padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect(jQuery('#readingPadSide'), padSide);
    if (padSide == "none") {
        jQuery('#readingPadChar').attr('disabled', 'disabled');
        jQuery('#readingPadCharSelect').attr('disabled', 'disabled');
    } else {
        jQuery('#readingPadChar').removeAttr('disabled');
        jQuery('#readingPadCharSelect').removeAttr('disabled');
    }
}

// gets all the initial values for the current selected field
function updateTimestampFormatFields(timestampDiv, option){

    // gets the initial readingType value
    var readingTypeValue = getAttributeValue(option, 'readingType');
    jQuery('#timestampReadingType').val(readingTypeValue);

    // gets the initial readingChannel value
    var readingChannelValue = getAttributeValue(option, 'readingChannel');
    jQuery('#timestampReadingChannel').val(readingChannelValue);
    
    // gets the initial format value
    var format = getAttributeValue(option, 'format');
    jQuery('#timestampFormat').val(format);
    selectFormatOption(jQuery('#timestampFormatSelect'), format);
    
    // get the initial maxLength value
    var maxLength = getAttributeValue(option, 'maxLength');
    jQuery('#timestampMaxLength').val(maxLength);

    // get the initial padChar value
    var padChar = getAttributeValue(option, 'padChar');
    jQuery('#timestampPadChar').val(padChar);
    selectPadCharSelectOption(jQuery('#timestampPadCharSelect'), padChar);

    // get the initial padChar value
    var padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect(jQuery('#timestampPadSide'), padSide);
    if (padSide == "none") {
        jQuery('#timestampPadChar').attr('disabled', 'disabled');
        jQuery('#timestampPadCharSelect').attr('disabled', 'disabled');
    } else {
        jQuery('#timestampPadChar').removeAttr('disabled');
        jQuery('#timestampPadCharSelect').removeAttr('disabled');
    }
}

// gets all the initial values for the current selected field
function updatePlainTextFormatFields(plainTextDiv, option){

    // gets the initial format value
    var format = getAttributeValue(option, 'format');
    jQuery('#plainFormat').val(format);
    
}

// gets all the initial values for the current selected field
function updateGenericFormatFields(genericFormatDiv, option){

    // gets the initial readingType value
    var readingTypeValue = getAttributeValue(option, 'readingType');
    jQuery('#genericReadingType').val(readingTypeValue);
    
    // get the initial maxLength value
    var maxLength = getAttributeValue(option, 'maxLength');
    jQuery('#genericMaxLength').val(maxLength);

    // get the initial padChar value
    var padChar = getAttributeValue(option, 'padChar');
    jQuery('#genericPadChar').val(padChar);
    selectPadCharSelectOption(jQuery('#genericPadCharSelect'), padChar);

    // get the initial padChar value
    var padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect(jQuery('#genericPadSide'), padSide);
    if (padSide == "none") {
        jQuery('#genericPadChar').attr('disabled', 'disabled');
        jQuery('#genericPadCharSelect').attr('disabled', 'disabled');
    } else {
        jQuery('#genericPadChar').removeAttr('disabled');
        jQuery('#genericPadCharSelect').removeAttr('disabled');
    }
}
function toggleHelperPopup(id) {
    jQuery('#'+ id).toggle();
}
