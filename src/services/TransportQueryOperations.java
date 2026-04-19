package services;

import com.scm.core.SCMException;
import com.scm.handler.SCMExceptionHandler;
import com.scm.subsystems.TransportLogisticsSubsystem;
import entities.Carrier;
import entities.Shipment;
import entities.Territory;
import repositories.TransportRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

final class TransportQueryOperations {
    private static final int DEFAULT_TIMEOUT_MS = 3000;

    private final TransportRepository repository;
    private final TransportLogisticsSubsystem exceptions;

    TransportQueryOperations(TransportRepository repository, TransportLogisticsSubsystem exceptions) {
        this.repository = repository;
        this.exceptions = exceptions;
    }

    List<Shipment> getAllShipments(String status, int page, int size) {
        List<Shipment> all = repository.getAllShipments();
        if (status != null) {
            all = all.stream().filter(s -> status.equals(s.getStatus())).collect(Collectors.toList());
        }
        if (page < 1 || size <= 0) {
            return Collections.emptyList();
        }

        int start = (page - 1) * size;
        if (start >= all.size()) {
            return Collections.emptyList();
        }

        int end = Math.min(start + size, all.size());
        return all.subList(start, end);
    }

    List<Carrier> getAllCarriers(String mode) {
        return repository.getAllCarriers(mode);
    }

    Carrier createCarrier(Carrier carrier) {
        repository.addCarrier(carrier);
        return carrier;
    }

    Map<String, Object> optimizeRoute(String origin, String destination, String constraints) {
        if (origin == null || destination == null || origin.isBlank() || destination.isBlank()) {
            exceptions.onNoViableRouteFound("UNKNOWN", "UNKNOWN");
            return Collections.emptyMap();
        }

        if (constraints != null && constraints.toLowerCase(Locale.ROOT).contains("timeout")) {
            exceptions.onCarrierApiTimeout("CARRIER_OPTIMIZER_API", DEFAULT_TIMEOUT_MS);
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("route", Arrays.asList(origin, "Intermediate Point", destination));
        result.put("estimatedTime", "4 hours");
        result.put("cost", 120.0);
        return result;
    }

    List<Territory> getAllTerritories() {
        return repository.getAllTerritories();
    }

    String reportException(SCMException exception) {
        SCMExceptionHandler.INSTANCE.handle(exception);
        return "Exception ID: " + exception.getExceptionId();
    }

    Map<String, Object> getTrackingData(String shipmentId) {
        if (shipmentId == null || shipmentId.isBlank()) {
            exceptions.onGpsSignalLost("UNKNOWN_VEHICLE");
            return Collections.emptyMap();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("currentLocation", "En route to destination");
        data.put("eta", "2026-04-20T14:00:00Z");
        data.put("status", "On time");
        return data;
    }
}
