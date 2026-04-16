package entities;

/**
 * Entity: Supplier
 * GRASP: Information Expert - Supplier manages its own data
 * SOLID: SRP - Single responsibility (represent supplier)
 */
public class Supplier {
    private String supplierId;
    private String supplierName;
    private String location;
    private String contactInfo;

    // Constructor
    public Supplier(String supplierId, String supplierName, String location, String contactInfo) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.location = location;
        this.contactInfo = contactInfo;
    }

    // Getters
    public String getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getLocation() {
        return location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    // Setters
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId='" + supplierId + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", location='" + location + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }
}