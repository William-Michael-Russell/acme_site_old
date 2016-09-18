(function() {
    'use strict';

    angular
        .module('acmeSiteApp')
        .factory('ImageUploadSearch', ImageUploadSearch);

    ImageUploadSearch.$inject = ['$resource'];

    function ImageUploadSearch($resource) {
        var resourceUrl =  'api/_search/image-uploads/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
