package hr.foi.daspicko.iotmas.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hr.foi.daspicko.iotmas.models.Agent;
import hr.foi.daspicko.iotmas.records.SshCommandResult;
import hr.foi.daspicko.iotmas.repositories.AgentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class AgentRunManager {
    private SshCommandService sshCommandService;
    private AgentRepository agentRepository;
    private OpenFireClient openFireClient;

    @Value("${git.agents.repository.path}")
    private String repositoryPath;

    @Value("${git.agents.folder.path}")
    private String agentsFolderPath;

    @Value("${git.agents.destinationFolder}")
    private String destinationFolder;

    @Autowired
    public void setSshCommandService(SshCommandService sshCommandService) {
        this.sshCommandService = sshCommandService;
    }

    @Autowired
    public void setAgentRepository(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Autowired
    public void setOpenFireClient(OpenFireClient openFireClient) {
        this.openFireClient = openFireClient;
    }

    public SshCommandResult startAgent(final Long id) {
        SshCommandResult result = null;

        final Optional<Agent> a = agentRepository.findById(id);
        if (a.isPresent()) {
            final Agent agent = a.get();

            sshCommandService.runCommand(agent, String.format("rm -rf %s", destinationFolder));
            sshCommandService.copyFileToHost(agent, repositoryPath + agentsFolderPath, destinationFolder);

            String startCmd;
            final JsonParser jsonParser = new JsonParser();
            final JsonObject agentData = new JsonObject();
            agentData.addProperty("name", agent.getName());
            agentData.addProperty("wgiPort", agent.getWgiPort());

            final JsonObject xmpp = new JsonObject();
            xmpp.addProperty("hostname", agent.getXmppHostname());
            xmpp.addProperty("jid", agent.getJid());
            xmpp.addProperty("password", agent.getXmppPassword());

            agentData.add("xmpp", xmpp);
            if (StringUtils.isNotBlank(agent.getAdditionalData())) {
                agentData.add("data", jsonParser.parse(agent.getAdditionalData()).getAsJsonObject());
            } else {
                agentData.add("data", new JsonObject());
            }

            startCmd = String.format("nohup python3 %s/%s %s &", destinationFolder, agent.getScript(), Base64.getEncoder().encodeToString(agentData.toString().getBytes()));

            result = sshCommandService.runCommand(agent, startCmd);
            //openFireClient.createUser(agent.getJid(), agent.getPassword(), "");
        }

        return result;
    }

    public SshCommandResult stopAgent(final Long id) {
        SshCommandResult result = null;

        final Optional<Agent> a = agentRepository.findById(id);
        if (a.isPresent()) {
            Agent agent = a.get();

            final String processCmd = String.format("ps -ef | grep 'python3 %s' | grep -v grep | awk '{ print $2 }'", destinationFolder);
            final String pid = sshCommandService.runCommand(agent, processCmd).getResult();

            if (StringUtils.isNotBlank(pid)) {
                final String cmdRemove = String.format("kill -SIGINT %s", pid);
                result = sshCommandService.runCommand(agent, cmdRemove);
            }
            else {
                result = new SshCommandResult(0, "Error stopping agent. PID Does not exists!");
            }
        }

        return result;
    }

    public SshCommandResult getAgentStatus(final Long id) {
        SshCommandResult result = null;

        final Optional<Agent> a = agentRepository.findById(id);
        if (a.isPresent()) {
            Agent agent = a.get();

            final String cmd = String.format("ps -ef | grep 'python3 %s/%s' | grep -v grep", destinationFolder, agent.getScript());
            result = sshCommandService.runCommand(agent, cmd);
        }

        return result;
    }

}
