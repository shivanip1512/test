<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="pickers">
<tags:styleguide page="pickers">

<style>
.picker-example .one { line-height: 26px; }
.description { line-height: 22px; }
</style>

<c:set var="pickerType" value="pointPicker"/>
<c:if test="${!empty param.pickerType}">
    <c:set var="pickerType" value="${param.pickerType}"/>
</c:if>

<c:choose>
    <c:when test="${pickerType == 'pointPicker'}">
        <c:set var="itemIdProperty" value="pointId"/>
        <c:set var="itemNameProperty" value="pointName"/>
    </c:when>
    <c:when test="${pickerType == 'paoPicker'}">
        <c:set var="itemIdProperty" value="paoId"/>
        <c:set var="itemNameProperty" value="paoName"/>
    </c:when>
    <c:when test="${pickerType == 'assignedProgramPicker'}">
        <c:set var="itemIdProperty" value="assignedProgramId"/>
        <c:set var="itemNameProperty" value="displayName"/>
    </c:when>
    <c:when test="${pickerType == 'availableLoadProgramPicker'}">
        <c:set var="itemIdProperty" value="programId"/>
        <c:set var="itemNameProperty" value="programName"/>
    </c:when>
    <c:when test="${pickerType == 'customerAccountPicker'}">
        <c:set var="itemIdProperty" value="accountId"/>
        <c:set var="itemNameProperty" value="accountNumber"/>
    </c:when>
    <c:when test="${pickerType == 'userPicker'}">
        <c:set var="itemIdProperty" value="userId"/>
        <c:set var="itemNameProperty" value="userName"/>
    </c:when>
    <c:when test="${pickerType == 'userGroupPicker'}">
        <c:set var="itemIdProperty" value="userGroupId"/>
        <c:set var="itemNameProperty" value="userGroupName"/>
    </c:when>
    <c:when test="${pickerType == 'loginGroupPicker'}">
        <c:set var="itemIdProperty" value="groupId"/>
        <c:set var="itemNameProperty" value="groupName"/>
    </c:when>
    <c:when test="${pickerType == 'notificationGroupPicker'}">
        <c:set var="itemIdProperty" value="notificationGroupId"/>
        <c:set var="itemNameProperty" value="name"/>
    </c:when>
        <c:when test="${pickerType == 'monitorPicker'}">
        <c:set var="itemIdProperty" value="monitorId"/>
        <c:set var="itemNameProperty" value="monitorName"/>
    </c:when>
</c:choose>

<p class="description">
Pickers are components built by the 
<span class="label label-attr">&lt;tags:pickerDialog&gt;</span> tag that allow for easy selection of items; either inline 
or in a popup.
</p>

<h2>Tag Attributes</h2>
<h4>Required</h4>
<tags:nameValueContainer>
    <tags:nameValue name="type">
    The type of picker, i.e. <strong>pointPicker</strong>, <strong>userPicker</strong>.
    </tags:nameValue>
    <tags:nameValue name="id">
    A unique identifier for this picker. This will be used as a global javascript variable
    and also used as an html element id so it must be javascript and html safe.
    </tags:nameValue>
</tags:nameValueContainer>
<h4>Optional</h4>
<tags:nameValueContainer tableClass="stacked-lg">
    <tags:nameValue name="destinationFieldId">
    Id of the input used to store the selected item id's.  The item id's are stored in the value attribute of the input. 
    This element is expected to exist and will not be created.  It should not be used if also using 
    <span class="label label-attr">destinationFieldName</span>.
    </tags:nameValue>
    <tags:nameValue name="destinationFieldName">
    Name of the input used to store the selected item id's.  The item id's are stored in the value attribute of the input. 
    This element is NOT expected to exist and will be created programatically.  It should not be used if also using 
    <span class="label label-attr">destinationFieldId</span>.
    </tags:nameValue>
    <tags:nameValue name="excludeIds">
    The ids that cannot be selected in the picker.  Should be a java object that will parse to a json array. 
    (like an ArrayList)
    </tags:nameValue>
    <tags:nameValue name="multiSelectMode">
    If <strong>true</strong> the picker will allow multiple selections to be made. Defaults to <strong>false</strong>.
    </tags:nameValue>
    <tags:nameValue name="immediateSelectMode">
    If <strong>true</strong> the picker will close the popup and fire the optional 
    <span class="label label-attr">endAction</span> function when an item is selected.  Setting this to
    <strong>true</strong> implies this is not a <span class="label label-attr">multiSelectMode</span> picker.
    Defaults to <strong>false</strong>.
    </tags:nameValue>
    <tags:nameValue name="endAction">
    Javascript function called when item selection is finalized either immediately when using 
    <span class="label label-attr">immediateSelectMode</span> or when the <span class="label label-attr">ok</span> 
    button is clicked. The function will be passed two arguments, the array of selected items as the first
    argument and the javascript picker object itself as the second. <span class="label label-warning">Warning</span>
    The end action function must exist before the picker is created.  
    Consider using <span class="label label-attr">endEvent</span> instead.
    </tags:nameValue>
    <tags:nameValue name="endEvent">
    The name of an event to fire when item selection is finalized either immediately when using 
    <span class="label label-attr">immediateSelectMode</span> or when the <span class="label label-attr">ok</span> 
    button is clicked. The event will also have the array of selected items and the javascript picker object itself 
    passed with.
    </tags:nameValue>
    <tags:nameValue name="cancelAction">
    Javascript function called when the <span class="label label-attr">cancel</span> button is clicked.
    <span class="label label-warning">Warning</span> The cancel action function must exist before the picker is created.
    </tags:nameValue>
    <tags:nameValue name="memoryGroup">
    A string used to name the search text used by a picker.  When used, pickers will open up with previous search text 
    populated (as long as no page refresh between). Can be shared between multiple pickers.
    </tags:nameValue>
    <tags:nameValue name="linkType">
    Type of component used to pop up the picker.  Can be <strong>normal</strong> (the default--a plain anchor tag link), 
    <strong>button</strong>, <strong>selection</strong> or <strong>none</strong>. <strong>selection</strong> pickers 
    are an icon and text listing the name of the first selected item and the # of the remaining.
    </tags:nameValue>
    <tags:nameValue name="nameKey">
    The i18n key.  Required if <span class="label label-attr">linkType</span> is <strong>button</strong>.  Only used for 
    <strong>button</strong> or <strong>selection</strong>.
    </tags:nameValue>
    <tags:nameValue name="styleClass">
    CSS class names applied to the picker's outer container.
    </tags:nameValue>
    <tags:nameValue name="anchorStyleClass">
    CSS class names applied to the picker's &lt;a&gt; element when <span class="label label-attr">linkType</span> is 
    <strong>normal</strong> or unspecified.
    </tags:nameValue>
    <tags:nameValue name="buttonStyleClass">
    CSS class names applied to the picker's button when <span class="label label-attr">linkType</span> is 
    <strong>button</strong> or <strong>selection</strong>.
    </tags:nameValue>
    <tags:nameValue name="buttonRenderMode">
    Only valid when <span class="label label-attr">linkType</span> is <strong>button</strong>; specifies the render 
    mode of the button. See the <a href="<cti:url value="/dev/styleguide/buttons#render-mode-example"/>">renderMode</a> 
    examples in the buttons style guide. 
    </tags:nameValue>
    <tags:nameValue name="icon">
    Icon used on the button when <span class="label label-attr">linkType</span> is <strong>button</strong>.
    </tags:nameValue>
    <tags:nameValue name="extraArgs">
    Addes another parameter named <strong>extraArgs</strong> to the xhr request when searching for items. Usually used to 
    include an energy company id.  It is javascript escaped internally so do not escape it yourself.
    </tags:nameValue>
    <tags:nameValue name="extraDestinationFields">
    Used when a selection has been finalized; a semicolon separated list of property to fieldId pairs
    <br>i.e. <span class="label label-attr">paoName:myPaoNameDiv;paoId:myPaoIdDiv</span>
    where property specifies a property of the data to use and fieldId specifies the id of an HTML element to update.
    If the element is an input then the value is update, otherwise the innerHTML is updated.  Available properties depend
    on the type of picker being used.
    </tags:nameValue>
    <tags:nameValue name="selectionProperty">
    Required for and only used for <span class="label label-attr">linkType</span> of <strong>selection</strong>. 
    The name of the property from selected items to display in the picker popup trigger component. i.e. 'paoName' 
    or 'paoId' for a pao picker.
    </tags:nameValue>
    <tags:nameValue name="allowEmptySelection">
    Allow an empty selection.  Only valid when <span class="label label-attr">multiSelectMode</span> is 
    <strong>true</strong>.
    </tags:nameValue>
    <tags:nameValue name="initialId">
    Id of an item to be pre-selected.
    </tags:nameValue>
    <tags:nameValue name="initialIds">
    Id of items to be pre-selected.
    </tags:nameValue>
    <tags:nameValue name="useInitialIdsIfEmpty">
    Clears selection if initial id(s) is empty.  Yes...the name confuses me too.
    </tags:nameValue>
    <tags:nameValue name="container">
    The id of the container element.  Used to load the picker element into a container on the page instead of a popup 
    triggered by a component on the page. This is referred to as an 'inline' picker.
    </tags:nameValue>
    <tags:nameValue name="viewOnlyMode">
    Only valid when <span class="label label-attr">linkType</span> is <strong>selection</strong>.  Causes picker 
    trigger component to be unclickable text.
    </tags:nameValue>
    <tags:nameValue name="buttonRenderMode">
    Passes the render mode to the cti:button tag.  Only usable 'linkType' is 'button'.  See cti:button 'renderMode' attribute.
    </tags:nameValue>
    <tags:nameValue name="includeRemoveButton">
    Adds a red x next to the picker to clear out the selected item
    </tags:nameValue>
    <tags:nameValue name="removeValue">
    Value to set the item to when cleared.  Default will be blank.  Used with includeRemoveButton.
    </tags:nameValue>
    <tags:nameValue name="maxNumSelections">
    An upper bound on the number of selections that can be made with the picker. If multiSelectMode is disabled, this option will automatically get disabled.
    </tags:nameValue>
</tags:nameValueContainer>

<form id="picker-select-form" action="<cti:url value="/dev/styleguide/pickers"/>" class="clearfix">
    <h1 class="dib">Examples</h1>
    <div class="dib fr">
        <label>Picker Type:
            <select name="pickerType" onchange="$('#picker-select-form').submit()">
                <option value="pointPicker"<c:if test="${pickerType == 'pointPicker'}"> selected</c:if>>Point Picker</option>
                <option value="paoPicker"<c:if test="${pickerType == 'paoPicker'}"> selected</c:if>>PAO Picker (Lucene)</option>
                <option value="assignedProgramPicker"<c:if test="${pickerType == 'assignedProgramPicker'}"> selected</c:if>>Assigned Program Picker (Database)</option>
                <option value="availableLoadProgramPicker"<c:if test="${pickerType == 'availableLoadProgramPicker'}"> selected</c:if>>Available Program Picker for Scenario (Database)</option>
                <option value="customerAccountPicker"<c:if test="${pickerType == 'customerAccountPicker'}"> selected</c:if>>Customer Account Picker (Lucene)</option>
                <option value="userPicker"<c:if test="${pickerType == 'userPicker'}"> selected</c:if>>User Picker (Lucene)</option>
                <option value="userGroupPicker"<c:if test="${pickerType == 'userGroupPicker'}"> selected</c:if>>User Group Picker (Lucene)</option>
                <option value="loginGroupPicker"<c:if test="${pickerType == 'loginGroupPicker'}"> selected</c:if>>Role Group Picker (Lucene)</option>
                <option value="notificationGroupPicker"<c:if test="${pickerType == 'notificationGroupPicker'}"> selected</c:if>>Notification Group Picker (Custom)</option>
                <option value="monitorPicker"<c:if test="${pickerType == 'monitorPicker'}"> selected</c:if>>Monitor Picker (Lucene)</option>
            </select>
        </label>
    </div>
</form>

<h2 id="simple-picker-example">Simple Picker</h2>

<p class="description">
    A simple picker with a link trigger component.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:pickerDialog id="p1" type="${pickerType}" destinationFieldId="p1-item">Pick Something</tags:pickerDialog>
        <input type="text" id="p1-item">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:pickerDialog id=&quot;p1&quot; type=&quot;${pickerType}&quot; destinationFieldId=&quot;p1-items&quot;&gt;Pick Something&lt;/tags:pickerDialog&gt;
&lt;input type=&quot;text&quot; id=&quot;p1-item&quot;&gt;
</pre>

<h2>Immediate Selection</h2>

<p class="description">
    A picker that selects the first item clicked immediately.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:pickerDialog id="p2" type="${pickerType}" destinationFieldId="p2-item"
            immediateSelectMode="true">Pick Something</tags:pickerDialog>
        <input type="text" id="p2-item">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:pickerDialog id=&quot;p2&quot; type=&quot;${pickerType}&quot; destinationFieldId=&quot;p2-item&quot;
    immediateSelectMode=&quot;true&quot;&gt;Pick Something&lt;/tags:pickerDialog&gt;
&lt;input type=&quot;text&quot; id=&quot;p2-item&quot;&gt;
</pre>

<h2>Multiple Selection</h2>

<p class="description">
    A picker that allows selection of multiple items.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:pickerDialog id="p3" type="${pickerType}" destinationFieldId="p3-items"
            multiSelectMode="true">Pick Something</tags:pickerDialog>
        <input type="text" id="p3-items">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:pickerDialog id=&quot;p3&quot; type=&quot;${pickerType}&quot; destinationFieldId=&quot;p3-items&quot;
    multiSelectMode=&quot;true&quot;&gt;Pick Something&lt;/tags:pickerDialog&gt;
&lt;input type=&quot;text&quot; id=&quot;p3-items&quot;&gt;
</pre>

<h2>End Action and Empty Selection</h2>

<p class="description">
    A picker that will fire a javascript function when a selection has been made; either immediately when 
    <span class="label label-attr">immediateSelectMode</span> is 'true' or when the 'OK' button is clicked.
    This picker also allows empty selection.  Note: the end action function must exist before the picker is
    created.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <script>
        function youPickedSomething(hits, picker) {
            if (hits && hits.length > 0) {
                var first = JSON.stringify(hits[0]);
                alert('You have selected ' + hits.length + ' item(s). \n The first one was:\n' + first);
            } else {
                alert('Nothing has been selected');
            }
        }
        </script>
        <tags:pickerDialog id="p4" type="${pickerType}" destinationFieldId="p4-items" endAction="youPickedSomething" 
            allowEmptySelection="true" multiSelectMode="true">Pick Something</tags:pickerDialog>
        <input type="text" id="p4-items">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;script&gt;
function youPickedSomething(hits, picker) {
    if (hits &amp;&amp; hits.length &gt; 0) {
        var first = JSON.stringify(hits[0]);
        alert('You have selected ' + hits.length + ' item(s). \n The first one was:\n' + first);
    } else {
        alert('Nothing has been selected');
    }
}
&lt;/script&gt;
&lt;tags:pickerDialog id=&quot;p4&quot; type=&quot;${pickerType}&quot; destinationFieldId=&quot;p4-items&quot; endAction=&quot;youPickedSomething&quot; 
    allowEmptySelection=&quot;true&quot; multiSelectMode=&quot;true&quot;&gt;Pick Something&lt;/tags:pickerDialog&gt;
&lt;input type=&quot;text&quot; id=&quot;p4-items&quot;&gt;
</pre>

<h2>Destination Field Name (using programatically created destination inputs)</h2>

<p class="description">
    A picker that creates the input(s) to store the selected item(s). A hidden input will be created for each item selected.  
    All the inputs will have the name set to the value of <span class="label label-attr">destinationFieldName</span>.
    Multiple inputs with the same name and different values is the correct way to submit a list of values in a request.
    The controller request mapping should expect a String[] argument named after <span class="label label-attr">destinationFieldName</span>.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:pickerDialog id="p5" type="${pickerType}" destinationFieldName="p5item" 
            multiSelectMode="true">Pick Something</tags:pickerDialog>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:pickerDialog id=&quot;p5&quot; type=&quot;${pickerType}&quot; destinationFieldName=&quot;p5item&quot; 
    multiSelectMode=&quot;true&quot;&gt;Pick Something&lt;/tags:pickerDialog&gt;
</pre>

<h2>Button Triggers</h2>

<p class="description">
    Pickers whose trigger components are buttons. Note: when using <span class="label label-attr">linkType</span>
    of <strong>button</strong>, <em>.label</em> will be appended to the <span class="label label-attr">nameKey</span> and
    <em>yukon.web.components.button.</em> is added to the scope for you. Therefore the <strong>choose</strong> nameKey in the 
    examples below are actually referencing the <em>yukon.web.components.button.choose.label</em> key.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:pickerDialog id="p6" type="${pickerType}" destinationFieldName="p6item" 
            linkType="button" nameKey="choose"/>
        <tags:pickerDialog id="p7" type="${pickerType}" destinationFieldName="p7item" 
            linkType="button" nameKey="choose" icon="icon-folder-edit"/>
        <tags:pickerDialog id="p8" type="${pickerType}" destinationFieldName="p8item" 
            linkType="button" nameKey="choose" buttonStyleClass="action primary"/>
        <tags:pickerDialog id="p9" type="${pickerType}" destinationFieldName="p9item" 
            linkType="button" nameKey="choose" buttonRenderMode="buttonImage" icon="icon-folder-edit"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:pickerDialog id=&quot;p6&quot; type=&quot;${pickerType}&quot; destinationFieldName=&quot;p6item&quot; 
    linkType=&quot;button&quot; nameKey=&quot;choose&quot;/&gt;
&lt;tags:pickerDialog id=&quot;p7&quot; type=&quot;${pickerType}&quot; destinationFieldName=&quot;p7item&quot; 
    linkType=&quot;button&quot; nameKey=&quot;choose&quot; icon=&quot;icon-folder-edit&quot;/&gt;
&lt;tags:pickerDialog id=&quot;p8&quot; type=&quot;${pickerType}&quot; destinationFieldName=&quot;p8item&quot; 
    linkType=&quot;button&quot; nameKey=&quot;choose&quot; buttonStyleClass=&quot;action primary&quot;/&gt;
&lt;tags:pickerDialog id=&quot;p9&quot; type=&quot;${pickerType}&quot; destinationFieldName=&quot;p9item&quot; 
    linkType=&quot;button&quot; nameKey=&quot;choose&quot; buttonRenderMode=&quot;buttonImage&quot; icon=&quot;icon-folder-edit&quot;/&gt;
</pre>

<h2>Memory Grouped Pickers</h2>

<p class="description">
    Pickers that remember the last search query.  Can be shared across multiple pickers.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:pickerDialog id="p10" type="${pickerType}" destinationFieldName="p10item" 
            linkType="button" nameKey="choose" memoryGroup="my-memory-group"/>
        <tags:pickerDialog id="p11" type="${pickerType}" destinationFieldName="p11item" 
            linkType="button" nameKey="choose" memoryGroup="my-memory-group"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:pickerDialog id=&quot;p10&quot; type=&quot;${pickerType}&quot; destinationFieldName=&quot;p10item&quot; 
    linkType=&quot;button&quot; nameKey=&quot;choose&quot; memoryGroup=&quot;my-memory-group&quot;/&gt;
&lt;tags:pickerDialog id=&quot;p11&quot; type=&quot;${pickerType}&quot; destinationFieldName=&quot;p11item&quot; 
    linkType=&quot;button&quot; nameKey=&quot;choose&quot; memoryGroup=&quot;my-memory-group&quot;/&gt;
</pre>

<h2>Selection Pickers</h2>

<p class="description">
    Pickers with special trigger components that list selected items.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:pickerDialog id="p12" type="${pickerType}" destinationFieldId="p12-item"
            linkType="selection" selectionProperty="${itemNameProperty}"/>
        <tags:pickerDialog id="p13" type="${pickerType}" destinationFieldId="p13-item"
            linkType="selection" selectionProperty="${itemNameProperty}"
            icon="icon-add" nameKey="add"/>
        <tags:pickerDialog id="p14" type="${pickerType}" destinationFieldId="p14-item"
            linkType="selection" selectionProperty="${itemIdProperty}" multiSelectMode="true"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:pickerDialog id=&quot;p12&quot; type=&quot;${pickerType}&quot; destinationFieldId=&quot;p12-item&quot;
    linkType=&quot;selection&quot; selectionProperty=&quot;${itemNameProperty}&quot;/&gt;
&lt;tags:pickerDialog id=&quot;p13&quot; type=&quot;${pickerType}&quot; destinationFieldId=&quot;p13-item&quot;
    linkType=&quot;selection&quot; selectionProperty=&quot;${itemNameProperty}&quot;
    icon=&quot;icon-add&quot; nameKey=&quot;choose&quot;/&gt;
&lt;tags:pickerDialog id=&quot;p14&quot; type=&quot;${pickerType}&quot; destinationFieldId=&quot;p14-item&quot;
    linkType=&quot;selection&quot; selectionProperty=&quot;${itemIdProperty}&quot; multiSelectMode=&quot;true&quot;/&gt;
</pre>

<h2>Initially Selected Items</h2>

<p class="description">
    Pickers that have item(s) preselected.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <cti:url var="url" value="/dev/styleguide/pickers"/>
        <form action="${url}">
            <c:if test="${!empty param.pickerType}">
              <input type="hidden" name="pickerType" value="${param.pickerType}">
            </c:if>
            <tags:pickerDialog id="p15" type="${pickerType}" linkType="selection" selectionProperty="${itemNameProperty}"
                destinationFieldName="p15items" allowEmptySelection="true" multiSelectMode="true"
                initialIds="${paramValues['p15items']}"/>
            <cti:button type="submit" label="Submit"/>
        </form>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;cti:url var=&quot;url&quot; value=&quot;/dev/styleguide/pickers&quot;/&gt;
&lt;form action=&quot;&#36;{url}&quot;&gt;
    &lt;c:if test=&quot;${!empty param.pickerType}&quot;&gt;
      &lt;input type=&quot;hidden&quot; name=&quot;pickerType&quot; value=&quot;${param.pickerType}&quot;&gt;
    &lt;/c:if&gt;
    &lt;tags:pickerDialog id=&quot;p15&quot; type=&quot;${pickerType}&quot; linkType=&quot;selection&quot; selectionProperty=&quot;${itemNameProperty}&quot;
        destinationFieldName=&quot;p15items&quot; allowEmptySelection=&quot;true&quot; multiSelectMode=&quot;true&quot;
        initialIds=&quot;&#36;{paramValues['p15items']}&quot;/&gt;
    &lt;cti:button type=&quot;submit&quot; label=&quot;Submit&quot;/&gt;
&lt;/form&gt;
</pre>

<h2>Inline pickers</h2>

<p class="description">
    Pickers that are rendered on the page instead of shown in a popup.  Simply using the 
    <span class="label label-attr">container</span> argument causes a picker to be inlined.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div id="inline-picker-container"></div>
        <script>
        $(function() { p16.show(); });
        
        function inlinePickerSelectionMade(selectedItems) {
            var itemIdProperty = '${itemIdProperty}';
            var itemNameProperty = '${itemNameProperty}';
            alert('You selected ' + selectedItems[0][itemNameProperty] + ' with an id of ' +
                    selectedItems[0][itemIdProperty] + '.');
        }
        </script>
        <tags:pickerDialog id="p16" type="${pickerType}" linkType="none" destinationFieldName="p16item"
            immediateSelectMode="true" endAction="inlinePickerSelectionMade"
            container="inline-picker-container"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div id=&quot;inline-picker-container&quot;&gt;&lt;/div&gt;
&lt;script&gt;
$(function() { p16.show(); });

function inlinePickerSelectionMade(selectedItems) {
    var itemIdProperty = '${itemIdProperty}';
    var itemNameProperty = '${itemNameProperty}';
    alert('You selected ' + selectedItems[0][itemNameProperty] + ' with an id of ' +
            selectedItems[0][itemIdProperty] + '.');
}
&lt;/script&gt;
&lt;tags:pickerDialog id=&quot;p16&quot; type=&quot;${pickerType}&quot; linkType=&quot;none&quot; destinationFieldName=&quot;p16item&quot;
    immediateSelectMode=&quot;true&quot; endAction=&quot;inlinePickerSelectionMade&quot;
    container=&quot;inline-picker-container&quot;/&gt;
</pre>

<h1>Picker Javascript API</h1>
<h2>Showing Pickers Programmatically</h2>

<p class="description">
    Pickers can be shown via the javascript api.  Usefully when <span class="label label-attr">linkType</span> is 
    <strong>none</strong>. 
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <script>
        $(function() {
            $('#p17-trigger').click(function (e) {
                p17.show(); 
            });
        });
        </script>
        <tags:pickerDialog id="p17" type="${pickerType}" linkType="none" destinationFieldName="p17item"/>
        <cti:button id="p17-trigger" label="Show My Hidden Picker"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;script&gt;
$(function() {
    $('#p17-trigger').click(function (e) {
        p17.show(); 
    });
});
&lt;/script&gt;
&lt;tags:pickerDialog id=&quot;p17&quot; type=&quot;${pickerType}&quot; linkType=&quot;none&quot; destinationFieldName=&quot;p17item&quot;/&gt;
&lt;cti:button id=&quot;p17-trigger&quot; label=&quot;Show My Hidden Picker&quot;/&gt;
</pre>

<h2>Maximum Number of Selections</h2>

<p class="description">
    A picker that limits how many selections can be made.
</p>
<div class="column-4-20 clearfix picker-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:pickerDialog id="p18" type="${pickerType}" destinationFieldId="p18-items"
            multiSelectMode="true" maxNumSelections="3">Pick Something</tags:pickerDialog>
        <input type="text" id="p18-items">
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:pickerDialog id=&quot;p18&quot; type=&quot;${pickerType}&quot; destinationFieldId=&quot;p18-items&quot;
    multiSelectMode=&quot;true&quot;maxNumSelections=&quot;3&quot;&gt;Pick Something&lt;/tags:pickerDialog&gt;
&lt;input type=&quot;text&quot; id=&quot;p18-items&quot;&gt;
</pre>

</tags:styleguide>
</cti:standardPage>