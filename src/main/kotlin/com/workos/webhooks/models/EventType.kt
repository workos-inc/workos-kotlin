package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonValue

/**
 * An enumeration of event types for a [WebhookEvent]
 *
 * @param value The Event Type string value.
 */
enum class EventType(
  @JsonValue @JvmField
  val value: String
) {
  /**
   * Triggers when a user successfully verifies their email.
   */
  AuthenticationEmailVerificationSucceeded("authentication.email_verification_succeeded"),

  /**
   * Triggers when a user fails to authenticate via Magic Auth.
   */
  AuthenticationMagicAuthFailed("authentication.magic_auth_failed"),

  /**
   * Triggers when a user successfully authenticates via Magic Auth.
   */
  AuthenticationMagicAuthSucceeded("authentication.magic_auth_succeeded"),

  /**
   * Triggers when a user successfully authenticates with a multi-factor authentication code.
   */
  AuthenticationMfaSucceeded("authentication.mfa_succeeded"),

  /**
   * Triggers when a user fails to authenticate via OAuth.
   */
  AuthenticationOauthFailed("authentication.oauth_failed"),

  /**
   * Triggers when a user successfully authenticates via OAuth.
   */
  AuthenticationOauthSucceeded("authentication.oauth_succeeded"),

  /**
   * Triggers when a user fails to authenticate with password credentials.
   */
  AuthenticationPasswordFailed("authentication.password_failed"),

  /**
   * Triggers when a user successfully authenticates with password credentials.
   */
  AuthenticationPasswordSucceeded("authentication.password_succeeded"),

  /**
   * Triggers when a user fails to authenticate with Single Sign-On.
   */
  AuthenticationSsoFailed("authentication.sso_failed"),

  /**
   * Triggers when a user successfully authenticates with Single Sign-On.
   */
  AuthenticationSsoSucceeded("authentication.sso_succeeded"),

  /**
   * Triggers when a Connection's state changes to active.
   */
  ConnectionActivated("connection.activated"),

  /**
   * Triggers when a Connection's state changes to inactive.
   */
  ConnectionDeactivated("connection.deactivated"),

  /**
   * Triggers when a Connection is deleted.
   */
  ConnectionDeleted("connection.deleted"),

  /**
   * Triggers when a Directory's state changes to active.
   */
  DirectoryActivated("dsync.activated"),

  /**
   * Triggers when a Directory is deleted.
   */
  DirectoryDeleted("dsync.deleted"),

  /**
   * Triggers when a user is created or added to the directory
   */
  DirectoryUserCreated("dsync.user.created"),

  /**
   * Triggers when a user's properties have been updated.
   */
  DirectoryUserUpdated("dsync.user.updated"),

  /**
   * Triggers when a user has been removed from a directory.
   */
  DirectoryUserDeleted("dsync.user.deleted"),

  /**
   * Triggers when a group is created or added to a directory.
   */
  DirectoryGroupCreated("dsync.group.created"),

  /**
   * Triggers when a group's properties have been updated.
   */
  DirectoryGroupUpdated("dsync.group.updated"),

  /**
   * Triggers when a user has been added to a group.
   */
  DirectoryGroupUserAdded("dsync.group.user_added"),

  /**
   * Triggers when a user has been removed from a group.
   */
  DirectoryGroupUserRemoved("dsync.group.user_removed"),

  /**
   * Triggers when a group is removed from a directory.
   */
  DirectoryGroupDeleted("dsync.group.deleted"),

  /**
   * Triggers when a user is required to verify their email.
   */
  EmailVerificationCreated("email_verification.created"),

  /**
   * Triggers when a user is invited to sign up or to join an organization.
   */
  InvitationCreated("invitation.created"),

  /**
   * Triggers when an organization membership is created.
   */
  OrganizationMembershipCreated("organization_membership.created"),

  /**
   * Triggers when a user initiates Magic Auth and an authentication code is created.
   */
  MagicAuthCreated("magic_auth.created"),

  /**
   * Triggers when an organization membership is deleted.
   */
  OrganizationMembershipDeleted("organization_membership.deleted"),

  /**
   * Triggers when an organization membership is updated.
   */
  OrganizationMembershipUpdated("organization_membership.updated"),

  /**
   * Triggers when a user requests to reset their password.
   */
  PasswordResetCreated("password_reset.created")
}
