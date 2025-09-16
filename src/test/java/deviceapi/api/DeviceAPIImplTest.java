package deviceapi.api;

import deviceapi.exception.DeviceNotFoundException;
import deviceapi.message.APIMessage;
import deviceapi.model.Device;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class DeviceAPIImplTest {
    private DeviceAPI deviceAPI;

    @BeforeEach
    void setUp() {
        deviceAPI = new DeviceAPIImpl();
    }

    @Test
    void testRegisterAndRetrieveDevice() {
        deviceAPI.registerDevice(Device.DeviceType.GATEWAY, "G1", null);
        Device retrievedDevice = deviceAPI.getDeviceByMacAddress("G1");
        assertNotNull(retrievedDevice);
        assertEquals(Device.DeviceType.GATEWAY, retrievedDevice.getDeviceType());
        assertEquals("G1", retrievedDevice.getMacAddress());
    }

    @Test
    void testGetAllRegisteredDevicesSortedByType() {
        deviceAPI.registerDevice(Device.DeviceType.ACCESS_POINT, "AP1", "S1");
        deviceAPI.registerDevice(Device.DeviceType.GATEWAY, "G1", null);
        deviceAPI.registerDevice(Device.DeviceType.SWITCH, "S1", "G1");

        List<Device> sortedDevices = deviceAPI.getAllRegisteredDevicesSortedByType();
        assertEquals(3, sortedDevices.size());
        assertEquals(Device.DeviceType.GATEWAY, sortedDevices.get(0).getDeviceType());
        assertEquals(Device.DeviceType.SWITCH, sortedDevices.get(1).getDeviceType());
        assertEquals(Device.DeviceType.ACCESS_POINT, sortedDevices.get(2).getDeviceType());
    }

    @Test
    void testGetAllNetworkDeviceTopology() {
        deviceAPI.registerDevice(Device.DeviceType.GATEWAY, "G1", null);
        deviceAPI.registerDevice(Device.DeviceType.SWITCH, "S1", "G1");
        deviceAPI.registerDevice(Device.DeviceType.ACCESS_POINT, "AP1", "S1");
        deviceAPI.registerDevice(Device.DeviceType.ACCESS_POINT, "AP2", "S1");

        Map<String, List<String>> topology = deviceAPI.getAllNetworkDeviceTopology();
        assertTrue(topology.containsKey("G1"));
        assertTrue(topology.get("G1").contains("S1"));
        assertTrue(topology.containsKey("S1"));
        assertTrue(topology.get("S1").contains("AP1"));
        assertTrue(topology.get("S1").contains("AP2"));
    }

    @Test
    void testGetNetworkDeviceTopologyFromSpecificDevice() {
        deviceAPI.registerDevice(Device.DeviceType.GATEWAY, "G1", null);
        deviceAPI.registerDevice(Device.DeviceType.SWITCH, "S1", "G1");
        deviceAPI.registerDevice(Device.DeviceType.ACCESS_POINT, "AP1", "S1");
        deviceAPI.registerDevice(Device.DeviceType.ACCESS_POINT, "AP2", "S1");
        deviceAPI.registerDevice(Device.DeviceType.SWITCH, "S2", "G1");

        Map<String, List<String>> subTopology = deviceAPI.getNetworkDeviceTopologyFromDevice("S1");
        assertFalse(subTopology.containsKey("G1"));
        assertTrue(subTopology.containsKey("S1"));
        assertTrue(subTopology.get("S1").contains("AP1"));
        assertTrue(subTopology.get("S1").contains("AP2"));
        assertFalse(subTopology.containsKey("S2"));
    }

    @Test
    void testDeleteDevice() {
        deviceAPI.registerDevice(Device.DeviceType.GATEWAY, "G1", null);
        APIMessage message = deviceAPI.deleteDevice("G1");
        assertTrue(message.getMessage().contains("Device with MAC G1 successfully deleted."));
        DeviceNotFoundException deviceNotFoundException = assertThrowsExactly(DeviceNotFoundException.class,
                () -> {
                    Device retrievedDevice = deviceAPI.getDeviceByMacAddress("G1");
                });
        assertTrue(deviceNotFoundException.getMessage().contains("Device with MAC G1 not found"));
    }

    @Test
    void testUpdateDevice() {
        deviceAPI.registerDevice(Device.DeviceType.SWITCH, "S1", null);
        deviceAPI.registerDevice(Device.DeviceType.GATEWAY, "G1", "S1");
        deviceAPI.registerDevice(Device.DeviceType.SWITCH, "S2", null);
        Device device = new Device(Device.DeviceType.GATEWAY, "G1", "S2");
        deviceAPI.updateDevice(device);
        Device retrievedDevice = deviceAPI.getDeviceByMacAddress("G1");
        assertNotNull(retrievedDevice);
        assertEquals("S2", retrievedDevice.getUplinkMacAddress());
        Map<String, List<String>> subTopology = deviceAPI.getNetworkDeviceTopologyFromDevice("S1");
        assertTrue(subTopology.get("S1").isEmpty());
        subTopology = deviceAPI.getNetworkDeviceTopologyFromDevice("S2");
        assertTrue(subTopology.get("S2").contains("G1"));
    }
}