package com.cannontech.rest.api.documentation;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public abstract class DocumentationFields {

    static public class Fields {
        /** Request fields definition */
        List<FieldDescriptor> requestFields;
        /** Response fields definition */
        List<FieldDescriptor> responseFields;
        /** pathParameter to validate in response */
        String responseFieldPath;
        /** field description for responseFieldPath */
        String responseFieldDesc;
        /** the object of the request */
        Object body;
        /** the URL to call */
        String url;

        /**
         * @param requestFields     - Request fields definition
         * @param responseFields    - Response fields definition
         * @param responseFieldPath - PathParameter to validate in response
         * @param responseFieldDesc - Field description for responseFieldPath
         * @param body              - The object of the request
         * @param url               - The URL to call
         */
        public Fields(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields,
                String responseFieldPath, String responseFieldDesc, Object body, String url) {
            super();
            this.requestFields = requestFields;
            this.responseFields = responseFields;
            this.responseFieldPath = responseFieldPath;
            this.responseFieldDesc = responseFieldDesc;
            this.body = body;
            this.url = url;
        }
    }

    /**
     * Helper class for Create
     */
    static public class Create extends Fields {
        /**
         * @param requestFields     - Request fields definition
         * @param responseFields    - Response fields definition
         * @param responseFieldPath - PathParameter to validate in response
         * @param responseFieldDesc - Field description for responseFieldPath
         * @param body              - The object of the request
         * @param url               - The URL to call
         */
        public Create(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, String responseFieldPath,
                String responseFieldDesc, Object body, String url) {
            super(requestFields, responseFields, responseFieldPath, responseFieldDesc, body, url);
        }
    }

    /**
     * Helper class for Update
     */
    static public class Update extends Fields {
        /**
         * @param requestFields     - Request fields definition
         * @param responseFields    - Response fields definition
         * @param responseFieldPath - PathParameter to validate in response
         * @param responseFieldDesc - Field description for responseFieldPath
         * @param body              - The object of the request
         * @param url               - The URL to call
         */
        public Update(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, String responseFieldPath,
                String responseFieldDesc, Object body, String url) {
            super(requestFields, responseFields, responseFieldPath, responseFieldDesc, body, url);
        }
    }

    /**
     * Helper class for Copy
     */
    static public class Copy extends Fields {
        /**
         * @param requestFields     - Request fields definition
         * @param responseFields    - Response fields definition
         * @param responseFieldPath - PathParameter to validate in response
         * @param responseFieldDesc - Field description for responseFieldPath
         * @param body              - The object of the request
         * @param url               - The URL to call
         */
        public Copy(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, String responseFieldPath,
                String responseFieldDesc, Object body, String url) {
            super(requestFields, responseFields, responseFieldPath, responseFieldDesc, body, url);
        }
    }

    static public class Get {
        /** Response fields definition */
        List<FieldDescriptor> responseFields;
        /** the URL to call */
        String url;

        /**
         * @param responseFields - Response fields definition
         * @param url            - The URL to call
         */
        public Get(List<FieldDescriptor> responseFields, String url) {
            super();
            this.responseFields = responseFields;
            this.url = url;
        }
    }

    static public class GetWithBody extends Get {
        /** Request fields definition */
        List<FieldDescriptor> requestFields;
        /** the object of the request */
        Object body;

        /**
         * @param responseFields - Response fields definition
         * @param url            - The URL to call
         */
        public GetWithBody(List<FieldDescriptor> responseFields, List<FieldDescriptor> requestFields,
                Object body, String url) {
            super(responseFields, url);
            this.requestFields = requestFields;
            this.body = body;
        }
    }

    static public class Delete {
        /** the URL to call */
        String url;

        /**
         * @param url - The URL to call
         */
        public Delete(String url) {
            super();
            this.url = url;
        }
    }

    static public class DeleteWithBody extends Delete {
        /** Request fields definition */
        List<FieldDescriptor> requestFields;
        /** Response fields definition */
        List<FieldDescriptor> responseFields;
        /** the object of the request */
        Object body;

        /**
         * @param requestFields  - Request fields definition
         * @param responseFields - Response fields definition
         * @param body           - The object of the request
         * @param url            - The URL to call
         */
        public DeleteWithBody(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields,
                Object body, String url) {
            super(url);
            this.requestFields = requestFields;
            this.responseFields = responseFields;
            this.body = body;
        }
    }
}