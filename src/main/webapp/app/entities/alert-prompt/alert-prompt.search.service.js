(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('AlertPromptSearch', AlertPromptSearch);

    AlertPromptSearch.$inject = ['$resource'];

    function AlertPromptSearch($resource) {
        var resourceUrl =  'api/_search/alert-prompts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
