package subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import subway.testhelper.JsonPathHelper;
import subway.testhelper.StationApiCaller;
import subway.testhelper.StationFixture;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DirtiesContext
@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {
    private static final String 강남역 = "강남역";
    private static final String 삼성역 = "삼성역";
    private StationFixture stationFixture;

    @BeforeEach
    void setUp() {
        stationFixture = new StationFixture();
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        // then
        ExtractableResponse<Response> response = StationApiCaller.지하철_역_생성(stationFixture.get강남역_params());

        // then
        List<String> actual = JsonPathHelper.getAll(StationApiCaller.지하철_역들_조회(), "name", String.class);
        String expected = 강남역;
        assertThat(actual).containsAnyOf(expected);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역들의 목록을 조회한다.")
    @Test
    void findStations() {
        // given
        StationApiCaller.지하철_역_생성(stationFixture.get강남역_params());
        StationApiCaller.지하철_역_생성(stationFixture.get삼성역_params());

        // when
        List<String> actual = JsonPathHelper.getAll(StationApiCaller.지하철_역들_조회(), "name", String.class);

        // then
        List<String> expected = List.of(강남역, 삼성역);
        assertThat(actual).containsAll(expected);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> stationResponse = StationApiCaller.지하철_역_생성(stationFixture.get강남역_params());
        String location = stationResponse.header("Location");

        // when
        StationApiCaller.지하철_역_삭제(location);

        // then
        List<String> actual = JsonPathHelper.getAll(StationApiCaller.지하철_역들_조회(), "name", String.class);
        List<String> expected = Collections.emptyList();
        assertThat(actual).containsAll(expected);
    }

}
