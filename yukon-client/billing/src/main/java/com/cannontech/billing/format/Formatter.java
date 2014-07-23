package com.cannontech.billing.format;

import com.cannontech.billing.SimpleBillingFormat;

public enum Formatter {
    ATS {
        SimpleBillingFormat getNew() {
            return new ATSRecordFormatter();
        }
    },
    NCDC {
        SimpleBillingFormat getNew() {
            return new NCDCRecordFormatter();
        }
    },
    DAFFRON {
        SimpleBillingFormat getNew() {
            return new DAFFRONRecordFormatter();
        }
    },
    SEDC54 {
        SimpleBillingFormat getNew() {
            return new SEDC54RecordFormatter();
        }
    };

    // Get new instance of the formatter
    abstract SimpleBillingFormat getNew();
}
