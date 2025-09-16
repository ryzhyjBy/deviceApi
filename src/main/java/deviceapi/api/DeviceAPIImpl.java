package deviceapi.api;

import deviceapi.exception.DeviceAlreadyExistException;
import deviceapi.exception.DeviceNotFoundException;
import deviceapi.message.APIMessage;
import deviceapi.model.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeviceAPIImpl implements DeviceAPI {
    private final Map<String, Device> devices = new HashMap<>();
    private final Map<String, List<String>> topology = new HashMap<>();


    /**
     * @param deviceType       Type of device. See reference deviceapi.model.Device.DeviceType
     * @param macAddress       String representing device MAC address
     * @param uplinkMacAddress String representing uplink device MAC address
     * @return Created and stored in device and topology maps Device
     */
    @Override
    public Device registerDevice(Device.DeviceType deviceType, String macAddress, String uplinkMacAddress) {
        if (ObjectUtils.isEmpty(deviceType)) {
            log.info("deviceType is not provided");
            throw new HttpMessageNotReadableException("deviceType is required");
        }
        if (devices.containsKey(macAddress)) {
            log.info("Device with MAC {} is already registered.", macAddress);
            throw new DeviceAlreadyExistException("Device with MAC " + macAddress
                    + " is already registered. Please use update or delete methods instead.");
        }
        Device newDevice = new Device(deviceType, macAddress, uplinkMacAddress);
        devices.put(macAddress, newDevice);
        if (uplinkMacAddress != null) {
            topology.computeIfAbsent(uplinkMacAddress, k -> new ArrayList<>()).add(macAddress);
        } else {
            topology.putIfAbsent(macAddress, new ArrayList<>()); // root
        }
        log.info("Registered {}.", newDevice.toString());
        return newDevice;
    }

    /**
     * @param macAddress String representing device MAC address
     * @return Message about deleting result
     */
    @Override
    public APIMessage deleteDevice(String macAddress) {
        if (!devices.containsKey(macAddress)) {
            log.info("Device with MAC " + macAddress + " not found");
            throw new DeviceNotFoundException("Device with MAC " + macAddress + " not found");
        }
        devices.remove(macAddress);
        for (List<String> leafs : topology.values()) {
            leafs.remove(macAddress);
        }
        log.info("Device with MAC {} successfully deleted.", macAddress);
        return new APIMessage(HttpStatus.OK.value(), "Device with MAC "
                + macAddress + " successfully deleted.");
    }

    /**
     * Trying to update device. MAC address is required.
     * If device type is not set that it will be not updated.
     * If uplink MAC is not set it will be set to null.
     *
     * @param device Device that should be updated
     * @return updated Device
     */
    @Override
    public Device updateDevice(Device device) {
        if (!devices.containsKey(device.getMacAddress())) {
            log.info("Device with MAC " + device.getMacAddress() + " not found");
            throw new DeviceNotFoundException("Device with MAC " + device.getMacAddress() + " not found");
        }
        log.info("Old {}", devices.get(device.getMacAddress()));
        if (device.getUplinkMacAddress() != devices.get(device.getMacAddress()).getUplinkMacAddress()) {
            for (List<String> leafs : topology.values()) {
                leafs.remove(device.getMacAddress());
            }
        }
        if (device.getDeviceType() == null) {
            device.setDeviceType(devices.get(device.getMacAddress()).getDeviceType());
        }
        devices.put(device.getMacAddress(), device);
        if (device.getUplinkMacAddress() != null) {
            topology.computeIfAbsent(device.getUplinkMacAddress(), k -> new ArrayList<>()).add(device.getMacAddress());
        } else {
            topology.putIfAbsent(device.getMacAddress(), new ArrayList<>()); // root
        }

        log.info("Updated {}", device);

        return device;
    }

    /**
     * @return List of devices sorted by device type. Order: Gateway > Switch > Access Point.
     */
    @Override
    public List<Device> getAllRegisteredDevicesSortedByType() {
        return devices.values().stream()
                .sorted(Comparator.comparing(Device::getDeviceType, (d1, d2) -> {
                    // Gateway > Switch > Access Point
                    if (d1 == Device.DeviceType.GATEWAY) return -1;
                    if (d2 == Device.DeviceType.GATEWAY) return 1;
                    if (d1 == Device.DeviceType.SWITCH) return -1;
                    if (d2 == Device.DeviceType.SWITCH) return 1;
                    return 0;
                }))
                .collect(Collectors.toList());
    }

    /**
     * @param macAddress String representing device MAC address
     * @return Device if it exists.
     */
    @Override
    public Device getDeviceByMacAddress(String macAddress) {
        if (devices.containsKey(macAddress)) {
            Device device = devices.get(macAddress);
            log.info("Received {}", device.toString());
            return device;
        } else {
            log.info("Device with MAC " + macAddress + " not found");
            throw new DeviceNotFoundException("Device with MAC " + macAddress + " not found");
        }
    }

    /**
     * @return Network topology
     */
    @Override
    public Map<String, List<String>> getAllNetworkDeviceTopology() {
        return Collections.unmodifiableMap(topology);
    }

    /**
     * @param macAddress String representing device MAC address where start to build topology from.
     * @return Network topology from requested device
     */
    @Override
    public Map<String, List<String>> getNetworkDeviceTopologyFromDevice(String macAddress) {
        if (!devices.containsKey(macAddress)) {
            log.info("Device with MAC " + macAddress + " not found");
            throw new DeviceNotFoundException("Device with MAC " + macAddress + " not found");
        }
        Map<String, List<String>> subTopology = new HashMap<>();
        buildSubTopology(macAddress, subTopology);
        return subTopology;
    }

    /**
     * Util class for recursive building of topology
     *
     * @param currentMac  String representing root device MAC address of subtree
     * @param subTopology resulting topology map
     */
    private void buildSubTopology(String currentMac, Map<String, List<String>> subTopology) {
        if (topology.containsKey(currentMac)) {
            List<String> children = topology.get(currentMac);
            subTopology.put(currentMac, new ArrayList<>(children));
            for (String childMac : children) {
                buildSubTopology(childMac, subTopology);
            }
        } else {
            subTopology.putIfAbsent(currentMac, new ArrayList<>()); // leaf
        }
    }
}