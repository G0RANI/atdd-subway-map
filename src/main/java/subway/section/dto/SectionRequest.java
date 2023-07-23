package subway.section.dto;

import subway.section.domain.Section;

public class SectionRequest {
    private Long downStationId;
    private Long upStationId;
    private Integer distance;

    public static class StationIdParams{
        private final Long stationId;

        public StationIdParams(Long stationId) {
            this.stationId = stationId;
        }

        public Long getStationId() {
            return stationId;
        }
    }

    public SectionRequest(Long downStationId, Long upStationId, Integer distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
