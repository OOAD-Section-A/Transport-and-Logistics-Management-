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

Runtime flow follows MVC plus layered architecture:

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
- MySQL server reachable when running DB integration smoke checks
- Shared SCM exception jars in `exception-handling` (canonical source):
  - `scm-exception-handler-v3.jar` (required)
  - `jna-5.18.1.jar` (required for Event Viewer logging)
  - `jna-platform-5.18.1.jar` (required for Event Viewer logging)
  - `scm-exception-viewer-gui.jar` (optional, only for GUI viewer)
- DB integration jar in `libs`:
  - `database-module-1.0.0-SNAPSHOT-standalone.jar` (optional unless validating DB integration)
- `libs\database.properties` must define DB credentials for database-module usage:
  - `db.url`
  - `db.username`
  - `db.password`

### Event Viewer source registration (one-time, run as Administrator)

```bat
reg add "HKLM\SYSTEM\CurrentControlSet\Services\EventLog\Application\SCM-TransportandLogisticsManagement" /v EventMessageFile /t REG_SZ /d "%SystemRoot%\System32\EventCreate.exe" /f
```

## 4. Database module contract (latest)

Transport aligns with `database_module/INTEGRATION.md`:

- No manual `schema.sql` execution is required.
- Creating `SupplyChainDatabaseFacade` (or adapters) auto-initializes schema.
- The module drops and recreates `OOAD` on each run by design.
- Data loss in `OOAD` is expected during bootstrap.
- Credentials are mandatory (`db.url`, `db.username`, `db.password`).
- Config precedence is:
  - JVM system properties (`-Ddb.*`)
  - environment variables (`DB_*`)
  - `database.properties`

## 5. Build and run the subsystem

### Quick run (clean compile plus run demo)

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
javac -cp "src;libs;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" -d bin @sources.list
java -cp "bin;libs;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" transport.TransportApplication
del /F /Q sources.list
```

## 6. Integration guide for partner teams

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
- `exception-handling\scm-exception-handler-v3.jar`
- `exception-handling\jna-5.18.1.jar`
- `exception-handling\jna-platform-5.18.1.jar`
- Optional GUI: `exception-handling\scm-exception-viewer-gui.jar`
- Optional DB integration: `database-module-1.0.0-SNAPSHOT-standalone.jar`

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

If your team compiles source directly, include `src`, `libs`, and `exception-handling` jars in classpath and call `facade.TransportFacade` as above.

## 7. Public operations you can call

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

## 8. How to test this subsystem

### 8.1 Functional end-to-end demo

```bat
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-\cmd"
clean_and_run.bat
```

This validates controller-facade-proxy-service-repository flow and pattern demos.

### 8.2 Exception and DB integration smoke test

```bat
cd "c:\AIML\OOAD\Transport-and-Logistics-Management-"
if exist bin rmdir /S /Q bin
mkdir bin
dir /s /b src\*.java > sources.list
javac -cp "src;libs;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" -d bin @sources.list
java -Djava.awt.headless=true -cp "bin;libs;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar" transport.ExceptionDbIntegrationSmokeTest
del /F /Q sources.list
```

PowerShell-safe invocation for the same run:

```powershell
& java '-Djava.awt.headless=true' '-cp' 'bin;libs;exception-handling\scm-exception-handler-v3.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar;libs\database-module-1.0.0-SNAPSHOT-standalone.jar' 'transport.ExceptionDbIntegrationSmokeTest'
```

### 8.3 Validate Event Viewer and GUI

1. Trigger at least one exception (the smoke test already does this).
2. Open Event Viewer:
   - Win + R -> `eventvwr`
   - Windows Logs -> Application
  - Filter source: `SCM-TransportandLogisticsManagement`
3. Launch GUI viewer (optional):

```bat
java -cp "exception-handling\scm-exception-handler-v3.jar;exception-handling\scm-exception-viewer-gui.jar;exception-handling\jna-5.18.1.jar;exception-handling\jna-platform-5.18.1.jar" com.scm.gui.ExceptionViewerGUI
```

On first launch, select subsystem when prompted. GUI reads from local Event Viewer.

### 8.4 Unit tests

Test classes are under:

- `test\services\TransportServiceTest.java`
- `test\controllers\TransportControllerTest.java`
- `test\facade\TransportFacadeTest.java`

These use JUnit 5 and Mockito. Run from IDE test runner or your JUnit console setup.

## 9. How to implement new capabilities safely

Use this checklist for adding a new feature:

1. Create or update domain model in `entities`
2. Add factory logic in `services.TransportFeatureFactory` (if feature object creation is needed)
3. Extend `interfaces.ITransportService`
4. Implement method in `services.TransportService`
5. Forward method in `proxy.TransportServiceProxy` and `proxy.TransportProxyFeatureOperations`
6. Expose it in `facade.TransportFacade`
7. Add controller handler in `controllers.TransportController`
8. Add tests under `test\...`

If the feature has validation rules, keep them in service layer. If it needs shared exception reporting, call the mapped subsystem method from catch blocks and return immediately.

## 10. Implementation notes and constraints

- Destination pincode validation expects six digits (`\\d{6}`) in `TransportService.createShipment`
- Max shipment weight enforced: `100.0`
- Repository is in-memory by default, not persistent across restarts
- External transport integration is currently adapter-backed mock (`ExternalTransportAdapter`)

## 11. Troubleshooting

- `package com.scm... does not exist`
  - Ensure shared SCM jars are present in `exception-handling` and included in classpath.
- `NoClassDefFoundError: com/sun/jna/...`
  - Add `jna-5.18.1.jar` and `jna-platform-5.18.1.jar` to runtime classpath.
- Exception popup appears but no Event Viewer entries
  - Run `reg add` source registration as Administrator, then trigger exception again.
- GUI shows no rows
  - Confirm source registration, then click `Refresh Now` or wait auto-refresh.
- DB module check fails
  - Verify MySQL is reachable and `db.url`, `db.username`, `db.password` are set.
- `OOAD` data disappears on each run
  - Expected with current DB module contract (auto drop and recreate).
- Shipment creation returns `null`
  - Check pincode format, weight limit, and supplier id validity.

## Team

Team: CenterDiv

- Aarya Upadhya
- Anshull M Udyavar
- Advay
- Amit Kulkarni
