package org.winey.server.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.winey.server.domain.recommend.Recommend;

import java.awt.print.Pageable;
import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    // CREATE

    // READ
    Optional<Page<Recommend>> findAllOrderByCreatedAtDesc(Pageable pageable);
    // UPDATE

    // DELETE

}
