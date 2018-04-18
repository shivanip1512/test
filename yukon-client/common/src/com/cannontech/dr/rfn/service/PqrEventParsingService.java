package com.cannontech.dr.rfn.service;

import java.util.List;

import com.cannontech.dr.rfn.model.PqrEvent;

/**
 * Service that parses the Power Quality Response event log blob from the LCR TLV report into PqrEvent objects for
 * processing.
 */
public interface PqrEventParsingService {

    /**
     * Parse the log blob bytes into a list of PQR event objects.
     */
    List<PqrEvent> parseLogBlob(int inventoryId, byte[] pqrLogBlob);
}
