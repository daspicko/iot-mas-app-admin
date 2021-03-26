package hr.foi.daspicko.iotmas.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Agent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @NotNull @Size(min = 3)
    @Getter  @Setter
    private String name;

    @Getter @Setter
    private String description;

    @NotNull @Size(min = 3)
    @Getter @Setter
    private String xmppHostname;

    @NotNull @Size(min = 3)
    @Getter @Setter
    private String jid;

    @NotNull @Size(min = 3)
    @Getter @Setter
    private String xmppPassword;

    @Getter @Setter
    private int wgiPort = 9000;

    @Getter @Setter
    @NotNull @Size(min = 3)
    private String script;

    @Getter @Setter
    @NotNull @Size(min = 3)
    private String hostname;

    @NotNull @Size(min = 2)
    @Getter @Setter
    private String username;

    @NotNull @Size(min = 3)
    @Getter @Setter
    private String password;

    @Getter @Setter
    private String additionalData;

    @Column(columnDefinition = "VARCHAR(100) default '/img/rpi.jpg'")
    @Getter
    private String image = "/img/rpi.jpg";

}
