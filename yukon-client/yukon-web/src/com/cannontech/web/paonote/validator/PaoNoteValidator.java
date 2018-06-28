package com.cannontech.web.paonote.validator;

import static com.cannontech.common.pao.notes.service.PaoNotesService.MAX_CHARACTERS_IN_NOTE;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class PaoNoteValidator extends SimpleValidator<PaoNote> {

    public PaoNoteValidator() {
        super(PaoNote.class);
    }

    @Override
    protected void doValidation(PaoNote paoNote, Errors errors) {
        YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, "noteText", paoNote.getNoteText(), false,
            MAX_CHARACTERS_IN_NOTE);
    }

}
