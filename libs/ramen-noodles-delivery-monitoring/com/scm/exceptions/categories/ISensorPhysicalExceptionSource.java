package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface ISensorPhysicalExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void fireSafetyAlert(int exceptionId, String vehicleOrAssetId, double latitude, double longitude, String detail);
    void fireDeviceWarning(int exceptionId, String deviceId, String deviceType, String condition);
    void fireScanError(int exceptionId, String scannerLocation, String tagOrBarcode, String reason);
}