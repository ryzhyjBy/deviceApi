package deviceapi.controller;

import deviceapi.api.DeviceAPI;
import deviceapi.message.APIMessage;
import deviceapi.model.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/devices")
@Slf4j
public class DeviceController {

    @Autowired
    private final DeviceAPI deviceAPI;

    public DeviceController(DeviceAPI deviceAPI) {
        this.deviceAPI = deviceAPI;
    }

    @PostMapping("/register")
    public Device registerDevice(@Validated @RequestBody Device device) {
        log.info("Registering {}.", device.toString());
        return deviceAPI.registerDevice(device.getDeviceType(), device.getMacAddress(), device.getUplinkMacAddress());
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        log.info("Resolving all registered devices.");
        List<Device> devices = deviceAPI.getAllRegisteredDevicesSortedByType();
        log.info("Number of registered devices: {}", devices.size());
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping("/{macAddress}")
    public Device getDeviceByMacAddress(@PathVariable String macAddress) {
        log.info("Receiving device with MAC {}", macAddress);
        return deviceAPI.getDeviceByMacAddress(macAddress);
    }

    @GetMapping("/topology")
    public ResponseEntity<Map<String, List<String>>> getNetworkTopology() {
        log.info("Receiving network topology");
        Map<String, List<String>> topology = deviceAPI.getAllNetworkDeviceTopology();
        log.info("Network topology received");
        return new ResponseEntity<>(topology, HttpStatus.OK);
    }

    @GetMapping("/topology/{macAddress}")
    public ResponseEntity<Map<String, List<String>>> getDeviceTopology(@PathVariable String macAddress) {
        log.info("Receiving topology for device: {}", macAddress);
        Map<String, List<String>> topology = deviceAPI.getNetworkDeviceTopologyFromDevice(macAddress);
        log.info("Device topology received");
        return new ResponseEntity<>(topology, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{macAddress}")
    public APIMessage deleteDevice(@PathVariable String macAddress) {
        log.info("Deleting device: {}", macAddress);
        return deviceAPI.deleteDevice(macAddress);
    }

    @PostMapping("/update")
    public Device updateDevice(@Validated @RequestBody Device device) {
        log.info("Updating device: {}", device.getMacAddress());
        return deviceAPI.updateDevice(device);
    }
}
