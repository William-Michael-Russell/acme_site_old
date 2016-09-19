(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('RadioCheckboxFieldSearch', RadioCheckboxFieldSearch);

    RadioCheckboxFieldSearch.$inject = ['$resource'];

    function RadioCheckboxFieldSearch($resource) {
        var resourceUrl =  'api/_search/radio-checkbox-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
