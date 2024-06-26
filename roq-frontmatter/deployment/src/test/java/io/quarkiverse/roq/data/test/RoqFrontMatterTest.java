package io.quarkiverse.roq.data.test;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;

public class RoqFrontMatterTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClass(MyResource.class)
                    .addAsResource("templates/base.html")
                    .addAsResource("posts/article.html"));

    @Test
    public void writeYourOwnUnitTest() {
        RestAssured.when().get("/posts/my-cool-page").then().statusCode(200).log().ifValidationFails().body(equalToCompressingWhiteSpace("""
                <!DOCTYPE html>
                <html>
                  <head>
                    <title>My Cool Page</title>
                    <meta name="description" content="this is a very cool page"/>
                  </head>
                  <body>
                    <h1>Hello World</h1>
                    <p>bar</p>
                  </body>
                </html>
                """));
    }

    @ApplicationScoped
    @Path("/posts")
    public static class MyResource {

        @Inject
        @Named("posts/article.html")
        JsonObject fm;

        @CheckedTemplate(basePath = "posts")
        public static class Posts {
            public static native TemplateInstance article(Map<String, Object> fm);
        }

        @GET
        @Path("my-cool-page")
        @Produces(MediaType.TEXT_HTML)
        public TemplateInstance get() {
            return Posts.article(fm.getMap());
        }
    }
}
