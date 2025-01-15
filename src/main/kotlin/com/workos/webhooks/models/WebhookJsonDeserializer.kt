package com.workos.webhooks.models

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.workos.directorysync.models.Directory
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User
import com.workos.sso.models.Connection
import com.workos.usermanagement.models.AuthenticationEventData
import com.workos.usermanagement.models.EmailVerificationEventData
import com.workos.usermanagement.models.InvitationEventData
import com.workos.usermanagement.models.MagicAuthEventData
import com.workos.usermanagement.models.OrganizationMembership
import com.workos.usermanagement.models.PasswordResetEventData

/**
 * Custom JSON deserializer for [com.workos.webhooks.models.WebhookEvent] events.
 */
class WebhookJsonDeserializer : JsonDeserializer<WebhookEvent>() {
  private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

  init {
    mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  }

  /**
   * @suppress
   */
  override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): WebhookEvent {
    val rootNode = jp?.codec?.readTree<TreeNode>(jp)
    val id = mapper.readValue(rootNode?.get("id")?.traverse(), String::class.java)
    val eventType = mapper.readValue(rootNode?.get("event")?.traverse(), EventType::class.java)!!
    val data = rootNode?.get("data")
    val createdAt = mapper.readValue(rootNode?.get("created_at")?.traverse(), String::class.java)

    return when (eventType) {
      EventType.AuthenticationEmailVerificationSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationMagicAuthFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationMagicAuthSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationMfaSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationOauthFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationOauthSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationPasswordFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationPasswordSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationSsoFailed -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.AuthenticationSsoSucceeded -> AuthenticationEvent(id, eventType, deserializeData(data, AuthenticationEventData::class.java), createdAt)
      EventType.ConnectionActivated -> ConnectionActivatedEvent(id, eventType, deserializeData(data, Connection::class.java), createdAt)
      EventType.ConnectionDeactivated -> ConnectionDeactivatedEvent(id, eventType, deserializeData(data, Connection::class.java), createdAt)
      EventType.ConnectionDeleted -> ConnectionDeletedEvent(id, eventType, deserializeData(data, Connection::class.java), createdAt)
      EventType.DirectoryActivated -> DirectoryActivatedEvent(id, eventType, deserializeData(data, Directory::class.java), createdAt)
      EventType.DirectoryDeleted -> DirectoryDeletedEvent(id, eventType, deserializeData(data, Directory::class.java), createdAt)
      EventType.DirectoryUserCreated -> DirectoryUserCreatedEvent(id, eventType, deserializeData(data, User::class.java), createdAt)
      EventType.DirectoryUserUpdated -> DirectoryUserUpdatedEvent(id, eventType, deserializeData(data, UserUpdated::class.java), createdAt)
      EventType.DirectoryUserDeleted -> DirectoryUserDeletedEvent(id, eventType, deserializeData(data, User::class.java), createdAt)
      EventType.DirectoryGroupCreated -> DirectoryGroupCreatedEvent(id, eventType, deserializeData(data, Group::class.java), createdAt)
      EventType.DirectoryGroupUpdated -> DirectoryGroupUpdatedEvent(id, eventType, deserializeData(data, GroupUpdated::class.java), createdAt)
      EventType.DirectoryGroupDeleted -> DirectoryGroupDeletedEvent(id, eventType, deserializeData(data, Group::class.java), createdAt)
      EventType.DirectoryGroupUserAdded -> DirectoryGroupUserAddedEvent(id, eventType, deserializeData(data, DirectoryGroupUserEvent::class.java), createdAt)
      EventType.DirectoryGroupUserRemoved -> DirectoryGroupUserRemovedEvent(id, eventType, deserializeData(data, DirectoryGroupUserEvent::class.java), createdAt)
      EventType.EmailVerificationCreated -> EmailVerificationEvent(id, eventType, deserializeData(data, EmailVerificationEventData::class.java), createdAt)
      EventType.InvitationCreated -> InvitationEvent(id, eventType, deserializeData(data, InvitationEventData::class.java), createdAt)
      EventType.MagicAuthCreated -> MagicAuthEvent(id, eventType, deserializeData(data, MagicAuthEventData::class.java), createdAt)
      EventType.OrganizationMembershipCreated -> OrganizationMembershipEvent(id, eventType, deserializeData(data, OrganizationMembership::class.java), createdAt)
      EventType.OrganizationMembershipDeleted -> OrganizationMembershipEvent(id, eventType, deserializeData(data, OrganizationMembership::class.java), createdAt)
      EventType.OrganizationMembershipUpdated -> OrganizationMembershipEvent(id, eventType, deserializeData(data, OrganizationMembership::class.java), createdAt)
      EventType.PasswordResetCreated -> PasswordResetEvent(id, eventType, deserializeData(data, PasswordResetEventData::class.java), createdAt)
    }
  }

  private fun <T> deserializeData(data: TreeNode?, clazz: Class<T>): T {
    return mapper.treeToValue(data, clazz)
  }
}
