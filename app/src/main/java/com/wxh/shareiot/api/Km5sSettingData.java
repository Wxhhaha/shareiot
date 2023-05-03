package com.wxh.shareiot.api;

import com.wxh.shareiot.ble.SettingValue;

public class Km5sSettingData {
    private ControllerAgreement controllerAgreement;

    public ControllerAgreement getControllerAgreement() {
        return controllerAgreement;
    }

    public void setControllerAgreement(ControllerAgreement controllerAgreement) {
        this.controllerAgreement = controllerAgreement;
    }

    public class ControllerAgreement {
        private String ebikeId;
        private SettingValue data;

        public String getEbikeId() {
            return ebikeId;
        }

        public void setEbikeId(String ebikeId) {
            this.ebikeId = ebikeId;
        }

        public SettingValue getData() {
            return data;
        }

        public void setData(SettingValue data) {
            this.data = data;
        }
    }
}
