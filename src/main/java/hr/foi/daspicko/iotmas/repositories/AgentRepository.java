package hr.foi.daspicko.iotmas.repositories;

import hr.foi.daspicko.iotmas.models.Agent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AgentRepository extends CrudRepository<Agent, Long> {
}
