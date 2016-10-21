(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('CheckboxFieldSearch', CheckboxFieldSearch);

    CheckboxFieldSearch.$inject = ['$resource'];

    function CheckboxFieldSearch($resource) {
        var resourceUrl =  'api/_search/checkbox-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
