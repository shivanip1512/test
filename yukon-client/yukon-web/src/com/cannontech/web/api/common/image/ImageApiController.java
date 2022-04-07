package com.cannontech.web.api.common.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.Validator;

@RestController
public class ImageApiController {

    @Autowired private YukonImageDao imageDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @GetMapping("/common/images/{id}")
    public ResponseEntity<Object> imageFromId(@PathVariable("id") Integer id, YukonUserContext context) {

        LiteYukonImage liteYukonImage = imageDao.getLiteYukonImage(id);

        if (Validator.isNull(liteYukonImage)) {
            final MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
            final String imageNotFoundMsg = "yukon.web.image.error.IMAGE_NOT_FOUND";
            throw new NotFoundException(messageSourceAccessor.getMessage(imageNotFoundMsg));
        }

        byte[] byteImageValue = liteYukonImage.getImageValue();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(byteImageValue, headers, HttpStatus.OK);
    }
}
