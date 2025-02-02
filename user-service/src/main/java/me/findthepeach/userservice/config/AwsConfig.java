package me.findthepeach.userservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {
    private final CognitoProperties cognitoProperties;

    @Value("${aws.profile}")
    private String awsProfile;

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient.builder()
                .region(Region.of(cognitoProperties.getRegion()))
                .credentialsProvider(ProfileCredentialsProvider.create(awsProfile))
                .build();
    }
}