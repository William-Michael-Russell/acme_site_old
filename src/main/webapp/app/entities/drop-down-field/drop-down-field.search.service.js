(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('DropDownFieldSearch', DropDownFieldSearch);

    DropDownFieldSearch.$inject = ['$resource'];

    function DropDownFieldSearch($resource) {
        var resourceUrl =  'api/_search/drop-down-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
