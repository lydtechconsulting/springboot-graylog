package demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DemoControllerTest {

    private DemoController controller = new DemoController();

    @Test
    public void testGetItem_Success() {
        ResponseEntity<Void> response = controller.success();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void testGetItem_Error() {
        ResponseEntity<Void> response = controller.error();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }
}
