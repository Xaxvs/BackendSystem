package io.github.xaxis.rmvconflicts.agent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Service
public class AgentManager {

    private final AgentRepository agentRepository;

    public AgentManager(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Agent createAgent(String displayName, String email) {
        return agentRepository.save(new Agent(displayName, email));
    }

    public Optional<Agent> getAgentById(Long id) {
        return agentRepository.findById(id);
    }

    public Pageable createAgentPageable(int page, int size, String direction, String sortBy) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to process agents request");
        }
        if (Arrays.stream(AgentSortCases.values()).noneMatch(agentSortCases -> agentSortCases.getString().equals(sortBy))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad sortBy agents request");
        }
        if (direction == null || Arrays.stream(Sort.Direction.values()).noneMatch(dirEnum -> dirEnum.name().toLowerCase(Locale.ROOT).equals(direction))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad direction agents request");
        }
        return PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.fromString(direction),
                        sortBy,
                        "id"
                )
        );
    }

    public Page<Agent> findAgents(int page, int size, String direction, String sortBy) {
        return agentRepository.findAll(createAgentPageable(page, size, direction, sortBy));
    }

}
