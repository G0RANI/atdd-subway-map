package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.subway.config.BaseEntity;
import nextstep.subway.station.domain.Station;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
@Table
public class LineStation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "station_id")
    private Station station;

    @OneToOne
    @JoinColumn(name = "pre_line_station_id")
    @Nullable
    private Station preStation;

    @Column(nullable = false)
    private Integer distance;

    @Column(nullable = false)
    private Integer duration;

    protected LineStation() {
        // no-op
    }

    public static LineStation createLineStation(Station station, @Nullable Station preStation, Integer distance, Integer duration) {
        LineStation lineStation = new LineStation();

        lineStation.station = station;
        lineStation.distance = distance;
        lineStation.duration = duration;
        lineStation.preStation = preStation;

        return lineStation;
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return this.station;
    }

    public Station getPreStation() {
        return this.preStation;
    }

    public Integer getDistance() {
        return this.distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public void changePreStation(Station newPreStation) {
        this.preStation = newPreStation;
    }
}