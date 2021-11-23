package com.workos.webhooks.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.workos.directorysync.models.Email
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User
import com.workos.directorysync.models.UserState

/**
 * @suppress
 */
open class UserUpdated
@JsonCreator constructor(
  @JvmField
  @JsonProperty("object")
  override val obj: String = "directory_user",

  @JvmField
  override val id: String,

  @JvmField
  @JsonProperty("directory_id")
  override val directoryId: String,

  @JvmField
  @JsonProperty("idp_id")
  override val idpId: String,

  @JvmField
  @JsonProperty("username")
  override val userName: String?,

  @JvmField
  @JsonProperty("first_name")
  override val firstName: String?,

  @JvmField
  @JsonProperty("last_name")
  override val lastName: String?,

  @JvmField
  override val emails: List<Email>,

  @JvmField
  override val groups: List<Group>?,

  @JvmField
  override val state: UserState,

  @JvmField
  @JsonProperty("custom_attributes")
  override val customAttributes: Map<String, String>,

  @JvmField
  @JsonProperty("raw_attributes")
  override val rawAttributes: Map<String, Any>,

  /**
   * Object containing the names and values of attributes and their previous values.
   * New attributes that do not appear in the previous snapshot are indicated with a
   * null value.
   */
  @JvmField
  @JsonProperty("previous_attributes")
  val previousAttributes: Map<String, Any>,
) : User(obj, id, directoryId, idpId, userName, firstName, lastName, emails, groups, state, customAttributes, rawAttributes)
