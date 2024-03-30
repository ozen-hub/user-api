package com.lms.userapi.service;

import com.lms.userapi.dto.User;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class UserService {
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

   public void createUser(
           String username, String password, String firstName, String lastName, String email
   ){
       User user = User.builder()
               .username(username)
               .email(email)
               .firstName(firstName)
               .lastName(lastName)
               .password(password)
               .build();

       UserRepresentation userRep = mapUser(user);

       Keycloak keycloak = getKeycloakInstance();
       Response response = keycloak.realm(realm).users().create(userRep);

       if (response.getStatus() == Response.Status.CREATED.getStatusCode()){
           RoleRepresentation userRole = keycloak.realm(realm).roles().get("user").toRepresentation();
           String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
           keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(userRole));
       }

   }

    private Keycloak getKeycloakInstance() {
       return KeycloakBuilder.builder()
               .serverUrl(serverUrl)
               .realm(realm)
               .username("host")
               .password("1234")
               .clientSecret(clientSecret)
               .clientId(clientId)
               .build();
    }

    private UserRepresentation mapUser(User user) {
       UserRepresentation ur = new UserRepresentation();
       ur.setUsername(user.getUsername());
       ur.setLastName(user.getLastName());
       ur.setFirstName(user.getFirstName());
       ur.setEmail(user.getEmail());

       ur.setEnabled(true);
       ur.setEmailVerified(true);

       List<CredentialRepresentation> creds = new ArrayList<>();
       CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
       credentialRepresentation.setTemporary(false);
       credentialRepresentation.setValue(user.getPassword());
       creds.add(credentialRepresentation);
       ur.setCredentials(creds);
       return  ur;

    }


   public Object login(String username, String password){
       MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
       requestBody.add("client_id",clientId);
       requestBody.add("grant_type", OAuth2Constants.PASSWORD);
       requestBody.add("username",username);
       requestBody.add("client_secret",clientSecret);
       requestBody.add("password",password);

       String keyCloakApiUrl= "http://localhost:8080/realms/lms/protocol/openid-connect/token";

       HttpHeaders headers =  new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

     /*  HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
*/
       RestTemplate restTemplate = new RestTemplate();

       ResponseEntity<Object> response = restTemplate.postForEntity(keyCloakApiUrl, requestBody, Object.class);

       return response.getBody();
   }
}
