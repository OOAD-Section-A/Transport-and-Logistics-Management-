# Transport and Logistics Management Subsystem

This document explains how the subsystem works, how to integrate it into other teams' projects, how to test it, and how to implement new capabilities safely.

## 1. What this subsystem does

The Transport and Logistics Management subsystem handles:

- Shipment creation and lifecycle updates
- Carrier and route operations
- External transport data integration
- Domain features for freight audit, constraints, territory, order orchestration, supplier portal sync, tracking sync, and reverse logistics
- Centralized exception reporting through shared SCM exception modules

Main entrypoint:

- `transport.TransportApplication`

Core integration facade:

- `facade.TransportFacade`

## 2. How it works (architecture)

Runtime flow follows MVC + layered architecture:

1. `controllers.TransportController` receives action calls
2. Controller calls `facade.TransportFacade`
3. Facade delegates to proxied service (`proxy.TransportServiceProxy`)
4. Proxy logs and forwards to `services.TransportService`
5. Service validates and persists/query data through `repositories.TransportRepository`

Repository storage is currently in-memory (`HashMap`), so data is process-local and non-persistent by default.

### Design patterns currently used

- Factory (`factories.TransportFactory`)
- Builder (`factories.ShipmentBuilder`)
- Prototype (`factories.PrototypeRegistry`)
- Facade (`facade.TransportFacade`)
- Proxy (`proxy.TransportServiceProxy`)
- Adapter (`adapters.ExternalTransportAdapter` and feature adapters)
- Flyweight (`flyweight.CarrierFlyweightFactory`)
- Command (`behavioral.command.*`)
- Chain of Responsibility (`behavioral.chain.*`)
- Iterator (`behavioral.iterator.ShipmentIterator`)

## 3. Prerequisites

- JDK 17+ (JDK 21 recommended)
- Windows command prompt or PowerShell
- Shared SCM modules in `libs`:
	- `scm-exception-handler-v3.jar` (or exploded folder with same name)
	- `scm-exception-foundation.jar` (or exploded folder with same name)
	- Optional for DB-backed exception logging: `database-module-1.0.0-SNAPSHOT-standalone.jar`
	- For current `scm-exception-handler-v3.jar` runtime, keep `libs\database.properties` with:
		- `db.url=...`
		- `db.user=...`
		- `db.password=...`
	- Keep `db.username=...` in the same file for compatibility with database-module config readers
	- Environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) may still be used by database-module components, but do not replace the handler's `db.user` expectation
	- No external `schema.sql` copy is required; canonical schema is embedded in the database module JAR

## 4. Build and run the subsystem

### Quick run (clean compile + run demo)

```bat
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
clean_and_run.bat
```

### Manual compile and run

```bat
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-"
if exist bin rmdir /S /Q bin
mkdir bin

dir /s /b src\*.java > sources.list
javac -cp "src;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" -d bin @sources.list
java -cp "bin;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" transport.TransportApplication
del /F /Q sources.list
```

## 5. Integration guide for partner teams

### Option A: Integrate with exported transport JAR (recommended)

Build integration artifact:

```bat
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
build_integration_jar.bat
```

Artifacts produced:

- `libs\transport-and-logistics-management.jar`
- `dist\transport-and-logistics-management.jar`

Add these to consuming project's classpath:

- `transport-and-logistics-management.jar`
- `scm-exception-handler-v3.jar`
- `scm-exception-foundation.jar`
- Optional: `database-module-1.0.0-SNAPSHOT-standalone.jar`

Example usage from another subsystem:

```java
import facade.TransportFacade;
import interfaces.ITransportService;
import entities.Shipment;

public class PartnerIntegrationExample {
		public static void main(String[] args) {
				TransportFacade facade = new TransportFacade();
				ITransportService service = facade.getTransportService();

				Shipment shipment = facade.createShipmentBuilder("SHIP900")
								.withSupplierId("SUP001")
								.withCarrierId("CAR001")
								.withOrigin("Bengaluru")
								.withDestination("560001")
								.withWeight(25.0)
								.withCost(4500.0)
								.withStatus("Pending")
								.build();

				service.createShipment(shipment);
				service.updateShipmentStatus("SHIP900", "In-Transit");
				System.out.println(service.getShipment("SHIP900"));
		}
}
```

### Option B: Source-level integration

If your team compiles source directly, include `src` and `libs` in classpath and call `facade.TransportFacade` exactly as above.

## 6. Public operations you can call

Primary service contract (`interfaces.ITransportService`):

- Shipment lifecycle:
	- `createShipment`
	- `updateShipmentStatus`
	- `getShipment`
	- `getAllShipments`
- Feature APIs:
	- `auditFreight`
	- `planConstraints`
	- `manageTerritory`
	- `orchestrateOrder`
	- `integrateSupplierPortal`
	- `syncTracking`
	- `handleReverseLogistics`

## 7. How to test this subsystem

### 7.1 Functional end-to-end demo

```bat
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
clean_and_run.bat
```

This validates controller-facade-proxy-service-repository flow and pattern demos.

### 7.2 Exception and DB integration smoke test

```bat
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-"
if exist bin rmdir /S /Q bin
mkdir bin
dir /s /b src\*.java > sources.list
javac -cp "src;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" -d bin @sources.list
java -Djava.awt.headless=true -cp "bin;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" transport.ExceptionDbIntegrationSmokeTest
del /F /Q sources.list
```

PowerShell-safe invocation for the same run:

```powershell
& java '-Djava.awt.headless=true' '-cp' 'bin;libs;libs\scm-exception-handler-v3.jar;libs\scm-exception-foundation.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar' 'transport.ExceptionDbIntegrationSmokeTest'
```

### 7.3 Unit tests

Test classes are under:

- `test\services\TransportServiceTest.java`
- `test\controllers\TransportControllerTest.java`
- `test\facade\TransportFacadeTest.java`

These use JUnit 5 and Mockito. Run from IDE test runner or your JUnit console setup.

## 8. How to implement new capabilities in this subsystem

Use this checklist for adding a new feature safely:

1. Create or update domain model in `entities`
2. Add factory logic in `services.TransportFeatureFactory` (if feature object creation is needed)
3. Extend `interfaces.ITransportService`
4. Implement method in `services.TransportService`
5. Forward method in `proxy.TransportServiceProxy` and `proxy.TransportProxyFeatureOperations`
6. Expose it in `facade.TransportFacade`
7. Add controller handler in `controllers.TransportController`
8. Add tests under `test\...`

If the feature has validation rules, keep them in the service layer. If it needs cross-cutting logging or exception capture, keep those concerns in proxy or shared exception modules.

## 9. Implementation notes and constraints

- Destination pincode validation currently expects six digits (`\\d{6}`) in `TransportService.createShipment`
- Max shipment weight enforced: `100.0`
- Repository is in-memory by default, not persistent across restarts
- External transport integration is currently adapter-backed mock (`ExternalTransportAdapter`)

## 10. Troubleshooting

- `package com.scm... does not exist`
	- Ensure SCM exception modules are present in `libs` and included in classpath.
- DB-related exception logging not working
	- Verify `database-module-1.0.0-SNAPSHOT-standalone.jar` and `scm-exception-handler-v3.jar` are on classpath.
	- Verify `libs\database.properties` exists and includes `db.url`, `db.user`, and `db.password`.
	- Keep `db.username` as well for compatibility with database-module config readers.
- Shipment creation returns `null`
	- Check pincode format, weight limit, and supplier id validity.

## Team

Team: CenterDiv

- Aarya Upadhya
- Anshull M Udyavar
- Advay
- Amit Kulkarni
