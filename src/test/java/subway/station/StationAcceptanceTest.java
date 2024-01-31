package subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import subway.testhelper.StationApiCaller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {

    private static final String GANGNAM_STATION = "강남역";
    private static final String SAMSUNG_STATION = "삼성역";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", GANGNAM_STATION);

        // then
        ExtractableResponse<Response> response = StationApiCaller.지하철_역_생성(params);

        // then
        List<String> actual = StationApiCaller.지하철_역들_조회().jsonPath().getList("name", String.class);
        String expected = GANGNAM_STATION;
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
        Map<String, String> params = new HashMap<>();
        params.put("name", GANGNAM_STATION);
        StationApiCaller.지하철_역_생성(params);

        params.put("name", SAMSUNG_STATION);
        StationApiCaller.지하철_역_생성(params);

        // when
        List<String> actual = StationApiCaller.지하철_역들_조회().jsonPath().getList("name", String.class);

        // then
        List<String> expected = List.of(GANGNAM_STATION, SAMSUNG_STATION);
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
        Map<String, String> params = new HashMap<>();
        params.put("name", GANGNAM_STATION);
        ExtractableResponse<Response> stationResponse = StationApiCaller.지하철_역_생성(params);
        String location = stationResponse.header("Location");

        // when
        given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .extract();

        // then
        List<String> actual = StationApiCaller.지하철_역들_조회().jsonPath().getList("name", String.class);
        List<String> expected = Collections.emptyList();
        assertThat(actual).containsAll(expected);
    }

}
