package dev.struchkov.example.convert;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.struchkov.example.domain.EventContainer;
import dev.struchkov.example.domain.EventType;
import dev.struchkov.example.domain.input.ChatInputMessage;
import dev.struchkov.example.domain.input.ChatViewInput;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import lombok.SneakyThrows;

public class EventContainerDecoder implements Decoder.Text<EventContainer> {

    private final ObjectMapper jackson = EventContainerDecoder.getJackson();

    @Override
    @SneakyThrows
    public EventContainer decode(String s) throws DecodeException {
        final String eventType = jackson.readTree(s).get("eventType").asText();
        final JsonNode event = jackson.readTree(s).get("event");
        return switch (EventType.valueOf(eventType)) {
            case MESSAGE_NEW -> EventContainer.messageInput(jackson.treeToValue(event, ChatInputMessage.class));
            case MESSAGE_VIEWED -> EventContainer.viewInput(jackson.treeToValue(event, ChatViewInput.class));
        };
    }

    @Override
    public boolean willDecode(String s) {
        return s != null;
    }

    public static ObjectMapper getJackson() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.registerModule(new JavaTimeModule());
        return om;
    }

}
