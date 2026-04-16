package exceptions;

/**
 * Sensor Physical Exception Source
 */
public interface ISensorPhysicalExceptionSource {
    void fireSafetyAlert(int exceptionId, String vehicleOrAssetId, double latitude, double longitude, String detail);
    void fireDeviceWarning(int exceptionId, String deviceId, String deviceType, String condition);
    void fireScanError(int exceptionId, String scannerLocation, String tagOrBarcode, String reason);
}
