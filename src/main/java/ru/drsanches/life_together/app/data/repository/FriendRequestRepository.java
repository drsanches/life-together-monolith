package ru.drsanches.life_together.app.data.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.life_together.app.data.friends.model.FriendRequest;
import ru.drsanches.life_together.app.data.friends.model.FriendRequestKey;
import java.util.Set;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, FriendRequestKey> {

    Set<FriendRequest> findByIdFromUserId(String fromUserId);

    Set<FriendRequest> findByIdToUserId(String toUserId);
}