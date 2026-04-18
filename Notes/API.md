# TMS API Documentation

## Overview
This document provides detailed specifications for the REST APIs exposed by the Transport & Logistics Management System (TMS). These APIs enable seamless integration with other Supply Chain Management (SCM) components, particularly the Warehouse Management System (WMS). Each endpoint includes method, parameters, request/response formats, and a specific explanation of why WMS requires access to it for efficient warehouse operations.

**Base URL:** `http://tms-server:8080/api/v1`  
**Authentication:** Bearer token (to be implemented post-database integration)  
**Content-Type:** `application/json`  
**Version:** 1.0  
**Status:** Pre-Implementation (APIs defined; awaiting REST framework integration)

---

## 1. Shipment Management APIs

### GET /shipments/{id}
**Description:** Retrieves comprehensive details for a specific shipment by its unique identifier.

**Method:** GET  
**Path Parameters:**
- `id` (string, required): Unique shipment identifier (e.g., "SHP001")

**Query Parameters:** None  
**Request Body:** None  

**Response (200 OK):**
```json
{
  "shipmentId": "SHP001",
  "supplierId": "SUP001",
  "carrierId": "CAR001",
  "origin": "Warehouse A",
  "destination": "Retail Store X",
  "weight": 5.0,
  "status": "In-Transit",
  "createdDate": "2026-04-18T10:00:00Z",
  "deliveryDate": "2026-04-20T14:00:00Z",
  "cost": 150.00,
  "items": [
    {"sku": "ITEM001", "quantity": 10},
    {"sku": "ITEM002", "quantity": 5}
  ]
}
```

**Error Responses:**
- 404: Shipment not found
- 500: Internal server error

**Why WMS Needs It:**  
WMS requires real-time access to shipment details upon carrier arrival at the receiving dock. This enables:
- Validation of incoming goods against purchase orders
- Quality control checks (batch/serial numbers, expiration dates)
- Optimal putaway location determination based on product size, weight, and storage requirements (e.g., temperature-controlled zones)
- Synchronization with inventory tracking systems for 100% visibility

### POST /shipments
**Description:** Creates a new shipment record in TMS, triggering validation and initial processing.

**Method:** POST  
**Path Parameters:** None  
**Query Parameters:** None  
**Request Body (required):**
```json
{
  "supplierId": "SUP001",
  "carrierId": "CAR001",
  "origin": "Warehouse A",
  "destination": "Retail Store X",
  "weight": 5.0,
  "cost": 150.00,
  "items": [
    {"sku": "ITEM001", "quantity": 10, "description": "Electronics"}
  ]
}
```

**Response (201 Created):**
```json
{
  "shipmentId": "SHP002",
  "status": "Pending",
  "message": "Shipment created successfully"
}
```

**Error Responses:**
- 400: Invalid request data
- 409: Shipment with same details already exists

**Why WMS Needs It:**  
WMS initiates outbound shipments for fulfilled orders. This API allows WMS to:
- Notify TMS of goods ready for pickup, enabling carrier assignment
- Trigger route optimization based on warehouse location and delivery destination
- Ensure TMS has shipment data for tracking synchronization and exception handling
- Support cross-docking workflows where incoming shipments are immediately routed to outgoing carriers

### PUT /shipments/{id}/status
**Description:** Updates the status of an existing shipment (e.g., from "Pending" to "In-Transit").

**Method:** PUT  
**Path Parameters:**
- `id` (string, required): Shipment ID

**Query Parameters:** None  
**Request Body (required):**
```json
{
  "status": "Delivered",
  "notes": "Delivered on time"
}
```

**Response (200 OK):**
```json
{
  "shipmentId": "SHP001",
  "status": "Delivered",
  "updatedAt": "2026-04-20T14:00:00Z"
}
```

**Error Responses:**
- 404: Shipment not found
- 400: Invalid status transition

**Why WMS Needs It:**  
WMS updates shipment status during receiving and putaway processes. This enables:
- Real-time inventory adjustments upon delivery confirmation
- Triggering replenishment workflows when stock levels drop
- Updating yard and dock management systems for carrier departure
- Exception alerts if status updates indicate delays or issues

### GET /shipments
**Description:** Retrieves a paginated list of all shipments, with optional filtering.

**Method:** GET  
**Path Parameters:** None  
**Query Parameters:**
- `status` (string, optional): Filter by status (e.g., "Pending")
- `page` (integer, optional): Page number (default: 1)
- `size` (integer, optional): Items per page (default: 20)

**Request Body:** None  

**Response (200 OK):**
```json
{
  "shipments": [
    {
      "shipmentId": "SHP001",
      "status": "In-Transit",
      "destination": "Retail Store X"
    }
  ],
  "total": 150,
  "page": 1,
  "size": 20
}
```

**Why WMS Needs It:**  
WMS requires visibility into all active shipments for:
- Wave picking planning (grouping orders for efficient picking)
- Dock scheduling based on expected arrival times
- Labor management forecasts for staffing needs
- Cycle counting to ensure inventory accuracy without disrupting operations

---

## 2. Carrier Management APIs

### GET /carriers
**Description:** Lists all available carriers with their details and capacities.

**Method:** GET  
**Path Parameters:** None  
**Query Parameters:**
- `mode` (string, optional): Filter by transport mode (e.g., "Truck")

**Request Body:** None  

**Response (200 OK):**
```json
{
  "carriers": [
    {
      "carrierId": "CAR001",
      "name": "FastFreight Inc.",
      "mode": "Truck",
      "capacity": 10.0,
      "driverInfo": "John Doe"
    }
  ]
}
```

**Why WMS Needs It:**  
WMS uses carrier data for:
- Dock appointment scheduling to avoid congestion
- Yard management for vehicle flow and asset tracking
- Load planning based on carrier capacities and modes
- Integration with carrier APIs (e.g., FedEx) for automated documentation

### POST /carriers
**Description:** Registers a new carrier in the TMS system.

**Method:** POST  
**Request Body:**
```json
{
  "name": "NewCarrier Ltd.",
  "mode": "Rail",
  "capacity": 50.0,
  "driverInfo": "Jane Smith"
}
```

**Response (201 Created):** New carrier object with ID.

**Why WMS Needs It:**  
Allows WMS to onboard new carriers for expanded transportation options, ensuring TMS has up-to-date carrier information for assignments and optimizations.

---

## 3. Route Optimization APIs

### GET /routes/optimize
**Description:** Requests optimized route calculation for a shipment.

**Method:** GET  
**Query Parameters:**
- `origin` (string, required): Starting location
- `destination` (string, required): End location
- `constraints` (string, optional): JSON string of constraints (e.g., weight limits)

**Response (200 OK):**
```json
{
  "route": ["Warehouse A", "Highway 1", "Retail Store X"],
  "estimatedTime": "4 hours",
  "cost": 120.00
}
```

**Why WMS Needs It:**  
WMS integrates route data for:
- Last-mile optimization within warehouse facilities
- Slotting and layout adjustments to reduce picker travel time
- Synchronization with automated systems like AMRs and conveyors

---

## 4. Freight Audit APIs

### POST /freight-audit
**Description:** Performs freight cost audit on a shipment.

**Method:** POST  
**Request Body:**
```json
{
  "shipmentId": "SHP001",
  "invoicedAmount": 160.00,
  "contractAmount": 150.00
}
```

**Response (200 OK):**
```json
{
  "auditId": "AUD001",
  "discrepancy": 10.00,
  "flag": "OVERCHARGE"
}
```

**Why WMS Needs It:**  
WMS triggers audits for inbound shipments to:
- Validate carrier billing accuracy
- Identify cost-saving opportunities
- Maintain financial controls in supply chain operations

---

## 5. Territory Management APIs

### GET /territories
**Description:** Lists geographic territories for optimization.

**Method:** GET  
**Response:** List of territories with boundaries.

**Why WMS Needs It:**  
WMS uses territory data for:
- Demand-based slotting optimization
- Regional inventory allocation
- Supplier portal integrations for localized ASN generation

---

## 6. Exception Handling APIs

### POST /exceptions
**Description:** Reports an exception for centralized handling.

**Method:** POST  
**Request Body:**
```json
{
  "type": "ConnectivityException",
  "severity": "HIGH",
  "description": "Carrier GPS signal lost"
}
```

**Response (201 Created):** Exception ID.

**Why WMS Needs It:**  
WMS reports exceptions (e.g., damaged goods) to trigger:
- Unified alerts across SCM
- Contingency planning in TMS
- Automated recovery workflows

---

## 7. Tracking APIs

### GET /tracking/{shipmentId}
**Description:** Retrieves real-time tracking data.

**Method:** GET  
**Response:** Current location, ETA, status updates.

**Why WMS Needs It:**  
WMS monitors shipments for:
- Proactive receiving preparation
- Customer notifications via tracking sync
- Exception detection (e.g., delays)

---

## Implementation Notes
- All APIs will be implemented using a Java REST framework (e.g., Spring Boot) post-database integration.
- Rate limiting and authentication will be added for production.
- Webhook support for real-time notifications to WMS.

**Contact:** TMS Development Team for API access and testing.