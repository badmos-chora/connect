package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import org.backend.user.dto.FollowRequestDto;
import org.backend.user.projections.FollowRequestProjection;
import org.backend.user.repository.interfaces.FollowRequestRepository;
import org.backend.user.service.interfaces.FollowRequestService;
import org.backend.user.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class FollowRequestServiceImpl implements FollowRequestService {

    private FollowRequestRepository followRequestRepository;

    @Override
    public Set<FollowRequestDto> getFollowRequests() {
        Long presentUserId = SecurityUtils.getCurrentUserId();
       List<FollowRequestProjection> followRequestProjection = followRequestRepository.findBy(FollowRequestRepository.Specs.receiverId(presentUserId).and(FollowRequestRepository.Specs.fetchSenderAndReceiver()),q -> q.as(FollowRequestProjection.class).all());
        Set<FollowRequestDto> followRequestDtoList = new LinkedHashSet<>();
        for (FollowRequestProjection followRequest : followRequestProjection) {

            FollowRequestDto.FollowRequestDtoBuilder followRequestDtoBuilder = FollowRequestDto.builder();
            followRequestDtoBuilder.id(followRequest.getId())
                    .senderUserId(followRequest.getSenderUser().getId())
                    .senderFullName(followRequest.getSenderUser().getFullName())
                    .senderUserName(followRequest.getSenderUser().getUserName())
                    .receiverUserId(followRequest.getReceiverUser().getId())
                    .receiverFullName(followRequest.getReceiverUser().getFullName())
                    .receiverUserName(followRequest.getReceiverUser().getUserName())
                    .status(followRequest.getStatus()) ;


            Duration duration = Duration.between(followRequest.getSentAt(), Instant.now());
            if (duration.toDays() > 0) {
                followRequestDtoBuilder.sentAt(duration.toDays() + " days ago");
            } else if (duration.toHours() > 0) {
                followRequestDtoBuilder.sentAt(duration.toHours() + " hours ago");
            } else if (duration.toMinutes() > 0) {
                followRequestDtoBuilder.sentAt(duration.toMinutes() + " minutes ago");
            } else {
                followRequestDtoBuilder.sentAt(duration.toSeconds() + " seconds ago");
            }

            followRequestDtoList.add(followRequestDtoBuilder.build());
        }
        return followRequestDtoList;
    }
}
