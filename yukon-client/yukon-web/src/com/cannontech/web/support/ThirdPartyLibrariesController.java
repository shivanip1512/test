package com.cannontech.web.support;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.system.ThirdPartyCppLibrary;
import com.cannontech.system.ThirdPartyIconLibrary;
import com.cannontech.system.ThirdPartyJavaLibrary;
import com.cannontech.system.ThirdPartyJavaScriptLibrary;
import com.cannontech.system.ThirdPartyLibraries;
import com.cannontech.system.ThirdPartyLibraryParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/thirdParty/*")
public class ThirdPartyLibrariesController {

    @GetMapping("view")
    public String libraries(ModelMap model) throws IOException {
        ClassPathResource libraryYaml = new ClassPathResource("thirdPartyLibraries.yaml");
        ThirdPartyLibraries documentedLibraries = ThirdPartyLibraryParser.parse(libraryYaml.getInputStream());
        List<ThirdPartyCppLibrary> cpp = documentedLibraries.cppLibraries
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ThirdPartyCppLibrary:: getProject))), ArrayList::new));
        documentedLibraries.setCppLibraries(cpp);
        List<ThirdPartyJavaLibrary> java = documentedLibraries.javaLibraries
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ThirdPartyJavaLibrary:: getProject))), ArrayList::new));
        documentedLibraries.setJavaLibraries(java);
        List<ThirdPartyJavaScriptLibrary> js = documentedLibraries.jsLibraries
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ThirdPartyJavaScriptLibrary:: getProject))), ArrayList::new));
        documentedLibraries.setJsLibraries(js);
        List<ThirdPartyIconLibrary> icons = documentedLibraries.iconLibraries
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ThirdPartyIconLibrary:: getProject))), ArrayList::new));
        documentedLibraries.setIconLibraries(icons);
        model.addAttribute("libraries", documentedLibraries);
        return "thirdPartyLibraries.jsp";
    }

}
