package controllers;

import entities.*;
import facade.TransportFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransportControllerTest {
    private TransportFacade mockFacade;
    private TransportController controller;

    @BeforeEach
    void setUp() {
        mockFacade = mock(TransportFacade.class);
        controller = new TransportController();
        // Since controller creates its own facade, we need to inject or test via public methods
        // For simplicity, test the methods directly assuming facade works
    }

    @Test
    void testHandleAuditFreight() {
        // Since controller creates facade internally, test by calling method
        // In real test, would need to mock or use integration test
        // For now, just check that method exists and returns something
        TransportController testController = new TransportController();
        FreightAudit audit = testController.handleAuditFreight("A001", "S001", 100.0, 100.0);
        assertNotNull(audit);
        assertEquals("NONE", audit.getDiscrepancyFlag());
    }

    @Test
    void testHandlePlanConstraints() {
        TransportController testController = new TransportController();
        ConstraintPlanner planner = testController.handlePlanConstraints("P001", "S001", 5000.0, 10.0, 8, "9AM-5PM");
        assertNotNull(planner);
        assertEquals("P001", planner.getPlanId());
    }

    @Test
    void testHandleManageTerritory() {
        TransportController testController = new TransportController();
        Territory territory = testController.handleManageTerritory("T001", "Northeast", "US Northeast", 5);
        assertNotNull(territory);
        assertEquals("T001", territory.getTerritoryId());
    }

    @Test
    void testHandleOrchestrateOrder() {
        TransportController testController = new TransportController();
        OrderOrchestrator orchestrator = testController.handleOrchestrateOrder("O001", "SO001", true, "SUP001");
        assertNotNull(orchestrator);
        assertTrue(orchestrator.isThirdPartyItem());
    }

    @Test
    void testHandleIntegrateSupplierPortal() {
        TransportController testController = new TransportController();
        SupplierPortal portal = testController.handleIntegrateSupplierPortal("P001", "SUP001", "Order details");
        assertNotNull(portal);
        assertEquals("P001", portal.getPortalId());
    }

    @Test
    void testHandleSyncTracking() {
        TransportController testController = new TransportController();
        TrackingSync sync = testController.handleSyncTracking("S001", "O001", "TRK123");
        assertNotNull(sync);
        assertEquals("S001", sync.getSyncId());
    }

    @Test
    void testHandleReverseLogistics() {
        TransportController testController = new TransportController();
        ReverseLogistics logistics = testController.handleReverseLogistics("R001", "O001", "SUP001", 50.0);
        assertNotNull(logistics);
        assertEquals("R001", logistics.getReturnId());
    }
}