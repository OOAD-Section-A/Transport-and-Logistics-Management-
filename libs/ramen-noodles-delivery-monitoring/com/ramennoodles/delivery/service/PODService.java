package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.observer.*;

import java.util.*;

/**
 * Service for handling Proof of Delivery submissions.
 * Core component: POD Handler
 * 
 * Captures digital signatures, timestamps, and photographs at the
 * moment of delivery, synced back to the central system.
 */
public class PODService {

    // In-memory storage
    private final Map<String, PODRecord> podByOrder;    // order_id -> POD
    private final DeliveryEventManager eventManager;

    public PODService(DeliveryEventManager eventManager) {
        this.podByOrder = new HashMap<>();
        this.eventManager = eventManager;
    }

    /**
     * Submits a Proof of Delivery for an order.
     */
    public PODRecord submitPOD(String orderId, String signatureFile, String photoFile,
                                String notes, String riderId) {
        // Upload files to S3 (simulated)
        String signatureUrl = PODRecord.uploadToS3(signatureFile);
        String photoUrl = PODRecord.uploadToS3(photoFile);

        // Create POD record
        PODRecord pod = PODRecord.submit(orderId, signatureUrl, photoUrl, notes, riderId);
        podByOrder.put(orderId, pod);

        // Publish event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", orderId);
        eventData.put("podId", pod.getPodId());
        eventData.put("riderId", riderId);
        eventData.put("submittedAt", pod.getSubmittedAt().toString());
        eventManager.publish(DeliveryEventType.POD_SUBMITTED, eventData);

        return pod;
    }

    /**
     * Gets the POD for an order.
     */
    public PODRecord getPODByOrder(String orderId) {
        return podByOrder.get(orderId);
    }

    /**
     * Checks if a POD has been submitted for an order.
     */
    public boolean hasPOD(String orderId) {
        return podByOrder.containsKey(orderId);
    }
}
