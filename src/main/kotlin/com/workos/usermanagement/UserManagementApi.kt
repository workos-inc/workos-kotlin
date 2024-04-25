package com.workos.usermanagement

import com.workos.WorkOS
import com.workos.common.http.RequestConfig
import com.workos.usermanagement.builders.AuthenticationWithCodeOptionsBuilder
import com.workos.usermanagement.builders.AuthenticationWithEmailVerificationOptionsBuilder
import com.workos.usermanagement.builders.AuthenticationWithMagicAuthOptionsBuilder
import com.workos.usermanagement.builders.AuthenticationWithOrganizationSelectionOptionsBuilder
import com.workos.usermanagement.builders.AuthenticationWithPasswordOptionsBuilder
import com.workos.usermanagement.builders.AuthenticationWithRefreshTokenOptionsBuilder
import com.workos.usermanagement.builders.AuthenticationWithTotpOptionsBuilder
import com.workos.usermanagement.builders.AuthorizationUrlOptionsBuilder
import com.workos.usermanagement.builders.ResetPasswordOptionsBuilder
import com.workos.usermanagement.builders.SendMagicAuthCodeOptionsBuilder
import com.workos.usermanagement.builders.SendPasswordResetEmailOptionsBuilder
import com.workos.usermanagement.builders.UpdateOrganizationMembershipOptionsBuilder
import com.workos.usermanagement.models.Authentication
import com.workos.usermanagement.models.AuthenticationFactors
import com.workos.usermanagement.models.EnrolledAuthenticationFactor
import com.workos.usermanagement.models.Identity
import com.workos.usermanagement.models.Invitation
import com.workos.usermanagement.models.Invitations
import com.workos.usermanagement.models.OrganizationMembership
import com.workos.usermanagement.models.OrganizationMemberships
import com.workos.usermanagement.models.RefreshAuthentication
import com.workos.usermanagement.models.User
import com.workos.usermanagement.models.Users
import com.workos.usermanagement.types.AuthenticationAdditionalOptions
import com.workos.usermanagement.types.CreateOrganizationMembershipOptions
import com.workos.usermanagement.types.CreateUserOptions
import com.workos.usermanagement.types.EnrolledAuthenticationFactorOptions
import com.workos.usermanagement.types.ListInvitationsOptions
import com.workos.usermanagement.types.ListOrganizationMembershipsOptions
import com.workos.usermanagement.types.ListUsersOptions
import com.workos.usermanagement.types.SendInvitationOptions
import com.workos.usermanagement.types.UpdateUserOptions
import org.apache.http.client.utils.URIBuilder

class UserManagementApi(private val workos: WorkOS) {
  /**
   * Get the details of an existing user.
   */
  fun getUser(userId: String): User {
    return workos.get("/user_management/users/$userId", User::class.java)
  }

  /**
   * Get a list of all the existing users matching the criteria specified.
   */
  fun listUsers(options: ListUsersOptions? = null): Users {
    return workos.get(
      "/user_management/users",
      Users::class.java,
      RequestConfig.builder().data(options ?: ListUsersOptions()).build()
    )
  }

  /**
   * Create a new user in the current environment.
   */
  fun createUser(options: CreateUserOptions): User {
    return workos.post(
      "/user_management/users",
      User::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /**
   * Updates properties of a user. The omitted properties will be left unchanged.
   */
  fun updateUser(userId: String, options: UpdateUserOptions): User {
    return workos.put(
      "/user_management/users/$userId",
      User::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /**
   * Deletes a user in the current environment.
   */
  fun deleteUser(userId: String) {
    workos.delete("/user_management/users/$userId")
  }

  /**
   * Get the identities associated with the user.
   */
  fun getUserIdentities(userId: String): Array<Identity> {
    return workos.get("/user_management/users/$userId/identities", Array<Identity>::class.java)
  }

  /**
   * Generate an Oauth2 authorization URL where users will
   * authenticate using the configured SSO Identity Provider.
   */
  fun getAuthorizationUrl(clientId: String, redirectUri: String): AuthorizationUrlOptionsBuilder {
    return AuthorizationUrlOptionsBuilder.create(workos.baseUrl, clientId, redirectUri)
  }

  /**
   * Authenticates a user using AuthKit, OAuth or an organization’s SSO connection.
   */
  fun authenticateWithCode(clientId: String, code: String, options: AuthenticationAdditionalOptions? = null): Authentication {
    return workos.post(
      "/user_management/authenticate",
      Authentication::class.java,
      RequestConfig.builder().data(
        AuthenticationWithCodeOptionsBuilder.create(
          clientId, workos.apiKey, code, options
        ).build()
      ).build()
    )
  }

  /**
   * Authenticates a user with email and password.
   */
  fun authenticateWithPassword(clientId: String, email: String, password: String, options: AuthenticationAdditionalOptions? = null): Authentication {
    return workos.post(
      "/user_management/authenticate",
      Authentication::class.java,
      RequestConfig.builder().data(
        AuthenticationWithPasswordOptionsBuilder.create(
          clientId, workos.apiKey, email, password, options
        ).build()
      ).build()
    )
  }

  /**
   * Authenticates a user by verifying the Magic Auth code sent to the user’s email.
   */
  fun authenticateWithMagicAuth(clientId: String, email: String, code: String, options: AuthenticationAdditionalOptions? = null): Authentication {
    return workos.post(
      "/user_management/authenticate",
      Authentication::class.java,
      RequestConfig.builder().data(
        AuthenticationWithMagicAuthOptionsBuilder.create(
          clientId, workos.apiKey, email, code, options
        ).build()
      ).build()
    )
  }

  /**
   * Use this endpoint to exchange a refresh token for a new access token. Refresh tokens are single use, so a new refresh token is returned.
   */
  fun authenticateWithRefreshToken(clientId: String, refreshToken: String, options: AuthenticationAdditionalOptions? = null): RefreshAuthentication {
    return workos.post(
      "/user_management/authenticate",
      RefreshAuthentication::class.java,
      RequestConfig.builder().data(
        AuthenticationWithRefreshTokenOptionsBuilder.create(
          clientId, workos.apiKey, refreshToken, options
        ).build()
      ).build()
    )
  }

  /**
   * Authenticates a user with an unverified email and verifies their email address.
   */
  fun authenticateWithEmailVerification(clientId: String, code: String, pendingAuthenticationToken: String, options: AuthenticationAdditionalOptions? = null): Authentication {
    return workos.post(
      "/user_management/authenticate",
      Authentication::class.java,
      RequestConfig.builder().data(
        AuthenticationWithEmailVerificationOptionsBuilder.create(
          clientId, workos.apiKey, code, pendingAuthenticationToken, options
        ).build()
      ).build()
    )
  }

  /**
   * Authenticates a user enrolled into MFA using time-based one-time password (TOTP).
   */
  fun authenticateWithTotp(clientId: String, code: String, authenticationChallengeId: String, pendingAuthenticationToken: String, options: AuthenticationAdditionalOptions? = null): Authentication {
    return workos.post(
      "/user_management/authenticate",
      Authentication::class.java,
      RequestConfig.builder().data(
        AuthenticationWithTotpOptionsBuilder.create(
          clientId, workos.apiKey, code, authenticationChallengeId, pendingAuthenticationToken, options
        ).build()
      ).build()
    )
  }

  /**
   * Authenticates a user into an organization they are a member of.
   */
  fun authenticateWithOrganizationSelection(clientId: String, organizationId: String, pendingAuthenticationToken: String, options: AuthenticationAdditionalOptions? = null): Authentication {
    return workos.post(
      "/user_management/authenticate",
      Authentication::class.java,
      RequestConfig.builder().data(
        AuthenticationWithOrganizationSelectionOptionsBuilder.create(
          clientId, workos.apiKey, organizationId, pendingAuthenticationToken, options
        ).build()
      ).build()
    )
  }

  /**
   * This hosts the public key that is used for verifying access tokens.
   */
  fun getJwksUrl(clientId: String): String {
    return URIBuilder(workos.baseUrl)
      .setPath("/sso/jwks/$clientId")
      .toString()
  }

  /**
   * Sends a one-time authentication code to the user’s email address. The code
   * expires in 10 minutes. To verify the code, authenticate the user with Magic Auth.
   */
  fun sendMagicAuthCode(email: String) {
    workos.post(
      "/user_management/magic_auth/send",
      RequestConfig.builder().data(
        SendMagicAuthCodeOptionsBuilder.create(
          email
        ).build()
      ).build()
    )
  }

  /**
   * Enrolls a user in a new authentication factor.
   */
  fun enrollAuthFactor(id: String, options: EnrolledAuthenticationFactorOptions? = null): EnrolledAuthenticationFactor {
    return workos.post(
      "/user_management/users/$id/auth_factors",
      EnrolledAuthenticationFactor::class.java,
      RequestConfig.builder().data(options ?: EnrolledAuthenticationFactorOptions()).build()
    )
  }

  /**
   * Get a list of all authentication factors of a given user.
   */
  fun listAuthFactors(id: String): AuthenticationFactors {
    return workos.get(
      "/user_management/users/$id/auth_factors",
      AuthenticationFactors::class.java,
    )
  }

  /**
   * Send a password reset email and change the user’s password.
   */
  fun sendPasswordResetEmail(email: String, passwordResetUrl: String) {
    return workos.post(
      "/user_management/password_reset/send",
      RequestConfig.builder().data(
        SendPasswordResetEmailOptionsBuilder.create(
          email, passwordResetUrl
        ).build()
      ).build()
    )
  }

  /**
   * Sets a new password using the `token` query parameter from the link that the user received.
   */
  fun resetPassword(token: String, newPassword: String): User {
    return workos.post(
      "/user_management/password_reset/confirm",
      User::class.java,
      RequestConfig.builder().data(
        ResetPasswordOptionsBuilder.create(
          token, newPassword
        ).build()
      ).build()
    )
  }

  /**
   * Get the details of an existing organization membership.
   */
  fun getOrganizationMembership(id: String): OrganizationMembership {
    return workos.get("/user_management/organization_memberships/$id", OrganizationMembership::class.java)
  }

  /**
   * Get a list of all organization memberships matching the criteria specified.
   */
  fun listOrganizationMemberships(options: ListOrganizationMembershipsOptions? = null): OrganizationMemberships {
    return workos.get(
      "/user_management/organization_memberships",
      OrganizationMemberships::class.java,
      RequestConfig.builder().data(options ?: ListOrganizationMembershipsOptions()).build()
    )
  }

  /**
   * Creates a new organization membership for the given organization and user.
   */
  fun createOrganizationMembership(options: CreateOrganizationMembershipOptions): OrganizationMembership {
    return workos.post(
      "/user_management/organization_memberships",
      OrganizationMembership::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /**
   * Update the details of an existing organization membership.
   */
  fun updateOrganizationMembership(id: String, roleSlug: String): OrganizationMembership {
    return workos.put(
      "/user_management/organization_memberships/$id",
      OrganizationMembership::class.java,
      RequestConfig.builder().data(
        UpdateOrganizationMembershipOptionsBuilder.create(
          id, roleSlug
        ).build()
      ).build()
    )
  }

  /**
   * Deletes an existing organization membership.
   */
  fun deleteOrganizationMembership(id: String) {
    workos.delete("/user_management/organization_memberships/$id")
  }

  /**
   * Get the details of an existing invitation.
   */
  fun getInvitation(id: String): Invitation {
    return workos.get("/user_management/invitations/$id", Invitation::class.java)
  }

  /**
   * Get a list of all the existing invitations matching the criteria specified.
   */
  fun listInvitations(options: ListInvitationsOptions? = null): Invitations {
    return workos.get(
      "/user_management/invitations",
      Invitations::class.java,
      RequestConfig.builder().data(options ?: ListInvitationsOptions()).build()
    )
  }

  /**
   * Sends an invitation email to the recipient.
   */
  fun sendInvitation(options: SendInvitationOptions): Invitation {
    return workos.post(
      "/user_management/invitations",
      Invitation::class.java,
      RequestConfig.builder().data(options).build()
    )
  }

  /**
   * Revokes an existing invitation.
   */
  fun revokeInvitation(id: String): Invitation {
    return workos.post("/user_management/invitations/$id/revoke", Invitation::class.java)
  }

  /**
   * End a user's session. The user's browser should be redirected to this URL.
   */
  fun getLogoutUrl(sessionId: String): String {
    return URIBuilder(workos.baseUrl)
      .setPath("/user_management/sessions/logout")
      .addParameter("session_id", sessionId)
      .toString()
  }
}
