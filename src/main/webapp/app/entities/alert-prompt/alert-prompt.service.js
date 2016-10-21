(function() {
    'use strict';
    angular
        .module('acmeSiteApp')
        .factory('AlertPrompt', AlertPrompt);

    AlertPrompt.$inject = ['$resource'];

    function AlertPrompt ($resource) {
        var resourceUrl =  'api/alert-prompts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
