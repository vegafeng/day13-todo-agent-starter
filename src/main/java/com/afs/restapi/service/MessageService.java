package com.afs.restapi.service;
import com.afs.restapi.entity.Message;
import com.afs.restapi.entity.Reaction;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author FENGVE
 */
@Service
public class MessageService {


    @Tool(description = "Get all reactions summary")
    public List<Reaction> getAllReactions(List<Message> messages) {
        Map<String, Integer> reactionMap = new HashMap<>();
        for (Message message : messages) {
            for (Reaction reaction : message.getReactions()) {
                reactionMap.merge(reaction.getEmoji(), reaction.getCount(), Integer::sum);
            }
        }
        return reactionMap.entrySet().stream()
                .map(e -> new Reaction(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
