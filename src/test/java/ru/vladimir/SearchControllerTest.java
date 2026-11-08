package ru.vladimir;

import java.util.UUID;

import io.restassured.authentication.FormAuthConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.vladimir.dto.UserRequest;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;
import ru.vladimir.model.User;
import ru.vladimir.repository.ProjectRepository;
import ru.vladimir.repository.UserRepository;
import ru.vladimir.service.UserService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User user;
    private Project project;

    @AfterEach
    void tearDown() {
        if (project != null) {
            projectRepository.deleteById(project.getId());
        }
        if (user != null) {
            userRepository.deleteById(user.getId());
        }
    }

    @Test
    void searchProjectsRejectsMissingStatus() {
        user = userService.addUser(new UserRequest(
                "api-" + UUID.randomUUID(), "Test", "User", "test-password"));

        given()
                .port(port)
                .auth().form(user.getLogin(), "test-password",
                        new FormAuthConfig("/login", "username", "password").withAdditionalField("_csrf"))
        .when()
                .get("/api/search/projects")
        .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("status", equalTo(400))
                .body("instance", equalTo("/api/search/projects"));
    }

    @Test
    void searchProjectsReturnsCreatedProjectAfterLogin() {
        String suffix = UUID.randomUUID().toString();
        user = userService.addUser(new UserRequest("api-" + suffix, "Test", "User", "test-password"));

        project = new Project();
        project.setName("API " + suffix);
        project.setStatus(ProjectStatus.NEW);
        project = projectRepository.save(project);

        given()
                .port(port)
                .auth().form(user.getLogin(), "test-password",
                        new FormAuthConfig("/login", "username", "password").withAdditionalField("_csrf"))
                .queryParam("status", "NEW")
        .when()
                .get("/api/search/projects")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", hasItem(project.getName()))
                .body("status", everyItem(equalTo("NEW")));
    }

    @Test
    void searchProjectsRejectsUnknownStatus() {
        user = userService.addUser(new UserRequest(
                "api-" + UUID.randomUUID(), "Test", "User", "test-password"));

        given()
                .port(port)
                .auth().form(user.getLogin(), "test-password",
                        new FormAuthConfig("/login", "username", "password").withAdditionalField("_csrf"))
                .queryParam("status", "UNKNOWN")
        .when()
                .get("/api/search/projects")
        .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .body("status", equalTo(400))
                .body("instance", equalTo("/api/search/projects"));
    }
}
