$(document).ready(function () {
    $("#modalAgentAdd_create").click(function () {
        createNewAgent();
    });

    $("#modalAgentDetails_update").click(function () {
        if ($(this).hasClass("disabled")) {
            return;
        }
        updateAgent();
    });

    $("#modalAgentDeleteButton").click(function () {
        deleteAgentPopulateData();
    });

    $("#modalAgentDeleteConfirmation_delete").click(function () {
        deleteAgent();
    });

    $("#modalAgentDetails_start").click(function () {
        startAgent();
    });

    $("#modalAgentDetails_stop").click(function () {
        stopAgent();
    });
});

function createNewAgent() {
    let $form = $("#modalAgentAdd form");
    let form = $form[0];

    if (form.checkValidity() === true) {
        let data = $form.serialize();

        $.post(`/api/v1/agent`, data, function (response) {
            $("#modalAgentAdd").modal("hide");
            form.classList.remove('was-validated');
            form.reset();
            fetchAllAgents();
        }).fail(function (response) {
            console.log("ERROR!", response);
        });
    }
    form.classList.add('was-validated');
}

function updateAgent() {
    let $form = $("#modalAgentDetails form");
    let form = $form[0];

    if (form.checkValidity() === true && selectedAgent) {
        let data = $form.serialize();

        $.post(`/api/v1/agent/${selectedAgent.id}`, data, function (response) {
            $("#modalAgentDetails").modal("hide");
            form.classList.remove('was-validated');
            form.reset();
            fetchAllAgents();
        }).fail(function (response) {
            console.log("ERROR!", response);
        });
    }
    form.classList.add('was-validated');
}

function deleteAgentPopulateData() {
    $("#modalAgentDeleteConfirmation_namedisplay").empty();
    $("#modalAgentDeleteConfirmation_namedisplay").append(selectedAgent.name);
    $("#modalAgentDetails").modal("hide");
}

function deleteAgent() {
    let userInputAgentName = $("#modalAgentDeleteConfirmation_name").val();

    let form = $("#modalAgentDeleteConfirmation form")[0];
    if (selectedAgent && selectedAgent.name === userInputAgentName && form && form.checkValidity() === true) {
        $.ajax({
            url: `/api/v1/agent/${selectedAgent.id}`,
            type: 'DELETE'
        }).done(function () {
            $("#modalAgentDeleteConfirmation").modal("hide");
            form.reset();
            fetchAllAgents();
        }).fail(function (response) {
            console.log("ERROR!", response);
        });
    }
}

function startAgent() {
    console.log("Starting agent ... " + selectedAgent.id);

    $.get(`/api/v1/agent/${selectedAgent.id}/start`, function (response) {
        // There is no response from server
    }).fail(function (error) {
        console.log("Failed to start agent!" + error.result);
    });

    setTimeout(function () {
        updateAgentStatusDetailsDialog(selectedAgent.id);
    }, 2500)
}

function stopAgent() {
    console.log("Stoping agent ... " + selectedAgent.id);

    $.get(`/api/v1/agent/${selectedAgent.id}/stop`, function (response) {
        if (response) {
            console.log(response);
            updateAgentStatusDetailsDialog(selectedAgent.id);
        }
    }).fail(function (error) {
        console.log("Failed to stop agent!" + error.result);
    });
}

