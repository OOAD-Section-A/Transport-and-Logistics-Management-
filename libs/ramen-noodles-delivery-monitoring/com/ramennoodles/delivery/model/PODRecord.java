package com.ramennoodles.delivery.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents Proof of Delivery (POD) captured at the moment of delivery.
 * Images are stored externally (S3); DB stores URL references only.
 * 
 * Shared with: VERTEX (Order Fulfillment) for order completion.
 * 
 * Relationships:
 *  - PODRecord belongs to 1 Order (0..1 relationship)
 */
public class PODRecord {
    private String podId;           // PK
    private String orderId;         // FK -> Order
    private String signatureUrl;    // External storage URL
    private String photoUrl;        // External storage URL
    private String notes;
    private LocalDateTime submittedAt;
    private String submittedBy;     // rider_id

    // --- Factory Method ---
    public static PODRecord submit(String orderId, String signatureUrl, String photoUrl,
                                    String notes, String submittedBy) {
        PODRecord pod = new PODRecord();
        pod.podId = "POD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        pod.orderId = orderId;
        pod.signatureUrl = signatureUrl;
        pod.photoUrl = photoUrl;
        pod.notes = notes;
        pod.submittedAt = LocalDateTime.now();
        pod.submittedBy = submittedBy;
        return pod;
    }

    // --- Business Methods ---
    /**
     * Simulates uploading a file to S3 and returning the URL.
     */
    public static String uploadToS3(String filePath) {
        // In a real system, this would upload to AWS S3
        return "https://s3.amazonaws.com/delivery-pod/" + UUID.randomUUID() + "/" + filePath;
    }

    public String getSignatureUrl() { return signatureUrl; }
    public String getPhotoUrl() { return photoUrl; }

    // --- Getters ---
    public String getPodId() { return podId; }
    public String getOrderId() { return orderId; }
    public String getNotes() { return notes; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public String getSubmittedBy() { return submittedBy; }
    public String getRiderId() { return submittedBy; } // Alias for compatibility

    @Override
    public String toString() {
        return String.format("PODRecord[%s, order=%s, by=%s, at=%s, notes='%s']",
                podId, orderId, submittedBy, submittedAt, notes);
    }
}
