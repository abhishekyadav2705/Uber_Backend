package com.backend.abhishek.uber.configs;

import com.backend.abhishek.uber.services.RideRequestService;
import com.backend.abhishek.uber.services.impl.RiderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeyExpirationListener implements MessageListener {

    private final RideRequestService rideRequestService;
    private static final Logger logger = LoggerFactory.getLogger(RedisKeyExpirationListener.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.info("Redis expiry message {}", message);
        String expiredKey = message.toString();
        System.out.println("Expired Key: " + expiredKey);

        // Assuming your key is like "rideRequest:<rideRequestId>"
        if (expiredKey.startsWith("rideRequest:")) {
            String rideRequestIdStr = expiredKey.substring("rideRequest:".length());
            try {
                Long rideRequestId = Long.parseLong(rideRequestIdStr);
                rideRequestService.expireRideRequest(rideRequestId);
            } catch (NumberFormatException e) {
                // Log or handle invalid key format
                System.err.println("Invalid ride request id in expired key: " + expiredKey);
            }
        }
    }
}

