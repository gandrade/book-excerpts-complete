'use strict';

angular.module('bookexcerptsApp')
    .factory('ExcerptSearch', function ($resource) {
        return $resource('api/_search/excerpts/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
