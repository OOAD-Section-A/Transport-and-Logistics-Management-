# TMS-WMS Integration Guide: Comprehensive System Architecture and Data Flow Analysis

## Document Overview
This 15-page guide provides an exhaustive analysis of the Transport & Logistics Management System (TMS) developed for the Supply Chain Management (SCM) suite, with detailed connections to Warehouse Management System (WMS) integration. The document covers every component, design pattern, data flow, and integration requirements for seamless TMS-WMS interoperability.

**Date:** April 18, 2026  
**Version:** 1.0  
**Authors:** AI Architecture Assistant  
**Status:** Pre-Integration (Awaiting Database and Execution Handling Team Input)

---

## Table of Contents
1. [TMS System Architecture Overview](#1-tms-system-architecture-overview)
2. [MVC Pattern Implementation in TMS](#2-mvc-pattern-implementation-in-tms)
3. [Design Patterns Deep Dive](#3-design-patterns-deep-dive)
4. [TMS Core Functionalities and Workflows](#4-tms-core-functionalities-and-workflows)
5. [WMS Core Functionalities Overview](#5-wms-core-functionalities-overview)
6. [TMS-WMS Integration Points](#6-tms-wms-integration-points)
7. [Data Requirements and Schema Analysis](#7-data-requirements-and-schema-analysis)
8. [API and Interface Specifications](#8-api-and-interface-specifications)
9. [Error Handling and Exception Management](#9-error-handling-and-exception-management)
10. [Performance and Scalability Considerations](#10-performance-and-scalability-considerations)
11. [Testing and Validation Framework](#11-testing-and-validation-framework)
12. [Deployment and Maintenance Guidelines](#12-deployment-and-maintenance-guidelines)
13. [Future Enhancements and Roadmap](#13-future-enhancements-and-roadmap)
14. [Appendices](#14-appendices)

---

## 1. TMS System Architecture Overview

### 1.1 System Purpose and Scope
The TMS is a Java-based application designed to manage transportation and logistics operations within an SCM ecosystem. It handles shipment creation, carrier management, route optimization, and exception handling using a clean MVC architecture and multiple design patterns.

**Key Objectives:**
- Provide a unified interface for transport operations
- Ensure scalability and maintainability through SOLID principles
- Integrate seamlessly with WMS for end-to-end supply chain visibility

### 1.2 Technology Stack
- **Language:** Java 8+
- **Architecture:** MVC (Model-View-Controller)
- **Patterns:** 7 core design patterns (Factory, Builder, Prototype, Adapter, Facade, Proxy, Flyweight)
- **Behavioral Patterns:** Command, Chain of Responsibility, Iterator
- **Data Storage:** In-memory HashMap (to be replaced with database integration)
- **Testing:** JUnit 5 (planned)
- **Build Tool:** Manual compilation (to be migrated to Maven/Gradle)

### 1.3 High-Level Architecture Diagram
```
[User Interface (Console)]
        ↓
[Controller Layer] - TransportController
        ↓
[Facade Layer] - TransportFacade (Single Entry Point)
        ↓
[Service Layer] - TransportService (Business Logic)
        ↓
[Repository Layer] - TransportRepository (Data Access)
        ↓
[Entities] - Shipment, Carrier, etc. (Data Models)
```

---

## 2. MVC Pattern Implementation in TMS

### 2.1 Model Layer (Entities)
The Model layer contains data models representing business entities.

#### Shipment Entity
**Purpose:** Represents a transport shipment with all relevant details.

**Fields:**
- `shipmentId` (String): Unique identifier
- `supplierId` (String): Supplier reference
- `carrierId` (String): Assigned carrier
- `destination` (String): Delivery location
- `weight` (double): Shipment weight in tons
- `status` (String): Current status (Pending, In-Transit, Delivered)
- `createdDate` (Date): Creation timestamp
- `deliveryDate` (Date): Delivery timestamp
- `cost` (double): Transportation cost
- `origin` (String): Origin location

**Methods:**
- `getShipmentId()`: Returns shipment ID
- `setStatus(String status)`: Updates shipment status
- `clone()`: Creates deep copy (Prototype pattern)

**Why Used:** Information Expert pattern - Shipment manages its own data.

#### Carrier Entity
**Purpose:** Represents transportation carriers.

**Fields:**
- `carrierId`, `carrierName`, `transportMode`, `driverInfo`, `capacity`

**Flyweight Pattern:** Shared instances to save memory.

#### Other Entities
- `Supplier`: Supplier details
- `FreightAudit`: Audit freight payments
- `ConstraintPlanner`: Planning constraints
- `Territory`: Geographic zones
- `OrderOrchestrator`: Drop-shipping orchestration
- `SupplierPortal`: Supplier integration
- `TrackingSync`: Tracking synchronization
- `ReverseLogistics`: Return handling

### 2.2 View Layer
**TransportView:** Handles console output and user interaction.

**Methods:**
- `displayWelcome()`: Shows welcome message
- `displaySection(String)`: Prints section headers
- `displayShipment(Shipment)`: Formats shipment display

**Why Used:** Separates presentation logic from business logic.

### 2.3 Controller Layer
**TransportController:** Entry point for user requests.

**Key Methods:**
- `handleCreateShipment(Shipment)`: Creates shipments with validation
- `handleUpdateShipment(String, String)`: Updates status
- `handleGetShipment(String)`: Retrieves shipments
- `handleListAllShipments()`: Lists all with Iterator
- `handleAssignCarrier(String, String)`: Assigns carriers

**Why Used:** Controller pattern - handles user interactions and delegates to Facade.

---

## 3. Design Patterns Deep Dive

### 3.1 Creational Patterns

#### Factory Pattern (TransportFactory)
**Purpose:** Creates entities uniformly.

**Methods:**
- `createSupplier(String id, String name, String location, String contact)`
- `createCarrier(String id, String name, String mode, String driver)`

**Parameters:**
- Entity-specific details (id, name, etc.)

**Output:** New entity instances

**Why Used:** Encapsulates creation logic, ensures consistency.

#### Builder Pattern (ShipmentBuilder)
**Purpose:** Constructs complex Shipment objects step-by-step.

**Methods:**
- `shipmentId(String)`, `supplierId(String)`, etc. (fluent API)
- `build()`: Validates and creates Shipment

**Parameters:** Individual field values

**Output:** Complete Shipment object

**Why Used:** Handles complex object construction with optional fields.

#### Prototype Pattern (PrototypeRegistry)
**Purpose:** Clones existing shipments.

**Methods:**
- `register(String name, Shipment)`: Stores template
- `getClone(String name)`: Returns deep copy

**Parameters:** Template name

**Output:** Cloned Shipment

**Why Used:** Faster object creation than building from scratch.

#### Flyweight Pattern (CarrierFlyweightFactory)
**Purpose:** Reuses Carrier objects.

**Methods:**
- `getCarrier(String id, String name, String mode, double capacity)`

**Parameters:** Carrier details

**Output:** Shared Carrier instance

**Why Used:** Memory optimization for repeated carriers.

### 3.2 Structural Patterns

#### Adapter Pattern (ExternalTransportAdapter)
**Purpose:** Integrates external transport systems.

**Methods:**
- `fetchExternalShipmentData(String shipmentId)`

**Parameters:** Shipment ID

**Output:** External data string

**Why Used:** Translates external formats to internal.

#### Facade Pattern (TransportFacade)
**Purpose:** Single entry point for all operations.

**Methods:**
- `createShipment(Shipment)`
- `getShipment(String)`
- `auditFreight(String, String, double, double)` (new)

**Parameters:** Operation-specific

**Output:** Results or entities

**Why Used:** Hides subsystem complexity.

#### Proxy Pattern (TransportServiceProxy)
**Purpose:** Adds logging to service calls.

**Methods:** Mirrors ITransportService with logging

**Parameters:** Same as service

**Output:** Same as service, with console logs

**Why Used:** Cross-cutting concerns without modifying service.

### 3.3 Behavioral Patterns

#### Command Pattern (CreateShipmentCommand, UpdateShipmentCommand)
**Purpose:** Encapsulates operations as objects.

**Methods:**
- `execute()`: Performs operation
- `undo()`: Reverses operation

**Parameters:** Service and data

**Output:** Operation result

**Why Used:** Enables undo/redo, queues operations.

#### Chain of Responsibility (ValidationHandler, LoggingHandler)
**Purpose:** Processes requests through a chain.

**Methods:**
- `handle(Shipment)`: Processes and passes to next

**Parameters:** Shipment to validate

**Output:** Validation result or error

**Why Used:** Flexible validation pipeline.

#### Iterator Pattern (ShipmentIterator)
**Purpose:** Safe traversal of collections.

**Methods:**
- `hasNext()`, `next()`, `getIndex()`

**Parameters:** Repository

**Output:** Shipment objects sequentially

**Why Used:** Prevents concurrent modification issues.

---

## 4. TMS Core Functionalities and Workflows

### 4.1 Shipment Management
**Workflow:**
1. Create Shipment (Builder Pattern)
2. Validate (Chain of Responsibility)
3. Execute Command (Command Pattern)
4. Store (Repository)

**Integration Points:** WMS receives shipment data for inventory allocation.

### 4.2 Carrier Management
**Workflow:**
1. Get/Create Carrier (Flyweight)
2. Assign to Shipment
3. Track via LiveTracking

**Integration Points:** WMS shares carrier schedules for dock management.

### 4.3 Route Optimization
**Workflow:**
1. Analyze RouteRequest
2. Solve via RoutingEngine
3. Fallback if needed
4. Multi-stop optimization

**Integration Points:** WMS provides warehouse layout for last-mile routing.

### 4.4 Exception Management
**Workflow:**
1. Detect via ExceptionSpec
2. Fire alerts (SCMEvents)
3. Handle via SCMExceptionHandler

**Integration Points:** WMS triggers alerts for inventory discrepancies.

### 4.5 New TMS Features
- **Freight Audit:** Compares invoiced vs. contract amounts
- **Constraint Planning:** Considers weight, height, hours, windows
- **Territory Management:** Geographic zones for optimization
- **Order Orchestration:** Drop-shipping detection
- **Supplier Portal:** ASN and packing slip generation
- **Tracking Sync:** Customer updates
- **Reverse Logistics:** Return processing

---


## 6. TMS-WMS Integration Points

### 6.1 Data Flow Connections
- TMS shipments trigger WMS receiving processes
- WMS inventory levels inform TMS carrier assignments
- TMS routes optimize WMS picking paths
- WMS exceptions alert TMS for contingency planning

### 6.2 API Integration Points
- TMS calls WMS for real-time inventory
- WMS pushes shipment status to TMS
- Shared carrier and dock scheduling
- Unified exception handling

### 6.3 Workflow Synchronization
- Shipment creation → WMS putaway allocation
- Carrier arrival → WMS dock assignment
- Delivery confirmation → WMS inventory update

---

## 7. Data Requirements and Schema Analysis

### 7.1 TMS Data Structures
**Shipment Table:**
- shipment_id (PK)
- supplier_id (FK)
- carrier_id (FK)
- origin, destination
- weight, cost
- status, dates

**Carrier Table:**
- carrier_id (PK)
- name, mode, capacity

**Audit Table:**
- audit_id (PK)
- shipment_id (FK)
- invoiced_amount, contract_amount, discrepancy_flag

### 7.2 WMS Data Needs from TMS
- Shipment details for receiving preparation
- Carrier schedules for dock management
- Route information for yard optimization
- Exception alerts for inventory adjustments

### 7.3 TMS Data Needs from WMS
- Real-time inventory levels for load planning
- Warehouse layout for route optimization
- Picking status for ETA updates
- Dock availability for scheduling

### 7.4 Shared Data Entities
- Product/SKU information
- Location coordinates
- Carrier performance metrics
- Exception codes and severities

---

## 8. API and Interface Specifications

### 8.1 TMS Interfaces
**ITransportService:** Defines service contracts
**IExternalTransportSystem:** External system integration
**ICommand:** Command pattern interface

### 8.2 TMS Exposed APIs
These are RESTful APIs that TMS exposes for other systems (including WMS) to consume. All endpoints return JSON responses.

- `GET /shipments/{id}`: Retrieve shipment details by ID
- `POST /shipments`: Create a new shipment
- `PUT /shipments/{id}/status`: Update shipment status
- `GET /shipments`: List all shipments (with pagination)
- `GET /carriers`: List available carriers
- `POST /carriers`: Register a new carrier
- `GET /routes/optimize`: Request route optimization for a shipment
- `POST /freight-audit`: Perform freight audit on a shipment
- `GET /territories`: List geographic territories
- `POST /exceptions`: Report an exception (for integration with Execution Handling)
- `GET /tracking/{shipmentId}`: Get real-time tracking data

### 8.3 WMS Integration APIs (TMS Calls)
These are APIs that TMS calls on WMS for data retrieval and notifications.

- `GET /inventory/{sku}`: Real-time stock levels
- `POST /receiving/notify`: Shipment arrival alerts
- `PUT /dock/assign`: Carrier dock assignments
- `GET /routes/optimize`: Route suggestions from WMS layout data

### 8.4 Data Exchange Formats
- JSON for API payloads
- XML for legacy system integration
- CSV for bulk data transfers

---

## 9. Error Handling and Exception Management

### 9.1 TMS Exception Framework
**SCMExceptionHandler:** Central exception management
**ExceptionSpec:** Defines error codes and severities
**SCMEvents:** Event emission system

### 9.2 Integration Error Handling
- Network timeouts between TMS and WMS
- Data synchronization conflicts
- API rate limiting
- Authentication failures

### 9.3 Recovery Mechanisms
- Retry logic for failed API calls
- Fallback to cached data
- Alert escalation for critical issues

---

## 10. Performance and Scalability Considerations

### 10.1 Current Limitations
- In-memory storage (HashMap)
- Single-threaded execution
- No caching layer

### 10.2 Scalability Requirements
- Database integration for persistence
- Multi-threading for concurrent operations
- Load balancing for high-volume scenarios

### 10.3 WMS Integration Performance
- Real-time data synchronization
- Event-driven architecture
- Asynchronous processing for heavy operations

---

## 11. Testing and Validation Framework

### 11.1 Unit Testing
- JUnit tests for services, controllers, facades
- Mock repositories for isolated testing
- Pattern validation tests

### 11.2 Integration Testing
- TMS-WMS API testing
- End-to-end workflow validation
- Performance benchmarking

### 11.3 Data Validation
- Schema compliance checks
- Business rule enforcement
- Exception scenario testing

---

## 12. Deployment and Maintenance Guidelines

### 12.1 Deployment Process
- Clean compilation with `clean_and_run.bat`
- Environment configuration
- Database migration scripts

### 12.2 Maintenance Tasks
- Regular .class file cleanup
- Log rotation
- Performance monitoring

### 12.3 Version Control
- Git integration with .gitignore
- Branching strategy for features
- CI/CD pipeline setup

---