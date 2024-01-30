package subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.station.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    private static final String 신분당선 = "신분당선";
    private static final String 강남역 = "강남역";
    private static final String 선릉역 = "선릉역";
    private static final String 교대역 = "교대역";
    private static final String 서초역 = "서초역";
    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line(신분당선,
                "bg-red-600",
                new Station(1L, 강남역),
                new Station(2L, 선릉역),
                10L);
    }

    @Test
    @DisplayName("생성된 라인에 구간을 더할 수 있다")
    void addSection1() {
        Section input = new Section(
                new Station(2L, 선릉역),
                new Station(3L, 교대역),
                5L);
        line.addSection(input);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(new Station(1L, 강남역),
                                new Station(2L, 선릉역),
                                10L),
                        new Section(new Station(2L, 선릉역),
                                new Station(3L, 교대역),
                                5L)));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 15L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("생성된 라인의 마지막 구간과 더하는 구간의 시작이 다르면 더할 수 없다")
    void addSection2() {
        Section input = new Section(
                new Station(3L, 교대역),
                new Station(4L, 서초역),
                5L);
        assertThrows(IllegalArgumentException.class, () -> line.addSection(input));
    }

}
