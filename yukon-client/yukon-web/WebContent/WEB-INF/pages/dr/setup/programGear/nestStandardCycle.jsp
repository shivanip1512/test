<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".preparationLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                            <tags:selectWithItems items="${preparationLoadShaping}" path="fields.prep" />
                        </cti:displayForPageEditModes>
                    </tags:nameValue2> 

                    <tags:nameValue2 nameKey=".peakLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                            <tags:selectWithItems items="${peakLoadShaping}" path="fields.peak" />
                        </cti:displayForPageEditModes>
                    </tags:nameValue2> 

                    <tags:nameValue2 nameKey=".postPeakLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                            <tags:selectWithItems items="${postPeakLoadShaping}" path="fields.post" />
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>  
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>
