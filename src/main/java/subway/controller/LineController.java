package subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import subway.dto.request.LineCreateRequest;
import subway.dto.response.LineResponse;
import subway.dto.request.LineUpdateRequest;
import subway.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineResponse line = lineService.saveLine(lineCreateRequest);
        return ResponseEntity.created(URI.create("/line/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines(){
        return ResponseEntity.ok().body(lineService.findAllLineResponse());
    }

    @PutMapping
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineUpdateRequest lineUpdateRequest){

        LineResponse line = lineService.updateLine(lineUpdateRequest);
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id){
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

}
