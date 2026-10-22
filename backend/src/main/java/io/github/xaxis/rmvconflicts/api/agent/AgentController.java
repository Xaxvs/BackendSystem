package io.github.xaxis.rmvconflicts.api.agent;


import jakarta.validation.Valid;
import io.github.xaxis.rmvconflicts.agent.Agent;
import io.github.xaxis.rmvconflicts.agent.AgentManager;
import io.github.xaxis.rmvconflicts.api.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/agents")
public class AgentController {

    private final AgentManager agentManager;

    public AgentController(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    private AgentResponse toAgentResponse(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getDisplayName(),
                agent.getEmail(),
                agent.getCreatedAt()
        );
    }

    @GetMapping("/{id}")
    public AgentResponse getAgent(@PathVariable long id) {
        Optional<Agent> optionalTicket = agentManager.getAgentById(id);
        if (optionalTicket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent not found");
        }
        return toAgentResponse(optionalTicket.get());
    }

    @GetMapping
    public ResponseEntity<PageResponse<AgentResponse>> getAgents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        Page<Agent> tickets = agentManager.findAgents(page, size, direction, sortBy);

        PageResponse<AgentResponse> request = new PageResponse<>(
                tickets.getContent().stream().map(this::toAgentResponse).toList(),
                tickets.getNumber(),
                tickets.getSize(),
                tickets.getTotalPages(),
                tickets.getTotalElements(),
                tickets.isFirst(),
                tickets.isLast()
        );

        return ResponseEntity.ok(request);
    }

    @PostMapping
    public ResponseEntity<AgentResponse> createAgent(@Valid @RequestBody AgentRequest agentRequest) {
        Agent agent = agentManager.createAgent(
                agentRequest.displayName(),
                agentRequest.email()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toAgentResponse(agent));
    }
}
