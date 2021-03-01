package ru.drsanches.life_together.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.life_together.data.friends.FriendRequest;
import ru.drsanches.life_together.data.friends.FriendRequestKey;
import java.util.Set;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, FriendRequestKey> {

    Set<FriendRequest> findByIdFromUserId(String fromUserId);

    Set<FriendRequest> findByIdToUserId(String toUserId);
}