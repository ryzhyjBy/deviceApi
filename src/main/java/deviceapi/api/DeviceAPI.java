package deviceapi.api;

import deviceapi.message.APIMessage;
import deviceapi.model.Device;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface DeviceAPI {
    Device registerDevice(Device.DeviceType deviceType, String macAddress, String uplinkMacAddress);

    List<Device> getAllRegisteredDevicesSortedByType();

    Device getDeviceByMacAddress(String macAddress);

    Map<String, List<String>> getAllNetworkDeviceTopology();

    Map<String, List<String>> getNetworkDeviceTopologyFromDevice(String macAddress);

    APIMessage deleteDevice(String macAddress);

    Device updateDevice(Device device);
}