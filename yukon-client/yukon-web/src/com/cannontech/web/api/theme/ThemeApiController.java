package com.cannontech.web.api.theme;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.web.admin.theme.dao.ThemeDao;
import com.cannontech.web.admin.theme.model.Theme;


@RestController
@RequestMapping("/theme")
public class ThemeApiController {
    
    @Autowired private ThemeDao themeDao;
    @Autowired private YukonImageDao imageDao;

    @GetMapping
    public ResponseEntity<Object> getTheme() {
        Theme currentTheme = themeDao.getCurrentTheme();
        return new ResponseEntity<>(currentTheme, HttpStatus.OK);
    }
    
    @GetMapping(value="/image/{id}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> getImage(@PathVariable int id) {
        LiteYukonImage fullImage = imageDao.getLiteYukonImage(id);
        final String encodedImage = Base64.getEncoder().encodeToString(fullImage.getImageValue());
        return new ResponseEntity<Object>(encodedImage, HttpStatus.OK);
    }

}
