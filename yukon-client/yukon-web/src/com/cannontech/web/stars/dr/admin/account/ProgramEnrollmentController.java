package com.cannontech.web.stars.dr.admin.account;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/account/programEnrollment/*")
public class ProgramEnrollmentController {

    // temporary class to hold mock-up data
    public static class ProgramEnrollment {
        public static class Hardware {
            public String serialNo;
            public String relay;

            public Hardware(String serialNo, String relay) {
                this.serialNo = serialNo;
                this.relay = relay;
            }
        }

        private PaoIdentifier paoIdentifier;
        private String name;
        private String applianceCategory;
        private String group;
        private List<Hardware> hardware;
        private String status;

        public ProgramEnrollment(int paoId, String name, String applianceCategory,
                String group, List<Hardware> hardware, String status) {
            this.paoIdentifier = new PaoIdentifier(paoId, PaoType.LM_DIRECT_PROGRAM);
            this.name = name;
            this.applianceCategory = applianceCategory;
            this.group = group;
            this.hardware = hardware;
            this.status = status;
        }

        public PaoIdentifier getPaoIdentifier() {
            return paoIdentifier;
        }

        public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
            this.paoIdentifier = paoIdentifier;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getApplianceCategory() {
            return applianceCategory;
        }

        public void setApplianceCategory(String applianceCategory) {
            this.applianceCategory = applianceCategory;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public List<Hardware> getHardware() {
            return hardware;
        }

        public void setHardware(List<Hardware> hardware) {
            this.hardware = hardware;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @RequestMapping
    public String edit(ModelMap model) {
        List<ProgramEnrollment> programEnrollmentList = Lists.newArrayList();
        List<ProgramEnrollment.Hardware> hardware = Lists.newArrayList();

        hardware.clear();
        hardware.add(new ProgramEnrollment.Hardware("777000002", "none"));
        programEnrollmentList.add(new ProgramEnrollment(1, "AC", "Air Conditioner", "ALL RES AC RELAY 3", hardware, "In Service"));

        hardware.clear();
        hardware.add(new ProgramEnrollment.Hardware("777000001", "3"));
        hardware.add(new ProgramEnrollment.Hardware("777000003", "none"));
        programEnrollmentList.add(new ProgramEnrollment(2, "WH", "Water Heater","ALL RES WH", hardware, "In Service"));

        SearchResult<ProgramEnrollment> programEnrollments = new SearchResult<ProgramEnrollment>();
        programEnrollments.setResultList(programEnrollmentList);
        programEnrollments.setBounds(0, 10, 2);
        model.addAttribute("programEnrollments", programEnrollments);

        return "account/programEnrollment/edit.jsp";
    }

    @RequestMapping
    public String editEnrollment(ModelMap model) {
        return "account/programEnrollment/editEnrollment.jsp";
    }
}
