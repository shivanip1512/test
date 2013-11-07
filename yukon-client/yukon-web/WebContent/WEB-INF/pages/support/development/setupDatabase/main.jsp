<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<cti:standardPage module="support" page="setupDatabase">
<cti:includeScript link="JQUERY_GRID"/>
<cti:includeScript link="JQUERY_COOKIE"/>
    
<style>

.widgetContent {
    padding:5px;
    margin:5px;
}

.subItemCheckbox {
    margin-left:10px;
    padding-left:5px;
    border-left: 1px solid #CCC;
}

.results {
    max-height:100px;
    overflow:auto;
}

.resultFailure {
    border: 1px solid red;
    background-color: pink;
    padding: 5px;
    margin: 3px;
}

.resultsuccess {
    border: 1px solid green;
    background-color: lightgreen;
    padding: 5px;
    margin: 3px;
}

.resultMessage {
    border: 1px solid blue;
    background-color: lightblue;
    padding: 5px;
    margin: 3px;
}

.widgetMessage {
    width:95%;
}

</style>

<div id="tabs">
    <ul>
        <li><a href="#rolePropertiesTab">Role Properties</a></li>
        <li><a href="#starsTab">Stars Accounts</a></li>
        <li><a href="#amrTab">AMR</a></li>
        <li><a href="#capControlTab">Cap Control</a></li>
        <li><a href="#eventLogTab">Event Log</a></li>
    </ul>

    <div id="rolePropertiesTab" class="clearfix"><%@include file="devRoleProperty.jspf"%></div>
    <div id="starsTab" class="clearfix"><%@include file="devStars.jspf"%></div>
    <div id="amrTab" class="clearfix"><%@include file="devAmr.jspf"%></div>
    <div id="capControlTab" class="clearfix"><%@include file="devCapControl.jspf"%></div>
    <div id="eventLogTab" class="clearfix"><%@include file="devEventLog.jspf"%></div>
</div>

<script type="text/javascript">
    jQuery(function() {
        jQuery('#tabs').tabs({'cookie' : {}, fx: { height: 'toggle', duration: 200 }});

            var ajaxSubmitOptions = {
                beforeSubmit:  beforeSubmit,        // pre-submit callback 
                success:       ajaxResponse,        // post-submit callback 
                error:         ajaxError,
                type:          'POST'
            }; 

            // bind form using 'ajaxForm'
            jQuery('#setupRolePropertiesForm').ajaxForm(ajaxSubmitOptions); 
            jQuery('#setupAMRForm').ajaxForm(ajaxSubmitOptions); 
            jQuery('#setupCapControlForm').ajaxForm(ajaxSubmitOptions); 
            jQuery('#setupStarsForm').ajaxForm(ajaxSubmitOptions);
            jQuery('#setupEventLogForm').ajaxForm(ajaxSubmitOptions); 
             
            function beforeSubmit(formData, jqForm, options) {
                setWidgetAvailability(jqForm.closest(".devWidget"), false);
                return true;
            } 

            function ajaxResponse(responseText, statusText, xhr, jqForm)  { 
                // The entire devWidget box will get replaced so this probably
                // isn't neccesary to call
                //setWidgetAvailability(jqForm.closest(".devWidget"), true);
                jqForm.closest(".devWidget").replaceWith(responseText);
                var formId = jqForm.attr("id");
                jQuery("#"+formId).ajaxForm(ajaxSubmitOptions);
            }

            function ajaxError(response, status, error) {
                // need to attatch this to only the box which it relates to.
                //jQuery(".widgetMessage").html("Error: Setup did not finish " + response.responseText);
            }
            
            function setWidgetAvailability(widget, isAvailable, progress) {
                if (isAvailable) {
                    widget.find(":input").not(".f-disabled").removeAttr("disabled");
                    widget.find(".widgetMessage").html("").removeClass("resultMessage");
                    jQuery('a[href$="#'+widget.attr("id")+'Tab"]').removeAttr("style");
                } else {
                    widget.find(".user-message").remove();
                    widget.find(":input").attr("disabled","disabled");
                    widget.find(".error").removeClass("error");
                    widget.find(".error").removeClass("error");
                    if (progress) {
                        widget.find(".widgetMessage").html("Setup is currently " + progress + "% complete...").addClass("resultMessage");
                    } else {
                        widget.find(".widgetMessage").html("Setup is currently running...").addClass("resultMessage");
                    }
                    slowFlash(jQuery('a[href$="#'+widget.attr("id")+'Tab"]'));
                    //jQuery('a[href$="#'+widget.attr("id")+'Tab"]').css("background-color","#FFCCCC");
                }
            }
            
            function slowFlash(selector){
                jQuery(selector).animate({"background-color":"#FFCCCC"}, 1000, function(){
                    jQuery(selector).animate({"background-color":"#FFFFFF"}, 1000);
                });
            }
            
            // Main UI Loop
            function checkAvailability() {
                jQuery.ajax({
                  url: "checkAvailability",
                  dataType: "json",
                  success: function(data) {
                    var data = jQuery.parseJSON(data);
                    setWidgetAvailability(jQuery("#roleProperties"),data.roleProperties);
                    setWidgetAvailability(jQuery("#capControl"),data.capControl, data.capControlProgress);
                    setWidgetAvailability(jQuery("#amr"),data.amr);
                    setWidgetAvailability(jQuery("#stars"),data.stars,data.starsProgress);
                    setWidgetAvailability(jQuery("#eventLog"),data.eventLog, data.eventLogProgress);
                  }
                });
              }
            checkAvailability();
            setInterval(checkAvailability,1500);
    	});
    </script>

</cti:standardPage>
