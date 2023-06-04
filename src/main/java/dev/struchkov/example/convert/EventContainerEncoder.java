package dev.struchkov.example.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.struchkov.example.domain.EventContainer;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import lombok.SneakyThrows;

public class EventContainerEncoder implements Encoder.Text<EventContainer> {

    private final ObjectMapper jackson = EventContainerDecoder.getJackson();

    @Override
    @SneakyThrows
    public String encode(EventContainer eventContainer) throws EncodeException {
        return jackson.writeValueAsString(eventContainer);
    }

}
