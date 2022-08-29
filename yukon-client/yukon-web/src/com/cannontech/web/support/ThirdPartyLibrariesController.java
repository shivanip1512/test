package com.cannontech.web.support;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.util.YamlParserUtils;
import com.cannontech.system.ThirdPartyLibraries;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/thirdParty/*")
public class ThirdPartyLibrariesController {

    @GetMapping("view")
    public String libraries(ModelMap model) throws IOException {
        ClassPathResource libraryYaml = new ClassPathResource("thirdPartyLibraries.yaml");
        ThirdPartyLibraries documentedLibraries = YamlParserUtils.parseToObject(libraryYaml.getInputStream(),
                ThirdPartyLibraries.class, libraryYaml.getFilename());
        model.addAttribute("libraries", documentedLibraries.getAllProjects());
        return "thirdPartyLibraries.jsp";
    }

}
