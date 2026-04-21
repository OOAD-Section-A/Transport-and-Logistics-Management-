package services;

import entities.*;
import interfaces.ITransportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.TransportRepository;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransportServiceTest {
    private TransportRepository mockRepository;
    private ITransportService service;

    @BeforeEach
    void setUp() {
        mockRepository = mock(TransportRepository.class);
        service = new TransportService(mockRepository);
    }

    @Test
    void testAuditFreight_NoDiscrepancy() {
        FreightAudit audit = service.auditFreight("A001", "S001", 100.0, 100.0);
        assertEquals("A001", audit.getAuditId());
        assertEquals("NONE", audit.getDiscrepancyFlag());
    }

    @Test
    void testAuditFreight_Overcharge() {
        FreightAudit audit = service.auditFreight("A001", "S001", 120.0, 100.0);
        assertEquals("OVERCHARGE", audit.getDiscrepancyFlag());
    }

    @Test
    void testPlanConstraints() {
        ConstraintPlanner planner = service.planConstraints("P001", "S001", 5000.0, 10.0, 8, "9AM-5PM");
        assertEquals("P001", planner.getPlanId());
        assertEquals(5000.0, planner.getVehicleWeightLimit());
    }

    @Test
    void testManageTerritory() {
        Territory territory = service.manageTerritory("T001", "Northeast", "US Northeast", 5);
        assertEquals("T001", territory.getTerritoryId());
        assertEquals(5, territory.getAssignedDrivers());
    }

    @Test
    void testOrchestrateOrder_ThirdParty() {
        OrderOrchestrator orchestrator = service.orchestrateOrder("O001", "SO001", true, "SUP001");
        assertTrue(orchestrator.isThirdPartyItem());
        assertEquals("PO-O001", orchestrator.getPoNumber());
    }

    @Test
    void testIntegrateSupplierPortal() {
        SupplierPortal portal = service.integrateSupplierPortal("P001", "SUP001", "Order details");
        assertEquals("P001", portal.getPortalId());
        assertEquals("ASN-P001", portal.getAsn());
    }

    @Test
    void testSyncTracking() {
        TrackingSync sync = service.syncTracking("S001", "O001", "TRK123");
        assertEquals("S001", sync.getSyncId());
        assertEquals("TRK123", sync.getTrackingNumber());
    }

    @Test
    void testHandleReverseLogistics() {
        ReverseLogistics logistics = service.handleReverseLogistics("R001", "O001", "SUP001", 50.0);
        assertEquals("R001", logistics.getReturnId());
        assertEquals(50.0, logistics.getRefundAmount());
        assertEquals("PENDING", logistics.getReconciliationStatus());
    }
    @Test
    public void testGetRiderDetails() {
        Rider_info rider = new Rider_info();
        rider.setRiderId("R001");
        when(transportRepository.getRider("R001")).thenReturn(rider);
        assertEquals("R001", transportService.getRiderDetails("R001").getRiderId());
}

    @Test
    public void testGetAvailableRiders() {
    // Mock and test filtering
}
}