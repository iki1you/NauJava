package ru.vladimir;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.vladimir.controller.RestExceptionHandler;
import ru.vladimir.controller.RestErrorResponseAdvice;
import ru.vladimir.controller.SearchController;
import ru.vladimir.controller.UnexpectedRestExceptionHandler;
import ru.vladimir.model.ProjectStatus;
import ru.vladimir.repository.ProjectRepository;
import ru.vladimir.repository.TaskRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestExceptionHandlerTest {
    private ProjectRepository projectRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        SearchController controller = new SearchController(projectRepository, mock(TaskRepository.class));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler(), new UnexpectedRestExceptionHandler(),
                        new RestErrorResponseAdvice())
                .build();
    }

    @Test
    void missingParameterReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/search/projects"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void missingResourceReturnsNotFound() throws Exception {
        when(projectRepository.findAllByStatusAndDeletedAtIsNull(ProjectStatus.NEW))
                .thenThrow(new NoSuchElementException("Project not found"));

        mockMvc.perform(get("/api/search/projects").param("status", "NEW"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void unexpectedFailureReturnsInternalServerError() throws Exception {
        when(projectRepository.findAllByStatusAndDeletedAtIsNull(ProjectStatus.NEW))
                .thenThrow(new IllegalStateException("Internal error"));

        mockMvc.perform(get("/api/search/projects").param("status", "NEW"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("Внутренняя ошибка сервера."));
    }
}
