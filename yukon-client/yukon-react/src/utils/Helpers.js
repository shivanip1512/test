export const checkForApiValidationError = (fieldName, apiValidationErrors) => {
    if (apiValidationErrors != null) {
        const fieldError = apiValidationErrors.filter((error) => {
            return error.field === fieldName
        });
        if (fieldError.length > 0) {
            return true;
        }
    }
    return false;
};

export const displayApiValidationError = (fieldName, apiValidationErrors) => {
    if (apiValidationErrors != null) {
        const fieldErrors = apiValidationErrors.filter((error) => {
            return error.field === fieldName
        });
        if (fieldErrors[0] != null) {
            return fieldErrors[0].detail;
        }
    }
};
