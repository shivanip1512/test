var patternCanBeSaved = false; //this indicate whether the pattern of the format can be saved or not 
var formatInfoCanBeSaved = false; //this indicate whether the format informoration can be saved such as format name and delimiter.
var prevHighlight = new Date(); //this is to indicate when is the last preview highlighting done
var errorHighlight = new Date(); //this is to indicate when is the last error highlighting done

// Method to add fields to the selected fields select input
function addToSelected (roundingMode) {
    
    var field = $('#availableFields').val(),
        selectedFields = $('#selectedFields'),
        newOpt = document.createElement('option'),
        options;
    
    newOpt.setAttribute('value', field);
    
    newOpt.setAttribute('format','');
    if (field.indexOf('timestamp') !== -1) {
        newOpt.setAttribute('format','MM/dd/yyyy');
    } else if (field.indexOf('reading') !== -1) {
        if (field.indexOf('Demand') !== -1) {
            newOpt.setAttribute('format','##0.00');
        } else if (field.indexOf('Consumption') !== -1) {
            newOpt.setAttribute('format','#####');
        }
    }

    if ((field.indexOf('reading') !== -1) ||
            (field.indexOf('timestamp') !== -1)) {
        newOpt.setAttribute('readingType', 'ELECTRIC');
    } else {
        newOpt.setAttribute('readingType', 'DEVICE_DATA');
    }

    newOpt.setAttribute('readingChannel', 'ONE');
    newOpt.setAttribute('roundingMode', roundingMode);
    newOpt.setAttribute('maxLength','0');
    newOpt.setAttribute('padChar',' ');
    newOpt.setAttribute('padSide','none');
    newOpt.setAttribute('selected','selected');
    newOpt.appendChild(document.createTextNode(field));
    selectedFields.append(newOpt);

    $('#upArrowButton').prop('disabled', false);

    // move down to newly added item, just makes IE sad though =~(
    options = selectedFields.children('option');
    selectedFields.selectedIndex = options.length - 1;

    selectedFieldsChanged();
}

// Method to remove fields from the selected fields select input
function removeFromSelected () {
    
    var selectedFields = $('#selectedFields'),
        options = selectedFields.children('option'),
        i;

    for (i = options.length - 1; i >= 0; i -= 1) {
        // this code supports multi-select, but the select element is currently not configured for it
        if (options[i].selected) {
            selectedFields[0].remove(i);
        }
    }

    selectedFieldsChanged();
}

function selectedFieldsChanged () {

    var selectedFields = $('#selectedFields'),
        options,
        option,
        valueDiv,
        selectedSelectedFields,
        timestampDiv,
        plainTextDiv,
        genericFormatDiv;

    // Enable/Disable left arrow
    selectedSelectedFields = selectedFields.val();

    if (selectedSelectedFields && selectedSelectedFields.length > 0) {
        $('#removeButton').prop('disabled', false);

        // Enable/Disable up arrow
        if (selectedFields[0].selectedIndex === 0) {
            $('#upArrowButton').prop('disabled', true);
        } else {
            $('#upArrowButton').prop('disabled', false);
        }

        options = selectedFields.find('option');

        // Enable/Disable down arrow
        if (options[options.length - 1].selected) {
            $('#downArrowButton').prop('disabled', true);
        } else {
            $('#downArrowButton').prop('disabled', false);
        }

        // Show format ui
        option = selectedFields.children('option:selected');

        // Checks to see if the format is reading
        valueDiv = $('#valueFormatDiv');
        if (-1 !== option.text().indexOf('reading')) {
            valueDiv.css('display', 'block');
            updateReadingFormatFields(option);
        } else {
            valueDiv.css('display', 'none');
        }

        // Checks to see if the format is timestamp
        timestampDiv = $('#timestampFormatDiv');
        if (-1 !== option.text().indexOf('timestamp')) {
            timestampDiv.css('display','block');
            updateTimestampFormatFields(timestampDiv, option);
        } else {
            timestampDiv.css('display','none');
        }

        // Checks to see if the format is plain text
        plainTextDiv = $('#plainTextDiv');
        if (option.text() === 'Plain Text') {
            plainTextDiv.css('display','block');
            updatePlainTextFormatFields(plainTextDiv, option);
        } else {
            plainTextDiv.css('display','none');
        }

        // Checks to see if it is non of the above cases
        genericFormatDiv = $('#genericFormatDiv');
        if (-1 !== option.text().indexOf('reading') || 
            -1 !== option.text().indexOf('timestamp') ||
            option.text() === 'Plain Text') {
            genericFormatDiv.css('display','none');
        } else {
            genericFormatDiv.css('display','block');
            updateGenericFormatFields(genericFormatDiv, option);
        }
    } else {
        $('#removeButton').prop('disabled', true);
    }

    updatePreview();
}


function addFieldButton () {

    var sel = $('#addFieldsDropDown');
    sel.toggle();

    if (sel.find(':visible').length > 0) {
        $('#addButton').prop('disabled', true);
    } else {
        $('#addButton').prop('disabled', false);
    }
    
}

function saveButton () {
    var form = $('#billingFormatForm'),
        errorMsg = [],
        name,
        err,
        currTime;

    form.attr('action', 'save');
    save();
    
    name = $('#formatName').val();
    err = $('#errorMsg');
    err.html('&nbsp;');
    
    //name and fields cannot be empty;
    if ($('#selectedFields option').length === 0) {
        errorMsg.push(BILLING_ERRORS['fieldsNotEmpty']);
    }
    if (0 === name.length || 0 === name.trim().length) {
        errorMsg.push(BILLING_ERRORS['nameNotEmpty']);
        $('#formatName').val('');
    }
    if (name.length > 100) {
        errorMsg.push(BILLING_ERRORS['nameMaxLength']);
    }
    //see whether fields + patterns can be saved or not
    if (!patternCanBeSaved) {
        errorMsg.push(BILLING_ERRORS['invalidPattern']);
        // TODO FIXME: This needs to highlight appropriate field(s)
    }
    //see whether format information can be saved or not
    if (!formatInfoCanBeSaved) {
        errorMsg.push(BILLING_ERRORS['invalidFormat']);
        // TODO FIXME: This needs to highlight appropriate field(s)
    }
    if (errorMsg.length > 0) {
        err.html(BILLING_ERRORS['errorSaving'] +'<br />' + errorMsg.join('<br/>') + '<br/>');
        currTime = new Date();
        //check if it's less than 2 sec = 2000 ms
        if ( currTime - errorHighlight >= 2000) {
            //do highlighting and set new date 
            err.flash();
            errorHighlight = new Date();
        }
    }
    else {
        $.post(yukon.url('/dynamicBilling/save.json'), $('#billingFormatForm').serialize()).done(function(result) {
            $('#billing_setup_overview').html(result);
            yukon.ami.billing.reset_setup_tab();
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            debug.log('saveButton: errorThrown:' + errorThrown);
        });
    }
    return false;
}

function save () {
    var selectedFieldsEl = document.getElementById('selectedFields'),
        jsonifiedFieldArr,
        fieldArr = $.map(selectedFieldsEl.options, function(currentOption, indexorkey) {
            return {
                'field': currentOption.text,
                'format': $(currentOption).attr('format'),
                'maxLength': $(currentOption).attr('maxLength'),
                'padChar': $(currentOption).attr('padChar'),
                'padSide': $(currentOption).attr('padSide'),
                'readingType': $(currentOption).attr('readingType'),
                'readingChannel': $(currentOption).attr('readingChannel'),
                'roundingMode': $(currentOption).attr('roundingMode')
            };
        });
    jsonifiedFieldArr = JSON.stringify(fieldArr);
    // Set value of fieldArray to be submitted with form on save
    $('#fieldArray').val(jsonifiedFieldArr);
}

function getAttributeValue (option, attribute) {
    var value = $(option).attr(attribute);

    if(value == null){
        return '';
    }
    return value;
}

//to disable or enable delimiter input textbox
function updateDelimiter () {
    var selection = $('#delimiterChoice').val(),
        delimiter = $('#delimiter');

    if (selection === 'Custom'){
        delimiter.prop('readOnly', false);
        delimiter.val('');
        delimiter.focus();
    } else {
        delimiter.prop('readOnly', true);
        delimiter.val(selection);
    }
    updatePreview();
}

function updateFormat (headerText, method) { 

    var selectedFields = $('#selectedFields'),
        options = selectedFields.children('option'),
        index = selectedFields[0].selectedIndex,
        option,
        attributeValue,
        selectDiv,
        patternSelected,
        inputValue;
    
    if (index !== -1 && method !== 'noUpdate') {
        option = options[index];

        switch (method) {
            case 'maxLength':
                attributeValue = $('#'+ headerText+'MaxLength').val();
                option.setAttribute('maxLength', attributeValue);
                break;
            case 'padChar':
                attributeValue = $('#'+ headerText+'PadChar').val();
                selectPadCharSelectOption($('#'+ headerText+'PadCharSelect'), attributeValue);
                option.setAttribute('padChar', attributeValue);
                break;
            case 'padSide':
                attributeValue = $('#'+ headerText+'PadSide').val();
                option.setAttribute('padSide', attributeValue);
                updatePadSideSelect($('#' + headerText + 'PadSide'), attributeValue);
                checkPaddingSide(headerText, attributeValue);
                break;
            case 'padCharSelect':
                attributeValue = getAttributeValue(option, 'padChar');
                selectDiv = $('#'+ headerText+'PadCharSelect');
                patternSelected = selectDiv.find('option:selected');
                if (patternSelected.length > 0) {
                    inputValue = patternSelected.val();
                    switch(inputValue) {
                        case 'Space':
                            attributeValue = ' ';
                            break;
                        case 'Zero':
                            attributeValue = '0';
                            break;
                        case 'Custom':
                            attributeValue = '';
                            $('#'+ headerText+'PadChar').focus();
                            break;
                        default:
                            break;
                    }
                }
                option.setAttribute('padChar', attributeValue);
                $('#'+ headerText+'PadChar').val(attributeValue);
                break;
            case 'readingType':
                attributeValue = $('#'+ headerText+'ReadingType').val();
                option.setAttribute('readingType', attributeValue);
                break;
            case 'readingChannel':
                attributeValue = $('#'+ headerText+'ReadingChannel').val();
                option.setAttribute('readingChannel', attributeValue);
                break;
            case 'roundingMode':
                attributeValue = $('#'+ headerText+'RoundingMode').val();
                option.setAttribute('roundingMode', attributeValue);
                break;
            case 'formatWithSelect':
                attributeValue = getAttributeValue(option, 'format');
                
                selectDiv = $('#'+ headerText+'FormatSelect');
                patternSelected = selectDiv.find('option:selected');
                if (patternSelected.length > 0) {
                    inputValue = patternSelected.val();
                    switch(inputValue) {
                        case 'No Format':
                            attributeValue = '';
                            break;
                        case 'Custom':
                            $('#'+ headerText+'Format').focus();
                            break;
                        default:
                            attributeValue = inputValue;
                            break;
                    }
                }
                options[index].setAttribute('format', attributeValue);
                $('#'+ headerText+'Format').val(attributeValue);
                break;
            case 'formatWithSelectText':
                attributeValue = $('#'+ headerText+'Format').val();
                options[index].setAttribute('format', attributeValue);
                selectFormatOption($('#'+ headerText+'FormatSelect'), attributeValue);
                break;
            case 'formatWithoutSelect':
                attributeValue = $('#'+ headerText+'Format').val();
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

function checkPaddingSide (headerText, padSideValue) {
    $('#' + headerText + 'PadChar').prop('disabled', padSideValue === 'none');
    $('#' + headerText + 'PadCharSelect').prop('disabled', padSideValue === 'none');
}

function selectPadCharSelectOption (jQuerySelect, value) {
    var options,
        i;

    if (value === '0') {
        value = 'Zero';
    }
    if (value === ' ') {
        value = 'Space';
    }
    options = jQuerySelect.children('option');
    for (i = 0; i < options.length; i += 1) {
        if (options[i].text == value) {
            jQuerySelect[0].selectedIndex = i;
            return;
        }
    }
    jQuerySelect[0].selectedIndex = 2;
}

function selectFormatOption(jQuerySelect, value) { 
    var options,
        i;

    if (value === '') {
        value = 'No Format';
    }
    options = jQuerySelect.children('option');
    for (i = 0; i < options.length; i++){
        if (options[i].text == value){
            jQuerySelect[0].selectedIndex = i;
            return;
        }
    }
    jQuerySelect[0].selectedIndex = 2;
}

function updateFormatName () {
    var theDiv = $('#preview'),
        url;

    //save everything first so that everything that can be submitted is up to date 
    save();
    url = yukon.url('/dynamicBilling/updateFormatName');

    //the ajax request to the server
    $.ajax({
        url: url,
        type: 'POST',
        data: { 
            //this is format id, -1 if new format creation
            formatId: $('#formatId').val(), 
            formatName: $('#formatName').val()
        }
    }).done(function (data, textStatus, jqXHR) {
        //if successful, display the string to the page
        var responseText = jqXHR.responseText;
        if (responseText.length > 0) {
            $('#errorMsg').html(responseText);
            formatInfoCanBeSaved = false;
        } else {
            $('#errorMsg').html('&nbsp;');
            formatInfoCanBeSaved = true;
        }
    }).fail(function (jqXHR, textStatus, errorThrown) {
        //any exception raised on the java side is displayed on the page
        theDiv.html("<label style='color: red;'>" + transport.jqXHR + '</label>');
        patternCanBeSaved = false;
    });
}

function updatePreview () {

    var theDiv = $('#preview'),
        url,
        currTime;

    //save everything first so that everything that can be submitted is up to date 
    save();
    url = yukon.url('/dynamicBilling/updatePreview');

    currTime = new Date();
    if (currTime - prevHighlight < 1000){
        //do nothing
    } else {
        //do highlighting and set new date 
        theDiv.flash();
        prevHighlight = new Date();
    }

    //the ajax request to the server
    $.ajax({
        url: url,
        type: 'POST',
        data: { 
            //send these to the server: delimiter, header, footer, field array, format id, name of format
            delimiter : $('#delimiter').val(),
            header : $('#headerField').val(), 
            footer : $('#footerField').val(), 
            fieldArray : $('#fieldArray').val(), 

            //this is format id, -1 if new format creation
            formatId: $('#formatId').val(), 
            formatName: $('#formatName').val()
        }
    }).done(function (data, textStatus, jqXHR) {
        //if successful, display the string to the page
        theDiv.html(jqXHR.responseText);
        patternCanBeSaved = true;
    }).fail(function (jqXHR, textStatus, errorThrown) {
        //any exception raised on the java side is displayed on the page
        var errorSpan = $('<span>');
        errorSpan.css('color', 'red');
        errorSpan.text(jqXHR.responseText);
        theDiv.html(errorSpan.html());
        patternCanBeSaved = false;
    });
}

//Helper function to make a html element display appear or disappear
function displayHelper (elem) {
    elem.toggle();
}

//Helps with setting the padding side
function updatePadSideSelect (padSideSelect, padSide) {
    padSideSelect.val(padSide);
}

// gets all the initial values for the current selected field
function updateReadingFormatFields (option) {
    var readingTypeValue,
        readingChannelValue,
        roundingModeValue,
        format,
        maxLength,
        padChar,
        padSide;

    // gets the initial readingType value
    readingTypeValue = getAttributeValue(option, 'readingType');
    $('#readingReadingType').val(readingTypeValue);

    // gets the initial readingChannel value
    readingChannelValue = getAttributeValue(option, 'readingChannel');
    $('#readingReadingChannel').val(readingChannelValue);

    // gets the initial roundingMode value
    roundingModeValue = getAttributeValue(option, 'roundingMode');
    $('#readingRoundingMode').val(roundingModeValue);
    
    // gets the initial format value
    format = getAttributeValue(option, 'format');
    $('#readingFormat').val(format);
    selectFormatOption($('#readingFormatSelect'), format);
    
    // get the initial maxLength value
    maxLength = getAttributeValue(option, 'maxLength');
    $('#readingMaxLength').val(maxLength);

    // get the initial padChar value
    padChar = getAttributeValue(option, 'padChar');
    $('#readingPadChar').val(padChar);
    selectPadCharSelectOption($('#readingPadCharSelect'), padChar);

    // get the initial padChar value
    padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect($('#readingPadSide'), padSide);
    checkPaddingSide('reading', padSide);
}

// gets all the initial values for the current selected field
function updateTimestampFormatFields (timestampDiv, option) {
    var readingTypeValue,
        readingChannelValue,
        format,
        maxLength,
        padChar,
        padSide;

    // gets the initial readingType value
    readingTypeValue = getAttributeValue(option, 'readingType');
    $('#timestampReadingType').val(readingTypeValue);

    // gets the initial readingChannel value
    readingChannelValue = getAttributeValue(option, 'readingChannel');
    $('#timestampReadingChannel').val(readingChannelValue);
    
    // gets the initial format value
    format = getAttributeValue(option, 'format');
    $('#timestampFormat').val(format);
    selectFormatOption($('#timestampFormatSelect'), format);
    
    // get the initial maxLength value
    maxLength = getAttributeValue(option, 'maxLength');
    $('#timestampMaxLength').val(maxLength);

    // get the initial padChar value
    padChar = getAttributeValue(option, 'padChar');
    $('#timestampPadChar').val(padChar);
    selectPadCharSelectOption($('#timestampPadCharSelect'), padChar);

    // get the initial padChar value
    padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect($('#timestampPadSide'), padSide);
    checkPaddingSide('timestamp', padSide);
}

// gets all the initial values for the current selected field
function updatePlainTextFormatFields (plainTextDiv, option) {
    // gets the initial format value
    var format = getAttributeValue(option, 'format');
    $('#plainFormat').val(format);
}

// gets all the initial values for the current selected field
function updateGenericFormatFields (genericFormatDiv, option) {
    var readingTypeValue,
        maxLength,
        padChar,
        padSide;

    // gets the initial readingType value
    readingTypeValue = getAttributeValue(option, 'readingType');
    $('#genericReadingType').val(readingTypeValue);
    
    // get the initial maxLength value
    maxLength = getAttributeValue(option, 'maxLength');
    $('#genericMaxLength').val(maxLength);

    // get the initial padChar value
    padChar = getAttributeValue(option, 'padChar');
    $('#genericPadChar').val(padChar);
    selectPadCharSelectOption($('#genericPadCharSelect'), padChar);

    // get the initial padChar value
    padSide = getAttributeValue(option, 'padSide');
    updatePadSideSelect($('#genericPadSide'), padSide);
    checkPaddingSide('generic', padSide);
}
function toggleHelperPopup (id) {
    $('#'+ id).toggle();
}
