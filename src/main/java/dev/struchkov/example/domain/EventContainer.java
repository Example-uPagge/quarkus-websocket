package dev.struchkov.example.domain;

import dev.struchkov.example.domain.input.ChatInputMessage;
import dev.struchkov.example.domain.input.ChatViewInput;
import dev.struchkov.example.domain.output.ChatOutputMessage;
import dev.struchkov.example.domain.output.ChatViewOutput;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventContainer {

    private EventType eventType;
    private Object event;

    public static EventContainer messageInput(ChatInputMessage chatInputMessage) {
        return EventContainer.builder()
                .eventType(EventType.MESSAGE_NEW)
                .event(chatInputMessage)
                .build();
    }

    public static EventContainer viewInput(ChatViewInput chatViewInput) {
        return EventContainer.builder()
                .eventType(EventType.MESSAGE_VIEWED)
                .event(chatViewInput)
                .build();
    }

    public static EventContainer viewedOutput(ChatViewOutput chatViewOutput) {
        return EventContainer.builder()
                .eventType(EventType.MESSAGE_VIEWED)
                .event(chatViewOutput)
                .build();
    }

    public static EventContainer messageOutput(ChatOutputMessage chatOutputMessage) {
        return EventContainer.builder()
                .eventType(EventType.MESSAGE_NEW)
                .event(chatOutputMessage)
                .build();
    }

}
