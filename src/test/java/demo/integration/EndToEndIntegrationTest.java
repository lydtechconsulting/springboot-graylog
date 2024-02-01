package demo.integration;

import demo.DemoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { DemoConfiguration.class } )
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class EndToEndIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetSuccess() {
        ResponseEntity<Void> response = restTemplate.getForEntity("/v1/demo/success", Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void testGetError() {
        ResponseEntity<Void> response = restTemplate.getForEntity("/v1/demo/error", Void.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }
}
