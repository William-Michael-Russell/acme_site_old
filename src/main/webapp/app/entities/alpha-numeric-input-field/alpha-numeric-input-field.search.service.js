(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('AlphaNumericInputFieldSearch', AlphaNumericInputFieldSearch);

    AlphaNumericInputFieldSearch.$inject = ['$resource'];

    function AlphaNumericInputFieldSearch($resource) {
        var resourceUrl =  'api/_search/alpha-numeric-input-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
