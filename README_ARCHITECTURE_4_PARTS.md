# Transport Logistics Subsystem Architecture (Split by Requested 6 Classes)

This document is now split using exactly these classes:

1. RouteOptimizer
2. Carrier
3. Supplier
4. Shipment
5. LiveTracking
6. Alert

---

## 1. Final Class Allocation

### Aarya (Part 1)
1. Carrier

### Anshull (Part 2)
2. Supplier

### Advay (Part 3)
3. Shipment
4. RouteOptimizer

### Amit (Part 4)
5. LiveTracking
6. Alert

---

## 2. Architecture Relationship Using These 6 Classes

```text
Supplier provides goods
   -> Shipment represents what must move
   -> Carrier represents who can move it
   -> RouteOptimizer decides how to move it (route + fallback)
   -> LiveTracking monitors movement in real time
   -> Alert escalates SLA/workflow issues by severity
```

This is the domain-level architecture storyline for your presentation.

## 2.1 MVC and Pattern Summary Table (Per Class)

| Class | MVC placement | Additional patterns connected to this class |
|---|---|---|
| Carrier | Model (entity/state) | Flyweight usage context, Factory creation context |
| Supplier | Model (entity/state) | Factory creation context |
| Shipment | Model (core domain entity) | Builder, Prototype |
| RouteOptimizer | Model (domain logic component) | Strategy, Adapter, Builder (request object) |
| LiveTracking | Model (runtime tracking logic) | Observer-style publishing, Adapter |
| Alert | Model (operational monitoring and escalation) | Chain of Responsibility |

---

## 3. Part 1 - Aarya (Class: Carrier)

Class:

- entities.Carrier

File:

- src/entities/Carrier.java

### Core responsibility

- Represents transport resource metadata
- Stores carrier capabilities and availability

### MVC pattern mapping for this class

- MVC layer: Model
- Why: Carrier stores domain state used by business logic; it does not render UI and does not orchestrate requests.
- MVC role type: Entity within the model layer.

### Additional patterns implemented wrt this class

1. Flyweight (used with this class)
   - Carrier objects are reused through a flyweight factory in the subsystem.
   - Benefit: avoids repeated identical carrier objects and reduces memory duplication.

2. Factory (creation context)
   - Carriers are often created through factory-based flows in demo/setup paths.
   - Benefit: centralizes object construction rules.

### Key fields

- carrierId
- carrierName
- transportMode (Road/Air/Sea/Rail)
- capacity
- available

### Why it matters architecturally

- Shipment assignment and route feasibility depend on carrier capability
- Route optimization logic uses carrier availability as input signal
- It is used with Flyweight strategy in the wider subsystem to reduce duplication

### Aarya speaking focus

- Explain Carrier as the resource entity of the system
- Emphasize that this class is simple by design (data expert)
- Show how a clean domain model helps all higher-level logic

---

## 4. Part 2 - Anshull (Class: Supplier)

Class:

- entities.Supplier

File:

- src/entities/Supplier.java

### Core responsibility

- Represents the origin-side business actor
- Holds supplier identity and contact/location attributes

### MVC pattern mapping for this class

- MVC layer: Model
- Why: Supplier is persistent domain data and business context for shipment creation.
- MVC role type: Entity within the model layer.

### Additional patterns implemented wrt this class

1. Factory (creation context)
   - Suppliers are created through factory-based setup in the subsystem demo flow.
   - Benefit: consistent supplier object construction and cleaner setup logic.

### Key fields

- supplierId
- supplierName
- location
- contactInfo

### Why it matters architecturally

- Shipment creation requires supplier identity
- Business validations in service layer rely on supplier presence/validity
- Upstream order and drop-shipping workflows are anchored to supplier records

### Anshull speaking focus

- Explain Supplier as the source node in logistics flow
- Connect supplier data quality to shipment success/failure
- Highlight SRP: this class only models supplier state, no transport logic

---

## 5. Part 3 - Advay (Classes: Shipment + RouteOptimizer)

## Class A: Shipment

Class:

- entities.Shipment

File:

- src/entities/Shipment.java

### Core responsibility

- Central business entity for movement lifecycle
- Encapsulates payload, route endpoints, status, and commercial data

### MVC pattern mapping for this class

- MVC layer: Model
- Why: Shipment is the primary business object that service rules validate and repository stores.
- MVC role type: Core entity in the model layer.

### Additional patterns implemented wrt this class

1. Builder
   - Shipment objects are assembled through ShipmentBuilder for fluent, readable construction.
   - Benefit: handles many optional fields without telescoping constructors in callers.

2. Prototype
   - Shipment implements Cloneable and is used in prototype-style cloning flows.
   - Benefit: fast template-based creation for similar shipment objects.

### Key fields

- shipmentId
- supplierId
- carrierId
- origin / destination
- weight
- status
- cost

### Pattern connection

- Implements Cloneable and Serializable
- Supports Prototype-style cloning in the subsystem

### Why it matters architecturally

- This is the entity all other logic revolves around
- Route planning, tracking, and alerts all attach to shipment context

---

## Class B: RouteOptimizer

Class:

- entities.RouteOptimizer

File:

- src/entities/RouteOptimizer.java

### Core responsibility

- Computes route plans under SLA and availability constraints
- Provides fallback behavior on route failure/unavailability

### MVC pattern mapping for this class

- MVC layer: Model
- Why: RouteOptimizer contains business decision logic and no presentation or request-controller behavior.
- MVC role type: Domain service/algorithm component inside model layer.

### Additional patterns implemented wrt this class

1. Strategy
   - RoutingEngine, FallbackPolicy, and CarrierGateway are injected strategy contracts.
   - Benefit: route solving and fallback behavior are swappable without changing RouteOptimizer core.

2. Adapter (integration context)
   - RouteOptimizer is consumed through adapter style wrappers (for example RouteOptimizerAdapter) and a Port interface.
   - Benefit: keeps external integration decoupled from core optimization logic.

3. Builder (request input)
   - RouteRequest uses a nested Builder for safe, readable request construction.
   - Benefit: improves input consistency for algorithm execution.

### Important nested contracts

- Port
- RoutingEngine
- FallbackPolicy
- CarrierGateway
- RouteRequest (Builder)
- RoutePlan

### Key logic highlights

- If no carriers available -> raises carrier unavailable flow and uses fallback policy
- If no viable route generated -> raises no viable route and uses fallback
- If ETA exceeds SLA -> raises critical delay and returns low-confidence plan
- Handles unexpected runtime failures with unregistered exception flow

### Why it matters architecturally

- This class is where decision intelligence sits
- It converts static shipment data into operational execution plans
- It links domain modeling to SLA-driven transport control

### Advay speaking focus

- Explain Shipment as state and RouteOptimizer as decision engine
- Show Builder usage in RouteRequest for robust input construction
- Emphasize fallback design for resiliency

---

## 6. Part 4 - Amit (Classes: LiveTracking + Alert)

## Class A: LiveTracking

Class:

- entities.LiveTracking

File:

- src/entities/LiveTracking.java

### Core responsibility

- Tracks active vehicle telemetry
- Detects low-confidence conditions and sensor/connectivity anomalies

### MVC pattern mapping for this class

- MVC layer: Model
- Why: LiveTracking processes operational telemetry and emits tracking state; it does not implement view or controller behavior.
- MVC role type: Runtime monitoring logic inside model layer.

### Additional patterns implemented wrt this class

1. Observer-style publishing
   - TrackingPublisher receives TrackingSnapshot outputs produced by LiveTracking.
   - Benefit: tracking producers and consumers remain loosely coupled.

2. Adapter
   - LegacyGpsAdapter adapts LegacyGpsClient data into the internal Telemetry format.
   - Benefit: legacy GPS feeds can be integrated without changing core tracking logic.

### Important nested contracts

- Port
- TrackingFeed
- LegacyGpsClient
- LegacyGpsAdapter
- TrackingPublisher
- Telemetry
- TrackingSnapshot

### Key logic highlights

- Null telemetry -> GPS signal lost path
- Stale telemetry age -> timeout path
- Low battery -> low confidence
- Geofence breach or route deviation -> elevated confidence risk
- Publishes snapshots for downstream consumers

### Why it matters architecturally

- This class turns transport from static planning into real-time observability
- It bridges legacy GPS feeds using adapter-style abstraction

---

## Class B: Alert

Class:

- entities.Alert

File:

- src/entities/Alert.java

### Core responsibility

- Routes alert envelopes by severity level
- Escalates SLA/workflow threshold breaches

### MVC pattern mapping for this class

- MVC layer: Model
- Why: Alert executes operational business rules for escalation/routing and remains independent from controller/view concerns.
- MVC role type: Monitoring and escalation component in model layer.

### Additional patterns implemented wrt this class

1. Chain of Responsibility
   - Internal Link chain routes AlertEnvelope by severity (CRITICAL/MAJOR/WARNING/INFO).
   - Benefit: channel routing rules are composable and extendable without rewriting a monolithic if-else block.

### Important nested parts

- Port
- AlertChannel
- AlertLevel enum
- AlertEnvelope record
- internal Link chain

### Key logic highlights

- CRITICAL/MAJOR/WARNING/INFO channel routing
- SLA breach detection
- Workflow timeout escalation
- Contract-expired message escalation path

### Why it matters architecturally

- Completes operational control loop after tracking and optimization
- Ensures execution problems are surfaced with priority-aware routing

### Amit speaking focus

- Explain LiveTracking as detection and Alert as escalation
- Show that monitoring + alerting makes system production-ready
- Emphasize chain-based routing and SLA governance behavior

---

## 7. Team Hand-off Script (Recommended)

1. Aarya: "We defined who can transport using Carrier."
2. Anshull: "We defined where shipments originate using Supplier."
3. Advay: "We then modeled movement with Shipment and optimized it with RouteOptimizer."
4. Amit: "Finally, we monitor execution with LiveTracking and enforce response with Alert."

---

## 8. Suggested Time Split

- Aarya: 2.5 minutes
- Anshull: 2.5 minutes
- Advay: 4.5 minutes (2 classes)
- Amit: 4.5 minutes (2 classes)

Total: ~14 minutes

---

## 9. Final Slide Summary

- The architecture is explained entirely through these six classes.
- Advay and Amit each handle two classes as requested.
- Domain flow remains clear and sequential:

```text
Supplier + Carrier -> Shipment -> RouteOptimizer -> LiveTracking -> Alert
```
