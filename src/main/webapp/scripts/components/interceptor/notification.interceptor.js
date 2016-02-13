 'use strict';

angular.module('bookexcerptsApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-bookexcerptsApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-bookexcerptsApp-params')});
                }
                return response;
            }
        };
    });
