package subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.LineAcceptanceTest.노선_만들기;
import static subway.LineAcceptanceTest.아이디_노선_조회;
import static subway.StationAcceptanceTest.역_만들기;

@DisplayName("지하철 구간 관련 기능")
@Sql("/teardown.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {
    public static final String 신분당선 = "신분당선";
    public static final String 분당선 = "분당선";
    public static final String 경인선 = "경인선";

    public static final String BG_YELLOW_600 = "BG_YELLOW_600";
    public static final String BG_RED_600 = "bg-red-600";
    public static final String BG_GREEN_600 = "BG_GREEN_600";

    public static final String 거리 = "10";
    public static final String 첫째지하철역이름 = "첫째지하철역";
    public static final String 세번째지하철역이름 = "세번째지하철역";
    public static final String 두번째지하철역이름 = "두번째지하철역";
    public static final String 네번째지하철역이름 = "네번째지하철역";
    private static String 첫째지하철역_아이디;
    private static String 두번째지하철역_아이디;
    private static String 세번째지하철역_아이디;
    private static String 네번째지하철역_아이디;
    private static String 첫째노선_아이디;
    private static String 둘째노선_아이디;
    @LocalServerPort
    int port;
    private ExtractableResponse<Response> 첫째지하철역;
    private ExtractableResponse<Response> 두번째지하철역;
    private ExtractableResponse<Response> 세번째지하철역;
    private ExtractableResponse<Response> 네번째지하철역;
    private ExtractableResponse<Response> 첫번째노선;
    private ExtractableResponse<Response> 두번째노선;

    private static ExtractableResponse<Response> 구간_추가(String lineId, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post(String.format("/lines/%s/sections", lineId))
                        .then().log().all()
                        .extract();
        return response;
    }

    private static ExtractableResponse<Response> 구간_제거(String lineId, String stationId) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete("/lines/{lindId}/sections?stationId={stationId}", lineId, stationId)
                        .then().log().all()
                        .extract();
        return response;
    }

    @BeforeEach
    public void setUpPort() {
        RestAssured.port = port;
        네개의_역_생성();

    }

    public void 네개의_역_생성() {
        첫째지하철역 = 역_만들기(첫째지하철역이름);
        두번째지하철역 = 역_만들기(두번째지하철역이름);
        세번째지하철역 = 역_만들기(세번째지하철역이름);
        네번째지하철역 = 역_만들기(네번째지하철역이름);

        첫째지하철역_아이디 = 첫째지하철역.jsonPath().getString("id");
        두번째지하철역_아이디 = 두번째지하철역.jsonPath().getString("id");
        세번째지하철역_아이디 = 세번째지하철역.jsonPath().getString("id");
        네번째지하철역_아이디 = 세번째지하철역.jsonPath().getString("id");
    }

    /**
     * Given 1개의 지하철 노선을 생성하고
     * When 2개의 중복되지 않은 지하철 구간을 등록하면 (등록되는 노선은 하행 종점역이다.)
     * Then 지하철 노선 목록 조회 시 추가된 구간의 하행역이 노선에 추가되어있다.
     */
    @DisplayName("1개의 구간을 노선에 추가한다.")
    @Test
    void addSectionToLineByRule() {
        // GIVEN
        첫번째노선 = 노선_만들기(신분당선, BG_RED_600, 두번째지하철역_아이디, 첫째지하철역_아이디, 거리);
        첫째노선_아이디 = 첫번째노선.jsonPath().getString("id");

        // WHEN
        ExtractableResponse<Response> response = 구간_추가(첫째노선_아이디, 첫째지하철역_아이디, 세번째지하철역_아이디, "10");

        // THEN
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // THEN
        ExtractableResponse<Response> getLineResponse = 아이디_노선_조회(첫째노선_아이디);
        List<String> stations = getLineResponse.jsonPath().getList("stations.name");
        assertThat(stations).containsExactly(두번째지하철역이름, 첫째지하철역이름, 세번째지하철역이름);
    }

    /**
     * Given 0개의 지하철 노선을 생성하고
     * When 지하철 구간을 추가할때
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("노선에 대한 구간을 생성 할때, 구간이 존재 하지 않을 경우")
    @Test
    void addSectionFromNotExistingLineThrowNoSuchElementError() {
        // GIVEN

        // WHEN
        ExtractableResponse<Response> response = 구간_추가("1", 네번째지하철역_아이디, 두번째지하철역_아이디, "10");

        // THEN
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * Given 1개의 지하철 노선을 생성하고 1개의 중복되지 않은 지하철 구간을 등록하고
     * When 지하철 구간 중 등록되어있는 하행역을 등록한다.
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("1개의 구간을 등록하고 등록되어있는 역을 등록한다.")
    @Test
    void addTwoSectionToLineAgainstRule() {
        // GIVEN
        첫번째노선 = 노선_만들기(신분당선, BG_RED_600, 두번째지하철역_아이디, 첫째지하철역_아이디, 거리);
        첫째노선_아이디 = 첫번째노선.jsonPath().getString("id");

        // WHEN
        ExtractableResponse<Response> response = 구간_추가(첫째노선_아이디, 첫째지하철역_아이디, 두번째지하철역_아이디, "10");

        // THEN
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * Given 1개의 지하철 노선을 생성하고
     * When 지하철 구간을 등록할때 등록 구간의 상행역이 하행 종점역이 아닐때를 등록한다
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("1개의 구간을 등록하고 하행 종점역이 아닌 노선을 등록한다. ")
    @Test
    void addSectionToLineThrowAlreadyRegisteredError() {
        // GIVEN
        첫번째노선 = 노선_만들기(신분당선, BG_RED_600, 두번째지하철역_아이디, 첫째지하철역_아이디, 거리);
        첫째노선_아이디 = 첫번째노선.jsonPath().getString("id");
        // WHEN
        ExtractableResponse<Response> response = 구간_추가(첫째노선_아이디, 세번째지하철역_아이디, 네번째지하철역_아이디, "10");
        // THEN
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * Given 1개의 지하철 노선을 생성하고 1개의 구간을 추가했을 때,
     * When 하행 노선을 제거한다.
     * Then 지하철 하행역이 전 구간의 하행역으로 변화한다.
     * Then 지하철 역이 3개에서 2개로 변화된다.
     */
    @DisplayName("하행 구간을 제거하면 구간의 역 리스트가 변경된다.")
    @Test
    void deleteSectionFromLine() {
        // GIVEN
        첫번째노선 = 노선_만들기(신분당선, BG_RED_600, 두번째지하철역_아이디, 첫째지하철역_아이디, 거리);
        첫째노선_아이디 = 첫번째노선.jsonPath().getString("id");

        // WHEN
        구간_추가(첫째노선_아이디, 첫째지하철역_아이디, 네번째지하철역_아이디, "10");
        구간_제거(첫째노선_아이디, 네번째지하철역_아이디);

        // THEN
        ExtractableResponse<Response> response = 아이디_노선_조회(첫째노선_아이디);
        assertThat(response.jsonPath().getList("stations.name")).contains(첫째지하철역이름, 두번째지하철역이름);

    }

    /**
     * Given 1개의 지하철 노선을 생성하고
     * When 지하철 구간을 제거할때 제거 구간이 하행 종점역이 아닐 때를 등록한다
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("구간을 제거할때 제거 구간이 하행 종점역이 아닐때 에러를 표기한다.")
    @Test
    void deleteSectionFromLineAgainstRule() {
        // GIVEN
        첫번째노선 = 노선_만들기(신분당선, BG_RED_600, 두번째지하철역_아이디, 첫째지하철역_아이디, 거리);
        첫째노선_아이디 = 첫번째노선.jsonPath().getString("id");
        구간_추가(첫째노선_아이디, 첫째지하철역_아이디, 세번째지하철역_아이디, "10");
        // WHEN
        ExtractableResponse<Response> response = 구간_제거(첫째노선_아이디, 첫째지하철역_아이디);

        // THEN
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * Given 1개의 지하철 노선을 생성하고
     * When 지하철 구간을 제거할때 제거 구간이 하나만 있을 때를 등록한다
     * Then 구간 등록에 실패한다.
     * +
     */
    @DisplayName("구간을 제거 할때, 제거 구간이 하나만 있을때 에러를 표기한다")
    @Test
    void deleteSectionFromLineThrowOnlyElementError() {
        // GIVEN
        첫번째노선 = 노선_만들기(신분당선, BG_RED_600, 두번째지하철역_아이디, 첫째지하철역_아이디, 거리);
        첫째노선_아이디 = 첫번째노선.jsonPath().getString("id");
        // WHEN
        ExtractableResponse<Response> response = 구간_제거(첫째노선_아이디, 첫째지하철역_아이디);

        // THEN
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * Given 0개의 지하철 노선을 생성하고
     * When 지하철 구간을 제거할때
     * Then 구간 제거에 실패한다.
     */
    @DisplayName("노선에 대한 구간을 제거 할때, 구간이 존재 하지 않을 경우")
    @Test
    void deleteSectionFromNotExistingLineThrowNoSuchElementError() {
        // GIVEN

        // WHEN
        ExtractableResponse<Response> response = 구간_제거("1", 첫째지하철역_아이디);

        // THEN
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

}
