package dev.struchkov.example.domain.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChatViewOutput {

    private UUID messageId;
    private UUID chatMemberId;
    private LocalDateTime dateRead;

}
