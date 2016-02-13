package io.github.gandrade.repository;

import io.github.gandrade.domain.Excerpt;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Excerpt entity.
 */
public interface ExcerptRepository extends JpaRepository<Excerpt,Long> {

    @Query("select excerpt from Excerpt excerpt where excerpt.user.login = ?#{principal.username}")
    List<Excerpt> findByUserIsCurrentUser();

}
