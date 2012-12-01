<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<cti:standardPage module="support" page="setupDatabase">
<cti:includeScript link="JQUERY_GRID"/>
    
<style>
.devWidget {
    margin: 15px;
    -moz-box-shadow: -2px -2px 10px rgba(0,0,0,0.2);
    -webkit-box-shadow: -2px -2px 10px rgba(0,0,0,0.2);
    box-shadow: -2px -2px 10px rgba(0,0,0,0.2);
}

.widgetContent {
    padding:5px;
    margin:5px;
    border-top:1px solid #CCC;
    border-bottom:1px solid #CCC;
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

<cti:dataGrid cols="2" tableClasses="twoColumnLayout split" cellStyle="padding-right:0px;">

    <cti:dataGridCell>
        <%@include file="devRoleProperty.jspf"%>
        <%@include file="devStars.jspf"%>
    </cti:dataGridCell>
    
    <cti:dataGridCell>
        <%@include file="devAmr.jspf"%>
        <%@include file="devCapControl.jspf"%>
    </cti:dataGridCell>
    
</cti:dataGrid>


    <script type="text/javascript">
    	jQuery(function() {
            
            jQuery("input#f_check_all_meters:checkbox").checkAll("input.f_check_single_meter:checkbox");
            jQuery("input#f_check_all_hardware:checkbox").checkAll("input.f_check_single_hardware:checkbox");

            jQuery('body').on('.f_ec_select', 'change', function() {
                if (jQuery('#createNewEnergyCompanyOpt').is(':selected')) {
                    jQuery('.newEnergyCompanyRow').show(800, function() {
                        jQuery('.newEnergyCompanyInput').focus();
                    });
                } else {
                    jQuery('.newEnergyCompanyRow').hide(500);
                }
            });
            
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
            
            function setWidgetAvailability(widget,isAvailable, progress) {
                if (isAvailable) {
                    widget.find(":input").not(".f_disabled").removeAttr("disabled");
                    widget.find(".widgetMessage").html("").removeClass("resultMessage");
                } else {
                    widget.find(".userMessage").remove();
                    widget.find(":input").attr("disabled","disabled");
                    widget.find(".error").removeClass("error");
                    widget.find(".errorMessage").removeClass("errorMessage");
                    if (progress) {
                        widget.find(".widgetMessage").html("Setup is currently " + progress + "% complete...").addClass("resultMessage");
                    } else {
                        widget.find(".widgetMessage").html("Setup is currently running...").addClass("resultMessage");
                    }
                }
            }
            
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
                  }
                });
              }
            checkAvailability();
            setInterval(checkAvailability,1500);
    	});
    </script>

</cti:standardPage>
