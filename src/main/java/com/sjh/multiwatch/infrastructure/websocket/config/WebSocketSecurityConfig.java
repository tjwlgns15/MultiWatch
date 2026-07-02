package com.sjh.multiwatch.infrastructure.websocket.config;

import com.sjh.multiwatch.infrastructure.security.MemberPrincipal;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SubscriptionAuthorizationInterceptor());
    }

    private static class SubscriptionAuthorizationInterceptor implements ChannelInterceptor {

        @Override
        public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                validateSubscription(accessor);
            }

            return message;
        }

        private void validateSubscription(StompHeaderAccessor accessor) {
            Long organizationId = extractOrganizationId(accessor.getDestination());
            MemberPrincipal principal = resolvePrincipal(accessor.getUser());

            if (!principal.getOrganizationId().equals(organizationId)) {
                throw new AccessDeniedException("해당 조직의 실시간 데이터를 구독할 권한이 없습니다.");
            }
        }

        private Long extractOrganizationId(String destination) {
            if (destination == null) {
                throw new AccessDeniedException("잘못된 구독 경로입니다.");
            }

            String[] parts = destination.split("/");
            try {
                return Long.parseLong(parts[3]);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                throw new AccessDeniedException("잘못된 구독 경로입니다.");
            }
        }

        private MemberPrincipal resolvePrincipal(Principal user) {
            if (user instanceof Authentication authentication
                && authentication.getPrincipal() instanceof MemberPrincipal principal) {
                return principal;
            }

            throw new AccessDeniedException("인증되지 않은 사용자는 구독할 수 없습니다.");
        }
    }
}
