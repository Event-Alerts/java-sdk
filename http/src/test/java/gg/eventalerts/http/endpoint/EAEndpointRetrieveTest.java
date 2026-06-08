package gg.eventalerts.http.endpoint;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.endpoint.EAEvents;
import gg.eventalerts.sdk.http.response.APIResponse;
import gg.eventalerts.sdk.http.response.ErrorResponse;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.http.response.SingleResponse;
import gg.eventalerts.sdk.object.EAEvent;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class EAEndpointRetrieveTest {
    private static HttpServer server;
    private static String baseUrl;

    private static final AtomicReference<String> lastPath = new AtomicReference<>();
    private static final AtomicReference<String> lastQuery = new AtomicReference<>();
    private static final AtomicReference<com.sun.net.httpserver.Headers> lastHeaders = new AtomicReference<>();

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", new TestHandler());
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/api/v1";
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
    void retrieveManyParsesPaginatedResponseAndSendsEncodedQueryAndHeaders() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0")
                .url(baseUrl)
                .bearerToken("bearer-token")
                .build();
        final EAEvents events = new EAEvents(http);

        final Map<String, Object> query = new LinkedHashMap<>();
        query.put("search term", "alpha & beta");

        final APIResponse response = events.retrieveMany(query);

        assertInstanceOf(PaginatedResponse.class, response);
        @SuppressWarnings("unchecked") final PaginatedResponse<EAEvent> paginatedResponse = (PaginatedResponse<EAEvent>) response;
        assertEquals(Integer.valueOf(200), paginatedResponse.code);
        assertEquals(Integer.valueOf(1), paginatedResponse.page);
        assertEquals(Integer.valueOf(1), paginatedResponse.limit);
        assertEquals(Integer.valueOf(1), paginatedResponse.count);
        assertEquals(Integer.valueOf(1), paginatedResponse.total);
        assertEquals(Integer.valueOf(1), paginatedResponse.all);
        assertNotNull(paginatedResponse.data);
        assertEquals(1, paginatedResponse.data.size());
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), paginatedResponse.data.get(0).id);
        assertEquals("Example Event", paginatedResponse.data.get(0).title);

        assertEquals("/api/v1/events", lastPath.get());
        assertEquals("search+term=alpha+%26+beta", lastQuery.get());
        assertEquals("application/json", lastHeaders.get().getFirst("Accept"));
        assertEquals("EventAlertsSDK/1.0", lastHeaders.get().getFirst("User-Agent"));
        assertEquals("Bearer bearer-token", lastHeaders.get().getFirst("Authorization"));
        assertNull(lastHeaders.get().getFirst("X-Player-Key"));
        assertNull(lastHeaders.get().getFirst("X-Server-Key"));
    }

    @Test
    void retrieveOneParsesSingleResponse() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0")
                .url(baseUrl)
                .build();
        final EAEvents events = new EAEvents(http);

        final APIResponse response = events.retrieveOneById(new ObjectId("507f1f77bcf86cd799439011"));

        assertInstanceOf(SingleResponse.class, response);
        @SuppressWarnings("unchecked") final SingleResponse<EAEvent> singleResponse = (SingleResponse<EAEvent>) response;
        assertEquals(Integer.valueOf(200), singleResponse.code);
        assertNotNull(singleResponse.data);
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), singleResponse.data.id);
        assertEquals("Example Event", singleResponse.data.title);

        assertEquals("/api/v1/events/id/507f1f77bcf86cd799439011", lastPath.get());
        assertNull(lastQuery.get());
    }

    @Test
    void retrieveReturnsErrorResponseForErrorPayload() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0")
                .url(baseUrl)
                .build();
        final EAEvents events = new EAEvents(http);

        final APIResponse response = events.retrieveOne("error");

        assertInstanceOf(ErrorResponse.class, response);
        final ErrorResponse errorResponse = (ErrorResponse) response;
        assertEquals(Integer.valueOf(400), errorResponse.code);
        assertEquals("Something went wrong", errorResponse.message);

        assertEquals("/api/v1/events/error", lastPath.get());
        assertNull(lastQuery.get());
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
            return new Response(200, "{\"code\":200,\"page\":1,\"limit\":1,\"count\":1,\"total\":1,\"all\":1,\"events\":[{\"id\":\"507f1f77bcf86cd799439011\",\"title\":\"Example Event\"}]}");
        }
        if ("/api/v1/events/id/507f1f77bcf86cd799439011".equals(path)) {
            return new Response(200, "{\"code\":200,\"event\":{\"id\":\"507f1f77bcf86cd799439011\",\"title\":\"Example Event\"}}");
        }
        if ("/api/v1/events/error".equals(path)) {
            return new Response(400, "{\"code\":400,\"message\":\"Something went wrong\"}");
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
