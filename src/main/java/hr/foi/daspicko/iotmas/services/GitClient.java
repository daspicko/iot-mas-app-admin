package hr.foi.daspicko.iotmas.services;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class GitClient {

    @Value("${webapp.private-key.location}")
    private String privateKeyLocation;

    @Value("${git.agents.url}")
    private String url;

    @Value("${git.agents.branch}")
    private String branch;

    @Value("${git.agents.repository.path}")
    private String repositoryPath;

    @Value("${git.agents.folder.path}")
    private String agentsFolderPath;

    private SshSessionFactory sshSessionFactory;
    private TransportConfigCallback transportConfigCallback;

    @PostConstruct
    private void init() {
        sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                // do nothing
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.addIdentity(privateKeyLocation);
                return defaultJSch;
            }
        };
        transportConfigCallback = new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        };
    }

    private void cloneRepository() throws GitAPIException {
        Git.cloneRepository()
                .setTransportConfigCallback(transportConfigCallback)
                .setURI(url)
                .setDirectory(new File(repositoryPath))
                .setBranch(branch)
                .call();
    }

    public void pullChangesFromCurrentBranch() throws IOException, GitAPIException {
        final File gitRepositoryFolder = new File(repositoryPath);
        if (gitRepositoryFolder.exists()) {
            Git.open(gitRepositoryFolder)
                    .pull()
                    .setTransportConfigCallback(transportConfigCallback)
                    .call();
        }
        else {
            cloneRepository();
        }
    }

    public List<String> fetchAgentScripts() {
        final List<String> agentScripts = new ArrayList<>();

        File agentsDirectory = new File(repositoryPath + agentsFolderPath);

        if (agentsDirectory.exists() && agentsDirectory.isDirectory()) {
            File[] files = agentsDirectory.listFiles();
            if (files != null && files.length > 0) {
                Arrays.stream(files).filter(file -> file.getName().endsWith(".py")).forEach(file -> agentScripts.add(file.getName()));
            }
        }

        return agentScripts;
    }
}
