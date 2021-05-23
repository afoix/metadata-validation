package com.afoix;

import com.google.common.net.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StressTest {

    private static final long MiB = 1024 * 1024L;
    private static final long GiB = 1024 * MiB;

    @LocalServerPort
    private int port;

    public static Stream<Object> payloadSizes() {
        return Stream.of(
                Named.of("100MiB", 100 * MiB),
                Named.of("500MiB", 500 * MiB),
                Named.of("1GiB", 1 * GiB),
                Named.of("2GiB", 2 * GiB),
                Named.of("3GiB", 3 * GiB),
                Named.of("4GiB", 4 * GiB)
        );
    }

    @ParameterizedTest
    @MethodSource("payloadSizes")
    public void testLargeMetadataPayload(Long size) throws IOException, InterruptedException {

        java.util.logging.LogManager.getLogManager().reset();

        int bodyElementSize = 100 * 1024 * 1024;
        String elemStart = "<element>";
        String elemEnd = "</element>";
        String bodyString = elemStart + "x".repeat(bodyElementSize - elemStart.length() - elemEnd.length()) + elemEnd;
        assertThat(bodyString).hasSize(bodyElementSize);

        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("sample/validate")
                .build().toUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .POST(HttpRequest.BodyPublishers.ofByteArrays(new Iterable<byte[]>() {
                    @NotNull
                    @Override
                    public Iterator<byte[]> iterator() {
                        return new BodyGenerator(size / bodyString.length(),
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document>".getBytes(StandardCharsets.UTF_8),
                                bodyString.getBytes(StandardCharsets.UTF_8),
                                "</document>".getBytes(StandardCharsets.UTF_8)
                        );
                    }
                }))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    /**
     * Iterator that produces the sequence (header, body, body, body..., footer) where body is repeated bodyCount times
     */
    private static class BodyGenerator implements Iterator<byte[]> {

        private final Long bodyCount;
        private final byte[] header;
        private final byte[] body;
        private final byte[] footer;
        private Long index;

        private BodyGenerator(Long bodyCount, byte[] header, byte[] body, byte[] footer) {
            this.index = -1L;
            this.bodyCount = bodyCount;
            this.header = header;
            this.body = body;
            this.footer = footer;
        }

        @Override
        public boolean hasNext() {
            return index <= bodyCount;
        }

        @Override
        public byte[] next() {
            index++;

            if (index == 0) {
                return header;
            }

            if (index >= bodyCount + 1) {
                return footer;
            }

            return body;
        }
    }

}
