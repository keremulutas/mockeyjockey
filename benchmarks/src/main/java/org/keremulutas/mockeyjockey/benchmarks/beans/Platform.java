package org.keremulutas.mockeyjockey.benchmarks.beans;

import java.util.Objects;

public class Platform {

    public String platform;
    public String deviceId;
    public String ipAddress;

    public Platform() {

    }

    public Platform(String platform, String deviceId, String ipAddress) {
        this.platform = platform;
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
    }

    public Platform(Platform source) {
        this.platform = source.platform;
        this.deviceId = source.deviceId;
        this.ipAddress = source.ipAddress;
    }

    public Platform clone() {
        return new Platform(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!( o instanceof Platform )) {
            return false;
        }
        Platform platform1 = (Platform) o;
        return Objects.equals(platform, platform1.platform) &&
            Objects.equals(deviceId, platform1.deviceId) &&
            Objects.equals(ipAddress, platform1.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, deviceId, ipAddress);
    }

    @Override
    public String toString() {
        return "Platform{" +
            "platform='" + platform + '\'' +
            ", deviceId='" + deviceId + '\'' +
            ", ipAddress='" + ipAddress + '\'' +
            '}';
    }

}
