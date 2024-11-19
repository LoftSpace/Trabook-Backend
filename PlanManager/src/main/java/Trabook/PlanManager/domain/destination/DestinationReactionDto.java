package Trabook.PlanManager.domain.destination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinationReactionDto {
    private String reactionType;
    private long userId;
    private long placeId;
}
