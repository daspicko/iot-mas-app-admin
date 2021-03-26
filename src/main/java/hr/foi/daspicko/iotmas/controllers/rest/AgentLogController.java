package hr.foi.daspicko.iotmas.controllers.rest;

import hr.foi.daspicko.iotmas.models.AgentLog;
import hr.foi.daspicko.iotmas.repositories.AgentLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/api/v1/log")
public class AgentLogController {

    @Autowired
    private AgentLogRepository agentLogRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON)
    private List<AgentLog> getAllLogs() {
        final List<AgentLog> logs = new ArrayList<>();
        agentLogRepository.findAll().forEach(logs::add);
        return logs;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON)
    private AgentLog addLog(@RequestParam String agentName, @RequestParam String message) {
        return agentLogRepository.save(new AgentLog(agentName, message));
    }

    @GetMapping(value = "/print", produces = MediaType.APPLICATION_JSON)
    private List<String> getAllLogsPrinted() {
        final List<String> logs = new ArrayList<>();
        StreamSupport.stream(agentLogRepository.findAll().spliterator(), false)
                .collect(Collectors.toList())
                .forEach(agentLog -> {
                    logs.add(String.format("[%s] [%s] %s\n", agentLog.getFormatedTimestamp(), agentLog.getAgentName(), agentLog.getMessage()));
                });

        return logs;
    }

}
