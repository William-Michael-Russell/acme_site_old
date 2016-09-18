(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('NumericInputFieldSearch', NumericInputFieldSearch);

    NumericInputFieldSearch.$inject = ['$resource'];

    function NumericInputFieldSearch($resource) {
        var resourceUrl =  'api/_search/numeric-input-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
