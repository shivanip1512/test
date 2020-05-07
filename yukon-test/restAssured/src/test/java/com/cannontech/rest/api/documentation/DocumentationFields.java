package com.cannontech.rest.api.documentation;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public abstract class DocumentationFields {

    static public class Create {
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
         * @param requestFields - Request fields definition
         * @param responseFields - Response fields definition
         * @param responseFieldPath - PathParameter to validate in response
         * @param responseFieldDesc - Field description for responseFieldPath
         * @param body - The object of the request
         * @param url- The URL to call
         */
        public Create(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields,
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
    
    static public class Update {
        
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
        /** the object identifier to request as part of url (ie: url=url/id) */
        String id;
        
        /**
         * @param requestFields - Request fields definition
         * @param responseFields - Response fields definition
         * @param responseFieldPath - PathParameter to validate in response
         * @param responseFieldDesc - Field description for responseFieldPath
         * @param body - The object of the request
         * @param url- The URL to call
         * @param id - The object identifier to request as part of url (ie: url=url/id)
         */
        public Update(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields,
                String responseFieldPath, String responseFieldDesc, Object body, String url, String id) {
            super();
            this.requestFields = requestFields;
            this.responseFields = responseFields;
            this.responseFieldPath = responseFieldPath;
            this.responseFieldDesc = responseFieldDesc;
            this.body = body;
            this.url = url;
            this.id = id;
        }
    }
    
    static public class Copy extends Update {

        /**
         * @param requestFields - Request fields definition
         * @param responseFields - Response fields definition
         * @param responseFieldPath - PathParameter to validate in response
         * @param responseFieldDesc - Field description for responseFieldPath
         * @param body - The object of the request
         * @param url- The URL to call
         * @param id - The object identifier to request as part of url (ie: url=url/id)
         */
        public Copy(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields, String responseFieldPath,
                String responseFieldDesc, Object body, String url, String id) {
            super(requestFields, responseFields, responseFieldPath, responseFieldDesc, body, url, id);
        }
    }
    
    static public class Get {
        /** Response fields definition */
        List<FieldDescriptor> responseFields;
        /** the URL to call */
        String url;
        /** the object identifier to request as part of url (ie: url=url/updateId) */
        String id;
        
        /**
         * @param responseFields - Response fields definition
         * @param url- The URL to call
         * @param id - The object identifier to request as part of url (ie: url=url/updateId)
         */
        public Get(List<FieldDescriptor> responseFields, String url, String id) {
            super();
            this.responseFields = responseFields;
            this.url = url;
            this.id = id;
        }
    }
    
    
    static public class GetWithBody extends Get{
        /** Request fields definition */
        List<FieldDescriptor> requestFields;
        /** the object of the request */
        Object body;

        /**
         * @param responseFields - Response fields definition
         * @param url- The URL to call
         * @param id - The object identifier to request as part of url (ie: url=url/updateId)
         */
        public GetWithBody(List<FieldDescriptor> responseFields, List<FieldDescriptor> requestFields, 
                Object body, String url, String id) {
            super(responseFields, url, id);
            this.requestFields = requestFields;
            this.body = body;
        }
    }
    
    static public class Delete {
        
        /** the URL to call */
        String url;
        /** the object identifier to request as part of url (ie: url=url/id) */
        String id;
        
        /**
         * @param url- The URL to call
         * @param id - The object identifier to request as part of url (ie: url=url/id)
         */
        public Delete(String url, String id) {
            super();
            this.url = url;
            this.id = id;
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
         * @param requestFields - Request fields definition
         * @param responseFields - Response fields definition
         * @param body - The object of the request
         * @param url- The URL to call
         * @param id - The object identifier to request as part of url (ie: url=url/id)
         */
        public DeleteWithBody(List<FieldDescriptor> requestFields, List<FieldDescriptor> responseFields,
                Object body, String url, String id) {
            super(url, id);
            this.requestFields = requestFields;
            this.responseFields = responseFields;
            this.body = body;
        }
    }
}
