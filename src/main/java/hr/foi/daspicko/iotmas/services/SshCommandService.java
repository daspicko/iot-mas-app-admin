package hr.foi.daspicko.iotmas.services;

import com.jcraft.jsch.*;
import hr.foi.daspicko.iotmas.models.Agent;
import hr.foi.daspicko.iotmas.records.SshCommandResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;

@Slf4j
@Service
public class SshCommandService {

    private static final String CHANNEL_TYPE_SFTP = "sftp";
    private static final String CHANNEL_TYPE_EXEC = "exec";

    public void copyFileToHost(final Agent agent, final String source, final String destination) {
        final JSch jsch = new JSch();
        final Session session;

        byte[] key = Base64.getDecoder().decode("");

        try {
            HostKey hostKey = new HostKey(agent.getHostname(), key);
            jsch.getHostKeyRepository().add(hostKey, null);

            session = jsch.getSession(agent.getUsername(), agent.getHostname());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(agent.getPassword());
            session.connect();

            Channel channel = session.openChannel(CHANNEL_TYPE_SFTP);
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;

            populateFilesInFolder(agent, sftp, new File(source), destination);

            sftp.exit();

            session.disconnect();
        } catch (JSchException | SftpException e) {
            log.error("Error copying file to remote!", e);
        }
    }

    public SshCommandResult runCommand(final String host, final String username, final String password, final String command) {
        final SshCommandResult commandResult = new SshCommandResult();

        final JSch jsch = new JSch();
        final Session session;

        byte[] key = Base64.getDecoder().decode("");

        try {
            HostKey hostKey = new HostKey(host, key);
            jsch.getHostKeyRepository().add(hostKey, null);

            session = jsch.getSession(username, host);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel(CHANNEL_TYPE_EXEC);
            InputStream is = channel.getInputStream();
            channel.setCommand(command);
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            final StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            commandResult.setStatusCode(channel.getExitStatus());
            commandResult.setResult(sb.toString());
            channel.disconnect();

            session.disconnect();
        } catch (JSchException | IOException e) {
            log.error("Error running command on remote!", e);
        }

        return commandResult;
    }

    public SshCommandResult runCommand(final Agent agent, final String command) {
        return runCommand(agent.getHostname(), agent.getUsername(), agent.getPassword(), command);
    }

    private void populateFilesInFolder(final Agent agent, final ChannelSftp sftp, final File file, final String destination) throws SftpException {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            runCommand(agent, "mkdir -p " + destination);
            final File[] children = file.listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) {
                    populateFilesInFolder(agent, sftp, child, destination + "/" + child.getName());
                }
            }
        } else {
            sftp.put(file.getPath(), destination);
        }

    }

}
