package deviceapi.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    public enum DeviceType {
        GATEWAY,
        SWITCH,
        ACCESS_POINT
    }

    private DeviceType deviceType;
    @Valid
    @NotNull(message = "macAddress is required")
    @NotEmpty(message = "macAddress cannot be empty")
    private String macAddress;
    private String uplinkMacAddress; // Can be null

    @Override
    public String toString() {
        return "deviceapi.model.Device [type=" + deviceType + ", mac=" + macAddress + ", uplink=" + uplinkMacAddress + "]";
    }
}
