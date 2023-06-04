package dev.struchkov.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import lombok.SneakyThrows;

public class ChatMessageEncoder implements Encoder.Text<ChatOutputMessage> {

    private final ObjectMapper jackson = ChatMessageDecoder.getJackson();

    @Override
    @SneakyThrows
    public String encode(ChatOutputMessage chatOutputMessage) throws EncodeException {
        return jackson.writeValueAsString(chatOutputMessage);
    }

}
