package subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
@Sql({"/test-sql/table-truncate.sql", "/test-sql/station-insert.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

    private Map<String, String> newBunDangLineParams;
    private Map<String, String> zeroLineParams;

    @BeforeEach
    void setUpClass() {
        newBunDangLineParams = new HashMap<>();
        newBunDangLineParams.put("name", "신분당선");
        newBunDangLineParams.put("color", "bg-red-600");
        newBunDangLineParams.put("upStationId", "1");
        newBunDangLineParams.put("downStationId", "2");
        newBunDangLineParams.put("distance", "10");

        zeroLineParams = new HashMap<>();
        zeroLineParams.put("name", "0호선");
        zeroLineParams.put("color", "bg-red-100");
        zeroLineParams.put("upStationId", "1");
        zeroLineParams.put("downStationId", "3");
        zeroLineParams.put("distance", "10");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        callApiCreateLines(newBunDangLineParams);

        // then
        List<String> actual = callApiFindLines().jsonPath().getList("name", String.class);
        String expected = "신분당선";
        assertThat(actual).containsAnyOf(expected);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선들의 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        callApiCreateLines(newBunDangLineParams);
        callApiCreateLines(zeroLineParams);

        // when
        List<LineResponse> linesResponse = callApiFindLines().jsonPath().getList(".", LineResponse.class);

        // then
        int actual = linesResponse.size();
        int expected = 2;
        assertThat(actual).isEqualTo(expected);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findLine() {
        // given
        ExtractableResponse<Response> createResponse = callApiCreateLines(newBunDangLineParams);
        String location = createResponse.header("location");

        // when
        ExtractableResponse<Response> response = given().log().all()
                .when().get(location)
                .then().log().all()
                .extract();

        // then
        String actual = response.jsonPath().getObject("name", String.class);
        String expected = "신분당선";
        assertThat(actual).isEqualTo(expected);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> response = callApiCreateLines(newBunDangLineParams);
        String location = response.header("location");

        // when
        LineUpdateRequest request = new LineUpdateRequest("다른분당선", "bg-red-600");
        ExtractableResponse<Response> updateResponse = given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(location)
                .then().log().all()
                .extract();

        // then
        LineResponse actual = callApiFindLines().jsonPath().getObject(".", LineResponse.class);
        LineResponse expected = new LineResponse(actual.getId(),
                "다른분당선",
                "bg-red-600",
                List.of(new StationResponse(1L, "강남역"), new StationResponse(2L, "삼성역")));
        assertThat(actual).isEqualTo(expected);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
//    @DisplayName("지하철노선을 삭제한다.")
//    @Test
//    void deleteStation() {
//        // given
//        ExtractableResponse<Response> response = callApiCreateLines(newBunDangLineParams);
//        String location = response.header("location");
//
//        // when
//        ExtractableResponse<Response> deleteResponse = given().log().all()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when().delete(location)
//                .then().log().all()
//                .extract();
//        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
//
//        // then
//        List<LineResponse> actual = callApiFindLines().jsonPath().getList(".", LineResponse.class);
//        List<LineResponse> expected = Collections.emptyList();
//        assertThat(actual).containsAll(expected);
//    }

    private ExtractableResponse<Response> callApiCreateLines(Map<String, String> params) {
        return given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> callApiFindLines() {
        return given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }
}
