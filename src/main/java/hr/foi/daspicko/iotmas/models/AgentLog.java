package hr.foi.daspicko.iotmas.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "agent_log")
public class AgentLog {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    private String agentName;

    @Getter @Setter
    private String message;

    @Getter @Setter
    private ZonedDateTime timestamp;

    public AgentLog() {
    }

    public AgentLog(final String agentName, final String message) {
        timestamp = ZonedDateTime.now(ZoneId.of("Europe/Zagreb"));
        this.agentName = agentName;
        this.message = message;
    }

    public String getFormatedTimestamp() {
        return timestamp.format(dateFormatter);
    }
}
