package com.customerissueresolutionportal.repository;


import com.customerissueresolutionportal.excpetion.NotFoundException;
import com.customerissueresolutionportal.model.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentRepository {
    private final Map<String, Agent> agentStore = new HashMap<>();  // use of cuncurrentMap for Thread Safety

    public Agent save(Agent agent) {
        agentStore.put(agent.getAgentId(), agent);
        return agent;
    }

    public Agent findById(String agentId) throws NotFoundException {
        Agent agent = agentStore.get(agentId);
        if (agent == null) {
            throw new NotFoundException("Agent", agentId);
        }
        return agent;
    }

    public List<Agent> findAll() {
        return new ArrayList<>(agentStore.values());
    }

    // Find agents by email, a common lookup method
    public Agent findByEmail(String agentEmail) {
        return agentStore.values().stream()
                .filter(a -> a.getAgentEmail().equals(agentEmail))
                .findFirst()
                .orElse(null);
    }
}