package com.ramennoodles.delivery.facade;

import com.ramennoodles.delivery.enums.*;
import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.observer.*;
import com.ramennoodles.delivery.service.*;
import com.ramennoodles.delivery.strategy.*;

import java.util.*;

import com.scm.exceptions.*;
import com.scm.exceptions.categories.*;

// Real Exception Module imports
import com.scm.handler.SCMExceptionHandler;
import com.scm.popup.SCMExceptionPopup;
import com.scm.db.ExceptionLogRepository;
import com.scm.factory.SCMExceptionFactory;
import com.scm.core.SCMException;

/**
 * ═══════════════════════════════════════════════════════════════
 * DELIVERY MONITORING FACADE
 * Design Pattern: Facade
 * ═══════════════════════════════════════════════════════════════
 * 
 * Provides a single, simplified entry point for all delivery 
 * monitoring operations. External subsystems (VERTEX, DEI Hires,
 * CenterDiv) interact with our system through this facade.
 * 
 * Internally coordinates all services:
 * - GPSTrackingService
 * - GeofencingService
 * - ETAService
 * - StatusUpdateService
 * - PODService
 * - NotificationService
 * - RoutePlanService
 * - FleetDashboardService
 */
public class DeliveryMonitoringFacade implements 
        IInputValidationExceptionSource, 
        IConnectivityExceptionSource, 
        IConcurrencyExceptionSource, 
        IResourceAvailabilityExceptionSource, 
        IStateWorkflowExceptionSource, 
        IDataIntegrityExceptionSource, 
        ISystemInfrastructureExceptionSource, 
        ISensorPhysicalExceptionSource, 
        IMLAlgorithmicExceptionSource {

    // Real Exception Module components
    private SCMExceptionPopup realExceptionPopup;
    // Note: Real Exception Module uses:
    // - com.scm.handler.SCMExceptionHandler.INSTANCE (enum with Windows Event Viewer)
    // - ExceptionLogRepository.INSTANCE (enum for database logging)
    // - SCMExceptionFactory static methods (create, createUnregistered)

    /**
     * Register handler method for SCM exception interface compatibility.
     * Note: Real Exception Module uses SCMExceptionHandler.INSTANCE (enum singleton)
     * so this method is kept for interface compatibility but the real handler
     * is used internally via the raise() method.
     */
    public void registerHandler(com.scm.exceptions.SCMExceptionHandler h) {
        // Interface compatibility - real Exception Module uses enum singleton
        // The real handler is used internally in raise() method
    }

    private static class ExceptionEntry {
        String name; com.scm.core.Severity severity; String subsystem; String message;
        ExceptionEntry(String n, com.scm.core.Severity s, String sub, String m) {
            this.name = n; this.severity = s; this.subsystem = sub; this.message = m;
        }
    }

    private ExceptionEntry getEntry(int id) {
        switch(id) {
            case 10: return new ExceptionEntry("INVALID_VEHICLE_ASSIGNMENT", com.scm.core.Severity.MINOR, "Real-Time Delivery", "Vehicle assignment references a vehicle not in the fleet.");
            case 21: return new ExceptionEntry("DELIVERY_STATUS_TRANSITION_INVALID", com.scm.core.Severity.MINOR, "Delivery Orders", "Attempted delivery status update represents an invalid transition.");
            case 54: return new ExceptionEntry("CARRIER_API_TIMEOUT", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "Carrier API did not respond within the expected window.");
            case 58: return new ExceptionEntry("TRAFFIC_DATA_UNAVAILABLE", com.scm.core.Severity.WARNING, "Real-Time Delivery", "Live traffic data feed is unavailable.");
            case 59: return new ExceptionEntry("GPS_SIGNAL_LOST", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "GPS signal from delivery vehicle is lost.");
            case 60: return new ExceptionEntry("IOT_SENSOR_FAILURE", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "IoT sensor on vehicle is not transmitting.");
            case 61: return new ExceptionEntry("NOTIFICATION_SEND_FAILURE", com.scm.core.Severity.MINOR, "Real-Time Delivery", "Notification service failed to deliver message.");
            case 63: return new ExceptionEntry("EPOD_UPLOAD_FAILURE", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "Electronic proof of delivery could not be uploaded.");
            case 64: return new ExceptionEntry("NOTIFICATION_DISPATCH_FAILED", com.scm.core.Severity.WARNING, "Delivery Orders", "DeliveryNotificationService failed to send a status update or the customer.");
            case 65: return new ExceptionEntry("REAL_TIME_STATUS_PUSH_FAILED", com.scm.core.Severity.WARNING, "Delivery Orders", "Delivery status update could not be pushed to Real-Time Monitoring.");
            case 109: return new ExceptionEntry("DUPLICATE_POD_SUBMISSION", com.scm.core.Severity.MINOR, "Real-Time Delivery", "Duplicate proof-of-delivery submission detected.");
            case 168: return new ExceptionEntry("AGENT_NOT_FOUND", com.scm.core.Severity.MINOR, "Delivery Orders", "Referenced delivery agent does not exist in the DeliveryAgents table.");
            case 169: return new ExceptionEntry("DELIVERY_ORDER_NOT_FOUND", com.scm.core.Severity.MINOR, "Delivery Orders", "Requested delivery order does not exist for the given delivery ID.");
            case 170: return new ExceptionEntry("ROUTE_INFORMATION_UNAVAILABLE", com.scm.core.Severity.MINOR, "Delivery Orders", "Route information for the delivery agent could not be retrieved.");
            case 171: return new ExceptionEntry("CUSTOMER_RECORD_NOT_FOUND", com.scm.core.Severity.MAJOR, "Delivery Orders", "Customer record referenced by customer_id does not exist.");
            case 211: return new ExceptionEntry("DELIVERY_TIMEOUT", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "Delivery has exceeded its maximum allowed time window.");
            case 213: return new ExceptionEntry("PARTIAL_DELIVERY_RECORDED", com.scm.core.Severity.WARNING, "Real-Time Delivery", "Only part of the order was delivered.");
            case 214: return new ExceptionEntry("PACKING_NOT_CONFIRMED", com.scm.core.Severity.MAJOR, "Delivery Orders", "Warehouse Management has not confirmed packing for the delivery.");
            case 215: return new ExceptionEntry("DELIVERY_CANCELLATION_FAILED", com.scm.core.Severity.MAJOR, "Delivery Orders", "Delivery order cancellation could not be completed.");
            case 216: return new ExceptionEntry("DISPATCH_CONFIRMATION_MISSING", com.scm.core.Severity.WARNING, "Delivery Orders", "Dispatch confirmation not received within expected timeframe.");
            case 319: return new ExceptionEntry("DUPLICATE_DELIVERY_ORDER", com.scm.core.Severity.MAJOR, "Delivery Orders", "Attempt to create a delivery order for an order_id that already has an active delivery record.");
            case 320: return new ExceptionEntry("DELIVERY_STATUS_HISTORY_INSERT_FAILED", com.scm.core.Severity.MINOR, "Delivery Orders", "Failed to insert a status update record into DeliveryStatusHistory.");
            case 366: return new ExceptionEntry("HISTORICAL_PLAYBACK_DATA_MISSING", com.scm.core.Severity.WARNING, "Real-Time Delivery", "Historical playback data for route analysis is unavailable.");
            case 367: return new ExceptionEntry("DELIVERY_ORDER_CREATION_FAILED", com.scm.core.Severity.MAJOR, "Delivery Orders", "Failed to create a new delivery order due to a database insert error.");
            case 368: return new ExceptionEntry("AGENT_ASSIGNMENT_FAILED", com.scm.core.Severity.MAJOR, "Delivery Orders", "No available delivery agent could be assigned to the delivery order.");
            case 369: return new ExceptionEntry("ORDER_PAYMENT_NOT_CONFIRMED", com.scm.core.Severity.MAJOR, "Delivery Orders", "Delivery order creation rejected because payment status is unconfirmed.");
            case 370: return new ExceptionEntry("ORDER_NOT_FOUND_IN_FULFILLMENT", com.scm.core.Severity.MAJOR, "Delivery Orders", "The referenced order_id does not exist in the Order Fulfilment subsystem.");
            case 371: return new ExceptionEntry("INVENTORY_UNAVAILABLE", com.scm.core.Severity.MAJOR, "Delivery Orders", "Required product inventory is insufficient or unavailable for the delivery order.");
            case 372: return new ExceptionEntry("DELIVERY_COST_CALCULATION_ERROR", com.scm.core.Severity.MINOR, "Delivery Orders", "DeliveryCostCalculator encountered invalid or missing input data.");
            case 401: return new ExceptionEntry("GEOFENCE_BREACH", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "Delivery vehicle has left the permitted geofence.");
            case 402: return new ExceptionEntry("TEMPERATURE_THRESHOLD_BREACH", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "Temperature-sensitive cargo has exceeded its safe range.");
            case 403: return new ExceptionEntry("ROUTE_DEVIATION_DETECTED", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "Vehicle is deviating from the assigned route.");
            case 404: return new ExceptionEntry("DRIVER_UNRESPONSIVE", com.scm.core.Severity.MAJOR, "Real-Time Delivery", "Driver has not responded to check-in prompts.");
            case 405: return new ExceptionEntry("LOW_DEVICE_BATTERY", com.scm.core.Severity.WARNING, "Real-Time Delivery", "Driver tracking device battery is critically low.");
            case 406: return new ExceptionEntry("STALE_LOCATION_DATA", com.scm.core.Severity.MINOR, "Real-Time Delivery", "Location data has not been updated within the expected window.");
            case 461: return new ExceptionEntry("ETA_RECALCULATION_FAILURE", com.scm.core.Severity.MINOR, "Real-Time Delivery", "ETA recalculation service returned an error.");
            default: return new ExceptionEntry("UNREGISTERED_EXCEPTION", com.scm.core.Severity.MINOR, "Real-Time Delivery", "An unregistered exception occurred.");
        }
    }

    /**
     * Raises an exception using the Real Exception Module.
     * Uses SCMExceptionFactory to create SCMException, then processes it through:
     * 1. SCMExceptionHandler.INSTANCE (Windows Event Viewer logging)
     * 2. SCMExceptionPopup (Actual Windows modal popup - created via reflection)
     * 3. ExceptionLogRepository.INSTANCE (Database logging)
     */
    private void raise(int id, String detail) {
        try {
            // Get exception definition
            ExceptionEntry e = getEntry(id);

            // Create real SCMException using factory static method
            SCMException exception = SCMExceptionFactory.create(
                id,                    // exception ID
                e.name,                // exception name
                e.subsystem,           // subsystem name
                e.message + " | Detail: " + detail,  // combined message
                e.severity             // severity
            );

            // Process through real Exception Module components
            // 1. Windows Event Viewer logging (using enum singleton)
            com.scm.handler.SCMExceptionHandler.INSTANCE.handle(exception);

            // 2. Actual Windows modal popup (using reflection to access private constructor)
            if (realExceptionPopup == null) {
                try {
                    java.lang.reflect.Constructor<SCMExceptionPopup> ctor =
                        SCMExceptionPopup.class.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    realExceptionPopup = ctor.newInstance();
                } catch (Exception ex) {
                    System.err.println("⚠️  Could not create SCMExceptionPopup: " + ex.getMessage());
                }
            }
            if (realExceptionPopup != null) {
                realExceptionPopup.show(exception);
            }

            // 3. Database logging (using singleton instance)
            ExceptionLogRepository.INSTANCE.insert(exception);

        } catch (Exception ex) {
            // Fallback to console if real Exception Module fails
            System.err.println("[Exception Module Error] " + ex.getMessage());
            System.err.println("[Original Exception] ID=" + id + ", Detail=" + detail);
        }
    }

    // Core services
    private final GPSTrackingService gpsService;
    private final GeofencingService geofencingService;
    private final ETAService etaService;
    private final StatusUpdateService statusService;
    private final PODService podService;
    private final NotificationService notificationService;
    private final RoutePlanService routePlanService;
    private final FleetDashboardService dashboardService;
    private final DeliveryEventManager eventManager;

    // In-memory order & customer registry
    private final Map<String, Order> orders;
    private final Map<String, Customer> customers;
    private final Map<String, Rider> riders;

    /**
     * Constructor: initializes all services with a shared event manager.
     * Also initializes the Real Exception Module components.
     */
    public DeliveryMonitoringFacade() {
        this.eventManager = new DeliveryEventManager();
        this.gpsService = new GPSTrackingService(eventManager);
        this.geofencingService = new GeofencingService(eventManager);
        this.etaService = new ETAService(eventManager);
        this.statusService = new StatusUpdateService(eventManager);
        this.podService = new PODService(eventManager);
        this.notificationService = new NotificationService();
        this.routePlanService = new RoutePlanService();
        this.dashboardService = new FleetDashboardService(gpsService, statusService, etaService);

        this.orders = new HashMap<>();
        this.customers = new HashMap<>();
        this.riders = new HashMap<>();

        // Initialize Real Exception Module components
        try {
            // Use reflection to create SCMExceptionPopup (private constructor)
            java.lang.reflect.Constructor<SCMExceptionPopup> popupConstructor =
                SCMExceptionPopup.class.getDeclaredConstructor();
            popupConstructor.setAccessible(true);
            this.realExceptionPopup = popupConstructor.newInstance();

            System.out.println("✅ Real Exception Module initialized successfully!");
            System.out.println("   - Windows Event Viewer logging: ENABLED (SCMExceptionHandler.INSTANCE)");
            System.out.println("   - Actual Windows popups: ENABLED (SCMExceptionPopup)");
            System.out.println("   - Database exception logging: ENABLED (ExceptionLogRepository.INSTANCE)");
            System.out.println("   - Exception factory: ENABLED (SCMExceptionFactory static methods)");
        } catch (Exception e) {
            System.err.println("⚠️  Warning: Real Exception Module initialization failed: " + e.getMessage());
            System.err.println("   Falling back to console output for exception handling");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // PHASE 1: Setup & Registration
    // ═══════════════════════════════════════════════════════════

    /**
     * Registers a new customer in the system.
     */
    public Customer registerCustomer(String name, String email, String phone) {
        Customer customer = Customer.createProfile(name, email, phone);
        customers.put(customer.getCustomerId(), customer);
        System.out.println("✅ Customer registered: " + customer);
        return customer;
    }

    /**
     * Registers a new rider in the system.
     */
    public Rider registerRider(String name, String phone, String vehicleType) {
        Rider rider = Rider.createProfile(name, phone, vehicleType);
        riders.put(rider.getRiderId(), rider);
        dashboardService.trackRider(rider);
        System.out.println("✅ Rider registered: " + rider);
        return rider;
    }

    /**
     * Registers a GPS device for a rider and activates the rider.
     */
    public Device registerDeviceForRider(Rider rider) {
        Device device = gpsService.registerDevice(rider.getRiderId());
        rider.activate();
        System.out.println("✅ Device registered: " + device);
        return device;
    }

    // ═══════════════════════════════════════════════════════════
    // PHASE 2: Order Creation & Tracking Initialization
    // ═══════════════════════════════════════════════════════════

    /**
     * Creates a new delivery order and initializes tracking.
     * This is the main entry point called by VERTEX/DEI Hires.
     */
    public Order createAndInitializeDelivery(String customerId, String pickupAddress,
                                              String dropoffAddress, Coordinate pickupCoord,
                                              Coordinate dropoffCoord) {
        if (!customers.containsKey(customerId)) {
            fireResourceNotFound(171, "Customer", customerId);
            return null;
        }

        try {
            // Create order
            Order order = Order.create(customerId, pickupAddress, dropoffAddress,
                    pickupCoord, dropoffCoord);
            orders.put(order.getOrderId(), order);

            // Initialize status tracking
            statusService.initializeOrder(order.getOrderId());

            // Register destination for ETA
            etaService.registerOrderDestination(order.getOrderId(), dropoffCoord);

            // Create geofence zones (200m radius)
            geofencingService.createZonesForOrder(order, 200);

            // Create route plan
            routePlanService.createRoutePlan(order.getOrderId(), pickupCoord, dropoffCoord);

            // Track in dashboard
            dashboardService.trackOrder(order);

            // Publish event
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", order.getOrderId());
            eventData.put("customerId", customerId);
            eventManager.publish(DeliveryEventType.ORDER_CREATED, eventData);

            // Notify customer
            notificationService.notifyMilestone(order.getOrderId(), customerId,
                    "Your order has been placed! We'll notify you when a rider is assigned.");

            System.out.println("✅ Order created: " + order);
            return order;
        } catch (Exception e) {
            firePlatformFailure(367, "DeliveryOrderDB", "INSERT", e.getMessage());
            return null;
        }
    }

    /**
     * Assigns a rider to an order and starts active tracking.
     */
    public void assignRiderToOrder(String orderId, String riderId) {
        Order order = orders.get(orderId);
        Rider rider = riders.get(riderId);

        if (order == null) {
            fireResourceNotFound(169, "DeliveryOrder", orderId);
            return;
        }
        if (rider == null) {
            fireResourceNotFound(168, "DeliveryAgent", riderId);
            return;
        }

        try {
            // Assign rider
            order.assignRider(riderId);
            routePlanService.assignRider(orderId, riderId);
            rider.updateStatus(RiderStatus.ON_DELIVERY);

            // Update status
            statusService.updateStatus(orderId, OrderStatus.ASSIGNED, "SYSTEM", "system");

            // Publish event
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", orderId);
            eventData.put("riderId", riderId);
            eventData.put("riderName", rider.getName());
            eventManager.publish(DeliveryEventType.RIDER_ASSIGNED, eventData);

            // Notify customer
            notificationService.notifyMilestone(orderId, order.getCustomerId(),
                    "Rider " + rider.getName() + " has been assigned to your delivery!");

            System.out.println("✅ Rider " + rider.getName() + " assigned to order " + orderId);
        } catch (IllegalStateException e) {
            fireInvalidStateTransition(21, orderId, order.getStatus().toString(), OrderStatus.ASSIGNED.toString());
        } catch (Exception e) {
            fireProcessingError(368, "AgentAssignment", orderId, e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // PHASE 3: Live Tracking (GPS + Geofencing + ETA)
    // ═══════════════════════════════════════════════════════════

    /**
     * Processes a GPS ping and performs all related checks.
     * This is called repeatedly during active delivery.
     */
    public GPSPing processLocationUpdate(String deviceId, String orderId,
                                          double latitude, double longitude) {
        // Process GPS ping
        GPSPing ping = gpsService.processGPSPing(deviceId, latitude, longitude);
        String riderId = ping.getRiderId();

        // Check geofences
        List<GeofenceEvent> events = geofencingService.checkGeofences(orderId, riderId,
                latitude, longitude);

        // Handle geofence events
        for (GeofenceEvent event : events) {
            handleGeofenceEvent(orderId, event);
        }

        // Calculate new ETA
        etaService.calculateETA(orderId, latitude, longitude);

        return ping;
    }

    /**
     * Handles a geofence event by updating status and notifying customer.
     */
    private void handleGeofenceEvent(String orderId, GeofenceEvent event) {
        Order order = orders.get(orderId);
        if (order == null) return;

        // Determine the zone type from the event
        List<GeofenceZone> zones = geofencingService.getZonesForOrder(orderId);
        for (GeofenceZone zone : zones) {
            if (zone.getZoneId().equals(event.getZoneId())) {
                if (event.getEventType() == EventType.ENTRY) {
                    if (zone.getZoneType() == ZoneType.PICKUP) {
                        System.out.println("📍 Rider arrived at PICKUP zone");
                    } else if (zone.getZoneType() == ZoneType.DROPOFF) {
                        System.out.println("📍 Rider arrived at DROPOFF zone — nearing destination!");
                        // Auto-transition to ARRIVING
                        OrderStatus current = statusService.getCurrentStatus(orderId);
                        if (current == OrderStatus.IN_TRANSIT) {
                            statusService.updateStatus(orderId, OrderStatus.ARRIVING,
                                    "GEOFENCE", "system");
                            notificationService.notifyMilestone(orderId, order.getCustomerId(),
                                    "Your rider is arriving! Please be ready to receive your order.");
                        }
                    }
                }
            }
        }
    }

    /**
     * Manually updates order status (used for pickup, in-transit transitions).
     */
    public void updateOrderStatus(String orderId, OrderStatus newStatus, String changedBy) {
        Order order = orders.get(orderId);
        if (order == null) {
            fireResourceNotFound(169, "DeliveryOrder", orderId);
            return;
        }

        try {
            statusService.updateStatus(orderId, newStatus, "MANUAL", changedBy);
            order.updateStatus(newStatus);
        } catch (IllegalStateException e) {
            fireInvalidStateTransition(21, orderId, order.getStatus().toString(), newStatus.toString());
            return;
        }

        // Send milestone notifications
        String message;
        switch (newStatus) {
            case PICKED_UP:
                message = "Your order has been picked up and is on its way!";
                break;
            case IN_TRANSIT:
                message = "Your order is in transit. Track it live!";
                break;
            case ARRIVING:
                message = "Your rider is arriving! Please be ready.";
                break;
            case DELIVERED:
                message = "Your order has been delivered! Thank you.";
                break;
            case FAILED:
                message = "Delivery attempt failed. We'll try again soon.";
                break;
            default:
                message = "Order status updated to: " + newStatus.getDescription();
        }
        
        try {
            notificationService.notifyMilestone(orderId, order.getCustomerId(), message);
        } catch (Exception e) {
            firePartialConnectivity(61, "NotificationService", "SMS/Email send");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // PHASE 4: Delivery Completion (POD)
    // ═══════════════════════════════════════════════════════════

    /**
     * Completes a delivery by submitting proof of delivery.
     */
    public PODRecord completeDelivery(String orderId, String signatureFile,
                                       String photoFile, String notes) {
        Order order = orders.get(orderId);
        if (order == null) {
            fireResourceNotFound(169, "DeliveryOrder", orderId);
            return null;
        }

        if (podService.hasPOD(orderId)) {
            fireDuplicateSubmission(109, "POD", orderId);
            return null;
        }

        String riderId = order.getRiderId();

        PODRecord pod = null;
        try {
            // Submit POD
            pod = podService.submitPOD(orderId, signatureFile, photoFile, notes, riderId);
        } catch (Exception e) {
            firePartialConnectivity(63, "S3UploadService", "EPOD upload");
            return null;
        }
        
        try {
            // Update status to DELIVERED
            statusService.updateStatus(orderId, OrderStatus.DELIVERED, "POD", riderId);
            order.updateStatus(OrderStatus.DELIVERED);
        } catch (Exception e) {
            fireWriteFailure(320, "DeliveryStatusHistory", orderId, "INSERT");
            return null;
        }

        // Release rider
        Rider rider = riders.get(riderId);
        if (rider != null) {
            rider.activate(); // Back to available
        }

        // Notify customer
        try {
            notificationService.notifyMilestone(orderId, order.getCustomerId(),
                    "Your order has been delivered! Thank you for choosing our service. 🎉");
        } catch (Exception e) {
            firePartialConnectivity(61, "NotificationService", "SMS/Email send");
        }

        System.out.println("✅ Delivery completed for order " + orderId);
        return pod;
    }

    // ═══════════════════════════════════════════════════════════
    // PHASE 5: Query Methods (for other subsystems)
    // ═══════════════════════════════════════════════════════════

    /**
     * Gets the current status of an order.
     */
    public OrderStatus getOrderStatus(String orderId) {
        return statusService.getCurrentStatus(orderId);
    }

    /**
     * Gets the latest ETA for an order.
     */
    public ETARecord getLatestETA(String orderId) {
        return etaService.getLatestETA(orderId);
    }

    /**
     * Gets the latest GPS position of a rider.
     */
    public GPSPing getRiderPosition(String riderId) {
        return gpsService.getLatestPing(riderId);
    }

    /**
     * Gets the POD for an order.
     */
    public PODRecord getPOD(String orderId) {
        return podService.getPODByOrder(orderId);
    }

    /**
     * Gets the full status history (audit trail) for an order.
     */
    public List<DeliveryStatusLog> getStatusHistory(String orderId) {
        return statusService.getStatusHistory(orderId);
    }

    /**
     * Generates a tracking URL for customer self-service.
     */
    public String getTrackingURL(String orderId) {
        return "https://track.ramennoodles.delivery/" + orderId;
    }

    /**
     * Prints the live fleet dashboard.
     */
    public void printDashboard() {
        System.out.println(dashboardService.generateDashboardSummary());
    }

    // ═══════════════════════════════════════════════════════════
    // EVENT SUBSCRIPTIONS (for external subsystems)
    // ═══════════════════════════════════════════════════════════

    /**
     * Allows external subsystems to subscribe to delivery events.
     */
    public void subscribeToEvents(DeliveryEventType eventType, DeliveryEventListener listener) {
        eventManager.subscribe(eventType, listener);
    }

    /**
     * Gets the event manager for advanced subscription management.
     */
    public DeliveryEventManager getEventManager() {
        return eventManager;
    }

    // ═══════════════════════════════════════════════════════════
    // GETTERS for internal state
    // ═══════════════════════════════════════════════════════════

    public Map<String, Order> getOrders() { return Collections.unmodifiableMap(orders); }
    public Map<String, Customer> getCustomers() { return Collections.unmodifiableMap(customers); }
    public Map<String, Rider> getRiders() { return Collections.unmodifiableMap(riders); }
    public NotificationService getNotificationService() { return notificationService; }

    // ─────────────────────────────────────────────────────────────
    // CATEGORY INTERFACE IMPLEMENTATIONS (All 31 fire* methods)
    // ─────────────────────────────────────────────────────────────

    // Category 1
    @Override public void fireInvalidInput(int id, String fieldName, String receivedValue, String rule) { raise(id, String.format("Field '%s' failed rule '%s'. Received: %s", fieldName, rule, receivedValue)); }
    @Override public void fireInvalidReference(int id, String refType, String refValue) { raise(id, String.format("Invalid reference %s: %s", refType, refValue)); }
    @Override public void fireConfigurationError(int id, String configKey, String reason) { raise(id, String.format("Config error for '%s': %s", configKey, reason)); }
    @Override public void fireInvalidStateTransition(int id, String entityId, String fromState, String toState) { raise(id, String.format("Entity %s invalid transition %s -> %s", entityId, fromState, toState)); }
    @Override public void fireValidationFailure(int id, String entityType, String entityId, String reason) { raise(id, String.format("%s %s failed validation: %s", entityType, entityId, reason)); }

    // Category 2
    @Override public void fireConnectionFailed(int id, String targetSystem, String host) { raise(id, String.format("Connection failed to %s at %s", targetSystem, host)); }
    @Override public void fireTimeout(int id, String targetSystem, int timeoutMs) { raise(id, String.format("Timeout accessing %s after %d ms", targetSystem, timeoutMs)); }
    @Override public void fireServiceUnavailable(int id, String targetSystem, String reason) { raise(id, String.format("Service %s unavailable: %s", targetSystem, reason)); }
    @Override public void firePartialConnectivity(int id, String targetSystem, String degradedCapability) { raise(id, String.format("Degraded %s capability on system %s", degradedCapability, targetSystem)); }

    // Category 3
    @Override public void fireDeadlock(int id, String entityType, String entityId, String operation) { raise(id, String.format("Deadlock on %s %s during %s", entityType, entityId, operation)); }
    @Override public void fireRollbackFailed(int id, String entityType, String entityId) { raise(id, String.format("Rollback failed for %s %s", entityType, entityId)); }
    @Override public void fireConflict(int id, String entityType, String entityId, String conflictReason) { raise(id, String.format("Conflict on %s %s: %s", entityType, entityId, conflictReason)); }
    @Override public void fireDuplicateSubmission(int id, String entityType, String entityId) { raise(id, String.format("Duplicate submission for %s %s", entityType, entityId)); }

    // Category 4
    @Override public void fireResourceNotFound(int id, String resourceType, String resourceId) { raise(id, String.format("%s not found: %s", resourceType, resourceId)); }
    @Override public void fireResourceExhausted(int id, String resourceType, String resourceId, int requested, int available) { raise(id, String.format("Resource %s:%s exhausted. Requested: %d, Available: %d", resourceType, resourceId, requested, available)); }
    @Override public void fireResourceBlocked(int id, String resourceType, String resourceId, String reason) { raise(id, String.format("Resource %s:%s blocked: %s", resourceType, resourceId, reason)); }
    @Override public void fireCapacityExceeded(int id, String resourceType, String resourceId, int limit) { raise(id, String.format("Capacity exceeded for %s:%s (Limit: %d)", resourceType, resourceId, limit)); }

    // Category 5
    @Override public void fireInvalidEntityState(int id, String entityType, String entityId, String currentState, String requiredState) { raise(id, String.format("%s %s is in state %s, required: %s", entityType, entityId, currentState, requiredState)); }
    @Override public void fireWorkflowTimeout(int id, String workflowName, String entityId, long elapsedMs) { raise(id, String.format("Workflow %s timed out for entity %s after %d ms", workflowName, entityId, elapsedMs)); }
    @Override public void fireExpiredEntity(int id, String entityType, String entityId, String expiredAttribute) { raise(id, String.format("%s %s expired at attribute: %s", entityType, entityId, expiredAttribute)); }
    @Override public void fireSLABreach(int id, String processName, String entityId, long slaMs, long actualMs) { raise(id, String.format("SLA Breach in %s for %s. SLA: %d ms, Actual: %d ms", processName, entityId, slaMs, actualMs)); }

    // Category 7
    @Override public void fireDuplicateRecord(int id, String entityType, String duplicateKey) { raise(id, String.format("Duplicate %s record with key %s", entityType, duplicateKey)); }
    @Override public void fireReferentialViolation(int id, String childEntity, String parentEntity, String key) { raise(id, String.format("Referential violation: %s depends on missing %s %s", childEntity, parentEntity, key)); }
    @Override public void fireDataInconsistency(int id, String entityType, String entityId, String description) { raise(id, String.format("Data inconsistency in %s %s: %s", entityType, entityId, description)); }
    @Override public void fireWriteFailure(int id, String entityType, String entityId, String operation) { raise(id, String.format("Write failure on %s %s during %s", entityType, entityId, operation)); }

    // Category 8
    @Override public void firePlatformFailure(int id, String component, String operation, String detail) { raise(id, String.format("Platform failure in %s during %s: %s", component, operation, detail)); }
    @Override public void fireProcessingError(int id, String processName, String entityId, String reason) { raise(id, String.format("Processing error in %s for %s: %s", processName, entityId, reason)); }
    @Override public void firePerformanceDegradation(int id, String component, long thresholdMs, long actualMs) { raise(id, String.format("Degradation in %s. Threshold: %d ms, Actual: %d ms", component, thresholdMs, actualMs)); }
    @Override public void fireRenderOrFormatError(int id, String component, String format, String reason) { raise(id, String.format("Render error in %s for format %s: %s", component, format, reason)); }

    // Category 9
    @Override public void fireSafetyAlert(int id, String vehicleOrAssetId, double latitude, double longitude, String detail) { raise(id, String.format("Safety alert for %s at [%f, %f]: %s", vehicleOrAssetId, latitude, longitude, detail)); }
    @Override public void fireDeviceWarning(int id, String deviceId, String deviceType, String condition) { raise(id, String.format("Warning for %s %s: %s", deviceType, deviceId, condition)); }
    @Override public void fireScanError(int id, String scannerLocation, String tagOrBarcode, String reason) { raise(id, String.format("Scan error at %s for %s: %s", scannerLocation, tagOrBarcode, reason)); }

    // Category 10
    @Override public void fireModelFailure(int id, String modelName, String reason) { raise(id, String.format("Model %s failed: %s", modelName, reason)); }
    @Override public void fireModelDegradation(int id, String modelName, String metric, double threshold, double actual) { raise(id, String.format("Model %s degraded on %s. Threshold: %f, Actual: %f", modelName, metric, threshold, actual)); }
    @Override public void fireMissingInputData(int id, String modelName, String missingDataType, String affectedPeriod) { raise(id, String.format("Model %s missing %s data for period %s", modelName, missingDataType, affectedPeriod)); }
    @Override public void fireAlgorithmicAlert(int id, String processName, String entityId, String detail) { raise(id, String.format("Alert from %s for %s: %s", processName, entityId, detail)); }
}
