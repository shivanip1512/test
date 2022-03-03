package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.util.JsonUtils;

public class GetYukonColorPaletteJson extends YukonTagSupport {

    @Override
    public void doTag() throws JspException, IOException {

        String colorsJson = JsonUtils.toJson(Arrays.stream(YukonColorPalette.values())
                .collect(Collectors.toMap(e -> e, YukonColorPalette::getHexValue)));
        JspWriter out = getJspContext().getOut();
        out.print(colorsJson);

    }
}