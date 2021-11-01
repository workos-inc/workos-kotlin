package com.workos.directorysync.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class User
@JsonCreator constructor(
  @JsonProperty("object")
  val obj: String = "directory_user",

  val id: String,

  @JsonProperty("directory_id")
  val directoryId: String,

  @JsonProperty("idp_id")
  val idpId: String,

  @JsonProperty("username")
  val userName: String?,

  @JsonProperty("first_name")
  val firstName: String?,

  @JsonProperty("last_name")
  val lastName: String?,

  val emails: List<Email>,

  val groups: List<Group>,

  val state: UserState,

  @JsonProperty("custom_attributes")
  val customAttributes: Map<String, String>,

  @JsonProperty("raw_attributes")
  val rawAttributes: Map<String, Any>,
)
