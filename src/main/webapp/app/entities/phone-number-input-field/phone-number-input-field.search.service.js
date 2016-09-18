(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('PhoneNumberInputFieldSearch', PhoneNumberInputFieldSearch);

    PhoneNumberInputFieldSearch.$inject = ['$resource'];

    function PhoneNumberInputFieldSearch($resource) {
        var resourceUrl =  'api/_search/phone-number-input-fields/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
