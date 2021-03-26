$.get("/api/v1/git/pull", function (response) {
    console.log("Git pull: " + response);
})

$(document).ready(function () {
    fetctAllAgentScripts();
    fetchAllAgents();
});

function fetchAllAgents() {
    $("#loading-screen").css("display", "block");
    $("#agents").empty();
    $.get("/api/v1/agent", function (response) {
        agents = response;
        if (agents.length > 0) {
            agents.forEach(agent => {
                $("#agents").append(createAgentCard(agent));
            });
        }
        else {
            $("#agents").append("<p>Cluster does not contain agents. Please add them.</p>");
        }
        if (!heartbeatStarted) {
            heartBeatCheckStatus();
            heartbeatStarted = true;
        }
        $("#loading-screen").css("display", "none");
    }).fail(function (response) {
        console.log("Error fetching clusters!", response);
    });
}

function fetctAllAgentScripts() {
    $.get("/api/v1/git/available-agents", function (response) {
        agentScripts = response;
        agentScripts.forEach(script => {
            $('.available-agent-scripts').append(new Option(script.text, script.value));
        });
    })
}

function createAgentCard(agent) {
    return `
        <div class="col-xs-12 col-md-6 col-lg-3 col-xl-2 p-2">
            <div class="card">
                <img src="${agent.image}" class="card-img-top" alt="${agent.name}" />
                <div class="card-body">
                    <h5 class="card-title">${agent.name}</h5>
                    <p class="card-text">${agent.description}</p>
                    <button class='btn btn-primary' onclick='openAgentDetails("${agent.id}")' data-toggle="modal" data-target="#modalAgentDetails">Details</button>
                    <span class="agent-status float-right" data-agent-id="${agent.id}"></span>
                </div>
            </div>
        </div>
    `;
}

function openAgentDetails(agentId) {
    let filteredAgents = agents.filter(agent => agent.id == agentId);

    if (filteredAgents.length > 0) {
        let agent = filteredAgents[0];

        selectedAgent = agent;
        $("#modalAgentDetails_id").val(agent.id);
        $("#modalAgentDetails_jid").val(agent.jid);
        $("#modalAgentDetails_xmppHostname").val(agent.xmppHostname);
        $("#modalAgentDetails_xmppPassword").val(agent.xmppPassword);
        $("#modalAgentDetails_hostname").val(agent.hostname);
        $("#modalAgentDetails_username").val(agent.username);
        $("#modalAgentDetails_password").val(agent.password);
        $("#modalAgentDetails_wgiPort").val(agent.wgiPort);
        $("#modalAgentDetails_name").val(agent.name);
        $("#modalAgentDetails_script").val(agent.script);
        $("#modalAgentDetails_description").val(agent.description);
        $("#modalAgentDetails_image").val(agent.image);
        $("#modalAgentDetails_additionalData").val(agent.additionalData);
        $("#modalAgentDetails_status").html(agent.status);

        $("#modalAgentDetails_web_console").attr("href", `http://${agent.hostname}:${agent.wgiPort}/spade`);

        updateAgentStatusDetailsDialog(agent.id);

        let data = {};
        try {
            data = JSON.parse(agent.additionalData);
        } catch (e) {

        }
        let agentData = {
            "name": agent.name,
            "wgiPort": agent.wgiPort,
            "xmpp": {
                "hostname": agent.xmppHostname,
                "jid": agent.jid,
                "password": agent.xmppPassword
            },
            "data": data
        }
        document.getElementById("agent-run-command").value = `python3 ${agent.script} ${btoa(JSON.stringify(agentData))}`;
    }
}

function copyToClipboard() {
    document.getElementById("agent-run-command").select();
    document.execCommand("copy");
    console.log(document.getElementById("agent-run-command").value);
}

function heartBeatCheckStatus() {
    updateAgentStatusOnCards();
    setTimeout(heartBeatCheckStatus, 5000);
}

function updateAgentStatusOnCards() {
    $.get(`/api/v1/agent/status`, function (response) {
        response.forEach(status => {
            let statusContainer = $(`span.agent-status[data-agent-id="${status.id}"]`);
            statusContainer.empty();
            statusContainer.append(status.running ? '<i class="fa fa-check-circle text-success"></i>' : '<i class="fa fa-times-circle text-danger"></i>');
        });
    }).fail(function () {
        let statusContainers = $(`span.agent-status`);
        statusContainers.forEach(statusContainer => {
            statusContainer.empty();
            statusContainer.append('<i class="fa fa-question-circle text-warning"></i>');
        })
    });
}

function updateAgentStatusDetailsDialog(agentId) {
    let statusContainer = $("#modalAgentDetails span.agent-status").data("agent-id", agentId);
    $.get(`/api/v1/agent/${agentId}/isRunning`, function (response) {
        if (response) {
            statusContainer.empty();
            statusContainer.append('<i class="fa fa-check-circle text-success"></i> Running');
            $("#modalAgentDetails_web_console").removeClass("disabled");
            $("#modalAgentDetails_start").addClass("disabled");
            $("#modalAgentDetails_stop").removeClass("disabled");
            $("#modalAgentDeleteButton").addClass("disabled");
            $("#modalAgentDetails_update").addClass("disabled");
        }
        else {
            statusContainer.empty();
            statusContainer.append('<i class="fa fa-times-circle text-danger"></i> Stoped');
            $("#modalAgentDetails_web_console").addClass("disabled")
            $("#modalAgentDetails_start").removeClass("disabled");
            $("#modalAgentDetails_stop").addClass("disabled");
            $("#modalAgentDeleteButton").removeClass("disabled");
            $("#modalAgentDetails_update").removeClass("disabled");
        }
    }).fail(function () {
        statusContainer.empty();
        statusContainer.append('<i class="fa fa-question-circle text-warning"></i> Unknown');
        $("#modalAgentDetails_web_console").addClass("disabled")
        $("#modalAgentDetails_start").removeClass("disabled");
        $("#modalAgentDetails_stop").addClass("disabled");
        $("#modalAgentDeleteButton").removeClass("disabled");
        $("#modalAgentDetails_update").removeClass("disabled");
    });
}