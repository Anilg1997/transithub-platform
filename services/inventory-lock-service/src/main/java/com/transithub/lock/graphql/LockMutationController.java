package com.transithub.lock.graphql;
import com.transithub.lock.service.LockService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LockMutationController {
    private final LockService service;
    public LockMutationController(LockService s) { this.service = s; }

    @MutationMapping
    public LockService.LockResult lockSeat(@Argument String inventoryType, @Argument String itemId,
                                           @Argument String seatId, @Argument String userId) {
        return service.lockSeat(inventoryType, itemId, seatId, userId);
    }

    @MutationMapping
    public boolean releaseSeat(@Argument String inventoryType, @Argument String itemId,
                               @Argument String seatId, @Argument String userId, @Argument String lockToken) {
        return service.releaseSeat(inventoryType, itemId, seatId, userId, lockToken);
    }

    @MutationMapping
    public boolean extendLock(@Argument String inventoryType, @Argument String itemId,
                              @Argument String seatId, @Argument String userId, @Argument String lockToken) {
        return service.extendLock(inventoryType, itemId, seatId, userId, lockToken);
    }
}
