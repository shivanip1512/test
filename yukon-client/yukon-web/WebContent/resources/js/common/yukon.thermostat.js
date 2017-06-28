var CURRENT_TIME_INPUT = null;
var CURRENT_TEMP_INPUT = null;
var KEYUP = 38;
var KEYDOWN = 40;
var TEMPERATURE_WINDOW_TYPES = ["HEAT","COOL"];

/**
 * Thermostat editing singleton
 * @class
 */
yukon.ThermostatScheduleEditor = {
    //Initializes the UI
    init: function(args) {
        yukon.ThermostatScheduleEditor.initArgs(args);
        yukon.ThermostatScheduleEditor.renderTime();
        
        //time UI
        $(document).on('focus', '.time input:text', this.showTimeSlider);
        $(document).on('blur', '.time input:text', this.blurTimeInput);
        $(document).on('keydown', '.time input:text', this.timeKeydown);

        //temperature UI
        $(document).on('focus', '.temp input:text', this.showTempSlider);
        $(document).on('blur', '.temp input:text', this.blurTempInput);
        $(document).on('keydown', '.temp input:text', this.tempKeydown);
        
        TIME_SLIDER = $("#timeSlider .track").slider({
            max: 24*60,
            min: 0,
            value: 0,
            slide: function(event, ui){
                CURRENT_TIME_INPUT.value = yukon.timeFormatter.formatTime(ui.value, parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60));
            },
            change: function(event, ui){
                yukon.ThermostatScheduleEditor.commitTimeValue(yukon.timeFormatter.formatTime(ui.value, parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60)), CURRENT_TIME_INPUT);
                CURRENT_TIME_INPUT.select();    //Good ol' IE needs this.
            }
        });
        
        TEMP_SLIDER = $("#tempSlider .track").slider({
            max: this.thermostat.HEAT.lower.getF(),
            min: this.thermostat.HEAT.upper.getF(),
            value: 72,
            slide: function(event, ui){
                var tempMode = CURRENT_TEMP_INPUT.getAttribute("data-temperatureMode");
                yukon.ThermostatScheduleEditor.thermostat.assignDegreesOrSnapToLimit(ui.value, tempMode);
                CURRENT_TEMP_INPUT.value = yukon.ThermostatScheduleEditor.thermostat[tempMode].temperature.sanitizedString();
            },
            change: function(event, ui){
                yukon.ThermostatScheduleEditor.commitTempValue(ui.value, CURRENT_TEMP_INPUT);
              CURRENT_TEMP_INPUT.select();    //Good ol' IE needs this.
            }
        });
        
        $(".schedules input:text, .createSchedule input:text").focus(function(e){$(this).attr("previousValue", this.value);});
        
        //update the temperature unit preference via ajax
        $(".tempControls input:radio").click(function(e){
            //ajax preference
            var form = $(this).closest('form'),
                inputElem = this;
            if(form[0]){
                var params = {temperatureUnit: this.value};
                var accountIdInput = form.find('input[name=accountId]');
                
                if(accountIdInput){
                    params.accountId = accountIdInput.val();
                }
                
                $.ajax({
                    url: form.attr('action'),
                    type: 'POST',
                    data: params
                }).done(function (data, textStatus, jqXHR) {
                    yukon.ThermostatScheduleEditor[inputElem.value]();
                });
            }
        });
        
        //just update the temperature preference, no saving of value
        $(".tempControlsNoUpdate input:radio").click(function(e){
            yukon.ThermostatScheduleEditor[this.value]();
        });
        
        $(".js-cancel").click(function(e){
            var dialog = $(this).closest("[id^=editSchedule], #createSchedule");
            dialog.dialog('close');
            dialog.find('form input[initialValue]').each(function(index, input){
                input.value = input.getAttribute('initialValue');
            });
            yukon.ThermostatScheduleEditor.renderTime();
            yukon.ThermostatScheduleEditor[yukon.ThermostatScheduleEditor.thermostat.COOL.temperature.unit]();
        });
        
        $(".js-copy").click(function(e){
            var form = $("#editSchedule_"+ $(this).closest("form").find("input[name=scheduleId]").val());
            $("input[name=scheduleId]", form).val(-1);
            $("input[name=scheduleName]", form).val(form.find('input[name=copyName]').val());
            $("button.js-delete", form).hide();
            
            //change title
            $('.title-bar .title', form).html($('input[name=copyTitle]', form).val());
        });
        
        $(".js-edit").click(function(e){
            var scheduleId = $(this).closest("form").find("input[name=scheduleId]").val();
            var form = $("editSchedule_" + scheduleId);
            $("input[name=scheduleId]", form).val(scheduleId);
            $("input[name=scheduleName]", form).val($("input[name=scheduleName]", form).val());
            
            $("button.js-delete", form).show();
            
            //clear error messages
            yukon.ThermostatScheduleEditor.clearErrors(form);
            
            //change title
            $('.title-bar .title', form).html($('input[name=editTitle]', form).val());
        });
        
        //This is a very specific editing mode for default schedules on energy companies.
        //There is only ever 1 schedule per page, but we need to provide capacity for changing modes
        //since you cannot delete one of these schedules.
        $(".editDefaultSchedule").click(function(e){
            //show the second page of the wizard, select the 'current' mode
            var form = $(this).closest("form");
            var mode = $("input[name=thermostatScheduleMode]", form).val();
            var page = $(".page_0:first"); 
            $("input[value="+ mode +"]", page).attr('checked', 'checked');
            $("button.js-next", page).removeAttr('disabled');
            
            //select the second page page.
            $(".schedule.editor", page.parent()).hide();
            $("."+ mode, page.parent()).show();
            yukon.ui.wizard.nextPage(page);
            
            
            var id = $("input[name=scheduleId]", form).val();
            var name = $("input[name=scheduleName]", form).val();
            var editForm = $("#form_" + id);
            $("input[name=scheduleId]", editForm).val(id);
            $("input[name=scheduleName]", editForm).val(name);
            
            $("button.js-delete", editForm).hide();
        });
        
        $(document).on('click', '.js-save', function(e){
            var form = null;
            if($(e.target).closest("#createSchedule")[0]){
                var mode = $("#createSchedule input[name=defaultScheduleMode]:checked").val();
                form = $(this).closest('#createSchedule').find('.'+ mode +' form');
            }else{
                form = $(this).closest('[id^=editSchedule]').find('form');
            }
            yukon.ThermostatScheduleEditor.prepForm(form);
            
            //the following relies on the jquery form plugin
            form.ajaxSubmit({
                error: function(xhr, status, err) {
                    var data = $.parseJSON(xhr.responseText);
                    //client errors
                    if(xhr.status >= 400 && xhr.status < 500){
                        yukon.ui.unblockPage();
                        yukon.ThermostatScheduleEditor.clearErrors(form);
                        var errors = data.errors;
                        for(error in errors){
                            var input = $("input[name="+ error +"]", form);
                            if(input[0]){
                                $("input[name="+ error +"]", form).addClass('error').after("<div class='error box'><small>" + errors[error] + "</small></div>");
                            }else{
                                $(".rows", form).parent().prepend("<div class='error box'><small>" + errors[error] + "</small></div>");
                            }
                        }
                    }else{
                        //reload the page in case of other server error
                        alert(err);
                        window.location = window.location;
                    }
                },
                success: function(raw, status, xhr) {
                    window.location = window.location;
                }
            });
            return false;
        });
        
        $(".page_0 input:radio").click(function(e){
                $(this).closest('.js-page').find('.js-next').removeAttr('disabled');
        });
        
        $(document).on('click', '.js-create', function(e){
            //show type picker
            yukon.ThermostatScheduleEditor.clearErrors($("#createSchedule"));
            yukon.ui.wizard.reset($("#createSchedule"));
            return false;
        });
        
        $(".page_0 .js-next").click(function(e){
            var input = $("#createSchedule input[name=defaultScheduleMode]:checked");
            $("#createSchedule .schedule.editor").each(function(index, elem){
                elem = $(elem);
                if(!elem.hasClass(input.val())){
                    elem.hide();
                    elem.removeClass("active");
                }else{
                    elem.show();
                    elem.addClass("active");
                }
            });
        });
        
        $(document).on('click', '.js-default', function(e){
            //find 'recommended schedule in the create popup
            var ourForm = $(this).closest("[id^=editSchedule], #createSchedule").find('form');
            var mode = $("input[name=thermostatScheduleMode]", ourForm).val();
            var recForm = $("#createSchedule ." + mode +" form");
            
            yukon.ThermostatScheduleEditor.resetDefaults({ourForm:ourForm, recForm:recForm});
            
        });
        
        $(document).on('click', '.js-createDefault', function(e){
            //find 'recommended schedule in the create popup
            var ourForm = $("#createSchedule .schedule.editor.active");
            var mode = $("input[name=thermostatScheduleMode]", ourForm).val();
            var recForm = $("#createSchedule ." + mode +" form");
            
            yukon.ThermostatScheduleEditor.resetDefaults({ourForm:ourForm, recForm:recForm});
            
        });
        
        $(document).click(function(e) {
            var target = $(e.target);
            //hide the sliders if we did NOT click on the slider or the inputs that spawn them
            if(target.closest('.slider').length == 0 && target.closest('.time').length == 0 && target.closest('.temp').length == 0){
                yukon.ThermostatScheduleEditor.hideTimeSlider();
                yukon.ThermostatScheduleEditor.hideTempSlider();
            }
        });
        
        //show the schedules
        $(".schedule, .schedule.editor").removeClass("vh");
    },
    
    initArgs: function(args) {
        this.thermostat = new yukon.Thermostat(args.thermostat);
        if(args.unit){
            yukon.ThermostatScheduleEditor[args.unit]();
        }else{
            yukon.ThermostatScheduleEditor[yukon.ThermostatScheduleEditor.thermostat.COOL.temperature.unit]();
        }
    },
    
    resetDefaults: function(args){
        if(args.recForm != null){
            var rows = $(".row", args.ourForm);
            var defaultDays = $(".row", args.recForm);
            for(var i=0; i<rows.length; i++){
                //copy values over
                var times = $("input[name=secondsFromMidnight]", rows[i]);
                var defaultTimes = $("input[name=secondsFromMidnight]", defaultDays[i]);
                for(var j=0; j<times.length; j++){
                    times[j].value = defaultTimes[j].getAttribute("defaultValue");
                }
                
                var heatTemps = $("input[name=heat_F]", rows[i]);
                var defaultHeatTemps = $("input[name=heat_F]", defaultDays[i]);
                for(var j=0; j<heatTemps.length; j++){
                    heatTemps[j].value = defaultHeatTemps[j].getAttribute("defaultValue");
                }
                
                var coolTemps = $("input[name=cool_F]", rows[i]);
                var defaultCoolTemps = $("input[name=cool_F]", defaultDays[i]);
                for(var j=0; j<coolTemps.length; j++){
                    coolTemps[j].value = defaultCoolTemps[j].getAttribute("defaultValue");
                }
            }
            
            //render times and temps
            yukon.ThermostatScheduleEditor.renderTime();
            yukon.ThermostatScheduleEditor[yukon.ThermostatScheduleEditor.thermostat.COOL.temperature.unit]();
        }
    },
    
    clearErrors: function(element){
        $(".error", element).removeClass('error');
        $(".error", element).remove();
    },
    
    prepForm: function(form) {
       //reconstruct the data object to submit
       var periodsArr = [];
       var periods = $(".period", form);
       
       for(var i=0; i<periods.length; i++) {
           var inputs = $("input[name]", periods[i]);
           if(inputs.length > 0){
               periodData = {};
               for(var j=0; j<inputs.length; j++) {
                   periodData[inputs[j].getAttribute('name')] = inputs[j].value;
               }
               periodsArr.push(periodData);
           }
       }
       
       var schedule = {};
       var inputs = $("input[name]", form);
       for(var i=0; i<inputs.length; i++){
           schedule[inputs[i].getAttribute('name')] = inputs[i].value;
       }
       
       schedule.periods = periodsArr;
       schedule.scheduleName = $("input[name=scheduleName]", form).val();
       schedule.scheduleId = $("input[name=scheduleId]", form).val();
       $("input[name=schedules]", form).val(JSON.stringify(schedule));
    },
    
    tempKeydown: function(event){
        //don't bother processing
        if(event.keyCode != KEYDOWN && event.keyCode != KEYUP){
            return;
        }
        
        switch(event.keyCode){
        case KEYUP:
            yukon.ThermostatScheduleEditor.thermostat.stepUp(event);
            break;
        case KEYDOWN:
            yukon.ThermostatScheduleEditor.thermostat.stepDown(event);
            break;
        default:
            //really this is redundant given the first check in this function
            return;
        }
        
        var windowModeType = event.currentTarget.getAttribute("data-temperatureMode");
        CURRENT_TEMP_INPUT.value = yukon.ThermostatScheduleEditor.thermostat[windowModeType].temperature.sanitizedString();
        TEMP_SLIDER.slider('value', yukon.ThermostatScheduleEditor.thermostat[windowModeType].temperature.getResolvedTemp());
        return false;
    },
    
    showTempSlider: function(event){
        var _self = yukon.ThermostatScheduleEditor;
        _self.hideTimeSlider();
        var input = $(this); 
        
        var startingValue = new Temperature({degrees: parseFloat(this.value), unit:_self.thermostat.COOL.temperature.unit});
        
        //determine range
        var start = null;
        var end = null;
        
        if(input.hasClass("heat_F")) {
            start = _self.thermostat.HEAT.lower;
            end = _self.thermostat.HEAT.upper;
            _self.thermostat.setMode('HEAT');
        } else if(input.hasClass("cool_F")) {
            start = _self.thermostat.COOL.lower;
            end = _self.thermostat.COOL.upper;
            _self.thermostat.setMode('COOL');
        }
        
        var windowModeType = event.currentTarget.getAttribute("data-temperatureMode");
        TEMP_SLIDER.slider('option', 'step', _self.thermostat[windowModeType].temperature.getResolution());
        var startLabel = start.sanitizedString();
        var endLabel = end.sanitizedString();
        
        CURRENT_TEMP_INPUT = this;
        TEMP_SLIDER.slider('option', 'min', start.degrees);
        TEMP_SLIDER.slider('option', 'max', end.degrees);
        TEMP_SLIDER.slider('value', startingValue.degrees);
        
        var tempSlider = $("#tempSlider");
        $(".startLabel .tempHolder", tempSlider).html(startLabel);
        $(".endLabel .tempHolder", tempSlider).html(endLabel);
        tempSlider.show();
        
        // 1/2 width of the targetted item + 1/2 width of slider
        var offsetLeft = input.offset().left + (input.width()/2) - (tempSlider.width()/2) - $(window).scrollLeft();
        //height of the input plus the height of the chevron
        var offsetTop = input.offset().top + input.height() + 9 - $(window).scrollTop();
        //position the slider
        tempSlider.css({top: offsetTop+"px", left: offsetLeft+"px"});
    },
    
    hideTempSlider: function(event){
        $("#tempSlider").hide();
    },
    
    blurTempInput: function(event) {
        yukon.ThermostatScheduleEditor.commitTempValue(this.value, this);
    },
    
    commitTempValue: function(value, input) {
        input = $(input);
        var _self = yukon.ThermostatScheduleEditor;
        value = parseFloat(value);
        
        if (isNaN(value)) {
            //revert to previousValue
            input.val(input.attr('previousValue'));
            return false;
        }
        
        var windowModeType = input.attr("data-temperatureMode");
        _self.thermostat.assignDegreesOrSnapToLimit(value, windowModeType);

        //put value into REAL input
        input.siblings("input:hidden")[0].value = _self.thermostat[windowModeType].temperature.getF();
        input.val(_self.thermostat[windowModeType].temperature.sanitizedString());
        input.closest('.temp').css({backgroundColor:_self.thermostat.color(input.attr("data-temperatureMode"))});
        return true;
    },
    
    timeKeydown: function(event){
        //don't bother processing if not the UP or DOWN key
        if(event.keyCode != KEYDOWN && event.keyCode != KEYUP){
            return;
        }
        
        //determine range
        var parent = $(this).closest('.period');
        var start = 0;
        var end = ((24*60)-parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsBetweenPeriods/60));  //11:45pm is the default
        var value = yukon.timeFormatter.parseTime(this.value);
                
        if(parent.prev('.period').length > 0) {
            start = parseInt($('.time input[name=secondsFromMidnight]', parent.prev('.period')).val());
            
            //round to the closest quarter hour (900 seconds = 15minutes)
            start = Math.ceil((start+yukon.ThermostatScheduleEditor.thermostat.secondsResolution)/60);
        }
        if(parent.next('.period').length > 0){
            end = parseInt($('.time input[name=secondsFromMidnight]', parent.next('.period')).val());
            
            //round to the closest quarter hour (900 seconds = 15minutes)
            end = Math.floor((end-yukon.ThermostatScheduleEditor.thermostat.secondsResolution)/60);
        }
        
        switch(event.keyCode){
        //Up arrow key
        case KEYUP:
            value += parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60);
            break;
        //Down arrow key
        case KEYDOWN:
            value -= parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60);
            break;
        default:
            //really this is redundant given the first check in this function
            return;
        }
        
        if(value >= start && value <= end){
            CURRENT_TIME_INPUT.value = yukon.timeFormatter.formatTime(value, parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60));
            
            //round to nearest 15
            TIME_SLIDER.slider('value', value);
        }
        return false;
    },
    
    showTimeSlider: function(event){
        var _self = yukon.ThermostatScheduleEditor;
        _self.hideTempSlider();
        
        var input = $(this);
        var startingValueSeconds = parseInt(input.siblings('input[name=secondsFromMidnight]:first').val());
        
        //determine range
        var parent = input.closest('.period');
        var startSeconds = 0;
        var endSeconds = 24*60*60;
        
        var previous = parent.prev('.period:not(.dn)');
        if(previous.length > 0) {
            var prevInput = $('.time input[name=secondsFromMidnight]', previous);
            if(prevInput.length > 0){
                //if the prevInput value == 0, bump up to 1
                var prevInputVal = parseInt(prevInput.val());
                startSeconds = prevInputVal ? prevInputVal : 1;
            }
        }
        
        var next = parent.next('.period:not(.dn)');
        if(next.length > 0){
            var nextInput = $('.time input[name=secondsFromMidnight]', next);
            if(nextInput.length > 0){
                endSeconds = parseInt(nextInput.val());
            }
        }
        
        if(startSeconds != 0){
            startSeconds = Math.ceil((startSeconds+_self.thermostat.secondsBetweenPeriods)/60)*60;    //round to the nearest interval
        }
        endSeconds = Math.floor((endSeconds+1-_self.thermostat.secondsBetweenPeriods)/60)*60;

        CURRENT_TIME_INPUT = this;
        TIME_SLIDER.slider('option', 'min', startSeconds/60);
        TIME_SLIDER.slider('option', 'max', endSeconds/60);
        TIME_SLIDER.slider('value', startingValueSeconds/60);
        
        var timeSlider = $("#timeSlider");
        $(".startLabel", timeSlider).html(yukon.timeFormatter.formatTime(TIME_SLIDER.slider('option', 'min'), parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60)));
        $(".endLabel", timeSlider).html(yukon.timeFormatter.formatTime(TIME_SLIDER.slider('option', 'max'), parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60)));
        timeSlider.show();
        
        // 1/2 width of the targetted item + 1/2 width of slider
        var offsetLeft = input.offset().left + (input.width()/2) - (timeSlider.width()/2) - $(window).scrollLeft();
        //height of the input plus the height of the chevron
        var offsetTop = input.offset().top + input.height() + 9 - $(window).scrollTop();
        //position the slider
        timeSlider.css({top: offsetTop+"px", left: offsetLeft+"px"});
    },
    
    hideTimeSlider: function(event){
        $("#timeSlider").hide();
    },
    
    blurTimeInput: function(event) {
        yukon.ThermostatScheduleEditor.commitTimeValue(this.value, this);
    },
    
    commitTimeValue: function(value, input){
        var curr = yukon.timeFormatter.parseTime(value)*60;
        input = $(input);
        if(curr != -1){
            //check bounds
            var parent = input.closest('.period');
            var start = -1;
            var end = ((24*60)+1-parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsBetweenPeriods/60))*60;
                    
            var previous = parent.prev('.period');
            if(previous.length > 0) {
                var previousInput = $('.time input[name=secondsFromMidnight]', previous);
                if(previousInput.length > 0){
                    start = parseInt(previousInput.val());
                }
            }
            
            var next = parent.next('.period');
            if(next.length > 0){
                var nextInput = $('.time input[name=secondsFromMidnight]', next);
                if(nextInput.length > 0){
                    end = parseInt(nextInput.val());
                }
            }
            
            //allow the user to set the start time to 12:00am
            if((start < curr) && (curr < end)){
                //set seconds from midnight value
                input.siblings('input[name=secondsFromMidnight]:first').val(yukon.timeFormatter.parseTime(input.val())*60);
                
                //get a nicely formatted time in case the user inputs some shorthand value such as '4pm'
                input.val(yukon.timeFormatter.formatTime(yukon.timeFormatter.parseTime(input.val()), parseInt(yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60)));
                input.attr('previousValue', input.val());
                return true;
            }
        }
        //revert to previousValue
        input.val(input.attr('previousValue'));
    },
    
    celsius: function(){
        var _self = yukon.ThermostatScheduleEditor;
        _self.thermostat.setUnit('C');
        
        $(".temp .value").each(function(index, elem){
            var temperature = new Temperature({degrees: parseFloat($(elem).next("input[type=hidden]").val()), unit: 'F'});  //IE doesn't like :hidden in this context - go figure
            $(elem).html(temperature.sanitizedString('C'));
        });
        $(".temp input:text").each(function(index, elem){
            var temperature = new Temperature({degrees: parseFloat($(elem).next("input:hidden").val()), unit: 'F'});
            elem.value = temperature.sanitizedString('C');
        });
        
        $(".temp, .tempLabel").removeClass('F').addClass('C');
        $("#tempSlider .startLabel, #tempSlider .endLabel").removeClass('F').addClass('C');
        $("input[name=temperatureUnit]").val("C");
        $(".tempSlider, .F").hide();
        $(".tempSlider, .C").show();
        _self.calcTempColor();
    },
    
    C: function(){
        yukon.ThermostatScheduleEditor.celsius();
    },
    
    fahrenheit: function(){
        var _self = yukon.ThermostatScheduleEditor;
        _self.thermostat.setUnit('F');
        
        $(".temp .value").each(function(index, elem){
            $(elem).html(Math.round($(elem).next("input[type=hidden]").val()));  //IE doesn't like :hidden in this context - go figure
        });
        $(".temp input:text").each(function(index, elem){
            elem.value = Math.round($(elem).next("input:hidden").val());
        });

        $(".temp").removeClass('C').addClass('F');
        $("#tempSlider .startLabel, #tempSlider .endLabel").removeClass('C').addClass('F');
        $("input[name=temperatureUnit]").val("F");
        $(".tempSlider, .C").hide();
        $(".tempSlider, .F").show();
        _self.calcTempColor();
    },
    
    F: function(){
        yukon.ThermostatScheduleEditor.fahrenheit();
    },
    
    renderTime: function(){
        $("input[name=secondsFromMidnight]").each(function(index, elem){
            elem = $(elem);
            elem.siblings("span.time").each(function(index, span){
                span.innerHTML = yukon.timeFormatter.formatTime(elem[0].value/60);
            });
            
            elem.siblings("input:text").each(function(index, input){
                input.value = yukon.timeFormatter.formatTime(elem[0].value/60);
            });
        });
    },
    
    calcTempColor: function(){
        var _self = yukon.ThermostatScheduleEditor;
        $(".temp input[type=hidden]").each(function(index, elem){
            elem = $(elem);
            var temperature = new Temperature({degrees: parseFloat(elem.val()), unit:'F'});
            var temp = elem.closest('.temp');
            if(temp.hasClass('heat')){
                temp.css({backgroundColor:_self.thermostat.calcHEATColor(temperature)});
            } else {
                temp.css({backgroundColor:_self.thermostat.calcCOOLColor(temperature)});
            }
        });
    }
};

/**
 * Thermostat manual editing singleton
 * @class
 */
yukon.ThermostatManualEditor = {
    thermostat: null,
    
    init: function(args){
        _self = yukon.ThermostatManualEditor;

        //prep schedule editor data
        _self.thermostat = new yukon.Thermostat(args.thermostat);
        _self.thermostat.setUnit(args.unit);
        
        $(".manualThermostat .state").click(_self.changeFanState);
        $(".manualThermostat .mode").click(_self.changeThermostatMode);
        $(".temperatureAdjust .up").click(_self.temperatureUp);
        $(".temperatureAdjust .down").click(_self.temperatureDown);
        $(".temperatureUnit .unit").click(_self.changeUnit);
        $("input[name=temperature_display]").blur(_self.onBlurTemperatureDisplay);
        $("input[name=temperature_display]").focus(_self.onFocusTemperatureDisplay);
        $("#sendSettingsSubmit").click(_self.prepForm);
        $(".closePopup").click(function(event){$(this).closest("[id^=editSchedule], #createSchedule").dialog('close');});
        $(".editLabel, .cancelLabelEdit").click(_self.toggleLabelEditor);
        
        _self.render();
        _self.renderOtherTemperatures(_self.thermostat.COOL.temperature.unit);
    },
    
    toggleLabelEditor: function(){
        $('#editName').toggle();
        $('#thermostatName').toggle();
    },
    
    changeUnit: function(event) {
        var unit = event.target.getAttribute('unit');
        yukon.ThermostatManualEditor.thermostat.setUnit(unit);
        yukon.ThermostatManualEditor.render();
        yukon.ThermostatManualEditor.renderOtherTemperatures(unit);
    },
    
    changeFanState: function(event) {
        yukon.ThermostatManualEditor.thermostat.setFan(event.target.getAttribute("state"));
        yukon.ThermostatManualEditor.render();
    },
    
    changeThermostatMode: function(event) {
        yukon.ThermostatManualEditor.thermostat.setMode(event.target.getAttribute("mode"));
        yukon.ThermostatManualEditor.render();
    },
    
    onBlurTemperatureDisplay: function(event) {
        if (isNaN(parseFloat(this.value))) {
            return false;
        }
        if(yukon.ThermostatManualEditor.thermostat.assignDegreesOrSnapToLimit(parseFloat(this.value), this.getAttribute("data-temperatureMode"))){
            yukon.ThermostatManualEditor.render();
        }else{
            this.value = this.getAttribute("previousValue");
        }
    },
    
    onFocusTemperatureDisplay: function(event) {
        $(this).attr("previousValue", this.value);
    },

    _resetCoolAndHeatTemperatures: function(event) {
        var popup = $("#" + event.target.getAttribute("popup_id"));

        $("input[name=coolTemperature]", popup).val("");
        $(".coolTemperatureConfirm", popup).html("");
        
        $("input[name=heatTemperature]", popup).val("");
        $(".heatTemperatureConfirm", popup).html("");
    },
    
    prepForm: function(event){
        var dialog = $("#" + event.currentTarget.getAttribute("popup_id")),
            widget = $(event.currentTarget).closest(".manualThermostat");
        
        $(".unit", dialog).hide();
        
        $("." + yukon.ThermostatManualEditor.thermostat.COOL.temperature.unit, dialog).show();
        
        $("input:hidden", dialog).each(function (index, input) {
            var name = input.getAttribute("name"),
                source = $('input[name="' + name + '"]', widget),
                checked;
            
            if(source.length > 0){
                //not really true for the checkbox, but we overwrite it down below
                input.value = source.val();
                if ($("."+ name +".selected", widget).length > 0) {
                    $("."+ name +"Confirm", dialog).html($("."+ name +".selected", widget).html()); 
                } else if ($("input[type=text]."+ name +"", widget).length > 0) {
                    $("."+ name  +"Confirm", dialog).html($("."+ name, widget).val());
                } else if ($("input[type=checkbox]."+ name +"", widget).length > 0) {

                    checked = $("."+ name, widget).is(":checked");
                    $("." + name + " ." + checked, dialog).show();
                    $("." + name + " ." + !checked, dialog).hide();
                    
                    input.value = source.is(":checked");
                }
            }
        });
        
        //send the actual requested value for logging
        var _self = yukon.ThermostatManualEditor;

        // Reset the request temperatures for heat and cool.
        _self._resetCoolAndHeatTemperatures(event);
        
        // Check to see if the cool temperature should be sent in the request.
        if ((!_self.thermostat.autoEnabled && _self.thermostat.mode != 'HEAT')  ||
              _self.thermostat.autoEnabled) {

            var coolTemperatureValue = $("input[name=temperature_display][data-temperatureMode='COOL']", widget).val();
            $("input[name=coolTemperature]", dialog).val(coolTemperatureValue);
            $(".coolTemperatureConfirm", dialog).html(coolTemperatureValue);

            // If we are looking at the non auto enabled page only show the one temperature in the confirm box
            if (!_self.thermostat.autoEnabled) {
                $("#coolTemperatureConfirm").show();
                $("#heatTemperatureConfirm").hide();
            }
        }

        // Check to see if the cool temperature should be sent in the request.
        if ((!_self.thermostat.autoEnabled && _self.thermostat.mode == 'HEAT') ||
             _self.thermostat.autoEnabled) {

            var heatTemperatureValue = $("input[name=temperature_display][data-temperatureMode='HEAT']", widget).val();
            $("input[name=heatTemperature]", dialog).val(heatTemperatureValue);
            $(".heatTemperatureConfirm", dialog).html(heatTemperatureValue);

            // If we are looking at the non auto enabled page only show the one temperature in the confirm box
            if (!_self.thermostat.autoEnabled) {
                $("#coolTemperatureConfirm").hide();
                $("#heatTemperatureConfirm").show();
            }
        }
        $("input[name=temperatureUnit]", dialog).val(yukon.ThermostatManualEditor.thermostat.COOL.temperature.unit);
        dialog.show();
    },

    temperatureUp: function(event) {
        var _self = yukon.ThermostatManualEditor;
        switch(_self.thermostat.mode){
            case 'AUTO':
            case 'EMERGENCY_HEAT':
            case 'HEAT':
            case 'COOL':
                _self.thermostat.stepUp(event);
                _self.render();
                return true;
            default:
                return false;
        }
    },
    
    temperatureDown: function(event) {
        var _self = yukon.ThermostatManualEditor;
        switch(_self.thermostat.mode){
            case 'AUTO':
            case 'EMERGENCY_HEAT':
            case 'HEAT':
            case 'COOL':
                _self.thermostat.stepDown(event);
                _self.render();
                return true;
            default:
                return false;
        }
    },
    
    render: function(){
        var _self = yukon.ThermostatManualEditor;
        $(".manualThermostat").each(function(index, widget){
            $("input[name=temperature_display]", widget).each(function(index, elem) {
                var temperatureMode = elem.getAttribute("data-temperatureMode");
                //set color
                $(elem).css({color: _self.thermostat.color(temperatureMode)});
                
                //set availability
                switch(_self.thermostat.mode){
                    case 'AUTO':
                    case 'EMERGENCY_HEAT':
                    case 'HEAT':
                    case 'COOL':
                        $(elem).prop('disabled', false);
                        break;
                    default:
                        $(elem).prop('disabled', true);
                    }
                
                //render the temperature w/ correct color and input availability
                elem.value = _self.thermostat[temperatureMode].temperature.sanitizedString();
                $("input[name=temperature]", $(elem).parent()).val(_self.thermostat[temperatureMode].degrees);
            });
            
            
            //render mode
            yukon.ui.exclusiveSelect($("li[mode=" + _self.thermostat.mode + "]", widget));
            $("#mode").val(_self.thermostat.mode);
            
            //render fan
            yukon.ui.exclusiveSelect($("li[state=" + _self.thermostat.fan + "]", widget));
            $("#fan").val(_self.thermostat.fan);

            //render unit
            yukon.ui.exclusiveSelect($("li[unit=" + _self.thermostat.COOL.temperature.unit + "]", widget));
            $("input[name=temperatureUnit]", widget).val(_self.thermostat.COOL.temperature.unit);
        });
    },
    
    renderOtherTemperatures: function(unit){
        $(".unit_label").hide();
        $("."+ unit +"_label").show();
        
        $(".raw_temperature_F").each(function(index, elem){
            var temperature = new Temperature({
                    degrees: parseFloat(elem.getAttribute('raw_temperature_F')),
                    unit: 'F'
                });
            
            elem.innerHTML = temperature.sanitizedString(unit);
        });
    }
};

/**
 * Thermostat singleton
 * @class
 * 
 * args requires the following values:
 * heat                 - object holding:
 *      lower           - Temperature representing the lower heat limit of this thermostat
 *      upper           - Temperature representing the upper heat limit of this thermostat
 * cool                  - object holding:
 *      lower           - Temperature representing the lower cool limit of this thermostat
 *      upper           - Temperature representing the upper cool limit of this thermostat
 * secondsResolution    - integer seconds to snap time values to
 * secondsBetweenPeriods- integer minimum time between periods in seconds
 *
 * optional args are:
 *      currentUnit          - string - Current units to display the schedules in
 *      heatColor            - object - {r:{start:[0..255], end:[0..255]}, g:{start:[0..255], end:[0..255]}, b:{start:[0..255], end:[0..255]}, a:{start:[0..255], end:[0..255]}}
 *      coolColor            - object - {r:{start:[0..255], end:[0..255]}, g:{start:[0..255], end:[0..255]}, b:{start:[0..255], end:[0..255]}, a:{start:[0..255], end:[0..255]}}
 */
yukon.Thermostat = function(args){
    this.HEAT = {
        upper: null,
        lower: null,
        temperature: null
    };
    this.COOL = {
        upper: null,
        lower: null,
        temperature: null
    };
    
    this.mode = '';
    this.fan = '';
    this.deadband=3;
    this.autoEnabled = false;
    this.secondsResolution = 0; //seconds
    this.secondsBetweenPeriods = 0; //minimum time between periods in seconds

    this.HEATColor = {
        r: {start: 242, end: 242},
        g: {start: 150, end: 0},
        b: {start: 29, end: 29},
        a: {start: 1, end: 1}
    };
    
    this.COOLColor = {
        r: {start: 64, end: 189},
        g: {start: 153, end: 220},
        b: {start: 255, end: 255},
        a: {start: 1, end: 1}
    };
    
    this._init = function() {
        // Fill in new object
        for(var key in args){
            if(typeof(this[key]) != 'undefined' && typeof(this[key]) != 'function'){
                this[key] = args[key];
            }
        }
        
        // Adjust for current deadband if needed
        if (this.autoEnabled) {
            if (!this._isDeadbandValid()) {
                this.HEAT.temperature.setF(this.COOL.temperature.getF() - this.deadband);
            }
        } 

        // Check if we're looking at a device in auto mode that is not on the autoEnabled view.  If so default to cool instead.
        if (this.mode == 'AUTO' && !this.autoEnabled) {
            this.setMode('COOL');
        }
        this.setMode(this.mode);
    };
    
    this.calcHEATColor = function(temperature){
        var r = this.HEATColor.r.start;
        var g = Math.round(this.HEATColor.g.start - Math.abs((temperature.getF() - this.HEAT.lower.getF()) * ((this.HEATColor.g.end - this.HEATColor.g.start)/(this.HEAT.upper.getF() - this.HEAT.lower.getF()))));
        var b = this.HEATColor.b.start; 
        return "rgb("+ r +", "+ g +", "+ b +")";
    };
    
    this.calcCOOLColor = function(temperature){
        var tempRange = this.COOL.upper.getF() - this.COOL.lower.getF();
        var RRange = this.COOLColor.r.end - this.COOLColor.r.start;
        var GRange = this.COOLColor.g.end - this.COOLColor.g.start;
        var r = Math.round(this.COOLColor.r.start + ((temperature.getF() - this.COOL.lower.getF()) * (RRange/tempRange)));
        var g = Math.round(this.COOLColor.g.start + ((temperature.getF() - this.COOL.lower.getF()) * (GRange/tempRange)));
        var b = this.COOLColor.b.start;
        return "rgb("+ r +","+ g +","+ b +")";
    };
    
    this.color = function(temperatureMode) {
        switch(this.mode){
        case 'AUTO':
        case 'EMERGENCY_HEAT':
        case 'HEAT':
        case 'COOL':
            return this["calc"+temperatureMode+"Color"](this[temperatureMode]["temperature"]);
        default:
            return "#CCC";
        }
    };
    
    this.assignDegrees = function(degrees, temperatureMode) {
        var newTemperature = new Temperature({degrees: degrees, unit: this[temperatureMode].temperature.unit});
        var coolTemperature = (temperatureMode == 'COOL') ? newTemperature : $.extend(true, {}, this.COOL.temperature);
        var heatTemperature = (temperatureMode == 'HEAT') ? newTemperature : $.extend(true, {}, this.HEAT.temperature);
        
        if(this.isValidTemperature(coolTemperature, heatTemperature)){
            this[temperatureMode].temperature = newTemperature;
            return true;
        }
        return false;
    };
    
    /**
     * If we are in an edge case mode say 45F (lower limit on ExpressStats) = 7.222222C rounds to 7C.
     * Users need to be able to enter 7.0C but have it save as 7.2222222C
     */
    this.assignDegreesOrSnapToLimit = function(degrees, temperatureMode) {
        
        switch(this.mode){
            case 'AUTO':
            case 'EMERGENCY_HEAT':
            case 'HEAT':
            case 'COOL':
                var temperatureSnapshot = this._temperatureSnapshot();
                
                this[temperatureMode].temperature = new Temperature({degrees: degrees, unit: this[temperatureMode].temperature.unit});
                var temps = this[temperatureMode];

                // snap to lower limit
                if(this[temperatureMode].temperature.getF() < temps.lower.getF()){
                    this[temperatureMode].temperature = new Temperature({degrees:temps.lower.degrees, unit:temps.lower.unit});
                }

                //snap to upper limit
                if(this[temperatureMode].temperature.getF() > temps.upper.getF()){
                    this[temperatureMode].temperature = new Temperature({degrees:temps.upper.degrees, unit:temps.upper.unit});
                }
                
                if(this.autoEnabled) {
                    //snap to upper cool deadband limit
                    if(temperatureMode == 'HEAT' && this.HEAT.temperature.getF() + this.deadband > this.COOL.temperature.getF()){
                        this.COOL.temperature.setF(this.HEAT.temperature.getF() + this.deadband);
                    }
    
                    //snap to upper heat deadband limit
                    if(temperatureMode == 'COOL' && this.COOL.temperature.getF() - this.deadband < this.HEAT.temperature.getF()) {
                        this.HEAT.temperature.setF(this.COOL.temperature.getF() - this.deadband);
                    }
                }

                if (!this.isValidTemperature(this.COOL.temperature, this.HEAT.temperature)) {
                    this._restoreFromTemperatureSnapshot(temperatureSnapshot);
                }
                
                return true;
            default:
                return false;
        }
        
        return false;
    };
    
    this.stepUp = function(event){
        switch(this.mode){
            case 'AUTO':
            case 'EMERGENCY_HEAT':
            case 'HEAT':
            case 'COOL':
                
                // Get temperatureSnapshot in case we need to revert the stepUp
                var temperatureSnapshot = this._temperatureSnapshot();
                
                var target = (event.currentTarget) ? event.currentTarget : event.target;
                var windowModeType = target.getAttribute("data-temperatureMode");
                this[windowModeType].temperature.stepUp();

                // Auto mode enabled.  We need to worry about two windows now.
                if(this.autoEnabled){
                    // Save current cool temp incase we need to roll back
                    if(!this._isDeadbandValid()){
                        this.COOL.temperature.setF(this.HEAT.temperature.getF() + this.deadband);
                    }
                    
                    // Check if we have a valid cool temperature
                    if(!this.isValidTemperature(this.COOL.temperature, this.HEAT.temperature)){
                        this._restoreFromTemperatureSnapshot(temperatureSnapshot);
                    }
                    
                }else{
                    if(!this.isValidTemperature(this.COOL.temperature, this.HEAT.temperature)){
                        this[windowModeType].temperature = new Temperature({degrees: this[windowModeType].upper.degrees, unit:this[windowModeType].upper.unit});
                    }
                }
                return;
             default:
                return;
        }
    };
    
    this.stepDown = function(event){
        switch(this.mode){
            case 'AUTO':
            case 'EMERGENCY_HEAT':
            case 'HEAT':
            case 'COOL':
                
                // Get temperatureSnapshot in case we need to revert the stepUp
                var temperatureSnapshot = this._temperatureSnapshot();
                
                var target = (event.currentTarget) ? event.currentTarget : event.target;
                var windowModeType = target.getAttribute("data-temperatureMode");
                this[windowModeType].temperature.stepDown();
    
                // Auto mode enabled.  We need to worry about two windows now.
                if(this.autoEnabled){
                    // Save current cool temp incase we need to roll back
                    if(!this._isDeadbandValid()){
                        this.HEAT.temperature.setF(this.COOL.temperature.getF() - this.deadband);
                    }
                    
                    // Check if we have a valid cool temperature
                    if(!this.isValidTemperature(this.COOL.temperature, this.HEAT.temperature)){
                        this._restoreFromTemperatureSnapshot(temperatureSnapshot);
                    }
                    
                }else{
                    if(!this.isValidTemperature(this.COOL.temperature, this.HEAT.temperature)){
                        this[windowModeType].temperature = new Temperature({degrees: this[windowModeType].lower.degrees, unit:this[windowModeType].lower.unit});
                    }
                }
                return;
             default:
                return;
        }
    };
    
    this._isDeadbandValid = function(){
        //@TODO: note about deadband in F vs. C
        //this.COOL.temperature.unit
        return !(this.COOL.temperature.getF() - this.HEAT.temperature.getF() < this.deadband);
    };
    
    // Gets the current temperatures.  This can then be used with _restoreFromTemperatureSnapshot to restore from an invalid temperature change.
    this._temperatureSnapshot = function() {
        var temperatureSnapshot = {};
        for (var i=0; i < TEMPERATURE_WINDOW_TYPES.length; i++) {
            var tempWindowType = TEMPERATURE_WINDOW_TYPES[i];
            var clonedTemperatureObject = $.extend(true, {}, this[tempWindowType].temperature);
            temperatureSnapshot[tempWindowType] = clonedTemperatureObject;
        }

        return temperatureSnapshot;
    };

    // Restores the temperture objects to the  
    this._restoreFromTemperatureSnapshot = function(temperatureSnapshot) {
        for (var i=0; i < TEMPERATURE_WINDOW_TYPES.length; i++) {
            var tempWindowType = TEMPERATURE_WINDOW_TYPES[i];
            this[tempWindowType].temperature = temperatureSnapshot[tempWindowType];
        }
    };
    
    this.setMode = function(mode) {
        this.mode = mode;
        
        switch(mode){
        case 'AUTO':
        case 'EMERGENCY_HEAT':
        case 'HEAT':
            if (!this.autoEnabled) {
                $('.coolDiv').hide();
                $('.heatDiv').show();
            }
            return this._toValidHeatTemperature(this.HEAT.temperature);
        case 'COOL':
            if (!this.autoEnabled) {
                $('.coolDiv').show();
                $('.heatDiv').hide();
            }
            return this._toValidCoolTemperature(this.COOL.temperature);
        default:
            
            // TODO: find a better way to do this.  
            if (!this.autoEnabled) {
                $('.coolDiv').show();
                $('.heatDiv').hide();
            }
            
            return this.temperature;
        }
    };
    
    this.setFan = function(fan){
        this.fan = fan;
    };
    
    this.setUnit = function(unit){
        this.HEAT.temperature.toUnit(unit);
        this.HEAT.upper.toUnit(unit);
        this.HEAT.lower.toUnit(unit);
        this.COOL.temperature.toUnit(unit);
        this.COOL.upper.toUnit(unit);
        this.COOL.lower.toUnit(unit);
    };
    
    this.toValidTemperature = function(temperature){
        switch(this.mode){
        case 'EMERGENCY_HEAT':
        case 'HEAT':
            return this._toValidHeatTemperature(temperature);
        case 'COOL':
            return this._toValidCoolTemperature(temperature);
        default:
            return this.temperature;
        }
    };
    
    /**
     * Validates the temperature against the limits for this thermostat according the selected mode
     */
    this.isValidTemperature = function(coolTemperature, heatTemperature){
        switch(this.mode){
        case 'AUTO':
        case 'EMERGENCY_HEAT':
        case 'HEAT':
        case 'COOL':
            if((heatTemperature.getF() < this.HEAT.lower.getF()) || (heatTemperature.getF() > this.HEAT.upper.getF())){
                return false;
            }
            if((coolTemperature.getF() < this.COOL.lower.getF()) || (coolTemperature.getF() > this.COOL.upper.getF())){
                return false;
            }
            if(this.autoEnabled && !this._isDeadbandValid()) {
                return false;
            }
            
            return true;
        default:
            return false;
        }
    };
    
    this._toValidHeatTemperature = function(temperature){
        if(temperature.getF() < this.HEAT.lower.getF()){
            temperature.setF(this.HEAT.lower.getF());
        }else if(temperature.getF() > this.HEAT.upper.getF()){
            temperature.setF(this.HEAT.upper.getF());
        }
        return temperature; 
    };
    
    this._toValidCoolTemperature = function(temperature){
        if(temperature.getF() < this.COOL.lower.getF()){
            temperature.setF(this.COOL.lower.getF());
        }else if(temperature.getF() > this.COOL.upper.getF()){
            temperature.setF(this.COOL.upper.getF());
        }
        return temperature;
    };
    
    // Calling init class to create an object.
    this._init();
};

/* Validate the temperature value */
$(document).on("click", "#sendSettingsSubmit", function () {
    if ($("div.coolDiv").is(":visible")) {
        var coolTemp = $("div.coolDiv input[name='temperature_display']").val();
        if (isNaN(parseFloat(coolTemp))) {
            $("div.coolDiv input[name='temperature_display']").addClass("error");
        } else {
            $("div.coolDiv input[name='temperature_display']").removeClass("error");
        }
    }
    if ($("div.heatDiv").is(":visible")) {
        var heatTemp = $("div.heatDiv input[name='temperature_display']").val();
        if (isNaN(parseFloat(heatTemp))) {
            $("div.heatDiv input[name='temperature_display']").addClass("error");
        } else {
            $("div.heatDiv input[name='temperature_display']").removeClass("error");
        }
    } 

    if ($("div.heatDiv input[name='temperature_display']").hasClass("error") || 
            $("div.coolDiv input[name='temperature_display']").hasClass("error")) {
        $("#validationMessage").removeClass("dn");
    } else {
        $("#validationMessage").addClass("dn");
        var confirmationPopId = "#" + $("#confirmPopup_id").val();
        $(confirmationPopId).dialog("open");
    }
});

$(document).on("click", "div.thermostatModes li", function () {
    hideErrors();
});

$(document).on("click", "div.temperatureDisplay div.temperatureUnit li.unit", function () {
    hideErrors();
});

$(document).on("click", "div.fanStates li", function () {
    hideErrors();
});

$(document).on("click", "div.coolDiv div.temperatureAdjust", function () {
    $("div.coolDiv input[name='temperature_display']").removeClass("error");
    if (!$("div.heatDiv input[name='temperature_display']").hasClass("error")) {
        $("#validationMessage").addClass("dn");
    }
});

$(document).on("click", "div.heatDiv div.temperatureAdjust", function () {
    $("div.heatDiv input[name='temperature_display']").removeClass("error");
    if (!$("div.coolDiv input[name='temperature_display']").hasClass("error")) {
        $("#validationMessage").addClass("dn");
    }
});

function hideErrors () {
    $("div.coolDiv input[name='temperature_display']").removeClass("error");
    $("div.heatDiv input[name='temperature_display']").removeClass("error");
    $("#validationMessage").addClass("dn");
}
