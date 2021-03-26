$(document).ready(function () {
    $(".btn-action-close").click(function () {
        let forms = $(this).closest(".modal-content").find("form");
        if (forms && forms.length > 0) {
            forms[0].reset();
        }
    });
});