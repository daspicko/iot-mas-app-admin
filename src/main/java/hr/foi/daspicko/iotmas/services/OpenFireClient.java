package hr.foi.daspicko.iotmas.services;

import org.igniterealtime.restclient.RestApiClient;
import org.igniterealtime.restclient.entity.AuthenticationToken;
import org.igniterealtime.restclient.entity.UserEntity;
import org.igniterealtime.restclient.enums.SupportedMediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class OpenFireClient {

    @Value("${openfire.server.url}")
    private String url;

    @Value("${openfire.server.port}")
    private int port;

    @Value("${openfire.server.secret}")
    private String secretKey;

    private RestApiClient openfireClient;

    @PostConstruct
    private void init() {
        openfireClient = new RestApiClient(url, port, new AuthenticationToken(secretKey), SupportedMediaType.JSON);
    }

    public boolean createUser(final String username, final String password, final String name) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(username);
        userEntity.setPassword(password);
        userEntity.setName(name);

        return openfireClient.createUser(userEntity).getStatus() == 201;
    }

    public boolean deleteUser(final String username) {
        return openfireClient.deleteUser(username).getStatus() == 200;
    }
}
