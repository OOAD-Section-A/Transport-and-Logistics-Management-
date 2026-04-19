# Database Module JAR Integration

## Purpose

The database module is packaged as a JAR so other Java subsystems can reuse the
same database access code instead of writing SQL connection logic again.

The main entry point is:

```java
com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade
```

Subsystems can either call the facade directly or use the subsystem-specific
adapter classes in:

```java
com.jackfruit.scm.database.adapter
```

## JAR Location

After building the project, the integration JAR is created at:

```text
dist/database-module-1.0.0-SNAPSHOT-standalone.jar
```

This standalone JAR includes the database module and its required runtime
dependencies, so it is the correct file to share with non-Maven subsystems.

Build it with:

```bash
mvn clean package
```

To install it into the local Maven repository:

```bash
mvn install
```

## Maven Integration

If another subsystem is a Maven project, add this dependency to its `pom.xml`:

```xml
<dependency>
    <groupId>com.jackfruit.scm</groupId>
    <artifactId>database-module</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

This is the recommended method because Maven also brings required dependencies
like MySQL Connector/J.

## Manual JAR Integration

If the subsystem is not using Maven:

1. Add `dist/database-module-1.0.0-SNAPSHOT-standalone.jar` to the subsystem classpath.
2. Make sure `database.properties` is available on the runtime classpath.
3. Import and call the required adapter class.

Example compile/run commands:

```bash
javac -cp "lib/database-module-1.0.0-SNAPSHOT-standalone.jar" MySubsystem.java
java -cp "lib/database-module-1.0.0-SNAPSHOT-standalone.jar;." MySubsystem
```

On Windows, use `;` in the classpath. On Linux/macOS, use `:`.

## Using Adapters

Yes, other subsystems can integrate through adapters. The JAR makes the adapter
classes available, and each adapter gives a subsystem-friendly API.

Example:

```java
import com.jackfruit.scm.database.adapter.InventoryAdapter;
import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;

public class InventorySubsystem {
    public static void main(String[] args) {
        try (SupplyChainDatabaseFacade facade = new SupplyChainDatabaseFacade()) {
            InventoryAdapter inventoryAdapter = new InventoryAdapter(facade);

            inventoryAdapter.listProducts()
                    .forEach(product -> System.out.println(product.productName()));
        }
    }
}
```

Other available adapters include:

- `PricingAdapter`
- `OrderAdapter`
- `InventoryAdapter`
- `WarehouseManagementAdapter`
- `LogisticsAdapter`
- `ReportingAdapter`
- `BarcodeTrackingAdapter`
- `ExceptionHandlingAdapter`

## Direct Facade Usage

Adapters are useful, but not mandatory. A subsystem can also use the facade
directly:

```java
try (SupplyChainDatabaseFacade db = new SupplyChainDatabaseFacade()) {
    db.inventory().listProducts();
    db.pricing().listPrices();
    db.orders().listOrders();
}
```

## Important Note

The JAR does not automatically connect subsystems by itself. It provides shared
Java classes. The actual integration happens when each subsystem imports the JAR
and calls the adapter or facade methods.
