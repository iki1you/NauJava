package ru.vladimir.config;

import java.util.List;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.SpringDocAnnotationsUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vladimir.dto.UserRequest;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenApiCustomizer userRequestSchema() {
        return openApi -> {
            Schema<?> schema = SpringDocAnnotationsUtils.extractSchema(openApi.getComponents(), UserRequest.class,
                    null, null, openApi.getSpecVersion());
            for (String path : List.of("/users", "/users/{id}")) {
                PathItem item = openApi.getPaths().get(path);
                if (item == null) {
                    continue;
                }
                for (Operation operation : new Operation[] { item.getPost(), item.getPut(), item.getPatch() }) {
                    if (operation != null && operation.getRequestBody() != null) {
                        operation.getRequestBody().getContent().values()
                                .forEach(content -> content.setSchema(schema));
                    }
                }
            }
        };
    }
}
