package org.winey.server.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.Repository;
import org.winey.server.domain.recommend.Recommend;

import org.springframework.data.domain.Pageable;

public interface RecommendRepository extends Repository<Recommend, Long> {
    // CREATE

    // READ
    Page<Recommend> findAllByOrderByCreatedAtDesc(Pageable pageable);
    // UPDATE

    // DELETE

}
