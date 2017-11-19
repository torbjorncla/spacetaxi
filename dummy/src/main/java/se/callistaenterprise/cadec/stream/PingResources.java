package se.callistaenterprise.cadec.stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingResources {
    @GetMapping("/ping")
    public ResponseEntity<Void> ping() {
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
