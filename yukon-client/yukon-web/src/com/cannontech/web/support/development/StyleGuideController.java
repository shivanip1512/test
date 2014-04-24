package com.cannontech.web.support.development;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.cannontech.web.taglib.MessageScopeHelper;

@Controller
@RequestMapping("/development/styleguide/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class StyleGuideController {
    
    @Autowired private ObjectFormattingService objectFormattingService;

    public enum Colors implements DisplayableEnum {
        NONE,
        RED,
        ORANGE,
        YELLOW,
        BLUE,
        GREEN,
        INDIGO,
        VIOLET,
        OTHER,
        ;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dev.i18nDemo.colors." + name();
        }
    }

    public static class ScopePeeker {
        private HttpServletRequest request;

        private ScopePeeker(HttpServletRequest request) {
            this.request = request;
        }

        public String getScope() {
            return MessageScopeHelper.forRequest(request).toString();
        }
    }

    private void setupModel(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        
        model.addAttribute("scopePeeker", new ScopePeeker(request));

        model.addAttribute("numberNegativeFive", -5);
        model.addAttribute("numberZero", 0);
        model.addAttribute("numberOne", 1);
        model.addAttribute("numberTwo", 2);
        model.addAttribute("numberFive", 5);
        model.addAttribute("numberOneThird", 1.0 / 3.0);
        model.addAttribute("numberOneHalf", 1.0 / 2.0);
        model.addAttribute("numberTwoThirds", 2.0 / 3.0);
        model.addAttribute("numberFiveNinths", 5.0 / 9.0);
        model.addAttribute("largeDecimal", 9523155.235);
        model.addAttribute("funArguments", new Object[] {7, "Cool Beans", 13, 54.231555});
        Instant now = Instant.now();
        Instant past = new Instant(now.minus(Duration.standardDays(5)).getMillis());
        Instant start = new Instant(now.minus(Duration.standardDays(7)).getMillis());
        Instant end = new Instant(now.plus(Duration.standardDays(7)).getMillis());
        model.addAttribute("now", now);
        model.addAttribute("past", past);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        List<BuiltInAttribute> atts =
            objectFormattingService.sortDisplayableValues(BuiltInAttribute.values(), null, null,
                                                          userContext);
        model.addAttribute("builtInAttributes", atts);
        List<Colors> colors =
            objectFormattingService.sortDisplayableValues(Colors.values(), Colors.NONE, Colors.OTHER,
                                                          userContext);
        model.addAttribute("colors", colors);
        model.addAttribute("thisNotThat", "This<br>Not That");
    }
    
    @RequestMapping("main")
    public void main(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
    }
    
    @RequestMapping("grid")
    public String grid() {
        return "development/styleguide/grid.jsp";
    }
    
    @RequestMapping("containers")
    public String containers(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "development/styleguide/containers.jsp";
    }
    
    @RequestMapping("buttons")
    public String buttons(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "development/styleguide/buttons.jsp";
    }
    
    @RequestMapping("pickers")
    public String pickers(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "development/styleguide/pickers.jsp";
    }
    
    @RequestMapping("i18nScopes")
    public String i18nScopes(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "development/styleguide/i18n_scopes.jsp";
    }
    
    @RequestMapping("i18n")
    public String i18n(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "development/styleguide/i18n.jsp";
    }
    
    @RequestMapping("dialogs")
    public String dialogs(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "development/styleguide/dialogs.jsp";
    }
    
    @RequestMapping("jsTesting")
    public String jsTesting(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "development/styleguide/jsTesting.jsp";
    }
    
    @RequestMapping("sprites")
    public String sprites(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        try {
            setupSprites(model, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "development/styleguide/sprites.jsp";
    }
    
    @RequestMapping("more")
    public String more(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        setupModel(model, request, userContext);
        return "development/styleguide/more.jsp";
    }
    
    private void setupSprites(ModelMap model, HttpServletRequest request) throws IOException {
        ArrayList<String> sprites16Array = new ArrayList<String>();
        ArrayList<String> sprites32Array = new ArrayList<String>();
        
        BufferedReader br = new BufferedReader(new FileReader(request.getServletContext().getRealPath("/WebConfig/yukon/styles/icons.css")));
        try {
            String line = br.readLine();
            while (line != null) {
                if (line.contains("icon-32-")) {
                    int endIndex = line.indexOf("{");
                    if (endIndex != -1) {
                        sprites32Array.add(line.substring(1, endIndex));
                    }
                } else if (line.contains("icon-") && !line.contains("icon-32")) {
                    int endIndex = line.indexOf("{");
                    if (endIndex != -1) {
                        sprites16Array.add(line.substring(1, endIndex));
                    }
                }
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        model.addAttribute("sprites16Array", sprites16Array);
        model.addAttribute("sprites32Array", sprites32Array);
    }
}
