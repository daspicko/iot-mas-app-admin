package hr.foi.daspicko.iotmas.controllers.rest;

import hr.foi.daspicko.iotmas.records.ValueText;
import hr.foi.daspicko.iotmas.services.GitClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/git")
public class GitController {

    @Autowired
    private GitClient gitClient;

    @GetMapping(value = "/available-agents", produces = MediaType.APPLICATION_JSON)
    public List<ValueText> getFiles() {
        final List<ValueText> response = new ArrayList<>();
        gitClient.fetchAgentScripts().forEach(script -> response.add(new ValueText(script, script)));
        return response;
    }

    @GetMapping(value = "/pull", produces = MediaType.APPLICATION_JSON)
    public boolean gitPull() {
        boolean success = false;

        try {
            gitClient.pullChangesFromCurrentBranch();
            success = true;
        } catch (IOException | GitAPIException e) {
            log.error("Error fetching from git repository", e);
        }

        return success;
    }
}
