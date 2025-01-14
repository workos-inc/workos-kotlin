package com.workos.test.usermanagement

import com.workos.common.models.ListMetadata
import com.workos.common.models.Order
import com.workos.test.TestBase
import com.workos.usermanagement.builders.AuthenticationAdditionalOptionsBuilder
import com.workos.usermanagement.builders.CreateMagicAuthOptionsBuilder
import com.workos.usermanagement.builders.CreateOrganizationMembershipOptionsBuilder
import com.workos.usermanagement.builders.CreatePasswordResetOptionsBuilder
import com.workos.usermanagement.builders.CreateUserOptionsBuilder
import com.workos.usermanagement.builders.EnrolledAuthenticationFactorOptionsBuilder
import com.workos.usermanagement.builders.ListInvitationsOptionsBuilder
import com.workos.usermanagement.builders.ListOrganizationMembershipsOptionsBuilder
import com.workos.usermanagement.builders.ListUsersOptionsBuilder
import com.workos.usermanagement.builders.SendInvitationOptionsBuilder
import com.workos.usermanagement.builders.UpdateUserOptionsBuilder
import com.workos.usermanagement.models.AuthenticationChallenge
import com.workos.usermanagement.models.AuthenticationFactor
import com.workos.usermanagement.models.AuthenticationTotp
import com.workos.usermanagement.models.EmailVerification
import com.workos.usermanagement.models.EnrolledAuthenticationFactor
import com.workos.usermanagement.models.Identity
import com.workos.usermanagement.models.Invitation
import com.workos.usermanagement.models.MagicAuth
import com.workos.usermanagement.models.OrganizationMembership
import com.workos.usermanagement.models.OrganizationMembershipRole
import com.workos.usermanagement.models.PasswordReset
import com.workos.usermanagement.models.RefreshAuthentication
import com.workos.usermanagement.models.User
import com.workos.usermanagement.types.IdentityProviderEnumType
import com.workos.usermanagement.types.InvitationStateEnumType
import com.workos.usermanagement.types.OrganizationMembershipStatusEnumType
import com.workos.usermanagement.types.UserManagementProviderEnumType
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class UserManagementApiTest : TestBase() {
  val workos = createWorkOSClient()

  @Test
  fun getUserShouldReturnValidUserObject() {
    stubResponse(
      "/user_management/users/user_123",
      """{
        "object": "user",
        "id": "user_123",
        "email": "test01@example.com",
        "email_verified": true,
        "profile_picture_url": "https://example.com/profile_picture.jpg",
        "first_name": "Test",
        "last_name": "User",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val user = workos.userManagement.getUser("user_123")

    assertEquals(
      User(
        "user_123",
        "test01@example.com",
        "Test",
        "User",
        true,
        "https://example.com/profile_picture.jpg",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      user
    )
  }

  @Test
  fun listUsersShouldReturnValidUsers() {
    stubResponse(
      "/user_management/users",
      """{
        "data": [
          {
            "object": "user",
            "id": "user_123",
            "email": "test01@example.com",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "user_234"
        }
      }"""
    )

    val users = workos.userManagement.listUsers(ListUsersOptionsBuilder().build())

    assertEquals(
      User(
        "user_123",
        "test01@example.com",
        null,
        null,
        false,
        null,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      users.data[0]
    )
    assertEquals(ListMetadata(null, "user_234"), users.listMetadata)
  }

  @Test
  fun listUsersShouldReturnValidUsersWithPaginationAndFilters() {
    stubResponse(
      "/user_management/users",
      """{
        "data": [
          {
            "object": "user",
            "id": "user_123",
            "email": "test01@example.com",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "user_234"
        }
      }"""
    )

    val options =
      ListUsersOptionsBuilder()
        .email("test01@example.com")
        .organizationId("org_123")
        .order(Order.Desc)
        .limit(10)
        .after("someAfterId")
        .before("someBeforeId")
        .build()

    val users = workos.userManagement.listUsers(options)

    assertEquals(
      User(
        "user_123",
        "test01@example.com",
        null,
        null,
        false,
        null,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      users.data[0]
    )
    assertEquals(ListMetadata(null, "user_234"), users.listMetadata)
  }

  @Test
  fun createUserShouldReturnValidUserObject() {
    stubResponse(
      "/user_management/users",
      """{
        "object": "user",
        "id": "user_123",
        "email": "test01@example.com",
        "email_verified": true,
        "profile_picture_url": null,
        "first_name": "Test",
        "last_name": "User",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody =
      """{
        "email": "test01@example.com",
        "password": "password",
        "first_name": "Test",
        "last_name": "User",
        "email_verified": true
      }"""
    )

    val options =
      CreateUserOptionsBuilder("test01@example.com")
        .password("password")
        .firstName("Test")
        .lastName("User")
        .emailVerified(true)
        .build()

    val user = workos.userManagement.createUser(options)

    assertEquals(
      User(
        "user_123",
        "test01@example.com",
        "Test",
        "User",
        true,
        null,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      user
    )
  }

  @Test
  fun updateUserShouldReturnValidUserObject() {
    stubResponse(
      "/user_management/users/user_123",
      """{
        "object": "user",
        "id": "user_123",
        "email": "test01@example.com",
        "email_verified": true,
        "profile_picture_url": null,
        "first_name": "Test",
        "last_name": "User",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody =
      """{
        "id": "user_123",
        "password": "password",
        "first_name": "Test",
        "last_name": "User",
        "email_verified": true
      }"""
    )

    val options =
      UpdateUserOptionsBuilder("user_123")
        .password("password")
        .firstName("Test")
        .lastName("User")
        .emailVerified(true)
        .build()

    val user = workos.userManagement.updateUser("user_123", options)

    assertEquals(
      User(
        "user_123",
        "test01@example.com",
        "Test",
        "User",
        true,
        null,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      user
    )
  }

  @Test
  fun deleteUserShouldWorkAndReturnNothing() {
    stubResponse("/user_management/users/user_123", "")

    assertDoesNotThrow() { workos.userManagement.deleteUser("user_123") }
  }

  @Test
  fun getUserIdentitiesShouldReturnValidIdentitiesObject() {
    stubResponse(
      "/user_management/users/user_123/identities",
      """[{
        "idp_id": "idp_123",
        "type": "OAuth",
        "provider": "MicrosoftOAuth"
      }]"""
    )

    val identities = workos.userManagement.getUserIdentities("user_123")

    assertEquals(
      Identity(
        "idp_123",
        "OAuth",
        IdentityProviderEnumType.MicrosoftOAuth,
      ),
      identities[0]
    )
  }

  @Test
  fun getAuthorizationUrlShouldReturnValidUrl() {
    val url =
      workos
        .userManagement
        .getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
        .provider(UserManagementProviderEnumType.AuthKit)
        .build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/user_management/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code&provider=authkit",
      url
    )
  }

  @Test
  fun getAuthorizationUrlShouldAcceptAdditionalParams() {
    val url =
      workos
        .userManagement
        .getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
        .connectionId("connection_value")
        .domainHint("domain_hint")
        .loginHint("login_hint")
        .screenHint("screen_hint")
        .provider(UserManagementProviderEnumType.AuthKit)
        .state("state_value")
        .build()

    assertEquals(
      "http://localhost:${getWireMockPort()}/user_management/authorize?client_id=client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fredirect&response_type=code&connection_id=connection_value&domain_hint=domain_hint&login_hint=login_hint&screen_hint=screen_hint&provider=authkit&state=state_value",
      url
    )
  }

  @Test
  fun getAuthorizationUrlShouldValidateUrlParams() {
    assertThrows(IllegalArgumentException::class.java) {
      workos
        .userManagement
        .getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
        .build()
    }
  }

  @Test
  fun getAuthorizationUrlShouldValidateUrlAdditionalParams() {
    assertThrows(IllegalArgumentException::class.java) {
      workos
        .userManagement
        .getAuthorizationUrl("client_id", "http://localhost:8080/redirect")
        .connectionId("connection_value")
        .domainHint("domain_hint")
        .loginHint("login_hint")
        .screenHint("screen_hint")
        .provider(UserManagementProviderEnumType.MicrosoftOAuth)
        .state("state_value")
        .build()
    }
  }

  @Test
  fun authenticateWithCodeShouldReturnAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/user_management/authenticate",
      """{
        "user": {
          "object": "user",
          "id": "user_123",
          "email": "test01@example.com",
          "first_name": "Test",
          "last_name": "User",
          "email_verified": true,
          "profile_picture_url": null,
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        },
        "organization_id": "org_456",
        "access_token": "access_token",
        "refresh_token": "refresh_token",
        "impersonator": {
          "email": "admin@foocorp.com",
          "reason": "Investigating an issue with the customer's account."
        }
      }""",
      requestBody =
      """{
        "client_id": "client_id",
        "client_secret": "apiKey",
        "grant_type": "authorization_code",
        "code": "code_123",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0"
      }"""
    )

    val response =
      workos.userManagement.authenticateWithCode(
        "client_id",
        "code_123",
        AuthenticationAdditionalOptionsBuilder()
          .ipAddress("192.0.2.1")
          .userAgent("Mozilla/5.0")
          .build()
      )

    assertEquals("test01@example.com", response.user?.email)
  }

  @Test
  fun authenticateWithPasswordShouldReturnAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/user_management/authenticate",
      """{
        "user": {
          "object": "user",
          "id": "user_123",
          "email": "test01@example.com",
          "first_name": "Test",
          "last_name": "User",
          "email_verified": true,
          "profile_picture_url": null,
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        },
        "organization_id": "org_456",
        "access_token": "access_token",
        "refresh_token": "refresh_token"
      }""",
      requestBody =
      """{
        "client_id": "client_id",
        "client_secret": "apiKey",
        "grant_type": "password",
        "email": "test01@example.com",
        "password": "password",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0"
      }"""
    )

    val response =
      workos.userManagement.authenticateWithPassword(
        "client_id",
        "test01@example.com",
        "password",
        AuthenticationAdditionalOptionsBuilder()
          .ipAddress("192.0.2.1")
          .userAgent("Mozilla/5.0")
          .build()
      )

    assertEquals("test01@example.com", response.user?.email)
  }

  @Test
  fun authenticateWithMagicAuthShouldReturnAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/user_management/authenticate",
      """{
        "user": {
          "object": "user",
          "id": "user_123",
          "email": "test01@example.com",
          "first_name": "Test",
          "last_name": "User",
          "email_verified": true,
          "profile_picture_url": null,
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        },
        "organization_id": "org_456"
      }""",
      requestBody =
      """{
        "client_id": "client_id",
        "client_secret": "apiKey",
        "grant_type": "urn:workos:oauth:grant-type:magic-auth:code",
        "code": "code_123",
        "email": "test01@example.com",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0"
      }"""
    )

    val response =
      workos.userManagement.authenticateWithMagicAuth(
        "client_id",
        "test01@example.com",
        "code_123",
        AuthenticationAdditionalOptionsBuilder()
          .ipAddress("192.0.2.1")
          .userAgent("Mozilla/5.0")
          .build()
      )

    assertEquals("test01@example.com", response.user?.email)
  }

  @Test
  fun authenticateWithRefreshTokenShouldReturnAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/user_management/authenticate",
      """{
        "access_token": "access_token",
        "refresh_token": "refresh_token"
      }""",
      requestBody =
      """{
        "client_id": "client_id",
        "client_secret": "apiKey",
        "grant_type": "refresh_token",
        "refresh_token": "refresh_token",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0"
      }"""
    )

    val response =
      workos.userManagement.authenticateWithRefreshToken(
        "client_id",
        "refresh_token",
        null,
        AuthenticationAdditionalOptionsBuilder()
          .ipAddress("192.0.2.1")
          .userAgent("Mozilla/5.0")
          .build()
      )

    assertEquals(RefreshAuthentication("access_token", "refresh_token"), response)
  }

  @Test
  fun authenticateWithEmailVerificationShouldReturnAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/user_management/authenticate",
      """{
        "user": {
          "object": "user",
          "id": "user_123",
          "email": "test01@example.com",
          "first_name": "Test",
          "last_name": "User",
          "email_verified": true,
          "profile_picture_url": null,
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        },
        "organization_id": "org_456"
      }""",
      requestBody =
      """{
        "client_id": "client_id",
        "client_secret": "apiKey",
        "grant_type": "urn:workos:oauth:grant-type:email-verification:code",
        "code": "code_123",
        "pending_authentication_token": "token_456",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0"
      }"""
    )

    val response =
      workos.userManagement.authenticateWithEmailVerification(
        "client_id",
        "code_123",
        "token_456",
        AuthenticationAdditionalOptionsBuilder()
          .ipAddress("192.0.2.1")
          .userAgent("Mozilla/5.0")
          .build()
      )

    assertEquals("test01@example.com", response.user?.email)
  }

  @Test
  fun authenticateWithTotpShouldReturnAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/user_management/authenticate",
      """{
        "user": {
          "object": "user",
          "id": "user_123",
          "email": "test01@example.com",
          "first_name": "Test",
          "last_name": "User",
          "email_verified": true,
          "profile_picture_url": null,
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        },
        "organization_id": "org_456"
      }""",
      requestBody =
      """{
        "client_id": "client_id",
        "client_secret": "apiKey",
        "grant_type": "urn:workos:oauth:grant-type:mfa-totp",
        "code": "code_123",
        "authentication_challenge_id": "challenge_789",
        "pending_authentication_token": "token_456",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0"
      }"""
    )

    val response =
      workos.userManagement.authenticateWithTotp(
        "client_id",
        "code_123",
        "challenge_789",
        "token_456",
        AuthenticationAdditionalOptionsBuilder()
          .ipAddress("192.0.2.1")
          .userAgent("Mozilla/5.0")
          .build()
      )

    assertEquals("test01@example.com", response.user?.email)
  }

  @Test
  fun authenticateWithOrganizationSelectionShouldReturnAuthenticationResponse() {
    val workos = createWorkOSClient()

    stubResponse(
      "/user_management/authenticate",
      """{
        "user": {
          "object": "user",
          "id": "user_123",
          "email": "test01@example.com",
          "first_name": "Test",
          "last_name": "User",
          "email_verified": true,
          "profile_picture_url": null,
          "created_at": "2021-06-25T19:07:33.155Z",
          "updated_at": "2021-06-25T19:07:33.155Z"
        },
        "organization_id": "org_456"
      }""",
      requestBody =
      """{
        "client_id": "client_id",
        "client_secret": "apiKey",
        "grant_type": "urn:workos:oauth:grant-type:organization-selection",
        "organization_id": "org_123",
        "pending_authentication_token": "token_456",
        "ip_address": "192.0.2.1",
        "user_agent": "Mozilla/5.0"
      }"""
    )

    val response =
      workos.userManagement.authenticateWithOrganizationSelection(
        "client_id",
        "org_123",
        "token_456",
        AuthenticationAdditionalOptionsBuilder()
          .ipAddress("192.0.2.1")
          .userAgent("Mozilla/5.0")
          .build()
      )

    assertEquals("test01@example.com", response.user?.email)
  }

  @Test
  fun getJwksUrlShouldReturnValidUrlResponse() {
    val url = workos.userManagement.getJwksUrl("client_123")

    assertEquals("http://localhost:${getWireMockPort()}/sso/jwks/client_123", url)
  }

  @Test
  fun getMagicAuthShouldReturnValidMagicAuthObject() {
    stubResponse(
      "/user_management/magic_auth/magic_auth_123",
      """{
        "id": "magic_auth_123",
        "user_id": "user_123",
        "email": "test01@example.com",
        "expires_at": "2021-07-01T19:07:33.155Z",
        "code": "123456",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val magicAuth = workos.userManagement.getMagicAuth("magic_auth_123")

    assertEquals(
      MagicAuth(
        "magic_auth_123",
        "user_123",
        "test01@example.com",
        "2021-07-01T19:07:33.155Z",
        "123456",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      magicAuth
    )
  }

  @Test
  fun createMagicAuthShouldReturnValidMagicAuthObject() {
    stubResponse(
      "/user_management/magic_auth",
      """{
        "id": "magic_auth_123",
        "user_id": "user_123",
        "email": "test01@example.com",
        "expires_at": "2021-07-01T19:07:33.155Z",
        "code": "123456",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody =
      """{
        "email": "test01@example.com",
        "invitation_token": null
      }"""
    )

    val options = CreateMagicAuthOptionsBuilder("test01@example.com").build()

    val magicAuth = workos.userManagement.createMagicAuth(options)

    assertEquals(
      MagicAuth(
        "magic_auth_123",
        "user_123",
        "test01@example.com",
        "2021-07-01T19:07:33.155Z",
        "123456",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      magicAuth
    )
  }

  @Test
  fun sendMagicAuthCodeShouldWorkAndReturnNothing() {
    stubResponse("/user_management/magic_auth/send", "")

    assertDoesNotThrow() { workos.userManagement.sendMagicAuthCode("test01@example.com") }
  }

  @Test
  fun enrollAuthFactorShouldReturnValidEnrolledAuthenticationFactorObject() {
    stubResponse(
      "/user_management/users/user_123/auth_factors",
      """{
        "challenge": {
          "object": "authentication_challenge",
          "id": "auth_challenge_123",
          "created_at": "2022-02-15T15:26:53.274Z",
          "updated_at": "2022-02-15T15:26:53.274Z",
          "expires_at": "2022-02-15T15:36:53.279Z",
          "authentication_factor_id": "auth_factor_123"
        },
        "factor": {
          "object": "authentication_factor",
          "id": "auth_factor_123",
          "created_at": "2022-02-15T15:14:19.392Z",
          "updated_at": "2022-02-15T15:14:19.392Z",
          "type": "totp",
          "totp": {
            "issuer": "Foo Corp",
            "user": "alan.turing@example.com",
            "qr_code": "data:image/png;base64,{base64EncodedPng}",
            "secret": "secret",
            "uri": "otpauth://totp/FooCorp:alan.turing@example.com?secret=secret&issuer=FooCorp"
          },
          "user_id": "user_123"
        }
      }""",
      requestBody =
      """{
        "type": "totp",
        "totp_issuer": "Foo Corp",
        "totp_user": "test01@example.com"
      }"""
    )

    val options =
      EnrolledAuthenticationFactorOptionsBuilder()
        .totpUser("test01@example.com")
        .totpIssuer("Foo Corp")
        .build()

    val enrolledAuthenticationFactor = workos.userManagement.enrollAuthFactor("user_123", options)

    assertEquals(
      EnrolledAuthenticationFactor(
        AuthenticationChallenge(
          "auth_challenge_123",
          "2022-02-15T15:26:53.274Z",
          "2022-02-15T15:26:53.274Z",
          "2022-02-15T15:36:53.279Z",
          "auth_factor_123"
        ),
        AuthenticationFactor(
          "auth_factor_123",
          "2022-02-15T15:14:19.392Z",
          "2022-02-15T15:14:19.392Z",
          "totp",
          AuthenticationTotp(
            "Foo Corp",
            "alan.turing@example.com",
            "data:image/png;base64,{base64EncodedPng}",
            "secret",
            "otpauth://totp/FooCorp:alan.turing@example.com?secret=secret&issuer=FooCorp"
          ),
          "user_123"
        ),
      ),
      enrolledAuthenticationFactor
    )
  }

  @Test
  fun listAuthenticationFactorsShouldReturnValidAuthenticationFactors() {
    stubResponse(
      "/user_management/users/user_123/auth_factors",
      """{
        "data": [
          {
            "object": "authentication_factor",
            "id": "auth_factor_123",
            "created_at": "2022-02-15T15:14:19.392Z",
            "updated_at": "2022-02-15T15:14:19.392Z",
            "type": "totp",
            "totp": {
              "issuer": "Foo Corp",
              "user": "alan.turing@example.com",
              "qr_code": "data:image/png;base64,{base64EncodedPng}",
              "secret": "secret",
              "uri": "otpauth://totp/FooCorp:alan.turing@example.com?secret=secret&issuer=FooCorp"
            },
            "user_id": "user_123"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "auth_factor_234"
        }
      }"""
    )

    val authenticationFactors = workos.userManagement.listAuthFactors("user_123")

    assertEquals(
      AuthenticationFactor(
        "auth_factor_123",
        "2022-02-15T15:14:19.392Z",
        "2022-02-15T15:14:19.392Z",
        "totp",
        AuthenticationTotp(
          "Foo Corp",
          "alan.turing@example.com",
          "data:image/png;base64,{base64EncodedPng}",
          "secret",
          "otpauth://totp/FooCorp:alan.turing@example.com?secret=secret&issuer=FooCorp"
        ),
        "user_123"
      ),
      authenticationFactors.data[0]
    )
    assertEquals(ListMetadata(null, "auth_factor_234"), authenticationFactors.listMetadata)
  }

  @Test
  fun getEmailVerificationShouldReturnValidEmailVerificationObject() {
    stubResponse(
      "/user_management/email_verification/email_verification_123",
      """{
        "id": "email_verification_123",
        "user_id": "user_123",
        "email": "test01@example.com",
        "expires_at": "2021-07-01T19:07:33.155Z",
        "code": "123456",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val emailVerification = workos.userManagement.getEmailVerification("email_verification_123")

    assertEquals(
      EmailVerification(
        "email_verification_123",
        "user_123",
        "test01@example.com",
        "2021-07-01T19:07:33.155Z",
        "123456",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      emailVerification
    )
  }

  @Test
  fun getPasswordResetShouldReturnValidPasswordResetObject() {
    stubResponse(
      "/user_management/password_reset/password_reset_123",
      """{
        "id": "password_reset_123",
        "user_id": "user_123",
        "email": "test01@example.com",
        "password_reset_token": "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "password_reset_url": "https://your-app.com/reset-password?token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "expires_at": "2021-07-01T19:07:33.155Z",
        "created_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val passwordReset = workos.userManagement.getPasswordReset("password_reset_123")

    assertEquals(
      PasswordReset(
        "password_reset_123",
        "user_123",
        "test01@example.com",
        "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "https://your-app.com/reset-password?token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "2021-07-01T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      passwordReset
    )
  }

  @Test
  fun createPasswordResetShouldReturnValidPasswordResetObject() {
    stubResponse(
      "/user_management/password_reset",
      """{
        "id": "password_reset_123",
        "user_id": "user_123",
        "email": "test01@example.com",
        "password_reset_token": "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "password_reset_url": "https://your-app.com/reset-password?token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "expires_at": "2021-07-01T19:07:33.155Z",
        "created_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody = """{
        "email": "test01@example.com"
      }"""
    )

    val options = CreatePasswordResetOptionsBuilder("test01@example.com").build()

    val passwordReset = workos.userManagement.createPasswordReset(options)

    assertEquals(
      PasswordReset(
        "password_reset_123",
        "user_123",
        "test01@example.com",
        "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "https://your-app.com/reset-password?token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "2021-07-01T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      passwordReset
    )
  }

  @Test
  fun sendPasswordResetEmailShouldWorkAndReturnNothing() {
    stubResponse("/user_management/password_reset/send", "")

    assertDoesNotThrow() {
      workos.userManagement.sendPasswordResetEmail(
        "test01@example.com",
        "https://your-app.com/reset-password"
      )
    }
  }

  @Test
  fun resetPasswordShouldReturnValidUserObject() {
    stubResponse(
      "/user_management/password_reset/confirm",
      """{
        "object": "user",
        "id": "user_123",
        "email": "test01@example.com",
        "email_verified": true,
        "profile_picture_url": null,
        "first_name": "Test",
        "last_name": "User",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody =
      """{
        "token": "token_123",
        "new_password": "new_password"
      }"""
    )

    val user = workos.userManagement.resetPassword("token_123", "new_password")

    assertEquals(
      User(
        "user_123",
        "test01@example.com",
        "Test",
        "User",
        true,
        null,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      user
    )
  }

  @Test
  fun getOrganizationMembershipShouldReturnValidOrganizationMembershipObject() {
    stubResponse(
      "/user_management/organization_memberships/om_123",
      """{
        "object": "organization_membership",
        "id": "om_123",
        "user_id": "user_456",
        "organization_id": "org_789",
        "role": {
          "slug": "admin"
        },
        "status": "active",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val organizationMembership = workos.userManagement.getOrganizationMembership("om_123")

    assertEquals(
      OrganizationMembership(
        "om_123",
        "user_456",
        "org_789",
        OrganizationMembershipRole("admin"),
        OrganizationMembershipStatusEnumType.Active,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z",
      ),
      organizationMembership
    )
  }

  @Test
  fun listOrganizationMembershipsShouldReturnValidOrganizationMemberships() {
    stubResponse(
      "/user_management/organization_memberships",
      """{
        "data": [
          {
            "object": "organization_membership",
            "id": "om_123",
            "user_id": "user_456",
            "organization_id": "org_789",
            "role": {
              "slug": "admin"
            },
            "status": "active",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "om_234"
        }
      }"""
    )

    val organizationMemberships =
      workos.userManagement.listOrganizationMemberships(
        ListOrganizationMembershipsOptionsBuilder().build()
      )

    assertEquals(
      OrganizationMembership(
        "om_123",
        "user_456",
        "org_789",
        OrganizationMembershipRole("admin"),
        OrganizationMembershipStatusEnumType.Active,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z",
      ),
      organizationMemberships.data[0]
    )
    assertEquals(ListMetadata(null, "om_234"), organizationMemberships.listMetadata)
  }

  @Test
  fun listOrganizationMembershipsShouldReturnValidOrganizationMembershipsWithPaginationAndFilters() {
    stubResponse(
      "/user_management/organization_memberships",
      """{
        "data": [
          {
            "object": "organization_membership",
            "id": "om_123",
            "user_id": "user_456",
            "organization_id": "org_789",
            "role": {
              "slug": "admin"
            },
            "status": "active",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "om_234"
        }
      }"""
    )

    val options =
      ListOrganizationMembershipsOptionsBuilder()
        .userId("id_456")
        .organizationId("org_789")
        .statuses(
          listOf<OrganizationMembershipStatusEnumType>(
            OrganizationMembershipStatusEnumType.Active,
            OrganizationMembershipStatusEnumType.Inactive
          )
        )
        .order(Order.Desc)
        .limit(10)
        .after("someAfterId")
        .before("someBeforeId")
        .build()

    val organizationMemberships = workos.userManagement.listOrganizationMemberships(options)

    assertEquals(
      OrganizationMembership(
        "om_123",
        "user_456",
        "org_789",
        OrganizationMembershipRole("admin"),
        OrganizationMembershipStatusEnumType.Active,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z",
      ),
      organizationMemberships.data[0]
    )
    assertEquals(ListMetadata(null, "om_234"), organizationMemberships.listMetadata)
  }

  @Test
  fun createOrganizationMembershipShouldReturnValidOrganizationMembershipObject() {
    stubResponse(
      "/user_management/organization_memberships",
      """{
        "object": "organization_membership",
        "id": "om_123",
        "user_id": "user_456",
        "organization_id": "org_789",
        "role": {
          "slug": "admin"
        },
        "status": "active",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody =
      """{
        "user_id": "user_456",
        "organization_id": "org_789",
        "role_slug": "admin"
      }"""
    )

    val options =
      CreateOrganizationMembershipOptionsBuilder("user_456", "org_789").roleSlug("admin").build()

    val organizationMembership = workos.userManagement.createOrganizationMembership(options)

    assertEquals(
      OrganizationMembership(
        "om_123",
        "user_456",
        "org_789",
        OrganizationMembershipRole("admin"),
        OrganizationMembershipStatusEnumType.Active,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z",
      ),
      organizationMembership
    )
  }

  @Test
  fun updateOrganizationMembershipShouldReturnValidOrganizationMembershipObject() {
    stubResponse(
      "/user_management/organization_memberships/om_123",
      """{
        "object": "organization_membership",
        "id": "om_123",
        "user_id": "user_456",
        "organization_id": "org_789",
        "role": {
          "slug": "member"
        },
        "status": "active",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody = """{
        "id": "om_123",
        "role_slug": "member"
      }"""
    )

    val organizationMembership =
      workos.userManagement.updateOrganizationMembership("om_123", "member")

    assertEquals(
      OrganizationMembership(
        "om_123",
        "user_456",
        "org_789",
        OrganizationMembershipRole("member"),
        OrganizationMembershipStatusEnumType.Active,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z",
      ),
      organizationMembership
    )
  }

  @Test
  fun deleteOrganizationMembershipShouldWorkAndReturnNothing() {
    stubResponse("/user_management/organization_memberships/om_123", "")

    assertDoesNotThrow() { workos.userManagement.deleteOrganizationMembership("om_123") }
  }

  @Test
  fun deactivateOrganizationMembershipShouldReturnValidOrganizationMembershipObject() {
    stubResponse(
      "/user_management/organization_memberships/om_123/deactivate",
      """{
        "object": "organization_membership",
        "id": "om_123",
        "user_id": "user_456",
        "organization_id": "org_789",
        "role": {
          "slug": "admin"
        },
        "status": "inactive",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val organizationMembership = workos.userManagement.deactivateOrganizationMembership("om_123")

    assertEquals(
      OrganizationMembership(
        "om_123",
        "user_456",
        "org_789",
        OrganizationMembershipRole("admin"),
        OrganizationMembershipStatusEnumType.Inactive,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z",
      ),
      organizationMembership
    )
  }

  @Test
  fun reactivateOrganizationMembershipShouldReturnValidOrganizationMembershipObject() {
    stubResponse(
      "/user_management/organization_memberships/om_123/reactivate",
      """{
        "object": "organization_membership",
        "id": "om_123",
        "user_id": "user_456",
        "organization_id": "org_789",
        "role": {
          "slug": "admin"
        },
        "status": "active",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val organizationMembership = workos.userManagement.reactivateOrganizationMembership("om_123")

    assertEquals(
      OrganizationMembership(
        "om_123",
        "user_456",
        "org_789",
        OrganizationMembershipRole("admin"),
        OrganizationMembershipStatusEnumType.Active,
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z",
      ),
      organizationMembership
    )
  }

  @Test
  fun getInvitationShouldReturnValidUserObject() {
    stubResponse(
      "/user_management/invitations/invitation_123",
      """{
        "id": "invitation_123",
        "email": "test01@example.com",
        "state": "pending",
        "accepted_at": null,
        "revoked_at": null,
        "expires_at": "2021-07-01T19:07:33.155Z",
        "token": "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "accept_invitation_url": "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "organization_id": "org_456",
        "inviter_user_id": "user_789",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val invitation = workos.userManagement.getInvitation("invitation_123")

    assertEquals(
      Invitation(
        "invitation_123",
        "test01@example.com",
        InvitationStateEnumType.Pending,
        null,
        null,
        "2021-07-01T19:07:33.155Z",
        "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "org_456",
        "user_789",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      invitation
    )
  }

  @Test
  fun findInvitationByTokenShouldReturnValidUserObject() {
    stubResponse(
      "/user_management/invitations/by_token/Z1uX3RbwcIl5fIGJJJCXXisdI",
      """{
        "id": "invitation_123",
        "email": "test01@example.com",
        "state": "pending",
        "accepted_at": null,
        "revoked_at": null,
        "expires_at": "2021-07-01T19:07:33.155Z",
        "token": "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "accept_invitation_url": "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "organization_id": "org_456",
        "inviter_user_id": "user_789",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val invitation = workos.userManagement.findInvitationByToken("Z1uX3RbwcIl5fIGJJJCXXisdI")

    assertEquals(
      Invitation(
        "invitation_123",
        "test01@example.com",
        InvitationStateEnumType.Pending,
        null,
        null,
        "2021-07-01T19:07:33.155Z",
        "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "org_456",
        "user_789",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      invitation
    )
  }

  @Test
  fun listInvitationsShouldReturnValidInvitations() {
    stubResponse(
      "/user_management/invitations",
      """{
        "data": [
          {
            "id": "invitation_123",
            "email": "test01@example.com",
            "state": "pending",
            "accepted_at": null,
            "revoked_at": null,
            "expires_at": "2021-07-01T19:07:33.155Z",
            "token": "Z1uX3RbwcIl5fIGJJJCXXisdI",
            "accept_invitation_url": "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
            "organization_id": "org_456",
            "inviter_user_id": "user_789",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "invitation_234"
        }
      }"""
    )

    val invitations = workos.userManagement.listInvitations(ListInvitationsOptionsBuilder().build())

    assertEquals(
      Invitation(
        "invitation_123",
        "test01@example.com",
        InvitationStateEnumType.Pending,
        null,
        null,
        "2021-07-01T19:07:33.155Z",
        "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "org_456",
        "user_789",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      invitations.data[0]
    )
    assertEquals(ListMetadata(null, "invitation_234"), invitations.listMetadata)
  }

  @Test
  fun listInvitationsShouldReturnValidInvitationsWithPaginationAndFilters() {
    stubResponse(
      "/user_management/invitations",
      """{
        "data": [
          {
            "id": "invitation_123",
            "email": "test01@example.com",
            "state": "pending",
            "accepted_at": null,
            "revoked_at": null,
            "expires_at": "2021-07-01T19:07:33.155Z",
            "token": "Z1uX3RbwcIl5fIGJJJCXXisdI",
            "accept_invitation_url": "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
            "organization_id": "org_456",
            "inviter_user_id": "user_789",
            "created_at": "2021-06-25T19:07:33.155Z",
            "updated_at": "2021-06-25T19:07:33.155Z"
          }
        ],
        "list_metadata": {
          "after": null,
          "before": "invitation_234"
        }
      }"""
    )

    val options =
      ListInvitationsOptionsBuilder()
        .email("test01@example.com")
        .organizationId("org_123")
        .order(Order.Desc)
        .limit(10)
        .after("someAfterId")
        .before("someBeforeId")
        .build()

    val invitations = workos.userManagement.listInvitations(options)

    assertEquals(
      Invitation(
        "invitation_123",
        "test01@example.com",
        InvitationStateEnumType.Pending,
        null,
        null,
        "2021-07-01T19:07:33.155Z",
        "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "org_456",
        "user_789",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      invitations.data[0]
    )
    assertEquals(ListMetadata(null, "invitation_234"), invitations.listMetadata)
  }

  @Test
  fun sendInvitationShouldReturnValidInvitationObject() {
    stubResponse(
      "/user_management/invitations",
      """{
        "id": "invitation_123",
        "email": "test01@example.com",
        "state": "pending",
        "accepted_at": null,
        "revoked_at": null,
        "expires_at": "2021-07-01T19:07:33.155Z",
        "token": "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "accept_invitation_url": "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "inviter_user_id": "user_789",
        "organization_id": "org_456",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }""",
      requestBody =
      """{
        "email": "test01@example.com",
        "organization_id": "org_456",
        "expires_in_days": 10,
        "inviter_user_id": "user_789",
        "role_slug": "admin"
      }"""
    )

    val options =
      SendInvitationOptionsBuilder("test01@example.com")
        .organizationId("org_456")
        .expiresInDays(10)
        .inviterUserId("user_789")
        .roleSlug("admin")
        .build()

    val invitation = workos.userManagement.sendInvitation(options)

    assertEquals(
      Invitation(
        "invitation_123",
        "test01@example.com",
        InvitationStateEnumType.Pending,
        null,
        null,
        "2021-07-01T19:07:33.155Z",
        "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "org_456",
        "user_789",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      invitation
    )
  }

  @Test
  fun revokeInvitationShouldWorkAndReturnValidInvitation() {
    stubResponse(
      "/user_management/invitations/invitation_123/revoke",
      """{
        "id": "invitation_123",
        "email": "test01@example.com",
        "state": "pending",
        "accepted_at": null,
        "revoked_at": "2021-08-01T19:07:33.155Z",
        "expires_at": "2021-07-01T19:07:33.155Z",
        "token": "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "accept_invitation_url": "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "inviter_user_id": "user_789",
        "organization_id": "org_456",
        "created_at": "2021-06-25T19:07:33.155Z",
        "updated_at": "2021-06-25T19:07:33.155Z"
      }"""
    )

    val invitation = workos.userManagement.revokeInvitation("invitation_123")

    assertEquals(
      Invitation(
        "invitation_123",
        "test01@example.com",
        InvitationStateEnumType.Pending,
        null,
        "2021-08-01T19:07:33.155Z",
        "2021-07-01T19:07:33.155Z",
        "Z1uX3RbwcIl5fIGJJJCXXisdI",
        "https://your-app.com/invite?invitation_token=Z1uX3RbwcIl5fIGJJJCXXisdI",
        "org_456",
        "user_789",
        "2021-06-25T19:07:33.155Z",
        "2021-06-25T19:07:33.155Z"
      ),
      invitation
    )
  }

  @Test
  fun getLogoutUrlShouldReturnValidUrlResponse() {
    val url = workos.userManagement.getLogoutUrl("session_123")

    assertEquals(
      "http://localhost:${getWireMockPort()}/user_management/sessions/logout?session_id=session_123",
      url
    )
  }

  @Test
  fun getLogoutUrlWithReturnToShouldReturnValidUrlResponse() {
    val url = workos.userManagement.getLogoutUrl("session_123", returnTo = "https://your-app.com")

    assertEquals(
      "http://localhost:${getWireMockPort()}/user_management/sessions/logout?session_id=session_123&return_to=https%3A%2F%2Fyour-app.com",
      url
    )
  }
}
