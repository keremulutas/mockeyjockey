package org.keremulutas.mockeyjockey.core.generator;

import java.util.ArrayList;
import java.util.List;

public abstract class IpAddressGenerator extends Generator<Void, String> {

    public static long ipToLong(String ipAddress) {
        long result = 0;

        String[] ipAddressInArray = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);
        }

        return result;
    }

    public static String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
    }

    private IpAddressGenerator(java.util.Random randomizer) {
        super(randomizer);
    }

    @Override
    public Class<String> getTargetObjectClass() {
        return String.class;
    }

    public static class Sequential extends IpAddressGenerator {

        private long _currentValue = 0L;

        public Sequential(java.util.Random randomizer) {
            super(randomizer);
        }

        public IpAddressGenerator.Sequential startFrom(String ip) {
            this._currentValue = ipToLong(ip);
            return this;
        }

        public IpAddressGenerator.Sequential startFrom(long ip) {
            this._currentValue = ip;
            return this;
        }

        @Override
        protected String generate() {
            String result = longToIp(_currentValue);
            _currentValue++;
            return result;
        }

    }

    public static class Random extends IpAddressGenerator {

        private Sequential _generator;
        private int bufferSize = 1_000;
        private List<String> _currentList;

        public Random(java.util.Random randomizer) {
            super(randomizer);
            this._generator = new Sequential(_randomizer);
        }

        public IpAddressGenerator.Random startFrom(String ip) {
            this._generator = this._generator.startFrom(ip);
            return this;
        }

        public IpAddressGenerator.Random startFrom(long ip) {
            this._generator = this._generator.startFrom(ip);
            return this;
        }

        public IpAddressGenerator.Random withBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        @Override
        protected String generate() {
            if (this._currentList == null || this._currentList.size() == 0) {
                this._currentList = new ArrayList<>(this._generator.list(this.bufferSize).get());
            }

            int index = this._randomizer.nextInt(this._currentList.size());
            return this._currentList.remove(index);
        }

    }

}
