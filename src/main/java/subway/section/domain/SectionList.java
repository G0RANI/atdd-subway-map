package subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import subway.common.exception.CustomException;
import subway.common.exception.ErrorCode;
import subway.line.domain.LineLastStations;
import subway.station.domain.Station;

@Embeddable
public class SectionList {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections;

    public SectionList() {
        this.sections = new ArrayList<>();
    }

    public void addSection(Section newSection) {
        if (isAlreadyInStation(newSection.getDownwardStation())) {
            throw new CustomException(ErrorCode.ALREADY_IN_LINE);
        }

        this.sections.add(newSection);
    }

    public boolean isAlreadyInStation(Station downwardStation) {
        for (Section section : sections) {
            if (section.checkStationInSection(downwardStation)){
                return true;
            }
        }
        return false;
    }

    public void removeSection(LineLastStations lastStations, Station targetStation){

        if (sections.size() <= 1) {
            throw new CustomException(ErrorCode.CAN_NOT_REMOVE_STATION);
        }

        for (Section section : sections) {
            if (section.hasSameDownwardStation(targetStation)) {
                lastStations.updateDownLastStation(section.getUpwardStation());
                sections.remove(section);
                break;
            }
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }
}
