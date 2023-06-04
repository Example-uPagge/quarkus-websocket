package dev.struchkov.example.domain.input;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatViewInput {

    private UUID messageId;

}
