(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('AlpaNumericInputFieldSearch', AlpaNumericInputFieldSearch);

    AlpaNumericInputFieldSearch.$inject = ['$resource'];

    function AlpaNumericInputFieldSearch($resource) {
        var resourceUrl =  'api/_search/alpa-numeric-input-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
