package com.transithub.document.graphql;
import com.transithub.document.s3.S3StorageService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DocumentQueryController {
    private final S3StorageService storage;
    public DocumentQueryController(S3StorageService storage) { this.storage = storage; }

    public record DocumentUrl(String url, String expiresAt, String fileName) {}

    @QueryMapping
    public DocumentUrl downloadTicket(@Argument String bookingRef) {
        String key = "tickets/" + bookingRef + ".pdf";
        String url = storage.generatePresignedUrl(key, java.time.Duration.ofHours(1));
        return new DocumentUrl(url, java.time.Instant.now().plusSeconds(3600).toString(), bookingRef + "-ticket.pdf");
    }

    @QueryMapping
    public DocumentUrl downloadBoardingPass(@Argument String bookingRef, @Argument String pnr) {
        String key = "boarding-passes/" + pnr + ".pdf";
        String url = storage.generatePresignedUrl(key, java.time.Duration.ofHours(1));
        return new DocumentUrl(url, java.time.Instant.now().plusSeconds(3600).toString(), pnr + "-boarding-pass.pdf");
    }

    @QueryMapping
    public DocumentUrl downloadReceipt(@Argument String transactionId) {
        String key = "receipts/" + transactionId + ".pdf";
        String url = storage.generatePresignedUrl(key, java.time.Duration.ofHours(1));
        return new DocumentUrl(url, java.time.Instant.now().plusSeconds(3600).toString(), transactionId + "-receipt.pdf");
    }
}
