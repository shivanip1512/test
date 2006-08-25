package com.cannontech.billing.format;

public enum Formatter {
    ATS {
        BillingFormatter getNew() {
            return new ATSRecordFormatter();
        }
    },
    NCDC {
        BillingFormatter getNew() {
            return new NCDCRecordFormatter();
        }
    },
    DAFFRON {
        BillingFormatter getNew() {
            return new DAFFRONRecordFormatter();
        }
    },
    SEDC54 {
        BillingFormatter getNew() {
            return new SEDC54RecordFormatter();
        }
    };

    // Get new instance of the formatter
    abstract BillingFormatter getNew();
}
