package subway.line.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.util.StringUtils;
import subway.common.exception.CustomException;
import subway.common.exception.ErrorCode;
import subway.section.domain.Section;
import subway.section.domain.SectionList;
import subway.section.domain.SectionStations;
import subway.station.domain.Station;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    private Integer distance;

    @Embedded
    private LineLastStations lastStations;

    @Embedded
    private SectionList sections;

    protected Line() {}

    public Line(String name, String color, LineLastStations lastStations, Integer distance) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(color)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        this.name = name;
        this.color = color;
        this.lastStations = lastStations;
        this.sections = new SectionList();
        this.distance = 0;

        this.addBaseSection(distance);
    }

    public void updateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void updateColor(String color) {
        if (!StringUtils.hasText(color)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    private void addBaseSection(Integer distance) {
        if (!sections.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        SectionStations stations = SectionStations.createLineBaseSection(lastStations);
        Section section = new Section(this, stations, distance);
        sections.addSection(section);
        this.distance += distance;
    }

    public LineLastStations getLastStations() {
        return lastStations;
    }

    public void addSection(Section section) {
        if (!lastStations.isLastDownwardIsSameWithSectionUpwardStation(section.getStations())) {
            throw new CustomException(ErrorCode.ONLY_DOWNWARD_CAN_BE_ADDED_TO_LINE);
        }

        sections.addSection(section);
        lastStations.updateDownLastStation(section.getDownwardStation());
        this.distance += section.getDistance();
    }

    public void deleteStation(Station targetStation) {

        if (!lastStations.isLastDownwardStation(targetStation)) {
            throw new CustomException(ErrorCode.CAN_NOT_REMOVE_STATION);
        }

        sections.removeSection(lastStations, targetStation);
    }
}
