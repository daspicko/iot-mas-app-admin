package hr.foi.daspicko.iotmas.controllers.rest;

import hr.foi.daspicko.iotmas.exceptions.ResourceNotFoundException;
import hr.foi.daspicko.iotmas.models.Agent;
import hr.foi.daspicko.iotmas.records.AgentStatus;
import hr.foi.daspicko.iotmas.records.SshCommandResult;
import hr.foi.daspicko.iotmas.repositories.AgentRepository;
import hr.foi.daspicko.iotmas.services.AgentRunManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/agent")
public class AgentController {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgentRunManager agentRunManager;

    @GetMapping(produces = MediaType.APPLICATION_JSON)
    public List<Agent> getAllAgents() {
        final List<Agent> agents = new ArrayList<>();
        agentRepository.findAll().forEach(agents::add);
        return agents;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON)
    public Agent addAgent(@Valid @ModelAttribute Agent agent) {
        agentRepository.save(agent);
        return agent;
    }

    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON)
    public Agent updateAgent(@PathVariable("id") Long id, @Valid @ModelAttribute Agent updatedAgent) {
        final Optional<Agent> a = agentRepository.findById(id);
        if (a.isPresent()) {
            Agent agent = a.get();

            agent.setXmppHostname(updatedAgent.getXmppHostname());
            agent.setJid(updatedAgent.getJid());
            agent.setXmppPassword(updatedAgent.getXmppPassword());
            agent.setHostname(updatedAgent.getHostname());
            agent.setUsername(updatedAgent.getUsername());
            agent.setPassword(updatedAgent.getPassword());
            agent.setWgiPort(updatedAgent.getWgiPort());
            agent.setName(updatedAgent.getName());
            agent.setScript(updatedAgent.getScript());
            agent.setDescription(updatedAgent.getDescription());
            agent.setAdditionalData(updatedAgent.getAdditionalData());

            agentRepository.save(agent);
            return agent;
        }
        throw new ResourceNotFoundException("Agent does not exists!");
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON)
    public boolean deleteAgent(@PathVariable("id") Long id) {
        final Optional<Agent> a = agentRepository.findById(id);
        if (a.isPresent()) {
            agentRepository.delete(a.get());
            return true;
        }

        return false;
    }

    @GetMapping(value = "/{id}/start", produces = MediaType.APPLICATION_JSON)
    public SshCommandResult startAgentAction(@PathVariable("id") Long id) {
        return agentRunManager.startAgent(id);
    }

    @GetMapping(value = "/{id}/stop", produces = MediaType.APPLICATION_JSON)
    public SshCommandResult stopAgentAction(@PathVariable("id") Long id) {
        return agentRunManager.stopAgent(id);
    }

    @GetMapping(value = "/{id}/isRunning", produces = MediaType.APPLICATION_JSON)
    public boolean isAgentRunningStatus (@PathVariable("id") Long id) {
        final SshCommandResult result = agentRunManager.getAgentStatus(id);
        if (result == null) {
            return true;
        }
        else {
            return StringUtils.isNotBlank(result.getResult());
        }
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON)
    public List<AgentStatus> getAllAgentStatuses() {
        final List<AgentStatus> statuses = new ArrayList<>();

        agentRepository.findAll().forEach(agent -> {
            final AgentStatus agentStatus = new AgentStatus();
            agentStatus.setId(agent.getId());
            agentStatus.setRunning(isAgentRunningStatus(agent.getId()));
            statuses.add(agentStatus);
        });

        return statuses;
    }
}
