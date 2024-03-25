package hei.school.soratra.endpoint.rest.controller.soratra;

import hei.school.soratra.service.soratra.SoratraService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class SoratraController {
    SoratraService soratraService;

    @PutMapping("/soratra/{id}")
    public ResponseEntity<SoratraData> saveById(@PathVariable int id, @RequestBody String poeticSentence) throws IOException {
        return ResponseEntity.ok(soratraService.saveById(id, poeticSentence));
    }

    @GetMapping("/soratra/{id}")
    public ResponseEntity<SoratraData> findById(@PathVariable int id) {
        return null; // ResponseEntity.ok(soratraService.findById(id));
    }
}
