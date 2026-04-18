# Detailed TMS API Documentation

## Overview
This document provides comprehensive details on the Transport & Logistics Management System (TMS) REST APIs. The APIs follow standard RESTful conventions and are designed for seamless integration with Warehouse Management System (WMS) and other SCM components.

**Framework Assumption:** Spring Boot (or equivalent) for implementation.  
**Base URL:** `http://tms-server:8080/api/v1`  
**Content-Type:** `application/json`  
**Authentication:** Bearer Token (to be implemented)  
**Status:** Ready for Framework Integration  

---

## API Design Principles
- **RESTful**: Uses HTTP methods (GET, POST, PUT) appropriately.
- **Stateless**: No server-side session state.
- **JSON Responses**: Consistent structure with `success`, `data`, `message`, `errors`.
- **Idempotent**: Safe operations (GET) and idempotent updates (PUT).
- **Versioned**: `/api/v1` for future compatibility.

---

## Common Response Format
All responses follow this structure:
```json
{
  "success": true,
  "data": { /* API-specific data */ },
  "message": "Operation successful",
  "timestamp": "2026-04-18T12:00:00Z",
  "errors": null
}
```

Error responses:
```json
{
  "success": false,
  "data": null,
  "message": "Error description",
  "timestamp": "2026-04-18T12:00:00Z",
  "errors": ["Detailed error 1", "Detailed error 2"]
}
```

---

## Authentication
- **Header:** `Authorization: Bearer <token>`
- **Token Generation:** To be implemented via centralized auth service.
- **Scopes:** `read`, `write` for different access levels.

---

## 1. Shipment Management APIs

### GET /shipments/{id}
**Purpose:** Retrieve detailed shipment information for WMS receiving and tracking.

**How It Works:**
1. Validates shipment ID exists.
2. Fetches shipment from repository.
3. Includes related data (items, carrier).
4. Logs access for audit.

**WMS Integration:** WMS calls this on shipment arrival to validate against POs and allocate putaway locations.

**Request:**
- Method: GET
- Path: `/shipments/{id}`
- Headers: Authorization

**Response (200):**
```json
{
  "success": true,
  "data": {
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
      {"sku": "ITEM001", "quantity": 10, "description": "Electronics"}
    ]
  },
  "message": "Shipment retrieved successfully"
}
```

**Errors:**
- 404: Shipment not found
- 401: Unauthorized
- 500: Internal error

**Business Logic:** Calls `TransportService.getShipment(id)` which queries repository.

---

### POST /shipments
**Purpose:** Create new shipments for outbound orders.

**How It Works:**
1. Validates input data (required fields).
2. Applies business rules (e.g., carrier capacity).
3. Saves to repository.
4. Triggers route optimization if needed.

**WMS Integration:** WMS initiates for fulfilled orders, ensuring TMS has data for carrier assignment.

**Request:**
- Method: POST
- Path: `/shipments`
- Body:
```json
{
  "supplierId": "SUP001",
  "carrierId": "CAR001",
  "origin": "Warehouse A",
  "destination": "Retail Store X",
  "weight": 5.0,
  "cost": 150.00,
  "items": [{"sku": "ITEM001", "quantity": 10}]
}
```

**Response (201):**
```json
{
  "success": true,
  "data": {
    "shipmentId": "SHP002",
    "status": "Pending"
  },
  "message": "Shipment created successfully"
}
```

**Errors:**
- 400: Invalid data
- 409: Duplicate shipment

**Business Logic:** Calls `TransportService.createShipment(shipment)` with validation via Chain of Responsibility.

---

### PUT /shipments/{id}/status
**Purpose:** Update shipment status for real-time tracking.

**How It Works:**
1. Validates status transition (e.g., Pending → In-Transit).
2. Updates repository.
3. Notifies subscribers (e.g., WMS for inventory updates).

**WMS Integration:** WMS updates status during putaway to trigger replenishment.

**Request:**
- Method: PUT
- Path: `/shipments/{id}/status`
- Body: `{"status": "Delivered"}`

**Response (200):** Updated shipment data.

**Errors:** 400 (Invalid transition), 404 (Not found)

**Business Logic:** Calls `TransportService.updateShipmentStatus(id, status)`.

---

### GET /shipments
**Purpose:** List shipments for overview and planning.

**How It Works:**
1. Applies filters (status, date range).
2. Paginates results.
3. Sorts by creation date.

**WMS Integration:** WMS fetches active shipments for wave picking and dock scheduling.

**Query Params:** `status`, `page`, `size`

**Response (200):** Paginated list of shipments.

---

## 2. Carrier Management APIs

### GET /carriers
**Purpose:** List available carriers for assignment.

**How It Works:** Filters by mode (Truck, Rail) and availability.

**WMS Integration:** WMS uses for dock scheduling and load planning.

**Response:** List of carriers with details.

---

### POST /carriers
**Purpose:** Register new carriers.

**How It Works:** Validates and stores carrier data.

**WMS Integration:** Allows WMS to onboard carriers for expanded options.

**Request:** Carrier JSON.

**Response:** Created carrier.

---

## 3. Route Optimization APIs

### GET /routes/optimize
**Purpose:** Get optimized routes for shipments.

**How It Works:** Uses routing engine with constraints.

**WMS Integration:** WMS integrates for last-mile optimization.

**Query Params:** `origin`, `destination`, `constraints`

**Response:** Route details, ETA, cost.

---

## 4. Freight Audit APIs

### POST /freight-audit
**Purpose:** Audit freight costs.

**How It Works:** Compares invoiced vs. contract amounts.

**WMS Integration:** WMS triggers for cost control.

**Request:** Shipment ID, amounts.

**Response:** Audit result with flag (OVERCHARGE, etc.).

---

## 5. Territory Management APIs

### GET /territories
**Purpose:** List territories for regional planning.

**How It Works:** Returns geographic zones.

**WMS Integration:** WMS uses for demand-based slotting.

**Response:** List of territories.

---

## 6. Exception Handling APIs

### POST /exceptions
**Purpose:** Report exceptions for handling.

**How It Works:** Logs and escalates via SCMExceptionHandler.

**WMS Integration:** WMS reports issues like damaged goods.

**Request:** Exception details.

**Response:** Exception ID.

---

## 7. Tracking APIs

### GET /tracking/{shipmentId}
**Purpose:** Get real-time tracking data.

**How It Works:** Aggregates from LiveTracking.

**WMS Integration:** WMS monitors for proactive receiving.

**Response:** Location, ETA, status.

---

## Error Handling
- **400 Bad Request:** Validation errors.
- **401 Unauthorized:** Invalid token.
- **403 Forbidden:** Insufficient permissions.
- **404 Not Found:** Resource missing.
- **409 Conflict:** Business rule violation.
- **500 Internal Server Error:** System issues.

## Rate Limiting
- 100 requests/minute per client.
- Headers: `X-Rate-Limit-Remaining`, `X-Rate-Limit-Reset`.

## Testing
Use Postman or curl for testing. Example:
```
curl -X GET "http://tms-server:8080/api/v1/shipments/SHP001" -H "Authorization: Bearer <token>"
```

## Future Enhancements
- Webhooks for real-time notifications.
- GraphQL support.
- API versioning.

This documentation ensures reliable integration. Contact TMS team for implementation support.