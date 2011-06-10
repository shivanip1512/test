Yukon.ThermostatScheduleEditor = {
        
    upperHeatF: 99,
    lowerHeatF: 39,
    upperCoolF: 99,
    lowerCoolF: 40,
    currentUnit: 'fahrenheit',
    
    heatColor: {
        r: {
            start: 242,
            end: 242
        },
        g: {
            start: 150,
            end: 0
        },
        b: {
            start: 29,
            end: 29
        },
        a: {
            start: 1,
            end: 1
        }
    },
    
    coolColor: {
        r: {
            start: 64,
            end: 189
        },
        g: {
            start: 153,
            end: 220
        },
        b: {
            start: 255,
            end: 255
        },
        a: {
            start: 1,
            end: 1
        }
    },
    
        
    init: function(opts) {
        var _self = Yukon.ThermostatScheduleEditor;
        //merge the options
        for(opt in opts){
            if(_self[opt] != 'undefined'){
                _self[opt] = opts[opt];
            }
        }
        
        if(opts.thermostat){
            for(key in opts.thermostat){
                _self[key] = opts.thermostat[key];
            }
            _self.thermostat = opts.thermostat;
        }
        
        _self.hookupControls();
        
        
        if(opts.schedules){
            $j(".schedules").addClass(Yukon.ui.getParameterByName('size') || "small");
            for(var i=0; i<opts.schedules.length; i++){
                if(opts.thermostat.currentScheduleId == opts.schedules[i].scheduleId){
                    $j(".schedules").prepend(_self.render(opts.schedules[i], {
                        meridian: true, 
                        size: Yukon.ui.getParameterByName('size') || "small",
                        tabs: Yukon.ui.getParameterByName('tabs'),
                        current: true
                    }));
                }else{
                    $j(".schedules").append(_self.render(opts.schedules[i], {
                        meridian: true, 
                        size: Yukon.ui.getParameterByName('size') || "small",
                        tabs: Yukon.ui.getParameterByName('tabs'),
                        current: false
                    }));
                }
            }
        }

        //set the interface into the default temperature scale
        _self[opts.units ? opts.units : 'fahrenheit']();
    },
        
    render: function(schedule, opts){
        
        var _self = Yukon.ThermostatScheduleEditor;
        
        if(!opts){
            opts = {};
        }
        
        var rowClass = ['even', 'odd'];
        var sched = $j(".templates .schedule").clone();
        
        sched.attr('id', "scheduleId_"+schedule.scheduleId);
        
        if(opts.current){
            sched.addClass("current");
        }
        
        sched.find(".title").html(schedule.scheduleName);
        
        //show the schedule type
//        if(_self.thermostat){
//            sched.find(".title").after(' <small><em>(' + _self.thermostat.modes[schedule.type.name] + '</em>)</small>');
//        }else{
//            sched.find(".title").after(' <small><em>(' + schedule.type.name + '</em>)</small>');
//        }
        
        //figure out tabs
        if(opts.tabs && schedule.type.days.length > 1){
            //render tab controls
            for(var l=0; l<schedule.type.days.length; l++) {
                var label = schedule.type.days[l];
                if(schedule.supported_period == 2){
                    label = schedule.type.days_label[l];
                }
                sched.find(".f_tabs").append('<li>'+ label +'</li>');
            }
            
            //don't need striping on tabbed interface
            rowClass = ['even', 'even'];
        }else{
            sched.find(".f_tabs").remove();
            sched.find(".f_tabbed").removeClass('f_tabbed');
            sched.find(".f_tab").removeClass('f_tab');
        }
        
        sched.find(".type").html("");
        
//            for(var k=0; k<TYPES.length; k++){
//                var checked = TYPES[k].name == schedule.type.name ? 'checked="checked"' : '';
//            }
        
        if(_self.thermostat){
            var periodLabels = [];
            for(period in _self.thermostat.periods) {
                periodLabels.push(_self.thermostat.periods[period]);
            }
            
            periodLabels.sort(function(a,b){
                if(a.order < b.order){
                    return -1;
                }else if(a.order > b.order){
                    return 1;
                }else{
                    return 0;
                }
            });
            
            for(var k=0; k<periodLabels.length; k++){
                sched.find(".labels").append('<div class="period">'+ periodLabels[k].name +'</div>');
            }
            
            sched.find(".labels .period").append($j(".templates .heat_cool_label").clone());
        }
        
        schedule.days = {};
        
        if(schedule.periods){
            _self.renderDays({schedule:schedule, elem:sched, mode:'view'});
        }
        
        for(key in schedule){
            sched.find("input[name="+key+"]").val(schedule[key]);
        }
        
        sched.find("input[name=schedules]").val(JSON.stringify(schedule));
        return sched;
    },
    
    renderEditor: function(schedule){
        
        var _self = Yukon.ThermostatScheduleEditor;
        var sched = $j(".templates .schedule_editor").clone();

//        sched.find(".type").html("");
        
        //fill-in all of the keyed values
        for(var key in schedule){
            sched.find("input[name="+key+"]").val(schedule[key]);
        }

        if(_self.thermostat){
            var periodLabels = [];
            for(period in _self.thermostat.periods) {
                periodLabels.push(_self.thermostat.periods[period]);
            }
            
            periodLabels.sort(function(a,b){
                if(a.order < b.order){
                    return -1;
                }else if(a.order > b.order){
                    return 1;
                }else{
                    return 0;
                }
            });
            
            for(var k=0; k<periodLabels.length; k++){
                sched.find(".labels").append('<div class="period">'+ periodLabels[k].name +'</div>');
            }
            
            sched.find(".labels .period").append($j(".templates .heat_cool_label").clone());
        }
        
        schedule.days = {};
        
        if(schedule.periods){
            _self.renderDays({schedule:schedule, elem:sched, mode:'edit'});
        }
        
        sched.find("input[name=schedules]").val(JSON.stringify(schedule));
        return sched;
    },
    
    renderDays: function(opts){
        var _self = Yukon.ThermostatScheduleEditor;
        var rowClass = ['even', 'odd'];
        
      //@TODO: cleanup this hack
        for(var j=0; j<opts.schedule.periods.length; j++){
            var entry = opts.schedule.periods[j];
            
            if(typeof(opts.schedule.days[entry.timeOfWeek]) == 'undefined') {
                opts.schedule.days[entry.timeOfWeek] = {periods:[]};
            }
            
            opts.schedule.days[entry.timeOfWeek].periods.push(entry);
        }
        
        for(var j=0; j<opts.schedule.days.length; j++) {
            opts.schedule.days[j].sort(function(a,b){
                if(a.secondsFromMidnight < b.secondsFromMidnight)
                    return -1;
                else if(a.secondsFromMidnight < b.secondsFromMidnight)
                    return 1;
                return 0;
            });
        }
        //END hack
        
        //render days
        var j=0;
        for(var key in opts.schedule.days){
            j++;
            
            var day = $j(".templates .day").clone();
            day.addClass("small");
            day.addClass(rowClass[j%2]);
            
            if(typeof(_self.thermostat.dayLabels[key]) == 'undefined'){
                day.find(".label").text(key);
            }else{
                day.find(".label").text(_self.thermostat.dayLabels[key].abbr);
            }
            
            //render periods
            for(var k=0; k<opts.schedule.days[key].periods.length; k++){
                var period = $j(".templates .period_"+opts.mode).clone();
                
                var time = opts.schedule.days[key].periods[k].secondsFromMidnight/60
                period.find(".name").text(opts.schedule.days[key].periods[k].name);
                
                if(opts.mode == "edit"){
                    period.find(".time").val(timeFormatter.formatTime(time));
                    period.find(".cool_F").val(opts.schedule.days[key].periods[k].cool_F);
                    period.find(".heat_F").val(opts.schedule.days[key].periods[k].heat_F);
                }else{
                    period.find(".time").text(timeFormatter.formatTime(time));
                    period.find(".cool .value").text(opts.schedule.days[key].periods[k].cool_F);
                    period.find(".heat .value").text(opts.schedule.days[key].periods[k].heat_F);
                }
                
                //store data
                for(periodKey in opts.schedule.days[key].periods[k]) {
                    period.find("input[name="+ periodKey +"]").val(opts.schedule.days[key].periods[k][periodKey]);
                }
                
                if(opts.schedule.days[key].periods[k].pseudo){
                    period.addClass('dn');
                }
                
                //render edit
                day.find(".periods").append(period);
            }
            opts.elem.find(".days").append(day);
        }
    },
    
    hookupControls: function() {
        var _self = Yukon.ThermostatScheduleEditor;
        
        $j('#conversion_buttons a').live('click', function(ev){
            var target = $j(this);
            var mode = target.attr('mode');
            
            if(!target.parent().hasClass('disabled')){
                target.addClass('active');
                target.siblings().removeClass('active');
                
                if(mode == 'c'){
                    _self.celcius();
                } else {
                    _self.fahrenheit();
                }
            }
            return false;
        });
        
        $j('span.close').live('click', function(){
            $j('.schedule_editor').hide();
            $j('.conversion_buttons').removeClass('disabled');
            return false;
        });
    
        $j(".edit").live("click", function(event){
            var target = $j(this);
            var _self = Yukon.ThermostatScheduleEditor;
            
//            target.closest("li.schedule").toggleClass("editing");
//            $j("#editingScreen").fadeIn();
//            target.closest("li.schedule").find(".editable").each(function(index, elem){
//                elem = $j(elem);
//                elem.hide();
//                elem.siblings(".hide_when_editing").hide();
//                elem.siblings(".editor").find("input:text").val(elem.text());
//            });
            
            var id = target.closest('.schedule').find('input[name=scheduleId]').val();
            for(var i=0; i<DATA.SCHEDULES.length; i++){
                if(DATA.SCHEDULES[i].scheduleId == id){
                    $j("#editSchedule_body .container").html(_self.renderEditor(DATA.SCHEDULES[i], {
                        meridian: _self.meridian, 
                        size: "small"
                    }));
                    break;
                }
            }
          //set the interface into the default temperature scale
            _self[_self.currentUnit]();
            return false;
        });
        
        $j(".save").live('click', function(event){
            var target = $j(this);
//            target.closest("li.schedule").toggleClass("editing");
//            $j("#editingScreen").fadeOut();
//            target.closest("li.schedule").find(".editable").each(function(index, elem){
//                elem = $j(elem);
//                elem.show();
//                elem.siblings(".hide_when_editing").show();
//                
//                //transfer values into inputs
//                elem.siblings("input:hidden").val(elem.siblings(".editor").find("input:text").val());
//            });
//            
//            $j("#slider").slider('destroy');
//            $j("#slider .chevron").remove();
//            _self.fahrenheit();
            
            var form = target.closest(".popUpDiv").find("form");
            _self.prepForm(form);
            form.submit();
            return false;
        });
        
//        $j("a.toggle").live("click", function(event){
//            event.stopPropagation();
//            var target = $j(this);
//            var parent = target.closest("div.toggle");
//            parent.hide()
//            parent.siblings(".toggle").show();
//            return false;
//        });
        
        $j(".schedule_editor input[type=text]").live('focus', function(e){
            this.select();
            //store the previous value
            $j(e.currentTarget).attr('previousValue', e.currentTarget.value);
            return false;
        });
        
        $j(".popUpDiv .cancel").live("click", function(e){
            $j(e.currentTarget).closest(".popUpDiv").hide();
            debug('hide');
            $j("#slider").slider('destroy');
            $j("#slider .chevron").remove();
            return false;
        });
        
        $j("#mode_buttons a").live("click", function(event){
            event.stopPropagation();
            var target = $j(this);
            target.addClass('active');
            target.siblings().removeClass('active');
            if(target.hasClass('cool')){
                $j('.temp.heat').css('visibility', 'hidden');
                $j('.temp.cool').css('visibility', 'visible');
            }else if(target.hasClass('heat')){
                $j('.temp.cool').css('visibility', 'hidden');
                $j('.temp.heat').css('visibility', 'visible');
            }else if(target.hasClass('auto')){
                $j('.temp.cool').css('visibility', 'visible');
                $j('.temp.heat').css('visibility', 'visible');
            }
            return false;
        });
        
        $j(".temp.heat input").live('focus', function(){
            var target = $j(this);
            $j( "#slider" ).offset({top:target.offset().top - 30, left:target.offset().left-100});
            $j( "#slider" ).slider({
                value:this.value,
                min: _self.lowerHeatF,
                max: _self.upperHeatF,
                step: 1,
                slide: function( event, ui ) {
                    target.val(ui.value);
                    target.closest(".temp").css('background-color', _self.calcHeatColor(ui.value));
                    $j("#slider").css('background-color', _self.calcHeatColor(ui.value));                    
                    $j("#slider a").text(ui.value);
                }
            });
            $j("#slider a").text(this.value);
            $j("#slider").css('background-color', _self.calcHeatColor(this.value));
            if(!$j("#slider .chevron")[0]){
                $j("#slider").append('<div class="chevron"></div>');
            }
            return false;
        });
        
        $j(".temp.cool input").live('focus', function(){
            var target = $j(this);
            $j( "#slider" ).offset({top:target.offset().top - 30, left:target.offset().left-100});
            $j( "#slider" ).slider({
                value:this.value,
                min: _self.lowerCoolF,
                max: _self.upperCoolF,
                step: 1,
                slide: function( event, ui ) {
                    target.val(ui.value);
                    target.closest(".temp").css('background-color', _self.calcCoolColor(ui.value));
                    $j("#slider a").text(ui.value);
                    $j("#slider").css('background-color', _self.calcCoolColor(ui.value));
                }
            });
            $j("#slider a").text(this.value);
            $j("#slider").css('background-color', _self.calcCoolColor(this.value));
            if(!$j("#slider .chevron")[0]){
                $j("#slider").append('<div class="chevron"></div>');
            }
            return false;
        });
        
        //time editing and validation
        $j("input.time").live('blur change', function(e){
            var curr = timeFormatter.parseTime(e.currentTarget.value);
            
            if(curr != -1){
                //check bounds
                var prev = timeFormatter.parseTime($j(e.currentTarget).closest(".period").prev(".period").find("input.time").val());
                var next = timeFormatter.parseTime($j(e.currentTarget).closest(".period").next(".period").find("input.time").val());
                if(!$j(e.currentTarget).closest(".period").next(".period")[0]){
                    next = (24*60)-1;
                }
                if((prev < curr) && (curr < next)){
                    //set seconds from midnight value
                    $j(e.currentTarget).siblings('input[type=hidden]').val(timeFormatter.parseTime(e.currentTarget.value)*60);
                    //get a nicely formatted time in case the user inputs some shorthand value such as '4pm'
                    e.currentTarget.value = timeFormatter.formatTime(timeFormatter.parseTime(e.currentTarget.value));
                    $j(e.currentTarget).attr('previousValue', e.currentTarget.value);
                    return false;
                }
            }
            //revert to previousValue
            e.currentTarget.value = $j(e.currentTarget).attr('previousValue');
            return false;
        });
        
        //temp validation
        $j("input.cool_F, input.heat_F").live('blur change', function(e){
            var _self = Yukon.ThermostatScheduleEditor;
            var value = parseFloat(e.currentTarget.value);
            var value_C = value;
            
            //convert the value to fahrenheit scale
            if(_self.currentUnit == 'celcius'){
                //only .5 increments are allowed
                value = Math.round(value*2)/2;
                value_C = value;
                value = ((value*9)/5)+32;
            }
            
            var pass = false;
            
            if($j(e.currentTarget).hasClass('cool_F')){
                if((_self.lowerCoolF <= value) && (value <= _self.upperCoolF)){
                    pass = true;
                    $j(e.currentTarget).closest(".temp").css('background-color',  _self.calcCoolColor(value));
                }
            }else if($j(e.currentTarget).hasClass('heat_F')){
                if((_self.lowerHeatF <= value) && (value <= _self.upperHeatF)){
                    pass = true;
                   $j(e.currentTarget).closest(".temp").css('background-color',  _self.calcHeatColor(value));
                }
            }
            
            if(pass){
                $j(e.currentTarget).siblings("input:hidden").val(value);
                if(_self.currentUnit == 'celcius'){
                    e.currentTarget.value = value_C;
                }else{
                    e.currentTarget.value = parseInt(value);
                }
                return false;
            }
            
            //revert to previousValue
            e.currentTarget.value = $j(e.currentTarget).attr('previousValue');
            return false;
        });
        
        
        $j(".delete").live('click', function(e){
           //confirmation popup already shown at this point
            $j("form[name=deleteSchedule] input[name=scheduleId]").val($j(e.currentTarget).closest(".popUpDiv").find("form").find('input[name=scheduleId]').val());
            return false;
        });
        
        $j(".send").live('click', function(e){
            //confirmation popup already shown at this point
             $j("form[name=sendSchedule] input[name=scheduleId]").val($j(e.currentTarget).closest('form').find('input[name=scheduleId]').val());
             return false;
         });
        
        $j(".create").live('click', function(){
            //show type picker
            $j(".page_0 input:radio").prop('checked', false);
            Yukon.ui.wizard.reset($("createSchedule_body"));
            return false;
        });
        
        $j(".page_0 input").live('change', function(e){
            $j(e.currentTarget).closest(".f_page").find(".f_next").removeAttr("disabled");
        });
        
        //creating a schedule
        $j(".page_0 .f_next").live('click', function(e){
            
            //if the selected mode does not match the default mode, then try to fill in the blanks or strip out unnecessary periods
            //WEEKDAY periods tend to trump
            
            //copy the default schedule
            var schedule = $j.extend(true, {}, DATA.DEFAULT);
            
            //separate by timeOfWeek
            periods = {WEEKDAY:[],WEEKEND:[],MONDAY:[],TUESDAY:[],WEDNESDAY:[],THURSDAY:[],FRIDAY:[],SATURDAY:[],SUNDAY:[]};
            for(var i=0; i<schedule.periods.length; i++){
                periods[schedule.periods[i].timeOfWeek].push(schedule.periods[i]);
            }
            
            //fill-in the blanks
            for(day in periods){
                if(periods[day].length == 0){
                    periods[day] = $j.extend(true, [], periods.WEEKDAY);
                }
                for(var i=0; i<periods[day].length; i++){
                    periods[day][i].timeOfWeek = day;
                    //fixup 4 <-> 2 period schedule type mismatch
                    if((DATA.thermostat.supportedPeriods == 2) && (i < 2)){
                        periods[day][i].pseudo = true;
                        periods[day][i].cool_F = -1;
                        periods[day][i].heat_F = -1;
                        periods[day][i].secondsFromMidnight = 0;
                    } else if(DATA.thermostat.supportedPeriods == 4) {
                        if(periods[day][i].pseudo) {
                            periods[day][i].pseudo = false;
                            periods[day][i].cool_F = DATA.thermostat.upperCoolF - ((DATA.thermostat.upperCoolF - DATA.thermostat.lowerCoolF)/2);
                            periods[day][i].heat_F = DATA.thermostat.upperHeatF - ((DATA.thermostat.upperHeatF - DATA.thermostat.lowerHeatF)/2);
                            periods[day][i].secondsFromMidnight = i*3600;
                        }
                    }
                }
            }
            
            schedule.thermostatScheduleMode = $j(".page_0 input:radio:checked").val();
            
            switch(schedule.thermostatScheduleMode){
            case 'ALL':
                schedule.periods = periods.WEEKDAY;
                break;
            case 'WEEKDAY_WEEKEND':
                schedule.periods = periods.WEEKDAY.concat(periods.WEEKEND);
                break;
            case 'WEEKDAY_SAT_SUN':
                schedule.periods = periods.WEEKDAY.concat(periods.SATURDAY, periods.SUNDAY);
                break;
            case 'SINGLE':
                schedule.periods = periods.MONDAY.concat(periods.TUESDAY, periods.WEDNESDAY, periods.THURSDAY, periods.FRIDAY, periods.SATURDAY, periods.SUNDAY);
                break;
            default:
                break;
            }
            
            //render the schedule in edit mode
            $j(".page_1 .createSchedule").html(_self.renderEditor(schedule, {
                meridian: true, 
                size: "small",
                tabs: false
            }));
            
          //set the interface into the default temperature scale
          _self[_self.currentUnit]();
            
            return false;
        });        
    },
    
    prepForm: function(form) {
       //reconstruct the data object to submit
        var periodsArr = [];
       var periods = form.find(".period");
       
       for(var i=0; i<periods.length; i++) {
           var inputs = $j(periods[i]).find("input[name!=]");
           if(inputs.length > 0){
               periodData = {};
               for(var j=0; j<inputs.length; j++) {
                   periodData[$j(inputs[j]).attr('name')] = $j(inputs[j]).val();
               }
               periodsArr.push(periodData);
           }
       }
       
       var schedule = JSON.parse(form.find("input[name=schedules]").val());
       schedule.periods = periodsArr;
       schedule.scheduleName = form.find("input[name=scheduleName]").val();
       form.find("input[name=schedules]").val(JSON.stringify(schedule));
    },
    
    celcius: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.currentUnit = 'celcius';
        $$(".temp .value").each(function(elem){
            var f = parseFloat(elem.next().value);
            f -= 32;
            var c = ((f*5)/9)
            var rounded = Math.round(c*2) / 2;
            elem.innerHTML = rounded.toFixed(1);
        });
        $$(".temp input:text").each(function(elem){
            var f = parseFloat($j(elem).siblings("input[type=hidden]").val());
            f -= 32;
            var c = ((f*5)/9)
            var rounded = Math.round(c*2) / 2;
            elem.value = rounded.toFixed(1);
        });
        _self.calcTempColor();
    },
    
    fahrenheit: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.currentUnit = 'fahrenheit';
        $$(".temp .value").each(function(elem){
            $j(elem).text(parseInt($j(elem).siblings("input[type=hidden]").val()));
        });
        $$(".temp input:text").each(function(elem){
            $j(elem).val(parseInt($j(elem).siblings("input[type=hidden]").val()));
        });
        _self.calcTempColor();
    },
    
    conversionTest: function(){
//        var fail = [];
//        
//        for(var i=1; i<120; i++) {
//            var c = ((i-32)*5/9);
//            var value = ((c*9)/5)+32;
//            if (i != value) {
//                fail.push(i + " FAIL!!!!!!! " + c + " " + value);
//            }
//        }
//        
//        if(fail.length > 0){
//            alert("FAIL/n"+fail.join("/n"));
//        }else{
//            alert("PASS")
//        }
        var fail = [];
        var c = 0;
        while(c<40) {
            //convert to fahrenheit
            var f = ((c*9)/5)+32;
            
            //convert back to celcius and round like we do on display and validate
            var d = Math.round(((f-32)*5/9)*2)/2;
            if (d != c) {
                fail.push(c + " FAIL!!!!!!! " + d + " " + f);
            }
            
            c += 0.5;
        }
        
        if(fail.length > 0){
            alert("FAIL\n"+fail.join("\n"));
        }else{
            alert("PASS")
        }
    },
    
    calcTempColor: function(){
        $j(".temp input[type=hidden]").each(function(idx, elem){
            elem = $j(elem);
            if(elem.closest('.temp').hasClass('heat')){
                var temp = parseInt(elem.val());
                elem.closest(".temp").css('backgroundColor', Yukon.ThermostatScheduleEditor.calcHeatColor(temp));
            } else {
                var temp = parseInt(elem.val());
                elem.closest(".temp").css('backgroundColor', Yukon.ThermostatScheduleEditor.calcCoolColor(temp));
            }
        });
    },
    
    calcHeatColor: function(temp){
        var _self = Yukon.ThermostatScheduleEditor;
        var r = _self.heatColor.r.start;
        var g = Math.round(_self.heatColor.g.start - Math.abs((temp - _self.lowerHeatF) * ((_self.heatColor.g.end - _self.heatColor.g.start)/(_self.upperHeatF - _self.lowerHeatF))));
        var b = _self.heatColor.b.start; 
        return "rgb("+ r +", "+ g +", "+ b +")";
    },
    
    calcCoolColor: function(temp){
        var _self = Yukon.ThermostatScheduleEditor;
        var tempRange = _self.upperCoolF - _self.lowerCoolF;
        var RRange = _self.coolColor.r.end - _self.coolColor.r.start;
        var GRange = _self.coolColor.g.end - _self.coolColor.g.start;
        var r = Math.round(_self.coolColor.r.start + ((temp - _self.lowerCoolF) * (RRange/tempRange)));
        var g = Math.round(_self.coolColor.g.start + ((temp - _self.lowerCoolF) * (GRange/tempRange)));
        var b = _self.coolColor.b.start;
        return "rgb("+ r +","+ g +","+ b +")";
    }
}