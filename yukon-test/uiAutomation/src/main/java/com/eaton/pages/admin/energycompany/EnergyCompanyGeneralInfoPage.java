package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyGeneralInfoPage extends PageBase {
    
    private Section ecInfoSection;
    private Button edit;
    
    private Section operatorGroupSection;
    private Button addOperatorGroup;
    
    private Section memberEcSection;
    private Button addMember;
    private Button createMember;
    
    private Section residentialCustGrpSection;
    private Button addResidentialCustGroup;

    public EnergyCompanyGeneralInfoPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_GENERAL_INFO + id;
        
        ecInfoSection = new Section(this.driverExt, "Energy Company Info");
        edit = new Button(this.driverExt, "Edit", ecInfoSection.getSection());
        
        operatorGroupSection = new Section(this.driverExt, "Operator Groups");
        addOperatorGroup = new Button(this.driverExt, "Add", operatorGroupSection.getSection());
        
        memberEcSection = new Section(this.driverExt, "Member Energy Companies");
        addMember = new Button(this.driverExt, "Add", memberEcSection.getSection());
        createMember = new Button(this.driverExt, "Create", memberEcSection.getSection());
        
        residentialCustGrpSection = new Section(this.driverExt, "Residential Customer Groups");
        addResidentialCustGroup = new Button(this.driverExt, "Add", residentialCustGrpSection.getSection());
    } 
    
    public EnergyCompanyGeneralInfoPage(DriverExtensions driverExt) {
        super(driverExt);
        
        ecInfoSection = new Section(this.driverExt, "Energy Company Info");
        edit = new Button(this.driverExt, "Edit", ecInfoSection.getSection());
        
        operatorGroupSection = new Section(this.driverExt, "Operator Groups");
        addOperatorGroup = new Button(this.driverExt, "Add", operatorGroupSection.getSection());
        
        memberEcSection = new Section(this.driverExt, "Member Energy Companies");
        addMember = new Button(this.driverExt, "Add", memberEcSection.getSection());
        createMember = new Button(this.driverExt, "Create", memberEcSection.getSection());
        
        residentialCustGrpSection = new Section(this.driverExt, "Residential Customer Groups");
        addResidentialCustGroup = new Button(this.driverExt, "Add", residentialCustGrpSection.getSection());
    }
    
    public Button getEditBtn() {
        return edit;
    }
    
    public Button getAddOperatorGroupBtn() {
        return addOperatorGroup;
    }
    
    public Button getAddMemberBtn() {
        return addMember;
    }
    
    public Button getCreateMember() {
        return createMember;
    }
    
    public Button getAddResidentialCustGroup() {
        return addResidentialCustGroup;
    }
}