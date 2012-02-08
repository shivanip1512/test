var CURRENT_TIME_INPUT = null;
var CURRENT_TEMP_INPUT = null;
var KEYUP = 38;
var KEYDOWN = 40;
var TEMPERATURE_WINDOW_TYPES = ["HEAT","COOL"];

Yukon.ThermostatScheduleEditor = {
    //Initializes the UI
    init: function(args) {
        Yukon.ThermostatScheduleEditor.initArgs(args);
        Yukon.ThermostatScheduleEditor.renderTime();
        Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor.thermostat.COOL.temperature.unit]();
       
        $$(".time input:text").each(function(input){
            input.observe('focus', Yukon.ThermostatScheduleEditor.showTimeSlider);
            input.observe('blur', Yukon.ThermostatScheduleEditor.blurTimeInput);
            input.observe('keydown', Yukon.ThermostatScheduleEditor.timeKeydown);
        });
        
        $$(".temp input:text").each(function(input){
            input.observe('focus', Yukon.ThermostatScheduleEditor.showTempSlider);
            input.observe('blur', Yukon.ThermostatScheduleEditor.blurTempInput);
            input.observe('keydown', Yukon.ThermostatScheduleEditor.tempKeydown);
        });
        
        TIME_SLIDER = new Control.Slider($("timeSlider").down('.handle'), $("timeSlider").down('.track'), {
            range: $R(0, 24*60),
            sliderValue: 0,
            onSlide: function(value, e) {
                CURRENT_TIME_INPUT.value = timeFormatter.formatTime(value, parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60));
            },
            onChange: function(value, e) {
                Yukon.ThermostatScheduleEditor.commitTimeValue(timeFormatter.formatTime(value, parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60)), CURRENT_TIME_INPUT);
                CURRENT_TIME_INPUT.select();    //Good ol' IE needs this.
            }
        });
        
        TEMP_SLIDER = new Control.Slider($("tempSlider").down('.handle'), $("tempSlider").down('.track'), {
            range: $R(this.thermostat.HEAT.lower.getF(), Yukon.ThermostatScheduleEditor.thermostat.HEAT.upper.getF()),
            sliderValue: 72,
            onSlide: function(value) {
            	var tempMode = CURRENT_TEMP_INPUT.readAttribute("data-temperatureMode");
            	Yukon.ThermostatScheduleEditor.thermostat.assignDegreesOrSnapToLimit(value, tempMode);
            	CURRENT_TEMP_INPUT.value = Yukon.ThermostatScheduleEditor.thermostat[tempMode].temperature.sanitizedString();
            },
            onChange: function(value, e) {
                Yukon.ThermostatScheduleEditor.commitTempValue(value, CURRENT_TEMP_INPUT);
                CURRENT_TEMP_INPUT.select();    //Good ol' IE needs this.
            }
        });

        $$(".schedules input:text, .createSchedule input:text").invoke('observe', 'focus', function(event){
                this.writeAttribute("previousValue", this.value);
                
        });
        
        $$(".tempControls input:radio").invoke('observe', 'click', function(e){
                //ajax preference
                var form = e.target.up('form');
                if(form){
                    var params = {temperatureUnit: this.value};
                    var accountIdInput = form.down('input[name=accountId]');
                    
                    if(accountIdInput){
                        params.accountId = accountIdInput.value;
                    }
                    
                    new Ajax.Request(form.readAttribute("action"), {
                                     method:"POST",
                                     parameters: params,
                                     onComplete: Yukon.ThermostatScheduleEditor[this.value]});
                }
        });
        
        $$(".tempControlsNoUpdate input:radio").invoke('observe', 'click', function(e){
                Yukon.ThermostatScheduleEditor[this.value]();
        });
        
        YEvent.observeSelectorClick(".send", function(e){
            //confirmation popup already shown at this point
            $$("form[name=sendSchedule] input[name=scheduleId]")[0].value = e.target.up("form").down("input[name=scheduleId]").value;
            $$("form[name=sendSchedule] p:first")[0].innerHTML = $$("form[name=sendSchedule] input[name=message]")[0].value.replace("{0}", e.target.up("form").down("input[name=scheduleName]").value);
        });
        
        YEvent.observeSelectorClick(".delete", function(e){
            //confirmation popup already shown at this point, so hide it
            e.target.up(".popUpDiv").hide();
            $$("form[name=deleteSchedule] input[name=scheduleId]")[0].value = e.target.up(".popUpDiv").down("form input[name=scheduleId]").value;
            $$("form[name=deleteSchedule] p:first")[0].innerHTML = $$("form[name=deleteSchedule] input[name=message]")[0].value.replace("{0}", e.target.up(".popUpDiv").down("form input[name=scheduleName]").value);
        });
        
        YEvent.observeSelectorClick(".cancel", function(e){
            e.target.up(".popUpDiv").hide();
            e.target.up(".popUpDiv").down('form').select("input[initialValue]").each(function(input){
                input.value = input.readAttribute('initialValue');
            });
            Yukon.ThermostatScheduleEditor.renderTime();
            Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor.thermostat.COOL.temperature.unit]();
        });
        
        YEvent.observeSelectorClick(".copy", function(e){
            var form = $("editSchedule_"+e.target.up("form").down("input[name=scheduleId]").value);
             form.down("input[name=scheduleId]").value = -1;
             form.down("input[name=scheduleName]").value = form.down('input[name=copyName]').value;
             form.down("button.delete").hide();
             
             //change title
             form.down('.titleBar .title').innerHTML = form.down('input[name=copyTitle]').value;
        });
        
        YEvent.observeSelectorClick(".edit", function(e){
            var form = $("editSchedule_"+e.target.up("form").down("input[name=scheduleId]").value);
            form.down("input[name=scheduleId]").value = e.target.up("form").down("input[name=scheduleId]").value;
            form.down("input[name=scheduleName]").value = e.target.up("form").down("input[name=scheduleName]").value;
            if(form.down("button.delete")){
                form.down("button.delete").show();
            }
            
            //clear error messages
            Yukon.ThermostatScheduleEditor.clearErrors(form);
            
            //change title
            form.down('.titleBar .title').innerHTML = form.down('input[name=editTitle]').value;
        });
        
        //This is a very specific editing mode for default schedules on energy companies.
        //There is only ever 1 schedule per page, but we need to provide capacity for changing modes
        //since you cannot delete one of these schedules.
        YEvent.observeSelectorClick(".editDefaultSchedule", function(e){
            //show the second page of the wizard, select the 'current' mode
            var mode = e.target.up("form").down("input[name=thermostatScheduleMode]").value;
            var page = $$(".page_0")[0]; 
            page.down("input[value="+ mode +"]").checked = true;
            page.down("button.f_next").enable();
            
            //select the second page page.
            page.up(1).select(".schedule_editor").invoke('hide');
            page.up(1).down("."+ mode).show();
            Yukon.ui.wizard.nextPage(page);
            
            
            var id = e.target.up("form").down("input[name=scheduleId]").value;
            var name = e.target.up("form").down("input[name=scheduleName]").value;
            var form = $("form_"+e.target.up("form").down("input[name=scheduleId]").value);
            form.down("input[name=scheduleId]").value = id;
            form.down("input[name=scheduleName]").value = name;
            
            if(form.down("button.delete")){
                form.down("button.delete").hide();
            }
        });
        
        YEvent.observeSelectorClick(".save", function(e){
            e.stop();
            var form = null;
            if(e.target.up("#createSchedule")){
                var mode = $("createSchedule_body").down("input[name=defaultScheduleMode]:checked").value;
                form = e.target.up('.popUpDiv').down('.'+ mode +' form');
            }else{
                form = e.target.up('.popUpDiv').down('form');
            }
            Yukon.ThermostatScheduleEditor.prepForm(form);
            
            form.request({
                onFailure: function(data) {
                    //client errors
                    if(data.status >= 400 && data.status < 500){
                        Yukon.ui.unblockPage();
                        Yukon.ThermostatScheduleEditor.clearErrors(form);
                        var errors = data.responseJSON.errors;
                        for(error in errors){
                            form.down("input[name="+ error +"]").addClassName('error').insert({after:"<div class='errorMessage box'><small>" + errors[error] + "</small></div>"});
                        }
                    }else{
                        //reload the page in case of other server error
                        alert(data.responseText);
                        window.location = window.location;
                    }
                },
                onSuccess: function(data) {
                    window.location = window.location;
                }
            });
            
            return false;
        });
        
        $$(".page_0 input:radio").invoke('observe', 'click', function(e){
                e.target.up('.f_page').down('.f_next').enable();
        });
        
        YEvent.observeSelectorClick(".create", function(e){
            //show type picker
            Yukon.ThermostatScheduleEditor.clearErrors($("createSchedule_body"));
            Yukon.ui.wizard.reset($("createSchedule_body"));
            return false;
        });
        
        YEvent.observeSelectorClick(".page_0 .f_next", function(e){
            var input = $("createSchedule_body").down("input[name=defaultScheduleMode]:checked")
            $("createSchedule_body").select(".schedule_editor").each(function(elem){
                if(!elem.hasClassName(input.value)){
                    elem.hide();
                    elem.removeClassName("active");
                }else{
                    elem.show();
                    elem.addClassName("active");
                }
            });
        });
        
        YEvent.observeSelectorClick(".default", function(e){
            //find 'recommended schedule in the create popup
            var ourForm = e.target.up(".popUpDiv").down('form');
            var mode = ourForm.down("input[name=thermostatScheduleMode]").value;
            var recForm = $("createSchedule").down('.'+ mode +' form');
            
            Yukon.ThermostatScheduleEditor.resetDefaults({ourForm:ourForm, recForm:recForm});
            
        });
        
        YEvent.observeSelectorClick(".createDefault", function(e){
            //find 'recommended schedule in the create popup
            var ourForm = $("createSchedule").down(".schedule_editor.active");
            var mode = ourForm.down("input[name=thermostatScheduleMode]").value;
            var recForm = $("createSchedule").down('.'+ mode +' form');
            
            Yukon.ThermostatScheduleEditor.resetDefaults({ourForm:ourForm, recForm:recForm});
            
        });
        
        $(document).observe('click', function(e) {
            //hide the sliders if we did NOT click on the slider or the inputs that spawn them
            if(e.target.up('.slider') == null && e.target.up('.time') == null && e.target.up('.temp') == null){
                Yukon.ThermostatScheduleEditor.hideTimeSlider();
                Yukon.ThermostatScheduleEditor.hideTempSlider();
            }
        });
        
        //show the schedules
        $$(".schedule").invoke('removeClassName', "vh");
        $$(".schedule_editor").invoke('removeClassName', "vh");
    },
    
    initArgs: function(args) {
        this.thermostat = new Yukon.Thermostat(args.thermostat);
    },
    
    resetDefaults: function(args){
        if(args.recForm != null){
            var days = args.ourForm.select(".day");
            var defaultDays = args.recForm.select(".day");
            for(var i=0; i<days.length; i++){
                var timeOfWeek = args.recForm.down("." + days[i].down("input[name=timeOfWeek]").value);
                
                //copy values over
                var times = days[i].select("input[name=secondsFromMidnight]");
                var defaultTimes = defaultDays[i].select("input[name=secondsFromMidnight]");
                for(var j=0; j<times.length; j++){
                    times[j].value = defaultTimes[j].getAttribute("defaultValue");
                }
                
                var heatTemps = days[i].select("input[name=heat_F]");
                var defaultHeatTemps = defaultDays[i].select("input[name=heat_F]");
                for(var j=0; j<heatTemps.length; j++){
                    heatTemps[j].value = defaultHeatTemps[j].getAttribute("defaultValue");
                }
                
                var coolTemps = days[i].select("input[name=cool_F]");
                var defaultCoolTemps = defaultDays[i].select("input[name=cool_F]");
                for(var j=0; j<coolTemps.length; j++){
                    coolTemps[j].value = defaultCoolTemps[j].getAttribute("defaultValue");
                }
            }
            
            //render times and temps
            Yukon.ThermostatScheduleEditor.renderTime();
            Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor.thermostat.COOL.temperature.unit]();
        }
    },
    
    clearErrors: function(element){
        element.select('.error').invoke('removeClassName', 'error');
        element.select('.errorMessage').invoke('remove');
    },
    
    prepForm: function(form) {
       //reconstruct the data object to submit
        var periodsArr = [];
       var periods = form.select(".period");
       
       for(var i=0; i<periods.length; i++) {
           var inputs = periods[i].select("input[name]");
           if(inputs.length > 0){
               periodData = {};
               for(var j=0; j<inputs.length; j++) {
                   periodData[inputs[j].readAttribute('name')] = inputs[j].value;
               }
               periodsArr.push(periodData);
           }
       }
       
       var schedule = {};
       var inputs = form.select("input[name]");
       for(var i=0; i<inputs.length; i++){
           schedule[inputs[i].readAttribute('name')] = inputs[i].value;
       }
       
       schedule.periods = periodsArr;
       schedule.scheduleName = form.down("input[name=scheduleName]").value;
       schedule.scheduleId = form.down("input[name=scheduleId]").value;
       form.down("input[name=schedules]").value = JSON.stringify(schedule);
    },
    
    tempKeydown: function(event){
        //don't bother processing
        if(event.keyCode != KEYDOWN && event.keyCode != KEYUP){
            return;
        }
        
        switch(event.keyCode){
        case KEYUP:
            Yukon.ThermostatScheduleEditor.thermostat.stepUp(event);
            break;
        case KEYDOWN:
            Yukon.ThermostatScheduleEditor.thermostat.stepDown(event);
            break;
        default:
            //really this is redundant given the first check in this function
            return;
        }
        
        var windowModeType = event.currentTarget.readAttribute("data-temperatureMode");
        CURRENT_TEMP_INPUT.value = Yukon.ThermostatScheduleEditor.thermostat[windowModeType].temperature.sanitizedString();
        TEMP_SLIDER.setValue(Yukon.ThermostatScheduleEditor.thermostat[windowModeType].temperature.getResolvedTemp());
        event.stop();
    },
    
    showTempSlider: function(event){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.hideTimeSlider();
        
        var startingValue = new Temperature({degrees: parseFloat(this.value), unit:_self.thermostat.COOL.temperature.unit});
        
        //determine range
        var start = null;
        var end = null;
        
        if(this.hasClassName("heat_F")) {
            start = _self.thermostat.HEAT.lower;
            end = _self.thermostat.HEAT.upper;
            _self.thermostat.setMode('HEAT');
        } else if(this.hasClassName("cool_F")) {
            start = _self.thermostat.COOL.lower;
            end = _self.thermostat.COOL.upper;
            _self.thermostat.setMode('COOL');
        }
        
        var windowModeType = event.currentTarget.readAttribute("data-temperatureMode");
        TEMP_SLIDER.increment = _self.thermostat[windowModeType].temperature.getResolution();
        var startLabel = start.sanitizedString();
        var endLabel = end.sanitizedString();
        
        CURRENT_TEMP_INPUT = this;
        TEMP_SLIDER.options.range.start = start.degrees;
        TEMP_SLIDER.options.range.end = end.degrees;
        TEMP_SLIDER.setValue(startingValue.degrees);
        
        $("tempSlider").down(".startLabel").innerHTML = startLabel;
        $("tempSlider").down(".endLabel").innerHTML = endLabel;
        $("tempSlider").show();
        
        var inputDimensions = this.getDimensions();
        var sliderDimensions = $("tempSlider").getDimensions();
        // 1/2 width of the targetted item + 1/2 width of slider
        var offsetLeft = -1 * ((sliderDimensions.width/2) - (inputDimensions.width/2));
        //height of the input plus the height of the chevron
        var offsetTop = inputDimensions.height + 16;
        
        $("tempSlider").clonePosition(this, {setWidth: false, setHeight: false, offsetLeft: offsetLeft, offsetTop: offsetTop});
    },
    
    hideTempSlider: function(event){
        $("tempSlider").hide();
    },
    
    blurTempInput: function(event) {
        Yukon.ThermostatScheduleEditor.commitTempValue(this.value, this);
    },
    
    commitTempValue: function(value, input){
        var _self = Yukon.ThermostatScheduleEditor;
        value = parseFloat(value);
        
        if(!isNaN(value)){
        	var windowModeType = input.readAttribute("data-temperatureMode")
            _self.thermostat.assignDegreesOrSnapToLimit(value, windowModeType);

            //determine range
            var start = null;
            var end = null;
                    
            if(input.hasClassName("heat_F")) {
                start = _self.thermostat.HEAT.lower;
                end = _self.thermostat.HEAT.upper;
            } else if(input.hasClassName("cool_F")) {
                start = _self.thermostat.COOL.lower;
                end = _self.thermostat.COOL.upper;
            }
            
            //put value into REAL input
            input.adjacent("input:hidden")[0].value = _self.thermostat[windowModeType].temperature.getF();
            input.value = _self.thermostat[windowModeType].temperature.sanitizedString();
            input.up('.temp').setStyle({backgroundColor:_self.thermostat.color(input.readAttribute("data-temperatureMode"))});
            return true;
        }
        //revert to previousValue
        input.value = input.readAttribute('previousValue');
        return false;
    },
    
    timeKeydown: function(event){
        //don't bother processing iff not the UP or DOWN key
        if(event.keyCode != KEYDOWN && event.keyCode != KEYUP){
            return;
        }
        
        //determine range
        var parent = this.up('.period');
        var start = 0;
        var end = ((24*60)-parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsBetweenPeriods/60));  //11:45pm is the default
        var value = timeFormatter.parseTime(this.value);
                
        if(parent.previous('.period')) {
            start = parseInt(parent.previous('.period').down('.time input[name=secondsFromMidnight]').value);
            
            //round to the closest quarter hour (900 seconds = 15minutes)
            start = Math.ceil((start+Yukon.ThermostatScheduleEditor.thermostat.secondsResolution)/60);
        }
        if(parent.next('.period')){
            end = parseInt(parent.next('.period').down('.time input[name=secondsFromMidnight]').value);
            
            //round to the closest quarter hour (900 seconds = 15minutes)
            end = Math.floor((end-Yukon.ThermostatScheduleEditor.thermostat.secondsResolution)/60);
        }
        
        switch(event.keyCode){
        //Up arrow key
        case KEYUP:
            value += parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60);
            break;
        //Down arrow key
        case KEYDOWN:
            value -= parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60);
            break;
        default:
            //really this is redundant given the first check in this function
            return;
        }
        
        if(value >= start && value <= end){
            CURRENT_TIME_INPUT.value = timeFormatter.formatTime(value, parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60));
            
            //round to nearest 15
            TIME_SLIDER.setValue(value);
        }
        event.stop();
    },
    
    showTimeSlider: function(event){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.hideTempSlider();
        
        var startingValueSeconds = parseInt(this.adjacent('input[name=secondsFromMidnight]')[0].value);
        var input = event.currentTarget;
        
        //determine range
        var parent = this.up('.period');
        var startSeconds = 0;
        var endSeconds = 24*60*60;
        
        var previous = parent.previous('.period');
        if(previous) {
            var prevInput = previous.down('.time input[name=secondsFromMidnight]');
            if(prevInput){
                startSeconds = parseInt(prevInput.value);
            }
        }
        
        var next = parent.next('.period');
        if(next){
            var nextInput = next.down('.time input[name=secondsFromMidnight]');
            if(nextInput){
                endSeconds = parseInt(nextInput.value);
            }
        }
        
        if(startSeconds != 0){
            startSeconds = Math.ceil((startSeconds+_self.thermostat.secondsBetweenPeriods)/60)*60;    //round to the nearest interval
        }
        endSeconds = Math.floor((endSeconds+1-_self.thermostat.secondsBetweenPeriods)/60)*60;

        CURRENT_TIME_INPUT = this;
        TIME_SLIDER.options.range.start = startSeconds/60;
        TIME_SLIDER.options.range.end = endSeconds/60;
        
        TIME_SLIDER.setValue(startingValueSeconds/60);
        
        $("timeSlider").down(".startLabel").innerHTML = timeFormatter.formatTime(TIME_SLIDER.options.range.start, parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60));
        $("timeSlider").down(".endLabel").innerHTML = timeFormatter.formatTime(TIME_SLIDER.options.range.end, parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60));
        $("timeSlider").show();
        
        var inputDimensions = this.getDimensions();
        var sliderDimensions = $("timeSlider").getDimensions();
        // 1/2 width of the targetted item + 1/2 width of slider
        var offsetLeft = -1 * ((sliderDimensions.width/2) - (inputDimensions.width/2));
        //height of the input plus the height of the chevron
        var offsetTop = inputDimensions.height + 16;
        $("timeSlider").clonePosition(this, {setWidth: false, setHeight: false, offsetLeft: offsetLeft, offsetTop: offsetTop});
    },
    
    hideTimeSlider: function(event){
        $("timeSlider").hide();
    },
    
    blurTimeInput: function(event) {
        Yukon.ThermostatScheduleEditor.commitTimeValue(this.value, this);
    },
    
    commitTimeValue: function(value, input){
        var curr = timeFormatter.parseTime(value)*60;
        if(curr != -1){
            //check bounds
            var parent = input.up('.period');
            var start = -1;
            var end = ((24*60)+1-parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsBetweenPeriods/60))*60;
                    
            var previous = parent.previous('.period');
            if(previous) {
                var previousInput = previous.down('.time input[name=secondsFromMidnight]');
                if(previousInput){
                    start = parseInt(previousInput.value);
                }
            }
            
            var next = parent.next('.period');
            if(next){
                var nextInput = next.down('.time input[name=secondsFromMidnight]');
                if(nextInput){
                    end = parseInt(nextInput.value);
                }
            }
            
            //allow the user to set the start time to 12:00am
            if((start < curr) && (curr < end)){
                //set seconds from midnight value
                input.adjacent('input[name=secondsFromMidnight]')[0].value = timeFormatter.parseTime(input.value)*60;
                
                //get a nicely formatted time in case the user inputs some shorthand value such as '4pm'
                input.value = timeFormatter.formatTime(timeFormatter.parseTime(input.value), parseInt(Yukon.ThermostatScheduleEditor.thermostat.secondsResolution/60));
                input.writeAttribute('previousValue', input.value);
                return true;
            }
        }
        //revert to previousValue
        input.value = input.readAttribute('previousValue');
    },
    
    celsius: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.thermostat.setUnit('C');
        $$(".temp .value").each(function(elem){
            var temperature = new Temperature({degrees: parseFloat(elem.next("input:hidden").value), unit: 'F'});
            elem.innerHTML = temperature.sanitizedString('C');
        });
        $$(".temp input:text").each(function(elem){
            var temperature = new Temperature({degrees: parseFloat(elem.next("input:hidden").value), unit: 'F'});
            elem.value = temperature.sanitizedString('C');
        });
        $$(".temp, .tempLabel").each(function(elem){
            elem.removeClassName('F');
            elem.addClassName('C');
        });
        $("tempSlider").select(".tempHolder").each(function(elem){
            elem.removeClassName('F');
            elem.addClassName('C');
        });
        $$("input[name=temperatureUnit]").each(function(elem){
            elem.value = "C";
        });
        _self.calcTempColor();
    },
    
    C: function(){
        Yukon.ThermostatScheduleEditor.celsius();
    },
    
    fahrenheit: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.thermostat.setUnit('F');
        $$(".temp .value").each(function(elem){
            elem.innerHTML = Math.round(elem.next("input:hidden").value);
        });
        $$(".temp input:text").each(function(elem){
            elem.value = Math.round(elem.next("input:hidden").value);
        });
        $$(".temp").each(function(elem){
            elem.removeClassName('C');
            elem.addClassName('F');
        });
        $("tempSlider").select(".tempHolder").each(function(elem){
            elem.removeClassName('C');
            elem.addClassName('F');
        });
        $$("input[name=temperatureUnit]").each(function(elem){
            elem.value = "F";
        });
        _self.calcTempColor();
    },
    
    F: function(){
        Yukon.ThermostatScheduleEditor.fahrenheit();
    },
    
    renderTime: function(){
        $$("input[name=secondsFromMidnight]").each(function(elem){
            elem.adjacent("span.time").each(function(span){
                span.innerHTML =timeFormatter.formatTime(elem.value/60);
            });
            
            elem.adjacent("input:text").each(function(input){
                input.value =timeFormatter.formatTime(elem.value/60);
            });
        });
    },
    
    calcTempColor: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        $$(".temp input[type=hidden]").each(function(elem){
            var temperature = new Temperature({degrees: parseFloat(elem.value), unit:'F'});
            if(elem.up('.temp').hasClassName('heat')){
                elem.up(".temp").setStyle({backgroundColor:_self.thermostat.calcHEATColor(temperature)});
            } else {
                elem.up(".temp").setStyle({backgroundColor:_self.thermostat.calcCOOLColor(temperature)});
            }
        });
    }
}

Yukon.ThermostatManualEditor = {
    thermostat: null,
    
    init: function(args){
        _self = Yukon.ThermostatManualEditor;

        //prep schedule editor data
        _self.thermostat = new Yukon.Thermostat(args.thermostat);
        _self.thermostat.setUnit(args.initialUnit);
        
        $$(".manualThermostat .state").invoke('observe', 'click', _self.changeFanState);
        $$(".manualThermostat .mode").invoke('observe', 'click', _self.changeThermostatMode);
        $$(".temperatureAdjust .up").invoke('observe', 'click', _self.temperatureUp);
        $$(".temperatureAdjust .down").invoke('observe', 'click', _self.temperatureDown);
        $$(".temperatureUnit .unit").invoke('observe', 'click', _self.changeUnit);
        $$("input[name=temperature_display]").invoke('observe', 'blur', _self.onBlurTemperatureDisplay);
        $$("input[name=temperature_display]").invoke('observe', 'focus', _self.onFocusTemperatureDisplay);
        $$("#sendSettingsSubmit").invoke('observe', 'click', _self.prepForm);
        $$(".closePopup").invoke('observe', 'click', function(event){this.up(".popUpDiv").hide();});
        $$(".editLabel, .cancelLabelEdit").invoke('observe', 'click', _self.toggleLabelEditor);
        
        _self.render();
        _self.renderOtherTemperatures(_self.thermostat.COOL.temperature.unit);
    },
    
    toggleLabelEditor: function(){
        $('editName').toggle();
        $('thermostatName').toggle();
    },
    
    changeUnit: function(event) {
        var unit = event.target.readAttribute('unit');
        Yukon.ThermostatManualEditor.thermostat.setUnit(unit);
        Yukon.ThermostatManualEditor.render();
        Yukon.ThermostatManualEditor.renderOtherTemperatures(unit);
    },
    
    changeFanState: function(event) {
        Yukon.ThermostatManualEditor.thermostat.setFan(event.target.readAttribute("state"));
        Yukon.ThermostatManualEditor.render();
    },
    
    changeThermostatMode: function(event) {
        Yukon.ThermostatManualEditor.thermostat.setMode(event.target.readAttribute("mode"));
        Yukon.ThermostatManualEditor.render();
    },
    
    onBlurTemperatureDisplay: function(event) {
        if(Yukon.ThermostatManualEditor.thermostat.assignDegreesOrSnapToLimit(parseFloat(this.value), this.readAttribute("data-temperatureMode"))){
            Yukon.ThermostatManualEditor.render();
        }else{
            this.value = this.readAttribute("previousValue");
        }
    },
    
    onFocusTemperatureDisplay: function(event) {
        this.writeAttribute("previousValue", this.value);
    },

    _resetCoolAndHeatTemperatures: function(event) {
        var popup = $(event.target.readAttribute("popup_id"));

    	popup.down("input[name=coolTemperature]").value = "";
        popup.down(".coolTemperatureConfirm").innerHTML = "";
    	
        popup.down("input[name=heatTemperature]").value = "";
        popup.down(".heatTemperatureConfirm").innerHTML = "";
    },
    
    prepForm: function(event){
        var popup = $(event.target.readAttribute("popup_id"));
        var widget = event.target.up(".manualThermostat");
        
        popup.select(".unit").invoke('hide');
        
        popup.select("." + Yukon.ThermostatManualEditor.thermostat.COOL.temperature.unit).invoke('show');
        
        popup.select("input:hidden").each(function(input){
            var name = input.readAttribute("name");
            var source = widget.down("input[name="+ name +"]");
            
            if(source){
                //not really true for the checkbox, but we overwrite it down below
                input.value = source.value;
                if(widget.down("."+ name +".selected")){
                    popup.down("."+ name +"Confirm").innerHTML = widget.down("."+ name +".selected").innerHTML; 
                }else if(widget.down("input[type=text]."+ name +"")){
                    popup.down("."+ name  +"Confirm").innerHTML = widget.down("."+ name).value;
                }else if(widget.down("input[type=checkbox]."+ name +"")){
                    popup.down("."+name).down("." + widget.down("."+ name).checked).show();
                    popup.down("."+name).down("." + !widget.down("."+ name).checked).hide();
                    input.value = source.checked;
                }
            }
        });
        
        //send the actual requested value for logging
        var _self = Yukon.ThermostatManualEditor;

        // Reset the request temperatures for heat and cool.
        _self._resetCoolAndHeatTemperatures(event);
        
        // Check to see if the cool temperature should be sent in the request.
        if ((!_self.thermostat.autoEnabled && _self.thermostat.mode != 'HEAT')  ||
              _self.thermostat.autoEnabled) {

        	var coolTemperatureValue = widget.down("input[name=temperature_display][data-temperatureMode='COOL']").value;
            popup.down("input[name=coolTemperature]").value = coolTemperatureValue;
            popup.down(".coolTemperatureConfirm").innerHTML = coolTemperatureValue;

            // If we are looking at the non auto enabled page only show the one temperature in the confirm box
            if (!_self.thermostat.autoEnabled) {
            	jQuery("#coolTemperatureConfirm").show();
            	jQuery("#heatTemperatureConfirm").hide();
            }
        }

        // Check to see if the cool temperature should be sent in the request.
        if ((!_self.thermostat.autoEnabled && _self.thermostat.mode == 'HEAT') ||
             _self.thermostat.autoEnabled) {

            var heatTemperatureValue = widget.down("input[name=temperature_display][data-temperatureMode='HEAT']").value;
            popup.down("input[name=heatTemperature]").value = heatTemperatureValue;
            popup.down(".heatTemperatureConfirm").innerHTML = heatTemperatureValue;

            // If we are looking at the non auto enabled page only show the one temperature in the confirm box
            if (!_self.thermostat.autoEnabled) {
            	jQuery("#coolTemperatureConfirm").hide();
            	jQuery("#heatTemperatureConfirm").show();
            }
        }

		popup.down("input[name=temperatureUnit]").value = Yukon.ThermostatManualEditor.thermostat.COOL.temperature.unit;
        
        popup.show();
    },

    temperatureUp: function(event) {
        var _self = Yukon.ThermostatManualEditor;
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
        var _self = Yukon.ThermostatManualEditor;
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
        var _self = Yukon.ThermostatManualEditor;
        $$(".manualThermostat").each(function(widget){
            widget.select("input[name=temperature_display]").each(function(elem) {
            	var temperatureMode = elem.readAttribute("data-temperatureMode");
            	//set color
                elem.setStyle({color: _self.thermostat.color(temperatureMode)});
                
                //set availability
                switch(_self.thermostat.mode){
                	case 'AUTO':
                    case 'EMERGENCY_HEAT':
                    case 'HEAT':
                    case 'COOL':
                    	elem.enable();
                    	break;
                    default:
                    	elem.disable();
                	}
                
                //render the temperature w/ correct color and input availability
                elem.value = _self.thermostat[temperatureMode].temperature.sanitizedString();
                elem.up().down("input[name=temperature]").value = _self.thermostat[temperatureMode].degrees;
            });
            
            
            //render mode
            Yukon.ui.exclusiveSelect(widget.down("li[mode=" + _self.thermostat.mode + "]"));
            $("mode").value = _self.thermostat.mode;
            
            //render fan
            Yukon.ui.exclusiveSelect(widget.down("li[state=" + _self.thermostat.fan + "]"));
            $("fan").value = _self.thermostat.fan;

            //render unit
            Yukon.ui.exclusiveSelect(widget.down("li[unit=" + _self.thermostat.COOL.temperature.unit + "]"));
            widget.down("input[name=temperatureUnit]").value = _self.thermostat.COOL.temperature.unit;
        });
    },
    
    renderOtherTemperatures: function(unit){
        $$(".unit_label").invoke('hide');
        $$("."+ unit +"_label").invoke('show');
        
        $$(".raw_temperature_F").each(function(elem){
            var temperature = new Temperature({
                    degrees: parseFloat(elem.readAttribute('raw_temperature_F')),
                    unit: 'F'
                });
            
            elem.innerHTML = temperature.sanitizedString(unit);
        });
    }
};



//args requires the following values:
// heat                 - object holding:
//      lower           - Temperature representing the lower heat limit of this thermostat
//      upper           - Temperature representing the upper heat limit of this thermostat
//cool                  - object holding:
//      lower           - Temperature representing the lower cool limit of this thermostat
//      upper           - Temperature representing the upper cool limit of this thermostat
// secondsResolution    - integer seconds to snap time values to
// secondsBetweenPeriods- integer minimum time between periods in seconds

//optional args are:
// currentUnit          - string - Current units to display the schedules in
// heatColor            - object - {r:{start:[0..255], end:[0..255]}, g:{start:[0..255], end:[0..255]}, b:{start:[0..255], end:[0..255]}, a:{start:[0..255], end:[0..255]}}
// coolColor            - object - {r:{start:[0..255], end:[0..255]}, g:{start:[0..255], end:[0..255]}, b:{start:[0..255], end:[0..255]}, a:{start:[0..255], end:[0..255]}}
Yukon.Thermostat = function(args){
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
	    var mode = this.mode
	    if (this.mode == 'AUTO' && !this.autoEnabled) {
	    	this.setMode('COOL');
	    }
	    this.setMode(this.mode);
    }
    
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
        var coolTemperature = (temperatureMode == 'COOL') ? newTemperature : jQuery.extend(true, {}, this.COOL.temperature);
        var heatTemperature = (temperatureMode == 'HEAT') ? newTemperature : jQuery.extend(true, {}, this.HEAT.temperature);
    	
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
            	
	        	var target = (event.currentTarget) ? event.currentTarget : event.srcElement;
	        	var windowModeType = target.readAttribute("data-temperatureMode");
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
	        	
	        	var target = (event.currentTarget) ? event.currentTarget : event.srcElement;
	        	var windowModeType = target.readAttribute("data-temperatureMode");
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
    		var clonedTemperatureObject = jQuery.extend(true, {}, this[tempWindowType].temperature);
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
        		jQuery('.coolDiv').hide();
        		jQuery('.heatDiv').show();
        	}
            return this._toValidHeatTemperature(this.HEAT.temperature);
        case 'COOL':
        	if (!this.autoEnabled) {
        		jQuery('.coolDiv').show();
        		jQuery('.heatDiv').hide();
        	}
            return this._toValidCoolTemperature(this.COOL.temperature);
        default:
        	
        	// TODO: find a better way to do this.  
        	if (!this.autoEnabled) {
        		jQuery('.coolDiv').show();
        		jQuery('.heatDiv').hide();
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
    }
    
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