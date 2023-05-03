package com.wxh.shareiot.api;

import java.util.List;

public class RegionFenceResponse {

    private List<RegionFencePointsDTO> regionFencePoints;

    public List<RegionFencePointsDTO> getRegionFencePoints() {
        return regionFencePoints;
    }

    public void setRegionFencePoints(List<RegionFencePointsDTO> regionFencePoints) {
        this.regionFencePoints = regionFencePoints;
    }

    public static class RegionFencePointsDTO {
        private String fenceName;
        private Integer fenceType;
        private String regionId;
        private List<RegionFencePointListDTO> regionFencePointList;
        private Integer drawType;
        private Object remark;
        private String fenceId;

        public String getFenceName() {
            return fenceName;
        }

        public void setFenceName(String fenceName) {
            this.fenceName = fenceName;
        }

        public Integer getFenceType() {
            return fenceType;
        }

        public void setFenceType(Integer fenceType) {
            this.fenceType = fenceType;
        }

        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

        public List<RegionFencePointListDTO> getRegionFencePointList() {
            return regionFencePointList;
        }

        public void setRegionFencePointList(List<RegionFencePointListDTO> regionFencePointList) {
            this.regionFencePointList = regionFencePointList;
        }

        public Integer getDrawType() {
            return drawType;
        }

        public void setDrawType(Integer drawType) {
            this.drawType = drawType;
        }

        public Object getRemark() {
            return remark;
        }

        public void setRemark(Object remark) {
            this.remark = remark;
        }

        public String getFenceId() {
            return fenceId;
        }

        public void setFenceId(String fenceId) {
            this.fenceId = fenceId;
        }

        public static class RegionFencePointListDTO {
            private String pointId;
            private String lng;
            private String fenceId;
            private String lat;
            private int serialNo;

            public String getPointId() {
                return pointId;
            }

            public void setPointId(String pointId) {
                this.pointId = pointId;
            }

            public String getLng() {
                return lng;
            }

            public void setLng(String lng) {
                this.lng = lng;
            }

            public String getFenceId() {
                return fenceId;
            }

            public void setFenceId(String fenceId) {
                this.fenceId = fenceId;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public int getSerialNo() {
                return serialNo;
            }

            public void setSerialNo(int serialNo) {
                this.serialNo = serialNo;
            }
        }
    }
}
