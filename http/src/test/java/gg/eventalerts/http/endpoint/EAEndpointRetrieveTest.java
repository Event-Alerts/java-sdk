package gg.eventalerts.http.endpoint;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.http.exception.EAHttpRequestException;
import gg.eventalerts.sdk.http.endpoint.EAEvents;
import gg.eventalerts.sdk.object.EAEvent;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class EAEndpointRetrieveTest {
    private static final int PORT = 0;
    private static final AtomicReference<String> lastPath = new AtomicReference<>();
    private static final AtomicReference<String> lastQuery = new AtomicReference<>();
    private static final AtomicReference<com.sun.net.httpserver.Headers> lastHeaders = new AtomicReference<>();
    private static HttpServer server;
    private static String baseUrl;

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/", new TestHandler());
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort() + "/api/v1";
    }

    @AfterAll
    static void stopServer() {
        if (server != null) server.stop(0);
    }

    @BeforeEach
    void resetCapture() {
        lastPath.set(null);
        lastQuery.set(null);
        lastHeaders.set(null);
    }

    @Test
    void retrievePageCompletesWithListAndSendsEncodedQueryAndHeaders() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0")
                .url(baseUrl)
                .bearerToken("bearer-token")
                .build();

        final Map<String, Object> query = new LinkedHashMap<>();
        query.put("search term", "alpha & beta");

        final PaginatedResponse<EAEvent> result = http.events.retrievePage(query).complete();

        assertEquals(1, result.items.size());
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), result.items.get(0).id);
        assertEquals("Example Event", result.items.get(0).title);
        assertEquals(1, result.page);
        assertEquals(1, result.count);
        assertEquals(1, result.total);

        assertEquals("/api/v1/events", lastPath.get());
        assertEquals("search+term=alpha+%26+beta", lastQuery.get());
        assertEquals("application/json", lastHeaders.get().getFirst("Accept"));
        assertEquals("EventAlertsSDK/1.0", lastHeaders.get().getFirst("User-Agent"));
        assertEquals("Bearer bearer-token", lastHeaders.get().getFirst("Authorization"));
        assertNull(lastHeaders.get().getFirst("X-Player-Key"));
        assertNull(lastHeaders.get().getFirst("X-Server-Key"));
    }

    @Test
    void retrieveOneDataCompletesWithSingleObject() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0").url(baseUrl).build();

        final EAEvent event = http.events.retrieveOneDataById(new ObjectId("507f1f77bcf86cd799439011")).complete().item;

        assertNotNull(event);
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), event.id);
        assertEquals("Example Event", event.title);

        assertEquals("/api/v1/events/id/507f1f77bcf86cd799439011", lastPath.get());
        assertNull(lastQuery.get());
    }

    @Test
    void retrieveOneOnErrorMapSeesTransportFailure() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0").url(baseUrl).build();
        final EAEvents events = new EAEvents(http) {
            @Override @NotNull
            protected ConnectionDetails openConnection(@NotNull String url, @NotNull String objectField) throws IOException {
                throw new IOException("boom");
            }
        };

        final AtomicReference<Throwable> captured = new AtomicReference<>();
        final EAEvent fallback = new EAEvent();
        fallback.title = "Recovered Event";

        final EAEvent event = events.retrieveOne("broken")
                .onErrorMap(error -> {
                    captured.set(error);
                    return fallback;
                })
                .complete();

        assertNotNull(event);
        assertEquals("Recovered Event", event.title);
        assertInstanceOf(EAHttpRequestException.class, captured.get());
        assertTrue(captured.get().getMessage().contains("GET " + events.buildUrl(events.getPath(), null, "broken")));
    }

    @Test
    void queueInvokesSuccessCallbackAsynchronously() throws InterruptedException {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0").url(baseUrl).build();

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<PaginatedResponse<EAEvent>> callbackResult = new AtomicReference<>();
        http.events.retrievePage().queue(result -> {
            callbackResult.set(result);
            latch.countDown();
        }, throwable -> {
            throw new AssertionError("unexpected failure", throwable);
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertNotNull(callbackResult.get());
        assertFalse(callbackResult.get().items.isEmpty());
    }

    @Test
    void retrieveDoesNotSwallowFatalErrors() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0").url(baseUrl).build();
        final EAEvents events = new EAEvents(http) {
            @Override @NotNull
            protected ConnectionDetails openConnection(@NotNull String url, @NotNull String objectField) {
                throw new AssertionError("fatal");
            }
        };

        assertThrows(AssertionError.class, () -> events.retrievePage(Collections.emptyMap()).complete());
    }

    private static class TestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            final URI uri = exchange.getRequestURI();
            lastPath.set(uri.getPath());
            lastQuery.set(uri.getRawQuery());
            lastHeaders.set(exchange.getRequestHeaders());

            final Response response = responseFor(uri);
            final byte[] body = response.body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(response.statusCode, body.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(body);
            }
        }
    }

    private static Response responseFor(URI uri) {
        final String path = uri.getPath();
        if ("/api/v1/events".equals(path)) {
            if (uri.getRawQuery() != null && uri.getRawQuery().contains("force_error=true")) {
                return new Response(400, "{\"code\":400,\"message\":\"Something went wrong\"}");
            }
            return new Response(200, "{\"code\":200,\"page\":1,\"limit\":1,\"count\":1,\"total\":1,\"all\":1,\"events\":[{\"code\":200,\"id\":\"507f1f77bcf86cd799439011\",\"title\":\"Example Event\"}]}");
        }
        if ("/api/v1/events/id/507f1f77bcf86cd799439011".equals(path)) {
            return new Response(200, "{\"code\":200,\"event\":{\"code\":200,\"id\":\"507f1f77bcf86cd799439011\",\"title\":\"Example Event\"}}");
        }
        if ("/api/v1/events/error".equals(path)) {
            return new Response(400, "{\"code\":400,\"message\":\"Something went wrong\"}");
        }
        if ("/api/v1/events/broken".equals(path)) {
            return new Response(200, "not-json");
        }
        return new Response(404, "{\"code\":404,\"message\":\"Not Found\"}");
    }

    private static final class Response {
        private final int statusCode;
        private final String body;

        private Response(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }
}
