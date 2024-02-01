package demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/demo")
public class DemoController {

    @GetMapping("/success")
    public ResponseEntity<Void> success() {
        log.info("REST request received successfully");
        return ResponseEntity.accepted().build();
    }


    @GetMapping("/error")
    public ResponseEntity<Void> error() {
        log.error("REST request triggered error");
        return ResponseEntity.badRequest().build();
    }
}

