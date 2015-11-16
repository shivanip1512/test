<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="deviceCollections">
<tags:styleguide page="device-collections">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    There are a couple UI components that build or display device collections.  
    <span class="label label-attr">&lt;tags:deviceCollectionPicker&gt;</span> can build a collection through a popup. 
    <span class="label label-attr">&lt;tags:selectedDevicesPopup&gt;</span> creates and icon or button component that shows
    a popup containing the devices in the collection.  <span class="label label-attr">&lt;tags:selectedDevices&gt;</span>
    uses <span class="label label-attr">&lt;tags:selectedDevicesPopup&gt;</span> but also adds a label, description, and
    count next to the popup trigger.
</p>

<h1>Device Collection Picker Tag</h1>

<h2>Tag Attributes</h2>
<tags:nameValueContainer tableClass="description stacked">
    <tags:nameValue name="key">
        I18n object to use for the alert message. Note: key can be an i18n key String, 
        <span class="label label-attr">MessageSourceResolvable</span>, 
        <span class="label label-attr">Displayable</span>, <span class="label label-attr">DisplayableEnum</span>, 
        or <span class="label label-attr">ResolvableTemplate</span>.
    </tags:nameValue>
    <tags:nameValue name="title">
        I18n object to use for the popup title. Note: title can be an i18n key String, 
        <span class="label label-attr">MessageSourceResolvable</span>, 
        <span class="label label-attr">Displayable</span>, <span class="label label-attr">DisplayableEnum</span>, 
        or <span class="label label-attr">ResolvableTemplate</span>.<br>
        Default: <em>yukon.common.device.selection.title</em> - 'Select By:'
    </tags:nameValue>
    <tags:nameValue name="event">
        Name of event to fire after successfully selecting devices.
    </tags:nameValue>
    <tags:nameValue name="submit">
        If <strong>true</strong>, the closest parent form is submitted after successfully selecting devices.<br>
        Default: <strong>false</strong>.
    </tags:nameValue>
    <tags:nameValue name="multi">
        If <strong>true</strong>, selection mode for device group will be multiselection. Default: <strong>true</strong>.
    </tags:nameValue>
    <tags:nameValue name="predicates">
        Optional comma separated strings of <span class="label label-attr">DeviceGroupPredicateEnum</span> entry names 
        to filter the tree data by.<br>
        Default: <strong>NON_HIDDEN</strong>.
    </tags:nameValue>
    <tags:nameValue name="callbacks">
        Set of callbacks to be preformed on each node in the device group tree. Can be used to disable selecting 
        certain nodes.
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib stacked">Examples</h1>

<p class="description">
    A simple device collection picker using defaults.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:deviceCollectionPicker >Choose Devices</tags:deviceCollectionPicker>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:deviceCollectionPicker&gt;Choose Devices&lt;/tags:deviceCollectionPicker&gt;
</pre>

<h1>Selected Devices Popup Tag</h1>

<h2>Tag Attributes</h2>
<tags:nameValueContainer tableClass="description stacked">
    <tags:nameValue name="deviceCollection">
        The device collection: any implementation of the <span class="label label-attr">DeviceCollection</span> interface.
        Either <span class="label label-attr">deviceCollection</span> or <span class="label label-attr">groupName</span> 
        are required and only one should be used. 
    </tags:nameValue>
    <tags:nameValue name="groupName">
        The device group name, used when referencing a device group based collection. 
        Either <span class="label label-attr">deviceCollection</span> or <span class="label label-attr">groupName</span> 
        are required and only one should be used.
    </tags:nameValue>
    <tags:nameValue name="type">
        The type of popup trigger component: <em>link</em> or <em>button</em>.  Default is <em>link</em>.
    </tags:nameValue>
    <tags:nameValue name="icon">
        The icon used for popup trigger component.  Default is <em>icon-magnifier</em>: 
        <cti:icon icon="icon-magnifier" classes="fn"/>
    </tags:nameValue>
</tags:nameValueContainer>

<h1 class="dib stacked">Examples</h1>

<p class="description">Simple usages of selected devices popups.</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:selectedDevicesPopup deviceCollection="${dc}"/>
        <tags:selectedDevicesPopup groupName="/Meters" type="button"/>
        <tags:selectedDevicesPopup groupName="/Meters" type="button" icon="icon-database-add"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:selectedDevicesPopup deviceCollection=&quot;&#36;{dc}&quot;/&gt;
&lt;tags:selectedDevicesPopup groupName=&quot;/Meters&quot; type=&quot;button&quot;/&gt;
&lt;tags:selectedDevicesPopup groupName=&quot;/Meters&quot; type=&quot;button&quot; icon=&quot;icon-database-add&quot;/&gt;
</pre>

<h1>Selected Devices Tag</h1>

<h2>Tag Attributes</h2>
<tags:nameValueContainer tableClass="description">
    <tr><td colspan="2"><h4>Required</h4></td></tr>
    <tags:nameValue name="deviceCollection">
        The device collection: any implementation of the <span class="label label-attr">DeviceCollection</span> interface.
        Either <span class="label label-attr">deviceCollection</span> or <span class="label label-attr">groupName</span> 
        are required and only one should be used. 
    </tags:nameValue>
    <tr><td colspan="2"><h4>Optional</h4></td></tr>
    <tags:nameValue name="id">
        The html <span class="label label-attr">id</span> attribute of the div container around the component.
    </tags:nameValue>
</tags:nameValueContainer>

<br><h1 class="dib buffered">Examples</h1><br>

<p class="description">Simple usages of selected devices popups.</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <tags:selectedDevices deviceCollection="${dc}"/>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;tags:selectedDevices deviceCollection=&quot;&#36;{dc}&quot;/&gt;
</pre>

</tags:styleguide>
</cti:standardPage>