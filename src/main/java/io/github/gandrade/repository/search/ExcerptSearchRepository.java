package io.github.gandrade.repository.search;

import io.github.gandrade.domain.Excerpt;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Excerpt entity.
 */
public interface ExcerptSearchRepository extends ElasticsearchRepository<Excerpt, Long> {
}
