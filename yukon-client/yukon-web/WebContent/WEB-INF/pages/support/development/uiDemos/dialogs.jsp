<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug"%>

<cti:msgScope paths="modules.dev.uiDemos">

<script type="text/javascript">
jQuery(document).ready(function() {
    jQuery('#confirmDeleteDlg').bind('yukonConfirmDialogOK', function() {
        jQuery('#confirmDeleteDlg').dialog('close');
        alert('You seem to be certain you want to delete it.');
    });

    jQuery('#inlineDialog3').bind('yukonOkPressed', function() {
        jQuery('#inlineDialog3').dialog('close');
        alert('You pressed OK.');
    });
});
</script>

<div id="ajaxDialog"></div>

<d:inline nameKey="inlineDialog1" okEvent="none" on="#sampleInlineBtn1">
    <p>I'm an example of an in-line dialog box "none" for the okEvent.<p>
    <p>Because of this, I have no OK button.</p>
</d:inline>

<d:inline id="inlineDialog2" nameKey="inlineDialog2" okEvent="yukonDialogSubmit" on="#sampleInlineBtn2">
    <form action="http://en.wikipedia.org/w/index.php" method="get">
        <p>I'm an example of an in-line dialog box with a "yukonDialogSubmit" okEvent.<p>
        <p>Because of this, you will submit a form when you press "OK".</p>
        <input type="hidden" name="title" value="Special:Search">
        <p>Search For: <input type="text" name="search"></p>
    </form>
</d:inline>

<d:inline id="inlineDialog3" nameKey="inlineDialog3" okEvent="yukonOkPressed" on="#sampleInlineBtn3">
    <p>I'm an example of an in-line dialog box with "yukonOkPressed" okEvent.<p>
    <p>Because of this, the dialog will trigger the "yukonOkPressed" custom event on the dialog
    div when you press OK.</p>
</d:inline>

<d:inline id="inlineDialog4" nameKey="inlineDialog4" okEvent="none" on="#sampleInlineBtn4"
    options="{'modal' : false}">
    <p>I'm an example of an in-line dialog box with options.  Specifically, my options are
    set to "{'modal' : false}" to turn of modality.<p>
</d:inline>

<cti:msg2 var="button1" key=".inlineDialog5.button1"/>
<cti:msg2 var="button2" key=".inlineDialog5.button2"/>
<cti:msg2 var="cancelText" key="yukon.web.components.button.cancel.label"/>
<d:inline id="inlineDialog5" okEvent="none" nameKey="inlineDialog5" on="#sampleInlineBtn5"
                options="{width: 550, 'buttons': [{text: '${button1}', click: function() { alert('Sup, Im button 1!'); }, 'class': 'inline_dialog_button_1' },
                                                  {text: '${button2}', click: function() { alert('Sup, Im button 2!'); } },
                                                  {text: '${cancelText}', click: function() { jQuery(this).dialog('close'); } }]}">
	<p>Options used in this dialog:</p>
	<ul>
		<li>-width</li>
		<li>-buttons (button 1 has a specified class on it)</li>
	</ul>
</d:inline>

<p class="pageDescription">
This page shows some examples of how to use AJAX dialogs in Yukon.
</p>

<tags:sectionContainer title="Confirmation Dialogs">
    <h3>Custom OK Handling</h3>
    <d:confirm on="#deleteBtn" nameKey="confirmDelete" argument="Thing to Delete"/>
    <cti:button id="deleteBtn" nameKey="delete"/>

    <h3>OK Goes to a Specified Link</h3>
    <d:confirm on="#gotoHome" nameKey="confirmGotoHome"/>
    <a href="/home" id="gotoHome">Home</a>

    <h3>OK Submits a Form</h3>
    <form action="http://en.wikipedia.org/w/index.php" method="get" id="wikipediaForm">
        <input type="hidden" name="title" value="Special:Search">
        <input type="text" name="search">
        <d:confirm on="#searchWikipedia" nameKey="searchWikipedia"/>
        <cti:button id="searchWikipedia" type="submit" label="Search Wikipedia"/>
    </form>

    <h3>OK Submits an Other Form</h3>
    <d:confirm on="#searchWikipedia2" nameKey="searchWikipedia" />
    <cti:button id="searchWikipedia2" data-form="#wikipediaForm" label="Search Wikipedia"/>
</tags:sectionContainer>

<tags:sectionContainer title="In-line Dialogs">
  <div class="clearfix">
  <cti:button id="sampleInlineBtn1" nameKey="inline1"/>
  <cti:button id="sampleInlineBtn2" nameKey="inline2"/>
  <cti:button id="sampleInlineBtn3" nameKey="inline3"/>
  <cti:button id="sampleInlineBtn4" nameKey="inline4"/>
  <cti:button id="sampleInlineBtn5" nameKey="inline5"/>
  </div>
</tags:sectionContainer>

</cti:msgScope>