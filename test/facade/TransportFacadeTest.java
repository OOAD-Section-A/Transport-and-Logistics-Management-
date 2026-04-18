package facade;

import entities.*;
import interfaces.ITransportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransportFacadeTest {
    private ITransportService mockService;
    private TransportFacade facade;

    @BeforeEach
    void setUp() {
        mockService = mock(ITransportService.class);
        // Facade creates its own components, so for unit test, we test the facade as is
        facade = new TransportFacade();
    }

    @Test
    void testAuditFreight() {
        FreightAudit mockAudit = new FreightAudit("A001", "S001", 100.0, 100.0, "NONE");
        // Since facade calls service, and service is proxied, test integration
        TransportFacade testFacade = new TransportFacade();
        FreightAudit audit = testFacade.auditFreight("A001", "S001", 100.0, 100.0);
        assertNotNull(audit);
        assertEquals("NONE", audit.getDiscrepancyFlag());
    }

    @Test
    void testPlanConstraints() {
        TransportFacade testFacade = new TransportFacade();
        ConstraintPlanner planner = testFacade.planConstraints("P001", "S001", 5000.0, 10.0, 8, "9AM-5PM");
        assertNotNull(planner);
        assertEquals("P001", planner.getPlanId());
    }

    @Test
    void testManageTerritory() {
        TransportFacade testFacade = new TransportFacade();
        Territory territory = testFacade.manageTerritory("T001", "Northeast", "US Northeast", 5);
        assertNotNull(territory);
        assertEquals("T001", territory.getTerritoryId());
    }

    @Test
    void testOrchestrateOrder() {
        TransportFacade testFacade = new TransportFacade();
        OrderOrchestrator orchestrator = testFacade.orchestrateOrder("O001", "SO001", true, "SUP001");
        assertNotNull(orchestrator);
        assertTrue(orchestrator.isThirdPartyItem());
    }

    @Test
    void testIntegrateSupplierPortal() {
        TransportFacade testFacade = new TransportFacade();
        SupplierPortal portal = testFacade.integrateSupplierPortal("P001", "SUP001", "Order details");
        assertNotNull(portal);
        assertEquals("P001", portal.getPortalId());
    }

    @Test
    void testSyncTracking() {
        TransportFacade testFacade = new TransportFacade();
        TrackingSync sync = testFacade.syncTracking("S001", "O001", "TRK123");
        assertNotNull(sync);
        assertEquals("S001", sync.getSyncId());
    }

    @Test
    void testHandleReverseLogistics() {
        TransportFacade testFacade = new TransportFacade();
        ReverseLogistics logistics = testFacade.handleReverseLogistics("R001", "O001", "SUP001", 50.0);
        assertNotNull(logistics);
        assertEquals("R001", logistics.getReturnId());
    }
}