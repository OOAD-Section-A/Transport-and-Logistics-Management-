package views;

import entities.Shipment;
import entities.Supplier;
import entities.Carrier;
import java.util.List;

/**
 * View: TransportView
 * MVC: View layer - Displays data to user
 * SOLID: SRP - Responsible only for presentation
 * 
 * In a real application, this would render to UI/Console
 */
public class TransportView {

    public void displayWelcome() {
        System.out.println("\n" +
                "╔════════════════════════════════════════════════════════════╗\n" +
                "║   TRANSPORT MANAGEMENT SYSTEM - COMPREHENSIVE DEMO         ║\n" +
                "║   Following MVC Architecture & SOLID + GRASP Principles   ║\n" +
                "╚════════════════════════════════════════════════════════════╝\n");
    }

    public void displaySection(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   " + title);
        System.out.println("=".repeat(60));
    }

    public void displayShipment(Shipment shipment) {
        if (shipment != null) {
            System.out.println("┌─ Shipment Details ─────────────────────────────────┐");
            System.out.println("│ ID:         " + padRight(shipment.getShipmentId(), 45) + "│");
            System.out.println("│ Supplier:   " + padRight(shipment.getSupplierId(), 45) + "│");
            System.out.println("│ Carrier:    " + padRight(shipment.getCarrierId(), 45) + "│");
            System.out.println("│ Origin:     " + padRight(shipment.getOrigin(), 45) + "│");
            System.out.println("│ Destination:" + padRight(shipment.getDestination(), 45) + "│");
            System.out.println("│ Weight:     " + padRight(shipment.getWeight() + " tons", 45) + "│");
            System.out.println("│ Status:     " + padRight(shipment.getStatus(), 45) + "│");
            System.out.println("│ Cost:       " + padRight("$" + shipment.getCost(), 45) + "│");
            System.out.println("└─────────────────────────────────────────────────────┘");
        } else {
            System.out.println("Shipment not found");
        }
    }

    public void displaySupplier(Supplier supplier) {
        System.out.println("Supplier: " + supplier.getSupplierName() + 
                         " (ID: " + supplier.getSupplierId() + 
                         ", Location: " + supplier.getLocation() + ")");
    }

    public void displayCarrier(Carrier carrier) {
        System.out.println("Carrier: " + carrier.getCarrierName() + 
                         " (ID: " + carrier.getCarrierId() + 
                         ", Mode: " + carrier.getTransportMode() + 
                         ", Capacity: " + carrier.getCapacity() + " tons)");
    }

    public void displayMessage(String message) {
        System.out.println("→ " + message);
    }

    public void displayInfo(String info) {
        System.out.println("\nℹ " + info);
    }

    public void displaySuccess(String message) {
        System.out.println("✓ " + message);
    }

    public void displayError(String message) {
        System.out.println("✗ " + message);
    }

    public void displayShipmentList(List<Shipment> shipments) {
        System.out.println("\nTotal Shipments: " + shipments.size());
        for (int i = 0; i < shipments.size(); i++) {
            Shipment s = shipments.get(i);
            System.out.printf("%2d. %s (Status: %s, Supplier: %s, Carrier: %s)%n",
                            i + 1, s.getShipmentId(), s.getStatus(), 
                            s.getSupplierId(), s.getCarrierId());
        }
    }

    private String padRight(String str, int length) {
        if (str == null) str = "";
        return String.format("%-" + length + "s", str);
    }

    public void displaySystemInfo(String info) {
        System.out.println("\n📊 SYSTEM INFO:\n" + info);
    }

    public void displayPatternUsage(String pattern, String description) {
        System.out.println("\n[PATTERN] " + pattern + ": " + description);
    }
}
