package com.cannontech.web.stars.dr.consumer;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableEnrollment;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;

@CheckRole(ResidentialCustomerRole.ROLEID)
@CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT)
@Controller
public class EnrollmentDescriptionController extends AbstractConsumerController {
	private DisplayableEnrollmentDao displayableEnrollmentDao;

	@RequestMapping(value = "/consumer/enrollment/details", method = RequestMethod.GET)
	public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount, 
			Integer programId, Integer categoryId, YukonUserContext yukonUserContext, ModelMap map) {

		List<DisplayableEnrollment> enrollments = displayableEnrollmentDao
				.getDisplayableEnrollments(customerAccount, yukonUserContext);
		
		// Get enrollment from customer enrollments for the category id
		DisplayableEnrollment currentEnrollment = null;
		for(DisplayableEnrollment enrollment : enrollments) {
			Integer currentCategoryId = enrollment.getApplianceCategory().getApplianceCategoryId();
			if(currentCategoryId.equals(categoryId)) {
				currentEnrollment = enrollment;
				break;
			}
		}
		
		// Get the program from the enrollment for the program id
		DisplayableEnrollmentProgram currentProgram = null;
		for(DisplayableEnrollmentProgram program : currentEnrollment.getEnrollmentPrograms()) {
			Integer currentProgramId = program.getProgram().getProgramId();
			if(currentProgramId.equals(programId)) {
				currentProgram = program;
				break;
			}
		}
		
		// The jsp will either include the url if it exists or display the descriptions
		// for each program in the category.
		String descriptionUrl = currentProgram.getDescriptionUrl();
		if(!StringUtils.isBlank(descriptionUrl)) {
			map.addAttribute("hasUrl", true);
			map.addAttribute("descriptionUrl", descriptionUrl);
		} else {
			map.addAttribute("hasUrl", false);
			map.addAttribute("applianceType", currentEnrollment.getApplianceType());
			map.addAttribute("programs", currentEnrollment.getEnrollmentPrograms());
		}
		
		return "consumer/enrollment/programDetails.jsp";
	}

	@Autowired
	public void setDisplayableEnrollmentDao(
			DisplayableEnrollmentDao displayableEnrollmentDao) {
		this.displayableEnrollmentDao = displayableEnrollmentDao;
	}

}
