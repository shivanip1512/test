var ctiContentMonitor = false;
function setContentChanged(changed) {
	ctiContentMonitor = changed;
}
function isContentChanged() {
	return ctiContentMonitor;
}
function warnUnsavedChanges() {
	return !isContentChanged() || confirm("Do you want to leave the page and discard all the changes you made?");
}
