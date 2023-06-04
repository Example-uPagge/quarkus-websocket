package dev.struchkov.example.domain.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChatOutputMessage {

    private UUID fromUserId;
    private String text;

}
