package io.github.xaxis.rmvconflicts.tickets;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class TicketSpecifications {

    public static Specification<Ticket> hasStatus(TicketStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Ticket> hasStatusAndText(TicketStatus status, String q) {
        return hasStatus(status).and(hasText(q));
    }

    public static Specification<Ticket> hasText(String q) {

        return (root, query, cb) -> {
            if (q == null ||q.isBlank()) {
                return cb.conjunction();
            }
            final String text = q.trim().toLowerCase(Locale.ROOT);

            return cb.or(
                    cb.like(
                            cb.lower(root.get("title")),
                            "%"+text+"%"
                    ),
                    cb.like(
                            cb.lower(root.get("description")),
                            "%"+text+"%"
                    )
            );
        };
    }

}
