package behavioral.iterator;

import entities.Shipment;
import repositories.TransportRepository;
import java.util.Iterator;
import java.util.List;

/**
 * Iterator: ShipmentIterator
 * BEHAVIORAL PATTERN: Iterator Pattern
 * Provides way to iterate through shipments without exposing internal structure
 * SOLID: SRP - Single responsibility (iteration)
 * 
 * Benefits:
 * - Decouples iteration from collection structure
 * - Provides uniform iteration interface
 * - Allows multiple simultaneous iterations
 */
public class ShipmentIterator implements Iterator<Shipment> {
    private List<Shipment> shipments;
    private int index = 0;

    public ShipmentIterator(TransportRepository repository) {
        this.shipments = repository.getAllShipments();
    }

    @Override
    public boolean hasNext() {
        return index < shipments.size();
    }

    @Override
    public Shipment next() {
        if (!hasNext()) {
            throw new IndexOutOfBoundsException("No more shipments");
        }
        return shipments.get(index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }

    /**
     * Get current position in iteration
     */
    public int getIndex() {
        return index;
    }

    /**
     * Reset iterator to beginning
     */
    public void reset() {
        index = 0;
    }

    /**
     * Get total count
     */
    public int getTotalCount() {
        return shipments.size();
    }
}
