package com.baeldung.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import com.baeldung.convert.BytesToClaimsHolderConverter;
import com.baeldung.convert.BytesToOAuth2AuthorizationRequestConverter;
import com.baeldung.convert.BytesToUsernamePasswordAuthenticationTokenConverter;
import com.baeldung.convert.ClaimsHolderToBytesConverter;
import com.baeldung.convert.OAuth2AuthorizationRequestToBytesConverter;
import com.baeldung.convert.UsernamePasswordAuthenticationTokenToBytesConverter;
import com.baeldung.repository.OAuth2AuthorizationGrantAuthorizationRepository;
import com.baeldung.repository.OAuth2RegisteredClientRepository;
import com.baeldung.repository.OAuth2UserConsentRepository;
import com.baeldung.service.RedisOAuth2AuthorizationConsentService;
import com.baeldung.service.RedisOAuth2AuthorizationService;
import com.baeldung.service.RedisRegisteredClientRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Configuration(proxyBeanMethods = false)
@EnableRedisRepositories
public class RedisConfig {

    private final String redisHost;
    private final int redisPort;
    private final RedisServer redisServer;

    public RedisConfig(@Value("${spring.data.redis.host}") String redisHost, @Value("${spring.data.redis.port}") int redisPort) throws IOException {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisServer = new RedisServer(redisPort);
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }

    @Bean
    @Order(1)
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    @Order(2)
    public RedisTemplate<?, ?> redisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    @Order(3)
    public RedisCustomConversions redisCustomConversions() {
        return new RedisCustomConversions(
          Arrays.asList(new UsernamePasswordAuthenticationTokenToBytesConverter(), new BytesToUsernamePasswordAuthenticationTokenConverter(),
            new OAuth2AuthorizationRequestToBytesConverter(), new BytesToOAuth2AuthorizationRequestConverter(), new ClaimsHolderToBytesConverter(),
            new BytesToClaimsHolderConverter()));
    }

    @Bean
    @Order(4)
    public RedisRegisteredClientRepository registeredClientRepository(OAuth2RegisteredClientRepository registeredClientRepository) {
        RedisRegisteredClientRepository redisRegisteredClientRepository = new RedisRegisteredClientRepository(registeredClientRepository);
        redisRegisteredClientRepository.save(RegisteredClients.messagingClient());

        return redisRegisteredClientRepository;
    }

    @Bean
    @Order(5)
    public RedisOAuth2AuthorizationService authorizationService(RegisteredClientRepository registeredClientRepository,
      OAuth2AuthorizationGrantAuthorizationRepository authorizationGrantAuthorizationRepository) {
        return new RedisOAuth2AuthorizationService(registeredClientRepository, authorizationGrantAuthorizationRepository);
    }

    @Bean
    @Order(6)
    public RedisOAuth2AuthorizationConsentService authorizationConsentService(OAuth2UserConsentRepository userConsentRepository) {
        return new RedisOAuth2AuthorizationConsentService(userConsentRepository);
    }
}
